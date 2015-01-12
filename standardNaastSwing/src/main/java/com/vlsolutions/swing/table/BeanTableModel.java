/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vlsolutions.swing.table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import standardNaast.annotations.PersonneColumnOrdering;

/**
 * The BeanTableModel will use reflection to determine the columns of data to be
 * displayed in the table model. Reflection is used to find all the methods
 * declared in the specified bean. The criteria used for adding columns to the
 * model are:
 *
 * a) the method name must start with either "get" or "is" b) the parameter list
 * for the method must contain 0 parameters
 *
 * You can also specify an ancestor class in which case the declared methods of
 * the ancestor and all its descendents will be included in the model.
 *
 * A column name will be assigned to each column based on the method name.
 *
 * The cell will be considered editable when a corresponding "set" method name
 * is found.
 *
 * Reflection will also be used to implement the getValueAt() and setValueAt()
 * methods.
 */
@SuppressWarnings("serial")
public class BeanTableModel<T> extends RowTableModel<T> {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("com.standardNaast.bundle.Bundle"); //$NON-NLS-1$

	// Map "type" to "class". Class is needed for the getColumnClass() method.

	private static Map<Class<?>, Class<?>> primitives = new HashMap<>(10);

	static {
		BeanTableModel.primitives.put(Boolean.TYPE, Boolean.class);
		BeanTableModel.primitives.put(Byte.TYPE, Byte.class);
		BeanTableModel.primitives.put(Character.TYPE, Character.class);
		BeanTableModel.primitives.put(Double.TYPE, Double.class);
		BeanTableModel.primitives.put(Float.TYPE, Float.class);
		BeanTableModel.primitives.put(Integer.TYPE, Integer.class);
		BeanTableModel.primitives.put(Long.TYPE, Long.class);
		BeanTableModel.primitives.put(Short.TYPE, Short.class);
	}

	private Class<?> beanClass;

	private Class<?> ancestorClass;

	private final List<ColumnInformation> columns = new ArrayList<ColumnInformation>();

	/**
	 * Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 * @param beanClass
	 *            class of the beans that will be added to the model. The class
	 *            is also used to determine the columns that will be displayed
	 *            in the model
	 */
	public BeanTableModel(final Class<T> beanClass) {
		this(beanClass, beanClass, new ArrayList<T>());
	}

	/**
	 * Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 * @param beanClass
	 *            class of the beans that will be added to the model.
	 * @param ancestorClass
	 *            the methods of this class and its descendents down to the bean
	 *            class can be included in the model.
	 */
	public BeanTableModel(final Class<T> beanClass, final Class<?> ancestorClass) {
		this(beanClass, ancestorClass, new ArrayList<T>());
	}

	/**
	 * Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 * @param beanClass
	 *            class of the beans that will be added to the model.
	 * @param modelData
	 *            the data of the table
	 */
	public BeanTableModel(final Class<T> beanClass, final List<T> modelData) {
		this(beanClass, beanClass, modelData);
	}

	/**
	 * Constructs an empty <code>BeanTableModel</code> for the specified bean.
	 *
	 * @param beanClass
	 *            class of the beans that will be added to the model.
	 * @param ancestorClass
	 *            the methods of this class and its descendents down to the bean
	 *            class can be included in the model.
	 * @param modelData
	 *            the data of the table
	 */
	public BeanTableModel(final Class<T> beanClass,
			final Class<?> ancestorClass, final List<T> modelData) {
		super(beanClass);
		this.beanClass = beanClass;
		this.ancestorClass = ancestorClass;

		// Use reflection on the beanClass and ancestorClass to find properties
		// to add to the TableModel

		// this.createColumnInformation();

		this.createColumnsInformation();

		// Initialize the column name List to the proper size. The actual
		// column names will be reset in the resetModelDefaults() method.

		final List<String> columnNames = new ArrayList<String>();

		for (final ColumnInformation info : this.columns) {
			columnNames.add(info.getName());
		}

		// Reset all the values in the RowTableModel

		super.setDataAndColumnNames(modelData, columnNames);
		this.resetModelDefaults();
	}

	/*
	 * Use reflection to find all the methods that should be included in the
	 * model.
	 */
	private void createColumnInformation() {
		final Method[] theMethods = this.beanClass.getMethods();

		// Check each method to make sure it should be used in the model

		for (int i = 0; i < theMethods.length; i++) {
			final Method theMethod = theMethods[i];

			if (theMethod.getParameterTypes().length == 0
					&& this.ancestorClass.isAssignableFrom(theMethod
							.getDeclaringClass())) {
				final String methodName = theMethod.getName();

				if (theMethod.getName().startsWith("get")) {
					this.buildColumnInformation(theMethod,
							methodName.substring(3));
				}

				if (theMethod.getName().startsWith("is")) {
					this.buildColumnInformation(theMethod,
							methodName.substring(2));
				}
			}
		}
	}

	private List<Field> getDeclaredFieldsFromMethods() {
		final List<Field> declaredFields = new ArrayList<>();
		final Method[] theMethods = this.beanClass.getMethods();

		// Check each method to make sure it should be used in the model
		try {

			for (int i = 0; i < theMethods.length; i++) {
				final Method theMethod = theMethods[i];

				if (theMethod.getParameterTypes().length == 0
						&& this.ancestorClass.isAssignableFrom(theMethod
								.getDeclaringClass())) {
					final String methodName = theMethod.getName();

					if (theMethod.getName().startsWith("get")) {
						final Field declaredField = this.beanClass
								.getDeclaredField(this
										.uncapitalizeFirstLetter(methodName
												.substring(3)));
						declaredFields.add(declaredField);

					}

					if (theMethod.getName().startsWith("is")) {
						final Field declaredField = this.beanClass
								.getDeclaredField(this
										.uncapitalizeFirstLetter(methodName
												.substring(2)));
						declaredFields.add(declaredField);
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException e) {

		}
		return declaredFields;
	}

	private void createColumnsInformation() {
		final List<Field> fields = this.getDeclaredFieldsFromMethods();

		final Map<Integer, Field> annotatedFieldsOrdering = new TreeMap<>();
		final List<Field> unAnnotatedFieldsOrdering = new ArrayList<>();

		for (int i = 0; i < fields.size(); i++) {
			final Field field = fields.get(i);
			field.setAccessible(true);
			if (field.isAnnotationPresent(PersonneColumnOrdering.class)) {
				final PersonneColumnOrdering annotation = field
						.getAnnotation(PersonneColumnOrdering.class);
				final int order = annotation.order();
				annotatedFieldsOrdering.put(order, field);
			} else {
				unAnnotatedFieldsOrdering.add(field);
			}
		}

		final Set<Integer> keySet = annotatedFieldsOrdering.keySet();
		for (final Integer key : keySet) {
			this.createColumnInformation(annotatedFieldsOrdering.get(key));
		}

		for (final Field field : unAnnotatedFieldsOrdering) {
			this.createColumnInformation(field);
		}
	}

	private void createColumnInformation(final Field field) {

		final String capitalizedField = this.capitalizeFirstLetter(field
				.getName());
		Method method = null;
		try {
			method = this.beanClass.getMethod("get" + capitalizedField);
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				method = this.beanClass.getMethod("is" + capitalizedField);
			} catch (NoSuchMethodException | SecurityException e1) {

			}
		}

		if (method != null) {
			this.buildColumnInformation(method, capitalizedField);
		}

	}

	public String uncapitalizeFirstLetter(final String original) {
		if (original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toLowerCase() + original.substring(1);
	}

	public String capitalizeFirstLetter(final String original) {
		if (original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	/*
	 * We found a method candidate so gather the information needed to fully
	 * implemennt the table model.
	 */
	private void buildColumnInformation(final Method theMethod,
			final String theMethodName) {
		// Make sure the method returns an appropriate type

		final Class<?> returnType = this.getReturnType(theMethod);

		if (returnType == null) {
			return;
		}

		// Convert the method name to a display name for each column and
		// then check for a related "set" method.

		final String headerName = RowTableModel.formatColumnName(theMethodName);

		Method setMethod = null;

		try {
			final String setMethodName = "set" + theMethodName;
			setMethod = this.beanClass.getMethod(setMethodName,
					theMethod.getReturnType());
		} catch (final NoSuchMethodException e) {
		}

		// We have all the information we need, so save it for later use
		// by the table model methods.

		final ColumnInformation ci = new ColumnInformation(headerName,
				returnType, theMethod, setMethod);
		this.columns.add(ci);
	}

	/*
	 * Make sure the return type of the method is something we can use
	 */
	private Class<?> getReturnType(final Method theMethod) {
		Class<?> returnType = theMethod.getReturnType();

		if (returnType.isInterface() || returnType.isArray()) {
			return null;
		}

		// The primitive class type is different then the wrapper class of the
		// primitive. We need the wrapper class.

		if (returnType.isPrimitive()) {
			returnType = BeanTableModel.primitives.get(returnType);
		}

		return returnType;
	}

	/*
	 * Use information collected from the bean to set model default values.
	 */
	private void resetModelDefaults() {
		this.columnNames.clear();

		for (int i = 0; i < this.columns.size(); i++) {
			final ColumnInformation info = this.columns.get(i);
			this.columnNames.add(info.getName());
			super.setColumnClass(i, info.getReturnType());
			super.setColumnEditable(i, info.getSetter() == null ? false : true);
		}
	}

	/**
	 * Returns an attribute value for the cell at <code>row</code> and
	 * <code>column</code>.
	 *
	 * @param row
	 *            the row whose value is to be queried
	 * @param column
	 *            the column whose value is to be queried
	 * @return the value Object at the specified cell
	 * @exception IndexOutOfBoundsException
	 *                if an invalid row or column was given
	 */
	@Override
	public Object getValueAt(final int row, final int column) {
		final ColumnInformation ci = this.columns.get(column);

		Object value = null;

		try {
			final Method getter = ci.getGetter();
			value = getter.invoke(this.getRow(row));
			final Class<?> returnType = getter.getReturnType();
			if (returnType.isEnum()) {
				value = BeanTableModel.BUNDLE.getString(((Enum<?>) value)
						.name());
			}
		} catch (final IllegalAccessException e) {
		} catch (final InvocationTargetException e) {
		}

		return value;
	}

	/**
	 * Sets the object value for the cell at <code>column</code> and
	 * <code>row</code>. <code>value</code> is the new value. This method will
	 * generate a <code>tableChanged</code> notification.
	 *
	 * @param value
	 *            the new value; this can be null
	 * @param row
	 *            the row whose value is to be changed
	 * @param column
	 *            the column whose value is to be changed
	 * @exception IndexOutOfBoundsException
	 *                if an invalid row or column was given
	 */
	@Override
	public void setValueAt(final Object value, final int row, final int column) {
		final ColumnInformation ci = this.columns.get(column);
		Object newValue = value;
		try {
			final Method setMethod = ci.getSetter();

			if (setMethod != null) {
				if (newValue.getClass().isEnum()) {
					newValue = new String(
							BeanTableModel.BUNDLE
									.getString(((Enum<?>) newValue).name()));
				}
				setMethod.invoke(this.getRow(row), newValue);
				this.fireTableCellUpdated(row, column);
			}
		} catch (final IllegalAccessException e) {
		} catch (final InvocationTargetException e) {
		}
	}

	/**
	 * You are not allowed to change the class of any column.
	 */
	@Override
	public void setColumnClass(final int column, final Class<?> columnClass) {
	}

	/**
	 * Sets the editability for the specified column.
	 *
	 * Override to make sure you can't set a column editable that doesn't have a
	 * defined setter method.
	 *
	 * @param column
	 *            the column whose Class is being changed
	 * @param isEditable
	 *            indicates if the column is editable or not
	 * @exception ArrayIndexOutOfBoundsException
	 *                if an invalid column was given
	 */
	@Override
	public void setColumnEditable(final int column, final boolean isEditable) {
		final ColumnInformation ci = this.columns.get(column);

		if (isEditable && ci.getSetter() == null) {
			return;
		}

		super.setColumnEditable(column, isEditable);
	}

	/**
	 * Convenience method to change the generated column header name.
	 *
	 * This method must be invoked before the model is added to the table.
	 *
	 * @param column
	 *            the column whose value is to be queried
	 * @exception IndexOutOfBoundsException
	 *                if an invalid column was given
	 */
	public void setColumnName(final int column, final String name) {
		final ColumnInformation ci = this.columns.get(column);
		ci.setName(name);
		this.resetModelDefaults();
	}

	/*
	 * Columns are created in the order in which they are defined in the bean
	 * class. This method will sort the columns by colum header name.
	 * 
	 * This method must be invoked before the model is added to the table.
	 */
	public void sortColumnNames() {
		Collections.sort(this.columns);
		this.resetModelDefaults();
	}

	/*
	 * Class to hold data required to implement the TableModel interface
	 */
	private class ColumnInformation implements Comparable<ColumnInformation> {

		private String name;

		private final Class<?> returnType;

		private final Method getter;

		private final Method setter;

		public ColumnInformation(final String name, final Class<?> returnType,
				final Method getter, final Method setter) {
			this.name = name;
			this.returnType = returnType;
			this.getter = getter;
			this.setter = setter;
		}

		/*
		 * The column class of the model
		 */
		public Class<?> getReturnType() {
			return this.returnType;
		}

		/*
		 * Used by the getValueAt() method to get the data for the cell
		 */
		public Method getGetter() {
			return this.getter;
		}

		/*
		 * The value used as the column header name
		 */
		public String getName() {
			return this.name;
		}

		/*
		 * Used by the setValueAt() method to update the bean
		 */
		public Method getSetter() {
			return this.setter;
		}

		/*
		 * Use to change the column header name
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/*
		 * Implement the natural sort order for this class
		 */
		@Override
		public int compareTo(final ColumnInformation o) {
			return this.getName().compareTo(o.getName());
		}
	}
}