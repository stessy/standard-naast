// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BenevolatDialog.java
package standardNaast.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import standardNaast.utils.NumericOnlyJTextField;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

// Referenced classes of package standardNaast.view:
//            EditionMembrePanel
public class BenevolatDialog extends JDialog {

	public BenevolatDialog() {
		this(((Frame) (null)), "", true);
	}


	public BenevolatDialog(final Frame parent, final String title, final boolean modal) {
		super(parent, title, modal);

		try {
			this.jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public BenevolatDialog(final Vector benevolat, final String type, final EditionMembrePanel editionMembrePanel) {
		this(((Frame) (null)), "B�n�volat", true);
		if (type.equals("add")) {
			this.setDefaultAddValue(benevolat);
			this.editionMembrePanel = editionMembrePanel;
		} else if (type.equals("update")) {
			this.setDefaultUpdateValue(benevolat);
			this.editionMembrePanel = editionMembrePanel;
		}
	}


	private void jbInit() throws Exception {
		this.textField.setEditable(false);
		this.jButtonAjouter.setText("Ajouter");
		this.jButtonAjouter.setBounds(new Rectangle(15, 235, 71, 23));
		this.jButtonAjouter.setEnabled(false);
		this.jButtonModifier.setText("Modifier");
		this.jButtonModifier.setBounds(new Rectangle(158, 235, 71, 23));
		this.jButtonModifier.setEnabled(false);
		this.jButtonEffacer.setText("Effacer");
		this.jButtonEffacer.setBounds(new Rectangle(300, 235, 71, 23));
		this.jButtonEffacer.setEnabled(false);
		this.setSize(new Dimension(400, 300));
		this.getContentPane().setLayout(null);
		this.membre.setText("Membre");
		this.membre.setBounds(new Rectangle(20, 43, 100, 15));
		this.membre.setHorizontalAlignment(4);
		this.date.setText("Date");
		this.date.setBounds(new Rectangle(20, 80, 100, 15));
		this.date.setHorizontalAlignment(4);
		this.montant.setText("Ristourne");
		this.montant.setBounds(new Rectangle(20, 116, 100, 15));
		this.montant.setHorizontalAlignment(4);
		this.typeBenevolat.setText("Type de b�n�volat");
		this.typeBenevolat.setBounds(new Rectangle(20, 170, 100, 15));
		this.typeBenevolat.setHorizontalAlignment(4);
		this.membreField.setBounds(new Rectangle(130, 40, 195, 20));
		this.membreField.setEditable(false);
		this.dateChooser.setBounds(new Rectangle(130, 77, 105, 20));
		this.montantField.setBounds(new Rectangle(130, 113, 130, 20));
		this.typeBenevolatField.setBounds(new Rectangle(130, 150, 180, 55));
		this.getContentPane().add(this.jButtonEffacer, null);
		this.getContentPane().add(this.jButtonModifier, null);
		this.getContentPane().add(this.jButtonAjouter, null);
		this.getContentPane().add(this.typeBenevolatField, null);
		this.getContentPane().add(this.montantField, null);
		this.getContentPane().add(this.dateChooser, null);
		this.getContentPane().add(this.membreField, null);
		this.getContentPane().add(this.typeBenevolat, null);
		this.getContentPane().add(this.montant, null);
		this.getContentPane().add(this.date, null);
		this.getContentPane().add(this.membre, null);
	}


	private void setDefaultUpdateValue(final Vector benevolatValue) {
		this.nomPersonne = (String) benevolatValue.get(0);
		this.benevolatID = ((Integer) benevolatValue.get(1)).intValue();
		this.membreField.setText(this.nomPersonne);
		this.typeBenevolatField.setText((String) benevolatValue.get(3));
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			this.dateChooser.setDate(format.parse((String) benevolatValue.get(2)));
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		this.montantField.setText((new StringBuilder()).append("").append(((Integer) benevolatValue.get(4)).intValue())
				.toString());
		this.personneID = ((Long) benevolatValue.get(5)).longValue();
		this.jButtonModifier.setEnabled(true);
		this.jButtonEffacer.setEnabled(true);
		this.jButtonAjouter.setEnabled(false);
	}


	private void setDefaultAddValue(final Vector benevolatValue) {
		this.nomPersonne = (String) benevolatValue.get(0);
		this.personneID = ((Long) benevolatValue.get(1)).longValue();
		this.membreField.setText(this.nomPersonne);
		this.jButtonModifier.setEnabled(false);
		this.jButtonEffacer.setEnabled(false);
		this.jButtonAjouter.setEnabled(true);
	}

	private final JLabel membre = new JLabel();

	private final JLabel date = new JLabel();

	private final JLabel montant = new JLabel();

	private final JLabel typeBenevolat = new JLabel();

	private final JTextField membreField = new JTextField();

	private final JTextField montantField = new NumericOnlyJTextField(5, "1234567890");

	private final JTextArea typeBenevolatField = new JTextArea();

	private final JDateChooser dateChooser = new JDateChooser("dd-MM-yyyy", "##-##-####", '-');

	JTextFieldDateEditor textField = (JTextFieldDateEditor) this.dateChooser.getDateEditor();

	private final JButton jButtonAjouter = new JButton();

	private final JButton jButtonModifier = new JButton();

	private final JButton jButtonEffacer = new JButton();

	String nomPersonne = new String();

	long personneID;

	int benevolatID;

	EditionMembrePanel editionMembrePanel;
}