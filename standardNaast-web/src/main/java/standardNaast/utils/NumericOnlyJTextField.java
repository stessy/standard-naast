// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   NumericOnlyJTextField.java
package standardNaast.utils;

import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.text.*;

public class NumericOnlyJTextField extends JTextField {

    class NumericOnlyDocument extends PlainDocument {

        private void jbInit() {
            maxLength = -1;
            allowedChars = "";
        }

        public void insertString(int offset, String str, AttributeSet a)
                throws BadLocationException {
            if (getLength() + str.length() > maxLength) {
                toolkit.beep();
            } else if (str.length() > 0 && allowedChars.length() == 0) {
                char charCode = str.charAt(0);
                if (!Character.isDigit(charCode)) {
                    toolkit.beep();
                } else {
                    super.insertString(offset, str, a);
                }
            } else {
                for (int i = 0; i < allowedChars.length(); i++) {
                    char charCode = str.charAt(0);
                    if (charCode == allowedChars.charAt(i)) {
                        super.insertString(offset, str, a);
                    }
                }

            }
        }
        private Toolkit toolkit;
        private int maxLength;
        private String allowedChars;
        final NumericOnlyJTextField this$0;

        public NumericOnlyDocument() {
            this$0 = NumericOnlyJTextField.this;
            /*super();
             jbInit();*/
        }

        public NumericOnlyDocument(int maxLength, String allow) {
            this$0 = NumericOnlyJTextField.this;
            /*super();
             jbInit();*/
            allowedChars = allow;
            toolkit = Toolkit.getDefaultToolkit();
            if (maxLength <= 0) {
                this.maxLength = 10;
            } else {
                this.maxLength = maxLength;
            }
        }
    }

    public NumericOnlyJTextField() {
        setDocument(new NumericOnlyDocument());
    }

    public NumericOnlyJTextField(String text) {
        super(text);
        setDocument(new NumericOnlyDocument());
    }

    public NumericOnlyJTextField(int columns, String allow) {
        super(columns);
        setDocument(new NumericOnlyDocument(columns, allow));
    }

    public NumericOnlyJTextField(String text, int columns, String allow) {
        super(text, columns);
        setDocument(new NumericOnlyDocument(columns, allow));
    }

    public NumericOnlyJTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }
}