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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Default implementation of a Filter cell editor : works only with String
 * content, as it uses a JTextField as input.
 *
 *
 * @author Lilian Chamontin, VLSolutions
 */
@SuppressWarnings("serial")
public class DefaultFilterCellEditor extends JTextField implements
		FilterCellEditor, DocumentListener {

	public DefaultFilterCellEditor() {
		this.getDocument().addDocumentListener(this);
	}

	@Override
	public void insertUpdate(final DocumentEvent e) {
		this.changed(e);
	}

	@Override
	public void changedUpdate(final DocumentEvent e) {
		this.changed(e);
	}

	@Override
	public void removeUpdate(final DocumentEvent e) {
		this.changed(e);
	}

	private void changed(final DocumentEvent e) {
		this.firePropertyChange(this.getFilterChangePropertyName(), null,
				this.getText());
	}

	@Override
	public Object getValue() {
		return this.getText();
	}

	@Override
	public void setValue(final Object value) {
		if (value == null) {
			this.setText("");
		} else {
			this.setText(value.toString());
		}
	}

	@Override
	public String getFilterChangePropertyName() {
		return "FilterContent";
	}
}