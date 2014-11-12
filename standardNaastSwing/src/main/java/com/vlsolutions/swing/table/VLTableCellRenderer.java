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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * The table cell renderer used to display sortable column headers.
 *
 * @author Lilian Chamontin, VLSolutions
 */
@SuppressWarnings("serial")
public class VLTableCellRenderer extends DefaultTableCellRenderer {

	protected VLJTable table;

	protected int col;

	public VLTableCellRenderer() {
	}

	VLTableCellRenderer(final VLJTable table, final int col) {
		this.col = col;
		this.table = table;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (table != null) {
			final JTableHeader header = table.getTableHeader();
			if (header != null) {
				this.setForeground(header.getForeground());
				this.setBackground(header.getBackground());
				this.setFont(header.getFont());
				this.setHorizontalAlignment(JLabel.CENTER);
			}
		}

		this.setText((value == null) ? "" : value.toString());
		this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return this;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		final int sort = this.table.getSortMode(this.col);
		if (sort == FilterModel.SORT_NONE) {
		} else {
			g.setColor(this.getBackground().darker());
			if (sort == FilterModel.SORT_ASCENDING) {
				g.drawImage(this.table.getAscendingSortImage(),
						this.getWidth() - 16, this.getHeight() / 2 - 8, null);
			} else {
				g.drawImage(this.table.getDescendingSortImage(),
						this.getWidth() - 16, this.getHeight() / 2 - 8, null);
			}
		}
	}
}
