/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package standardNaast.viewNew.members;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.Converter;

/*
 * Binding converter between String and regex RowFilter (encapsulated by RowSorterToStringConverter).
 */
public abstract class AbstractRowSorterToStringConverter extends Converter<TableRowSorter<TableModel>, String> {

	private JTable table;

	protected int column;

	protected String maskValue;


	public JTable getTable() {
		return this.table;
	}


	public void setTable(final JTable table) {
		this.table = table;
	}


	@Override
	public String convertForward(final TableRowSorter<TableModel> value) {
		return this.maskValue;
	}


	protected TableRowSorter<TableModel> convertReverse(final String mask, final int columnIndex) {
		this.maskValue = mask;
		System.out.println(this.table.getModel().getClass());
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.table.getModel());

		// The following statement makes the filter case-sensitive. If you want
		// filter to work in a case-insensitive way, uncomment the line below, comment
		// the 7 code lines below
		// sorter.setRowFilter(RowFilter.regexFilter(".\*" + mask + ".\*"));

		// The following 7 lines create a case-insensitive filter. If you want
		// the filter to be case-sensitive, comment them out and uncomment the
		// line above
		String m = StringUtils.isEmpty(mask) ? "" : mask;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < m.length(); i++) {
			char c = m.charAt(i);
			sb.append('[').append(Character.toLowerCase(c)).append(Character.toUpperCase(c)).append(']');
		}
		final String regexFilter = ".*" + sb + ".*";
		RowFilter<TableModel, Integer> rowFilter = RowFilter.regexFilter(regexFilter, columnIndex);
		sorter.setRowFilter(rowFilter);
		return sorter;
	}
}
