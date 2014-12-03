/*
 VLSolutions VLJTable : an enhanced JTable for Swing Applications
 Copyright (C) 2005 VLSolutions http://www.vlsolutions.com

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License version 2.1 as published by the Free Software Foundation.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.vlsolutions.swing.table;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * A simple pop-up added on the table, and triggered by key input.
 *
 * <p>
 * Allows fast positionning on the table.
 * <p>
 * Can be enabled or disabled from the VLJTable (with the popUpSelectorActive
 * property)
 *
 *
 * @author Lilian Chamontin, VLSolutions
 */
@SuppressWarnings("serial")
public class PopUpSelector extends JPopupMenu implements KeyListener,
DocumentListener, FocusListener, ActionListener {

	/**
	 * The input field
	 */
	protected JTextField textField = new JTextField(10);

	/**
	 * the searched column
	 */
	protected int col;

	/**
	 * the label for the column
	 */
	protected JLabel columnLabel = new JLabel();

	/**
	 * the target jtable
	 */
	protected VLJTable table;

	/**
	 * search can be case sensitive or not
	 */
	protected boolean isCaseSensitive = false;

	/**
	 * the container
	 */
	protected JPanel panel = new JPanel(new FlowLayout());

	/**
	 * Creates a new pop up menu for a given table
	 */
	public PopUpSelector(final VLJTable table) {
		this.table = table;
		this.textField.setOpaque(false);

		this.textField.addKeyListener(this);
		this.textField.addFocusListener(this);
		this.textField.addActionListener(this);
		this.textField.getDocument().addDocumentListener(this);

		this.panel.add(this.columnLabel);
		this.panel.add(this.textField);
		this.panel.setBackground(this.getBackground().brighter()); // make it
		// more
		// visible
		this.add(this.panel);
		this.setBorderPainted(true);
		this.setInvoker(table);
		this.pack();

	}

	/**
	 * Creates a new pop up menu for a given table and column
	 */
	public PopUpSelector(final VLJTable table, final int col) {
		this(table);
		this.setCol(col);
	}

	/**
	 * sets the column this pop up is for
	 */
	public void setCol(final int col) {
		this.col = col;
		this.columnLabel.setText(this.table.getModel().getColumnName(col));
	}

	/**
	 * process key event
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_TAB
				|| e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.returnToTable();
		}
	}

	/**
	 * process textfield action
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		this.returnToTable();
	}

	/**
	 * close this popup
	 */
	public void returnToTable() {
		this.setVisible(false);
		this.table.requestFocus();
	}

	/**
	 * implementation side effect : does nothing
	 */
	@Override
	public void keyTyped(final KeyEvent e) {
	}

	/**
	 * process key released to close the pop up
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_DOWN
				|| e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.returnToTable();
		}
	}

	/**
	 * implementation side effect : does nothing
	 */
	@Override
	public void focusGained(final FocusEvent e) {
	}

	/**
	 * closes the pop up on focus lost
	 */
	@Override
	public void focusLost(final FocusEvent e) {
		this.setVisible(false);
	}

	/**
	 * process text field document update : triggers a table positionning
	 */
	@Override
	public void insertUpdate(final DocumentEvent e) {
		this.updateSelection(e.getDocument());
	}

	/**
	 * process text field document update : triggers a table positionning
	 */
	@Override
	public void removeUpdate(final DocumentEvent e) {
		this.updateSelection(e.getDocument());
	}

	/**
	 * process text field document update : triggers a table positionning
	 */
	@Override
	public void changedUpdate(final DocumentEvent e) {
		this.updateSelection(e.getDocument());
	}

	private void updateSelection(final Document d) {
		try {
			this.table.selectFirstRowLike(this.col,
					d.getText(0, d.getLength()), this.isCaseSensitive);
		} catch (final Exception ignore) {
		}
	}

	/**
	 * update the case-sensitive property
	 */
	public void setCaseSensitive(final boolean caseSensitive) {
		this.isCaseSensitive = caseSensitive;
	}

	/**
	 * returns true if search is case sensitive
	 */
	public boolean isCaseSensitive() {
		return this.isCaseSensitive;
	}

	/**
	 * set the content of the text field
	 */
	public void setText(final String text) {
		this.textField.setText(text);
	}

	/**
	 * returns the content of the text field
	 */
	public String getText() {
		return this.textField.getText();
	}

	/**
	 * shows the popup on top of the table
	 */
	public void popUp() {
		final Insets i = this.getInsets();
		final int h = this.panel.getPreferredSize().height + i.top + i.bottom;
		// we get the height from the contained panel as getHeight() returns 0
		// until the
		// pop up is displayed
		if (this.table.getParent() instanceof JViewport) {
			// for JViewport usage (standard), we have to be located relatively
			// to the viewport
			this.show(this.table.getParent(), 0, -h);
		} else {
			this.show(this.table, 0, -h);
		}
		this.textField.requestFocus();
	}
}
