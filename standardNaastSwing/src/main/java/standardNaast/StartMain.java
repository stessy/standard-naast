/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package standardNaast;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import standardNaast.view.StandardNaastMain;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyRed;

/**
 * 
 * @author stessy
 */
public class StartMain {

	public static void main(final String args[]) {
		StartMain.start();
	}

	private static void start() {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				try {
					try {

						PlasticLookAndFeel.setPlasticTheme(new SkyRed());
						UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
					} catch (Throwable t) {
						try {
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					StandardNaastMain frame = new StandardNaastMain();
					// StandardNaastFrame frame = new StandardNaastFrame();
					frame.setDefaultCloseOperation(0);
					Dimension screenSize = Toolkit.getDefaultToolkit()
							.getScreenSize();
					Dimension frameSize = frame.getSize();
					if (frameSize.height > screenSize.height) {
						frameSize.height = screenSize.height;
					}
					if (frameSize.width > screenSize.width) {
						frameSize.width = screenSize.width;
					}
					frame.setLocation((screenSize.width - frameSize.width) / 2,
							(screenSize.height - frameSize.height) / 2);
					frame.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosing(final WindowEvent e) {
							System.exit(0);
						}
					});
					frame.setVisible(true);
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}

			}
		});

	}

}