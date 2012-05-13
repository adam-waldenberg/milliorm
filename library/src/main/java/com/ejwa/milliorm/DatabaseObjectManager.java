/*
 * Copyright © 2011 Ejwa Software. All rights reserved.
 * 
 * This file is part of milliorm. milliorm is a lightweight
 * object-relational-mapping library specifically developed for the
 * Android platform.
 *
 * milliorm is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * milliorm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with milliorm. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.ejwa.milliorm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.ejwa.milliorm.criteria.Criteria;
import com.ejwa.milliorm.annotation.Column;
import com.ejwa.milliorm.annotation.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseObjectManager {
	private final SQLiteDatabase sqLiteDatabase;
	private final Set<Object> newObjectsToCommit = new HashSet();
	//private final Set<Object> oldObjectsToCommit = new HashSet();

	protected DatabaseObjectManager(SQLiteDatabase sqLiteDatabase) {
		this.sqLiteDatabase = sqLiteDatabase;
	}

	protected boolean hasPendingObjects() {
		return !newObjectsToCommit.isEmpty();
	}

	private String makeCamelizedMethodString(Field field, String prefix) {
		final StringBuilder methodName = new StringBuilder();

		methodName.append(prefix);
		methodName.append(field.getName().substring(0, 1).toUpperCase());
		methodName.append(field.getName().substring(1));

		return methodName.toString();
	}

	private void callSetter(Field field, int column, Object object, Cursor cursor) throws
		SecurityException, NoSuchMethodException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException {

		final Object value = DatabaseTypeHandler.getObjectFromCursor(field, cursor, column);
		final String setterName = makeCamelizedMethodString(field, "set");
		final Method method = object.getClass().getMethod(setterName, new Class[] { field.getType() });

		method.invoke(object, new Object[] { value });
	}

	/**
	 * Create a new instance of an object and register it to the database. After changes have been made to the object,
	 * synchronize() can be called to sync the changes up to the database. The object is created by calling the default
	 * constructor.
	 *
	 * @param c The class type of the instance to return.
	 * @return A new instance of the above class type.
	 */
	public <T> T create(Class<T> c) {
		if (c.getAnnotation(Table.class) == null) {
			throw new IllegalArgumentException(String.format("%s has no @Table annotation.", c.getName()));
		}

		try {
			final T object = c.newInstance();
			newObjectsToCommit.add(object);
			return object;
		} catch (Exception ex) {
			Log.e(DatabaseObjectManager.class.getName(), String.format(ex.getMessage()));
		}

		throw new IllegalArgumentException(String.format("Failed to create instance of %s.", c.getName()));
	}

	private Cursor getCursor(String tableName, Criteria criteria) {
		return sqLiteDatabase.query(criteria.isDistinct(), tableName, null, criteria.getWhereString(),
		                            null, null, null, null, null);
	}

	/**
	 * Fetch a list of objects from the database. The returned objects are of type T.
	 *
	 * @param c The class type of the objects to be fetched from the database.
	 * @param criteria A criteria built with CriteriaBuilder describing the requirements of the returned objects.
	 * @return The objects fetched from the database.
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public <T> List<T> fetchList(Class<T> c, Criteria criteria) {
		final Cursor cursor = getCursor(c.getSimpleName().toUpperCase(), criteria);
		final List<T> objects = new ArrayList<T>();

		while (cursor.moveToNext()) {
			try {
				final T object = c.newInstance();
				int column = 0;

				for (Field f : c.getDeclaredFields()) {
					if (f.getAnnotation(Column.class) != null) {
						callSetter(f, column++, object, cursor);
					}
				}

				objects.add(object);
			} catch (Exception ex) {
				Log.e(DatabaseObjectManager.class.getName(), "Failed to fetch database object.");
				Log.e(DatabaseObjectManager.class.getName(), ex.getMessage());
				cursor.close();
			}
		}

		cursor.close();
		return objects;
	}

	/**
	 * Fetch a object from the database. The returned object is of type T.
	 *
	 * @param c The class type of the object to be fetched from the database.
	 * @param criteria A criteria built with CriteriaBuilder describing the requirements of the returned object.
	 * @return The object fetched from the database.
	 */
	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public <T> T fetch(Class<T> c, Criteria criteria) {
		final List<T> objects = fetchList(c, criteria);

		if (objects.size() > 1) {
			throw new IllegalArgumentException(String.format("Criteria (%s) resulted in fetch() returning " +
			                                                 "multiple values", criteria));
		}

		return objects.get(0);
	}

	private Object callGetter(Field field, Object object) throws 
		NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String prefix = "boolean".equals(field.getType().getName()) ? "is" : "get";
		final String getterName = makeCamelizedMethodString(field, prefix);
		final Method method = object.getClass().getMethod(getterName, (Class<?>[]) null);

		return method.invoke(object, (Object[]) null);
	}

	/**
	 * Synchronizes the current objects with the database. Any changes made to objects related to the database wil be
	 * commited to the database. A call to this method either means that an INSERT will be made for each object (if the
	 * object is a new one) or that an UPDATE call is made (if the object was previously fetched from the database).
	 * It is recommended to minimize the number of calls to this method.
	 */
	@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
	public void synchronize() {
		for (Object o : newObjectsToCommit) {
			final ContentValues cv = new ContentValues();

			for (Field f : o.getClass().getDeclaredFields()) {
				if (f.getAnnotation(Column.class) != null) {
					try {
						final Object valueToCommit = callGetter(f, o);
						DatabaseTypeHandler.putContentValue(f, cv, valueToCommit);
					} catch (Exception ex) {
						Log.e(DatabaseObjectManager.class.getName(), "Failed to synchronize database.");
						Log.e(DatabaseObjectManager.class.getName(), ex.getMessage());
						throw new IllegalArgumentException(ex);
					}

				}
			}

			final String tableName = o.getClass().getSimpleName().toUpperCase();
			sqLiteDatabase.insertOrThrow(tableName, null, cv);
		}
	}
}
