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

import java.util.ArrayList;
import java.util.Date;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * A filtering table model, to be used with VLJTable.
 *
 * The Sorting related content is based on the Java tutorial.
 *
 * @author Lilian Chamontin, VLSolutions
 */
@SuppressWarnings("serial")
public class FilterModel extends AbstractTableModel implements
TableModelListener {

	// our sorting constants (3 modes : unsorted, sorted asc, sorted desc)
	/**
	 * sort mode : unsorted
	 */
	public static final int SORT_NONE = 0;

	/**
	 * ascending sort
	 */
	public static final int SORT_ASCENDING = 1;

	/**
	 * descenging sort
	 */
	public static final int SORT_DESCENDING = 2;

	protected int[] filterIndex = new int[0];

	protected VLJTableFilter[] filters;

	protected int[] sortModes;

	protected int[] indexes;

	protected TableModel model;

	protected VLJTable table;

	/**
	 * Creates a filter model for a table, and with a given base model
	 */
	public FilterModel(final VLJTable table, final TableModel model) {
		this.table = table;
		this.model = model;
		this.filters = new VLJTableFilter[model.getColumnCount()];
		this.sortModes = new int[model.getColumnCount()];
		this.rebuildIndex();
		model.addTableModelListener(this);
	}

	/**
	 * returns the base (source) model used by this table model
	 */
	public TableModel getModel() {
		return this.model;
	}

	/**
	 * Removes all filters from the model (all data contained in the base model
	 * will be shown)
	 */
	public void clearFilters() {
		final int cols = this.model.getColumnCount();
		for (int i = 0; i < cols; i++) {
			this.filters[i].setFilter(null);
		}
		this.rebuildIndex();
	}

	/**
	 * Returns the corresponding row in the base model
	 */
	public int getSourceRow(final int row) {
		return this.filterIndex[this.indexes[row]];
	}

	/**
	 * Sets a filter value for a given column
	 */
	public void setFilter(final int col, final Object value) {
		this.filters[col].setFilter(value);
		this.rebuildIndex();
	}

	/**
	 * install a new filtering implementation on a column
	 */
	public void installFilter(final int col, final VLJTableFilter filter) {
		this.filters[col] = filter;
		this.rebuildIndex();
	}

	/**
	 * Returns the filtering implementation for a given column
	 */
	public VLJTableFilter getFilter(final int col) {
		return this.filters[col];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColumnCount() {
		return this.model.getColumnCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(final int col) {
		return this.model.getColumnName(col);
	}

	@Override
	public Class<?> getColumnClass(final int col) {
		/**
		 * {@inheritDoc}
		 */
		return this.model.getColumnClass(col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return this.filterIndex.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		return this.model.getValueAt(this.filterIndex[this.indexes[row]], col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable(final int row, final int col) {
		return this.model.isCellEditable(this.filterIndex[this.indexes[row]],
				col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValueAt(final Object o, final int row, final int col) {
		this.model.setValueAt(o, this.filterIndex[this.indexes[row]], col);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tableChanged(final TableModelEvent e) {
		final boolean sortOrFilter = this.isSortedOrFiltered();
		synchronized (this) {
			if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
				// this is how we recognize a structure change
				this.filters = new VLJTableFilter[this.model.getColumnCount()];
				this.sortModes = new int[this.model.getColumnCount()];
				this.rebuildIndex();
				if (this.table.isFilterHeaderVisible()) {
					this.table.installFilterHeader();
				} else {
					this.table.installHeader();
				}
			} else if (sortOrFilter) {
				this.rebuildIndex();
			} else if (e.getLastRow() > this.indexes.length) {
				this.indexes = new int[this.model.getRowCount()];
				this.filterIndex = new int[this.model.getRowCount()];
				for (int i = 0; i < this.indexes.length; i++) {
					this.indexes[i] = this.filterIndex[i] = i;
				}
				this.fireTableChanged(new TableModelEvent(this,
						e.getFirstRow(), e.getLastRow(), e.getColumn(), e
						.getType()));
			}
		}
	}

	// --------- contents below are borrowed from the java tutorial, and
	// reworked when possible
	/**
	 * Modify visible indexes based on filtering
	 */
	public void rebuildIndex() {
		boolean existFilter = false;
		for (int i = 0; i < this.model.getColumnCount(); i++) {
			if (this.filters[i] != null) {
				existFilter = true;
			}
		}

		if (existFilter) { // some filtering
			final int rowCount = this.model.getRowCount();
			final int colCount = this.model.getColumnCount();

			for (int j = 0; j < colCount; j++) {
				if (this.filters[j] != null) {
					this.filters[j].compile();
				}
			}

			final ArrayList<Integer> dataIndex = new ArrayList<>(
					this.model.getRowCount() / 2);
			for (int i = 0; i < rowCount; i++) {
				boolean reject = false;
				for (int j = 0; j < colCount && !reject; j++) {
					final VLJTableFilter filter = this.filters[j];
					if (filter != null) {
						if (!filter.accept(this.model.getValueAt(i, j))) {
							reject = true;
						}
					}
				}
				if (!reject) {
					// keep the row as all filters have passed the test
					dataIndex.add(new Integer(i));
				}
			}
			// dataIndex contains filtered lines
			this.filterIndex = new int[dataIndex.size()];
			for (int i = 0; i < dataIndex.size(); i++) {
				this.filterIndex[i] = dataIndex.get(i).intValue();
			}
		} else { // no filters
			this.filterIndex = new int[this.model.getRowCount()];
			for (int i = 0; i < this.filterIndex.length; i++) {
				this.filterIndex[i] = i;
			}
		}

		this.reallocateIndexes();

		// now we can sort
		this.sort();
		this.fireTableDataChanged();
	}

	protected boolean isSortedOrFiltered() {
		if (!this.table.isSortEnabled() && !this.table.isFilteringEnabled()) {
			return false;
		}
		boolean rep = false;
		for (int i = 0; i < this.sortModes.length && !rep; i++) {
			rep = this.sortModes[i] != FilterModel.SORT_NONE;
		}
		for (int i = 0; i < this.filters.length && !rep; i++) {
			rep = this.filters[i] != null;
		}
		return rep;
	}

	/**
	 * Return the sort mode for a given column. (between SORT_NONE,
	 * SORT_ASCENDING, SORT_DESCENDING)
	 */
	protected int getSortMode(final int col) {
		return this.sortModes[col];
	}

	/**
	 * Sets the sort mode for a given column. (between SORT_NONE,
	 * SORT_ASCENDING, SORT_DESCENDING)
	 */
	protected void setSortMode(final int col, final int mode) {
		this.sortModes[col] = mode;
		this.rebuildIndex();
	}

	private int compareRowsByColumn(final int row1, final int row2,
			final int column) {
		final TableModel data = this.model;
		final Class<?> type = this.getColumnClass(column);

		// Check for nulls.
		final Object o1 = data.getValueAt(row1, column);
		final Object o2 = data.getValueAt(row2, column);

		// If both values are null, return 0.
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			// Define null less than everything.
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		if (type.getSuperclass() == java.lang.Number.class) {
			final Number n1 = (Number) o1;
			final double d1 = n1.doubleValue();
			final Number n2 = (Number) o2;
			final double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		} else if (type == java.util.Date.class) {
			final Date d1 = (Date) o1;
			final long n1 = d1.getTime();
			final Date d2 = (Date) o2;
			final long n2 = d2.getTime();

			if (n1 < n2) {
				return -1;
			} else if (n1 > n2) {
				return 1;
			} else {
				return 0;
			}
		} else if (type == String.class) {
			final String s1 = (String) o1;
			final String s2 = (String) o2;
			final int result = s1.compareTo(s2);

			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		} else if (type == Boolean.class) {
			final Boolean bool1 = (Boolean) o1;
			final boolean b1 = bool1.booleanValue();
			final Boolean bool2 = (Boolean) o2;
			final boolean b2 = bool2.booleanValue();
			if (b1 == b2) {
				return 0;
			} else if (b1) {
				// Define false < true.
				return 1;
			} else {
				return -1;
			}
		} else {
			final String s1 = o1.toString();
			final String s2 = o2.toString();
			final int result = s1.compareTo(s2);
			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private int compare(final int row1, final int row2) {
		for (int i = 0; i < this.sortModes.length; i++) {
			final int sortMode = this.sortModes[i];
			if (sortMode != 0) {
				final int result = this.compareRowsByColumn(
						this.filterIndex[row1], this.filterIndex[row2], i);
				if (result != 0) {
					return sortMode == FilterModel.SORT_ASCENDING ? result
							: -result;
				}
			}
		}
		return 0;
	}

	/**
	 * Setup a new array of indexes with the right number of elements for the
	 * new data model.
	 */
	private void reallocateIndexes() {
		final int rowCount = this.filterIndex.length;
		this.indexes = new int[rowCount];

		// Initialise with the identity mapping.
		for (int row = 0; row < rowCount; row++) {
			this.indexes[row] = row;
		}
	}

	private void checkModel() {
		if (this.indexes.length != this.filterIndex.length) {
			throw new RuntimeException("Model changed");
		}
	}

	private void sort() {
		this.checkModel();
		this.shuttleSort(this.indexes.clone(), this.indexes, 0,
				this.indexes.length);
	}

	// private void n2sort() {
	// for (int i = 0; i < this.getRowCount(); i++) {
	// for (int j = i + 1; j < this.getRowCount(); j++) {
	// if (this.compare(this.indexes[i], this.indexes[j]) == -1) {
	// this.swap(i, j);
	// }
	// }
	// }
	// }

	private void shuttleSort(final int from[], final int to[], final int low,
			final int high) {
		if (high - low < 2) {
			return;
		}
		final int middle = (low + high) / 2;
		this.shuttleSort(to, from, low, middle);
		this.shuttleSort(to, from, middle, high);

		int p = low;
		int q = middle;

		if (high - low >= 4
				&& this.compare(from[middle - 1], from[middle]) <= 0) {
			for (int i = low; i < high; i++) {
				to[i] = from[i];
			}
			return;
		}

		// A normal merge.
		for (int i = low; i < high; i++) {
			if (q >= high
					|| (p < middle && this.compare(from[p], from[q]) <= 0)) {
				to[i] = from[p++];
			} else {
				to[i] = from[q++];
			}
		}
	}

	// private void swap(final int i, final int j) {
	// final int tmp = this.indexes[i];
	// this.indexes[i] = this.indexes[j];
	// this.indexes[j] = tmp;
	// }
}
