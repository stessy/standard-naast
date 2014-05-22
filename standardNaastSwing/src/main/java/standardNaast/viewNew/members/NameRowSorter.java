/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package standardNaast.viewNew.members;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/*
 * Binding converter between String and regex RowFilter (encapsulated by FirstNameRowSorter).
 */
public class NameRowSorter extends AbstractRowSorterToStringConverter {

	@Override
	public TableRowSorter<TableModel> convertReverse(final String mask) {
		return this.convertReverse(mask, 2);
	}
}
