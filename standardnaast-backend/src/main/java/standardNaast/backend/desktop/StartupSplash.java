package standardNaast.backend.desktop;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

final class StartupSplash implements AutoCloseable {
    private JDialog window;
    private JLabel status;

    static StartupSplash open(boolean disabled) throws IOException {
        var splash = new StartupSplash();
        if (!disabled && !GraphicsEnvironment.isHeadless()) {
            onUiThread(() -> {
                splash.window = new JDialog((Frame) null, "Standard Naast - Demarrage", false);
                splash.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                var panel = new JPanel(new BorderLayout(16, 16));
                panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
                var title = new JLabel("Standard Naast");
                title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
                panel.add(title, BorderLayout.NORTH);
                splash.status = new JLabel("Preparation du dossier de donnees...");
                panel.add(splash.status, BorderLayout.CENTER);
                var progress = new JProgressBar();
                progress.setIndeterminate(true);
                panel.add(progress, BorderLayout.SOUTH);
                splash.window.setContentPane(panel);
                splash.window.setSize(480, 170);
                splash.window.setLocationRelativeTo(null);
                splash.window.setVisible(true);
            });
        }
        return splash;
    }

    void update(String message) {
        if (window != null) {
            SwingUtilities.invokeLater(() -> status.setText(message));
        }
    }

    void visible(boolean visible) throws IOException {
        if (window != null) onUiThread(() -> window.setVisible(visible));
    }

    @Override
    public void close() throws IOException {
        if (window != null) onUiThread(window::dispose);
    }

    private static void onUiThread(Runnable action) throws IOException {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Affichage du demarrage interrompu.", e);
        } catch (InvocationTargetException e) {
            throw new IOException("Impossible d'afficher la fenetre de demarrage.", e.getCause());
        }
    }
}
