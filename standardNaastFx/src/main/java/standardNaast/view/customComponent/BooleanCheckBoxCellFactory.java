package standardNaast.view.customComponent;

import javafx.scene.control.TableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

public class BooleanCheckBoxCellFactory<T> implements Callback<Object, TableCell<T, Boolean>> {

	@Override
	public TableCell<T, Boolean> call(final Object param) {
		final CheckBoxTableCell<T, Boolean> checkBoxCell = new CheckBoxTableCell<>();
		return checkBoxCell;
	}

}
