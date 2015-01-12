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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This class extends the JTable component, adding filtering, sorting and key
 * based - selection capabilities.
 *
 * <p>
 * Activating the filter is external to the table (for example with a button)
 * <p>
 * Sorting is enabled with a click on a column header.
 * <p>
 * Key based selection is triggered by key input on a cell (when the column is
 * not editable).
 *
 * @author Lilian Chamontin, VLSolutions
 */
@SuppressWarnings("serial")
public class VLJTable extends JTable {

	// default Image set
	private static Image defaultAscImage, defaultDescImage;

	static {
		try {
			VLJTable.defaultAscImage = new ImageIcon(
					VLJTable.class.getResource("sortAsc16.png")).getImage();
			VLJTable.defaultDescImage = new ImageIcon(
					VLJTable.class.getResource("sortDsc16.png")).getImage();
		} catch (final Exception ignore) {
		}
	}

	// local images (arrows used to render the ascending and descending sorting)
	private Image ascendingSortImage, descendingSortImage;

	private boolean isFilterHeaderDisplayed = false;

	private boolean isFilteringEnabled = false;

	private boolean isSortEnabled = true;

	/**
	 * A pop-up triggered by key input, allows fast positionning on a sorted
	 * table.
	 */
	private PopUpSelector popupSelector;

	/**
	 * the associated key trigering listener
	 */
	private KeyListener popupSelectorKeyListener;

	/**
	 * The table header replacement
	 */
	private JPanel tableHeaderReplacement;

	private FilterColumnModel filterColumnModel;

	private boolean isFilterRequestingFocus = true;

	public VLJTable() {
		this.setAscendingSortImage(VLJTable.defaultAscImage);
		this.setDescendingSortImage(VLJTable.defaultDescImage);

		// don't allow column reordering because we're not treating this case
		// for the moment
		// (maybe in a future version)
		this.getTableHeader().setReorderingAllowed(false);
	}

	public void setFilterColumnModel(final FilterColumnModel fcmodel) {
		this.filterColumnModel = fcmodel;
		if (this.isFilterHeaderDisplayed) { // hide it, as it's not the right
			// one anymore
			this.setFilterHeaderVisible(false);
		}
	}

	/**
	 * Returns the filter column model used by this table.
	 * <p>
	 * The FilterColumnModel is responsible for providing JComponents used as
	 * filters for the table column headers.
	 */
	public FilterColumnModel getFilterColumnModel() {
		return this.filterColumnModel;
	}

	/**
	 * Replaces the default ascending sort image by a new one
	 */
	public void setAscendingSortImage(final Image image) {
		this.ascendingSortImage = image;
	}

	/**
	 * Replaces the default descending sort image by a new one
	 */
	public void setDescendingSortImage(final Image image) {
		this.descendingSortImage = image;
	}

	/**
	 * Returns the ascending sort image used by this table
	 */
	public Image getAscendingSortImage() {
		return this.ascendingSortImage;
	}

	/**
	 * Returns the descending sort image used by this table
	 */
	public Image getDescendingSortImage() {
		return this.descendingSortImage;
	}

	/**
	 * Enables filtering with a custom table header.
	 * <p>
	 * This doesn't show the filter zones, but simply allows it to pop-up on
	 * right mouse click
	 *
	 *
	 */
	public void setFilteringEnabled(final boolean enable) {
		this.isFilteringEnabled = enable;
		final FilterModel fm = ((FilterModel) this.getModel());
		if (fm != null) {
			fm.rebuildIndex();
		}
		final JTableHeader th = this.getTableHeader();
		if (th != null) {
			th.repaint();
		}
	}

	/**
	 * Returns a boolean indicating wether the filtering is enabled or not on
	 * this table
	 */
	public boolean isFilteringEnabled() {
		return this.isFilteringEnabled;
	}

	/**
	 * Enable or disable sorting on this table
	 */
	public void setSortEnabled(final boolean enable) {
		this.isSortEnabled = enable;
		final FilterModel fm = ((FilterModel) this.getModel());
		if (fm != null) {
			fm.rebuildIndex();
		}
		// must repaint as sorting images are not bound properties
		final JTableHeader th = this.getTableHeader();
		if (th != null) {
			th.repaint();
		}
	}

	/**
	 * Returns a boolean indicating if sorting is enabled on this table
	 */
	public boolean isSortEnabled() {
		return this.isSortEnabled;
	}

	/**
	 * Updates the visibility of the filters on this table.
	 * <p>
	 * When enabled, a row of editable JComponents is added under the table
	 * header.
	 * <p>
	 * This method is a shortcut to setFilterHeaderVisible(enable, true);
	 *
	 */
	public void setFilterHeaderVisible(final boolean visible) {
		this.setFilterHeaderVisible(visible, true);
	}

	/**
	 * Updates the visibility of the filters on this table.
	 * <p>
	 * When enabled, a row of editable JComponents is added under the table
	 * header.
	 * <p>
	 *
	 * @param visible
	 *            installs the filters if true, removes them if false
	 * @param requestFocus
	 *            position the focus on the first editable filter when the
	 *            filter header is shown
	 */
	public void setFilterHeaderVisible(final boolean visible,
			final boolean requestFocus) {
		if (visible) {
			this.isFilterRequestingFocus = requestFocus;
			this.installFilterHeader();
		} else {
			this.uninstallFilterHeader();
		}
	}

	/**
	 * Returns a boolean indicating if the filter header is visible
	 */
	public boolean isFilterHeaderVisible() {
		return this.isFilterHeaderDisplayed;
	}

	/**
	 * returns true is the focus should be put on the filter zone when it's
	 * displayed
	 */
	public boolean isFilterRequestingFocus() {
		return this.isFilterRequestingFocus;
	}

	/**
	 * enable the popup key selector (fast selection of a row when typing the
	 * first chars)
	 */
	public void setPopUpSelectorEnabled(final boolean enabled) {
		if (this.popupSelectorKeyListener != null) {
			this.removeKeyListener(this.popupSelectorKeyListener);
		}
		if (enabled) {
			this.popupSelectorKeyListener = new KeyAdapter() {

				@Override
				public void keyTyped(final KeyEvent e) {
					VLJTable.this.processTableKeyEvent(e);
				}
			};
			this.addKeyListener(this.popupSelectorKeyListener);
		}
	}

	/**
	 * Returns the PopUpSelector used
	 */
	public PopUpSelector getPopUpSelector() {
		if (this.popupSelector == null) {
			this.popupSelector = this.createPopUpSelector();
		}
		return this.popupSelector;
	}

	/**
	 * overriden to add sorting
	 */
	@Override
	public void addNotify() {
		super.addNotify();

		// installHeader();

		/*
		 * the standard table header is installed during the addNotify()
		 * process. Wo we have to wait addNotify to add our mouse listener.
		 */
		this.getTableHeader().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (VLJTable.this.isSortEnabled) {
					// rotate the sorting (unsorted => ascending => descending )
					final TableColumnModel columnModel = VLJTable.this
							.getColumnModel();
					final int viewColumn = columnModel.getColumnIndexAtX(e
							.getX());
					final int column = VLJTable.this
							.convertColumnIndexToModel(viewColumn);
					final int currentSort = VLJTable.this.getSortMode(column);
					if (currentSort == FilterModel.SORT_NONE) {
						VLJTable.this.setSortMode(column,
								FilterModel.SORT_ASCENDING);
					} else if (currentSort == FilterModel.SORT_ASCENDING) {
						VLJTable.this.setSortMode(column,
								FilterModel.SORT_DESCENDING);
					} else {
						VLJTable.this
						.setSortMode(column, FilterModel.SORT_NONE);
					}
				}
			}
		});

	}

	/**
	 * Installs the table model.
	 * <p>
	 * Warning : this method replaces the model by an internal
	 * (filtering-enabled) one, so getModel() won't return the original model,
	 * but the FilterModel.
	 * <p>
	 * To access the original model, use getBaseModel() instead of getModel().
	 *
	 */
	@Override
	public void setModel(final TableModel model) {
		// TableModel model = this.createDefaultDataModel();
		final FilterModel fModel = new FilterModel(this, model);
		super.setModel(fModel);
		for (int i = 0; i < model.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i)
			.setHeaderRenderer(new VLTableCellRenderer(this, i));
		}

		// install a default FilterColumnModel

		this.setFilterColumnModel(new FilterColumnModel(fModel));

		if (this.isFilterHeaderDisplayed) {
			// show again the filter zones as we were already displaying them
			this.installFilterHeader();
		}
	}

	/**
	 * Returns the base model used by the table (when no filtering or sorting is
	 * done).
	 * <p>
	 * Warning : setModel(model) installs a new FilterModel(model) as the table
	 * model, so getModel() doesn't return the same table model, and this is why
	 * you have to use this getBaseModel() method to keep an access on it.
	 * <p>
	 *
	 */
	public TableModel getBaseModel() {
		return ((FilterModel) this.getModel()).getModel();
	}

	/**
	 * Returns the base row index of a given visible row.
	 * <p>
	 * This method is used to retrieve the original position (in the "base"
	 * model) of a row, when no filtering/sorting is applied.
	 *
	 */
	public int getBaseRow(final int row) {
		return ((FilterModel) this.getModel()).getSourceRow(row);
	}

	/**
	 * Sets a filter on a filter column header.
	 */
	public void setFilterValue(final int col, final Object value) {
		if (this.filterColumnModel != null && this.isFilterHeaderDisplayed) {
			this.filterColumnModel.getFilterCellEditor(col).setValue(value);
		} else {
			((FilterModel) this.getModel()).setFilter(col, value);
			final Rectangle r = this.getCellRect(0, 0, false);
			this.scrollRectToVisible(r);
		}
	}

	/**
	 * Returns the filter used for a column
	 */
	public VLJTableFilter getFilter(final int col) {
		return ((FilterModel) this.getModel()).getFilter(col);
	}

	/**
	 * Activates sorting on a column
	 *
	 * @param col
	 *            the colunm
	 * @param mode
	 *            a sorting mode from FilterModel.SORT_NONE, _ASCENDING or
	 *            _DESCENDING
	 *
	 * @see FilterModel
	 */
	public void setSortMode(final int col, final int mode) {
		this.isSortEnabled = true;
		((FilterModel) this.getModel()).setSortMode(col, mode);
		this.getTableHeader().repaint();
	}

	/**
	 * Returns the sort mode of the given column
	 */
	public int getSortMode(final int col) {
		return ((FilterModel) this.getModel()).getSortMode(col);
	}

	/**
	 * Installs a filter on a column.
	 *
	 */
	public void installFilter(final int col, final VLJTableFilter filter) {
		((FilterModel) this.getModel()).installFilter(col, filter);
	}

	/**
	 * Select the first row of the table matching the given text.
	 * <p>
	 * If text is longer than the first column, a matching is tried with
	 * subsequent columns
	 */
	public void selectFirstRowLike(final int col, final String text,
			final boolean isCaseSensitive) {
		int idx = -1;
		for (int i = 0; i < this.getRowCount(); i++) {
			final StringBuffer sb = new StringBuffer();
			sb.append(this.getValueAt(i, col).toString());
			int colAppend = col + 1;
			final int len = text.length();
			while (sb.length() < len
					&& colAppend < this.getModel().getColumnCount()) {
				sb.append(this.getValueAt(i, colAppend++).toString());
			}
			final String str = sb.toString();
			final int compare = isCaseSensitive ? str.compareTo(text) : str
					.compareToIgnoreCase(text);
			if (compare >= 0) {
				idx = i;
				break;
			}
		}
		if (idx != -1) { // found
			this.setRowSelectionInterval(idx, idx);
			this.scrollRectToVisible(this.getCellRect(idx, col, true));
		} else {
			this.clearSelection();
		}
	}

	/**
	 * Creates a popUpSelector (protected access to allow specific (local)
	 * implementations.
	 * <p>
	 * The popup selector appears when the user press a key on a non-editable
	 * cell, and is used to select the first row beginning by the typed text.
	 */
	protected PopUpSelector createPopUpSelector() {
		return new PopUpSelector(this);
	}

	/**
	 * installs a filter header under the standard table header
	 */
	protected void installFilterHeader() {
		final Container parent = this.getParent();
		if (parent instanceof JViewport) {
			final JPanel newHeader = new JPanel(new BorderLayout());
			newHeader.add(this.getTableHeader(), BorderLayout.CENTER);
			final JPanel subHeader = new JPanel(new FlowLayout(FlowLayout.LEFT,
					0, 0));
			// add a nice border to isolate
			subHeader.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black),
					BorderFactory.createEmptyBorder(1, 0, 0, 0)));

			FilterCellEditor firstFilter = null;

			// install the filter for every column (except when it has a null
			// filter)
			for (int i = 0; i < this.getColumnCount(); i++) {
				final FilterCellEditor fcEditor = this.filterColumnModel
						.getFilterCellEditor(i);
				if (fcEditor != null) {
					// when null (no filter available), we replace it by a
					// simple label
					fcEditor.setValue(null); // empty at first show
				}
				final JComponent tfComp = fcEditor != null ? (JComponent) fcEditor
						: new JLabel();
				if (firstFilter == null) {
					firstFilter = fcEditor;
				}
				final int col = i;
				final int w = this.getColumnModel().getColumn(col).getWidth(); // 1ere
				// valeur
				// de
				// largeur
				final Dimension d = new Dimension(w,
						tfComp.getPreferredSize().height);
				tfComp.setPreferredSize(d);

				// listen to width change to adapt the subHeader
				this.getColumnModel()
				.getColumn(i)
				.addPropertyChangeListener(
						new PropertyChangeListener() {

							@Override
							public void propertyChange(
									final PropertyChangeEvent e) {
								if (e.getPropertyName().equals("width")) {
									// that property is updated with the
									// table column width
									final int w = VLJTable.this
											.getColumnModel()
											.getColumn(col).getWidth();
									final Dimension d = new Dimension(
											w,
											tfComp.getPreferredSize().height);
									tfComp.setPreferredSize(d);
									// as we're on a flow layout, the
									// subHeader will remain in sync
									// with the table header
									// as long as we maintain the same
									// preferred sizes
									tfComp.revalidate();
								}
							}
						});
				subHeader.add(tfComp);
				if (fcEditor != null) {
					fcEditor.addPropertyChangeListener(
							fcEditor.getFilterChangePropertyName(),
							new PropertyChangeListener() {

								@Override
								public void propertyChange(
										final PropertyChangeEvent e) {
									((FilterModel) VLJTable.this.getModel())
									.setFilter(col, e.getNewValue());
									final Rectangle r = VLJTable.this
											.getCellRect(0, 0, false);
									VLJTable.this.scrollRectToVisible(r);
								}
							});
				}
			}

			newHeader.add(subHeader, BorderLayout.SOUTH);
			this.tableHeaderReplacement = newHeader;
			this.installHeader();
			if (firstFilter != null && this.isFilterRequestingFocus) {
				final JComponent filter = (JComponent) firstFilter;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						filter.requestFocus();
					}
				});
			}
		}
		this.isFilterHeaderDisplayed = true;
	}

	/**
	 * overriden to bypass the scrollpane configuration
	 */
	@Override
	protected void configureEnclosingScrollPane() {
		// well, this is a copy of JTable source code, where the tableheader
		// installation
		// has been removed as it created problems with installHeader method.
		final Container p = this.getParent();
		if (p instanceof JViewport) {
			final Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				final JScrollPane scrollPane = (JScrollPane) gp;
				final Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					scrollPane.setBorder(UIManager
							.getBorder("Table.scrollPaneBorder"));
				}
			}
		}

	}

	/**
	 * installs an alternative header or the original one, depending on the
	 * filtering state
	 */
	protected void installHeader() {
		final Container parent = this.getParent();
		if (parent instanceof JViewport) {
			final JScrollPane enclosingScrollPane = (JScrollPane) parent
					.getParent();
			if (this.tableHeaderReplacement == null) {
				// get back to standard header
				enclosingScrollPane.setColumnHeaderView(this.getTableHeader());
			} else { // tableHeaderReplacement != null
				enclosingScrollPane
				.setColumnHeaderView(this.tableHeaderReplacement);
			}
		} // else parent isn't a viewport, and we cannot install a header
	}

	/**
	 * processing of key input on the table : triggers the popup selector if
	 * enabled
	 */
	private void processTableKeyEvent(final KeyEvent e) {
		int col = this.getSelectedColumn();
		int row = this.getSelectedRow();
		if (col == -1) {
			col = 0;
		}
		if (row == -1) {
			row = 0;
		}
		if (this.getModel().isCellEditable(row, col)) {
			return;
		}

		if (Character.isLetterOrDigit(e.getKeyChar())) {
			// avoid intercepting control keys (F10, ESCAPE...)
			if (this.popupSelector == null) {
				this.popupSelector = this.createPopUpSelector();
				this.popupSelector.setCol(col);
			} else {
				this.popupSelector.setCol(col);
			}
			this.popupSelector.setText(String.valueOf(e.getKeyChar()));
			this.popupSelector.popUp();
		}
	}

	/**
	 * removes the filter header (back to standard table header)
	 */
	private void uninstallFilterHeader() {
		this.tableHeaderReplacement = null;
		this.installHeader();
		((FilterModel) this.getModel()).clearFilters();
		this.isFilterHeaderDisplayed = false;
	}
}
