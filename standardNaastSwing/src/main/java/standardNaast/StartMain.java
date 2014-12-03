/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package standardNaast;

import org.jboss.weld.environment.se.Weld;

import com.standardNaast.view.MainApplication;

/**
 *
 * @author stessy
 */
public class StartMain {

	public static void main(final String args[]) {
		StartMain.start();

		// java.awt.EventQueue.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// try {
		// try {
		//
		// PlasticLookAndFeel.setPlasticTheme(new SkyRed());
		// UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		// } catch (final Throwable t) {
		// try {
		// UIManager.setLookAndFeel(UIManager
		// .getSystemLookAndFeelClassName());
		// } catch (final Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// final StandardNaastMain frame = new StandardNaastMain();
		// // StandardNaastFrame frame = new StandardNaastFrame();
		// frame.setDefaultCloseOperation(0);
		// final Dimension screenSize = Toolkit.getDefaultToolkit()
		// .getScreenSize();
		// final Dimension frameSize = frame.getSize();
		// if (frameSize.height > screenSize.height) {
		// frameSize.height = screenSize.height;
		// }
		// if (frameSize.width > screenSize.width) {
		// frameSize.width = screenSize.width;
		// }
		// frame.setLocation((screenSize.width - frameSize.width) / 2,
		// (screenSize.height - frameSize.height) / 2);
		// frame.addWindowListener(new WindowAdapter() {
		//
		// @Override
		// public void windowClosing(final WindowEvent e) {
		// System.exit(0);
		// }
		// });
		// frame.setVisible(true);
		// } catch (final Exception ex) {
		// ex.printStackTrace();
		// System.exit(1);
		// }
		//
		// }
		// });
	}

	private static void start() {

		final Weld weld = new Weld();
		weld.initialize().instance().select(MainApplication.class).get();

		// java.awt.EventQueue.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// try {
		// try {
		//
		// PlasticLookAndFeel.setPlasticTheme(new SkyRed());
		// UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		// } catch (Throwable t) {
		// try {
		// UIManager.setLookAndFeel(UIManager
		// .getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// StandardNaastMain frame = new StandardNaastMain();
		// // StandardNaastFrame frame = new StandardNaastFrame();
		// frame.setDefaultCloseOperation(0);
		// Dimension screenSize = Toolkit.getDefaultToolkit()
		// .getScreenSize();
		// Dimension frameSize = frame.getSize();
		// if (frameSize.height > screenSize.height) {
		// frameSize.height = screenSize.height;
		// }
		// if (frameSize.width > screenSize.width) {
		// frameSize.width = screenSize.width;
		// }
		// frame.setLocation((screenSize.width - frameSize.width) / 2,
		// (screenSize.height - frameSize.height) / 2);
		// frame.addWindowListener(new WindowAdapter() {
		//
		// @Override
		// public void windowClosing(final WindowEvent e) {
		// System.exit(0);
		// }
		// });
		// frame.setVisible(true);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// System.exit(1);
		// }
		//
		// }
		// });

	}

}