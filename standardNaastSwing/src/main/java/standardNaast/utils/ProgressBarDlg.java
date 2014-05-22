// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ProgressBarDlg.java
package standardNaast.utils;

import java.awt.*;
import javax.swing.*;

public class ProgressBarDlg extends JFrame {

    private void jbInit() {
        bar = new JProgressBar();
        label = new JLabel("Processing ...");
        value = 0;
    }

    public ProgressBarDlg(int max) {
        super("Please wait...");
        jbInit();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, "North");
        getContentPane().add(bar, "Center");
        bar.setMinimum(0);
        bar.setMaximum(max);
        bar.setPreferredSize(new Dimension(300, 30));
        bar.setMinimumSize(new Dimension(300, 30));
        bar.setStringPainted(true);
        setResizable(false);
        pack();
        setLocation(300, 300);
        setVisible(true);
    }

    public void doneOne() {
        bar.setValue(++value);
        label.setText((new StringBuilder()).append("Processing ").append(value).append(" of ").append(bar.getMaximum()).toString());
        setVisible(true);
    }

    public void close() {
        setVisible(false);
        dispose();
    }
    JProgressBar bar;
    JLabel label;
    int value;
}