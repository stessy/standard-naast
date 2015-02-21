// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TextEditor.java
package standardNaast.utils;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.text.Document;

public class TextEditor extends JPanel
        implements ActionListener {

    public TextEditor() {
        super(new GridBagLayout());
        textField = new JTextField(20);
        textField.addActionListener(this);
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 0;
        c.fill = 2;
        add(textField, c);
        c.fill = 1;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        add(scrollPane, c);
    }

    public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();
        textArea.append((new StringBuilder()).append(text).append("\n").toString());
        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("TextDemo");
        frame.setDefaultCloseOperation(3);
        frame.add(new TextEditor());
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TextEditor.createAndShowGUI();
            }
        });
    }
    protected JTextField textField;
    protected JTextArea textArea;
    private static final String newline = "\n";
}