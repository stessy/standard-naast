package com.standardNaast.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.jboss.weld.environment.se.bindings.Parameters;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import com.alee.laf.WebLookAndFeel;

@ApplicationScoped
public class MainApplication {

	private JFrame frmApplicationDuClub;

	public void start(@Observes final ContainerInitialized event,
			@Parameters final List<String> parameters) {
		WebLookAndFeel.install();
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				MainApplication.this.initialize();
				MainApplication.this.frmApplicationDuClub.setVisible(true);
			}
		});
	}

	/**
	 * @wbp.parser.entryPoint
	 */

	public void initialize() {
		this.frmApplicationDuClub = new JFrame();
		this.frmApplicationDuClub.setTitle("Application du club de Naast");
		this.frmApplicationDuClub.setBounds(100, 100, 1919, 928);
		this.frmApplicationDuClub
		.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frmApplicationDuClub.getContentPane().setLayout(
				new BorderLayout(0, 0));
		final JPanel panel = new JPanel();
		this.frmApplicationDuClub.getContentPane().add(panel,
				BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		final JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setOpaque(true);
		lblNewLabel_1.setBackground(new Color(153, 0, 0));
		panel.add(lblNewLabel_1, BorderLayout.NORTH);
		final JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setOpaque(true);
		lblNewLabel.setBackground(new Color(153, 0, 0));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(new ImageIcon(MainApplication.class
				.getResource("/com/standardNaast/view/banniere_club.png")));
		panel.add(lblNewLabel, BorderLayout.CENTER);

		final JMenuBar menuBar = new JMenuBar();

		final JToolBar toolBar = new JToolBar();
		toolBar.add(menuBar);

		final JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		final JMenuItem mntmNewMenuItem = new JMenuItem("New menu item");
		mnNewMenu.add(mntmNewMenuItem);
		panel.add(toolBar, BorderLayout.SOUTH);

		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.frmApplicationDuClub.getContentPane().add(tabbedPane,
				BorderLayout.CENTER);
		final MembersPanel membersPanel = new MembersPanel();
		tabbedPane.addTab("New tab", null, membersPanel, null);

	}

}
