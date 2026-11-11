package standardNaast.backend.desktop;

import org.junit.jupiter.api.Test;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Mockito.*;

class DesktopDirectoryChooserTest {
    @TempDir Path directory;
    @Test
    void directoryDialogIsCenteredAndRemainsAboveTheBrowser() {
        var dialog = mock(JDialog.class);
        DesktopStorage.prepareDirectoryDialog(dialog);
        verify(dialog).setAlwaysOnTop(true);
        verify(dialog).setLocationRelativeTo(null);
        verify(dialog).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    @Test
    void chooserAcceptsDirectoriesOnlyAndKeepsItsTitle() {
        var chooser = DesktopStorage.directoryChooser("", "Repertoire des logs");
        assertThat(chooser.getFileSelectionMode()).isEqualTo(JFileChooser.DIRECTORIES_ONLY);
        assertThat(chooser.isAcceptAllFileFilterUsed()).isFalse();
        assertThat(chooser.getDialogTitle()).isEqualTo("Repertoire des logs");
    }

    @Test
    void visibleNativeDialogRejectsNestedSelectionsAndWindowCloseCancelsTheRequest() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());
        var storage = new DesktopStorage(directory.toString());
        storage.activate(true);
        var opened = new CountDownLatch(1);
        var dialog = new AtomicReference<JDialog>();
        java.awt.event.AWTEventListener listener = event -> {
            if (event instanceof WindowEvent window && window.getID() == WindowEvent.WINDOW_OPENED
                    && window.getWindow() instanceof JDialog candidate
                    && candidate.getTitle().equals("Repertoire Google Drive des sauvegardes")) {
                dialog.set(candidate);
                opened.countDown();
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, java.awt.AWTEvent.WINDOW_EVENT_MASK);
        var result = CompletableFuture.supplyAsync(() -> {
            try { return storage.chooseDirectory("backup"); }
            catch (java.io.IOException e) { throw new java.util.concurrent.CompletionException(e); }
        });
        try {
            assertThat(opened.await(10, TimeUnit.SECONDS)).isTrue();
            SwingUtilities.invokeAndWait(() -> {
                assertThat(dialog.get().isVisible()).isTrue();
                assertThat(dialog.get().isAlwaysOnTop()).isTrue();
            });
            assertThatThrownBy(() -> storage.chooseDirectory("logs"))
                    .isInstanceOf(java.io.IOException.class).hasMessageContaining("deja ouverte");
            SwingUtilities.invokeAndWait(() -> dialog.get().dispatchEvent(
                    new WindowEvent(dialog.get(), WindowEvent.WINDOW_CLOSING)));
            assertThat(result.get(10, TimeUnit.SECONDS)).isNull();
        } finally {
            Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
            if (dialog.get() != null) SwingUtilities.invokeAndWait(() -> dialog.get().dispose());
        }
    }
}
