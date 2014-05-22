package standardNaast.view;

import java.awt.Dimension;

import javax.swing.JFrame;

public class TempFrame extends JFrame {

    public TempFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(null);
        this.setSize(new Dimension(400, 300));
        this.setVisible(true);
        //pack();
    }

    public static void main(String args[]) {
        new TempFrame();
    }
}
