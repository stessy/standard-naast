package standardNaast.backend.desktop;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JDialog;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Profile("desktop")
@DependsOn({"dataSource", "entityManagerFactory"})
public class DesktopStorage implements DisposableBean {
    public record SettingsView(String backupDirectory, String logsDirectory, boolean logsRestartRequired,
                               String latestBackup, int conflictingVersions, String backupError) {}

    private final DesktopBackupStore store;
    private final String currentLogs;
    private boolean active;
    private boolean interactive;
    private final AtomicBoolean choosingDirectory = new AtomicBoolean();

    public DesktopStorage(@Value("${app.desktop.data-dir}") String directory) throws IOException {
        store = new DesktopBackupStore(Path.of(directory));
        currentLogs = store.settings().logsDirectory();
    }

    void activate(boolean interactive) {
        this.active = true;
        this.interactive = interactive;
    }

    public synchronized SettingsView settings() throws IOException {
        var settings = store.settings();
        List<DesktopBackupStore.Version> versions;
        String error = null;
        try {
            versions = store.versions();
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger(DesktopStorage.class).error("Repertoire de sauvegarde indisponible", e);
            versions = List.of();
            error = e.getMessage();
        }
        int heads = store.heads(versions).size();
        return new SettingsView(settings.backupDirectory(), settings.logsDirectory(),
                !currentLogs.equals(settings.logsDirectory()),
                versions.isEmpty() ? null : versions.getFirst().created().toString(), heads > 1 ? heads : 0, error);
    }

    public synchronized SettingsView save(String backups, String logs) throws IOException {
        store.saveSettings(backups, logs);
        return settings();
    }

    public void preflight() throws IOException { store.preflight(); }

    public String chooseDirectory(String kind) throws IOException {
        if (!List.of("backup", "logs").contains(kind)) throw new IllegalArgumentException("Type de repertoire inconnu.");
        if (!interactive || GraphicsEnvironment.isHeadless()) {
            throw new IOException("Selection native indisponible dans ce mode.");
        }
        if (!choosingDirectory.compareAndSet(false, true)) {
            throw new IOException("Une selection de repertoire est deja ouverte dans Standard Naast.");
        }
        try {
            String initial = kind.equals("backup") ? store.settings().backupDirectory() : store.settings().logsDirectory();
            return onEventThread(() -> {
                var chooser = directoryChooser(initial,
                        kind.equals("backup") ? "Repertoire Google Drive des sauvegardes" : "Repertoire des logs");
                return chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                        ? chooser.getSelectedFile().toPath().toAbsolutePath().normalize().toString() : null;
            });
        } finally {
            choosingDirectory.set(false);
        }
    }

    static JFileChooser directoryChooser(String initial, String title) {
        var chooser = new JFileChooser(initial.isBlank() ? null : initial) {
            @Override
            protected JDialog createDialog(java.awt.Component parent) {
                JDialog dialog = super.createDialog(parent);
                prepareDirectoryDialog(dialog);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowOpened(java.awt.event.WindowEvent event) {
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.APP_REQUEST_FOREGROUND)) {
                                desktop.requestForeground(true);
                            }
                        }
                        dialog.toFront();
                        dialog.requestFocus();
                    }
                });
                return dialog;
            }
        };
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(title);
        return chooser;
    }

    static void prepareDirectoryDialog(JDialog dialog) {
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    static DesktopBackupStore.Version chooseRestore(List<DesktopBackupStore.Version> versions, boolean dirty) throws IOException {
        return onEventThread(() -> {
            String message = (versions.size() > 1
                    ? "Plusieurs versions concurrentes existent. Choisissez celle a utiliser.\n"
                    : "La base locale peut contenir des modifications non sauvegardees.\n")
                    + "Une copie locale de recuperation sera conservee avant toute restauration.\n"
                    + "Verifier que Google Drive a termine sa synchronisation.";
            var selection = new javax.swing.JComboBox<>(versions.toArray(DesktopBackupStore.Version[]::new));
            var panel = new javax.swing.JPanel(new java.awt.BorderLayout(8, 8));
            var explanation = new javax.swing.JTextArea(message);
            explanation.setEditable(false);
            explanation.setOpaque(false);
            panel.add(explanation, java.awt.BorderLayout.NORTH);
            panel.add(selection, java.awt.BorderLayout.CENTER);
            Object[] options = {"Restaurer la version selectionnee", "Conserver ma base locale", "Annuler le demarrage"};
            int choice = JOptionPane.showOptionDialog(null, panel,
                    dirty ? "Donnees locales non sauvegardees" : "Conflit de sauvegardes",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);
            if (choice == 0) return (DesktopBackupStore.Version) selection.getSelectedItem();
            if (choice == 1) return null;
            throw new CancellationException("Restauration annulee.");
        });
    }

    static boolean confirmLocalStart(IOException error) throws IOException {
        return onEventThread(() -> JOptionPane.showOptionDialog(null,
                "Impossible de verifier/restaurer la sauvegarde partagee.\n" + error.getMessage()
                        + "\nVous pouvez ouvrir la base locale pour corriger les parametres."
                        + "\nNe changez pas d'ordinateur tant que la sauvegarde n'est pas resolue.",
                "Standard Naast - Sauvegarde indisponible", JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE, null, new Object[]{"Ouvrir la base locale", "Annuler le demarrage"},
                "Annuler le demarrage") == 0);
    }
    private static <T> T onEventThread(Callable<T> action) throws IOException {
        var task = new FutureTask<>(action);
        try {
            if (SwingUtilities.isEventDispatchThread()) task.run();
            else SwingUtilities.invokeAndWait(task);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Dialogue interrompu.", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new IOException("Dialogue indisponible.", e.getCause());
        } catch (ExecutionException e) {
            if (e.getCause() instanceof CancellationException cancelled) throw cancelled;
            throw new IOException("Dialogue indisponible.", e.getCause());
        }
    }

    @Override
    public void destroy() {
        if (!active) return;
        try {
            store.finish();
            org.slf4j.LoggerFactory.getLogger(DesktopStorage.class).info("Sauvegarde desktop finalisee");
        } catch (IOException e) {
            org.slf4j.LoggerFactory.getLogger(DesktopStorage.class).error("Echec de la sauvegarde desktop. Base locale conservee.", e);
            if (interactive && !GraphicsEnvironment.isHeadless()) {
                try {
                    onEventThread(() -> {
                        JOptionPane.showMessageDialog(null,
                                "La sauvegarde n'a pas pu etre finalisee.\nLa base locale est conservee.\n"
                                        + "Ne commencez pas une session sur un autre ordinateur avant de resoudre ce probleme.\n"
                                        + e.getMessage(),
                                "Standard Naast - Echec de sauvegarde", JOptionPane.ERROR_MESSAGE);
                        return null;
                    });
                } catch (IOException dialogError) {
                    org.slf4j.LoggerFactory.getLogger(DesktopStorage.class).error("Notification de sauvegarde impossible", dialogError);
                }
            }
        }
    }
}
