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
import com.ejwa.milliorm.annotation.Column;
import java.lang.reflect.Field;

final class DatabaseTypeHandler {
	private DatabaseTypeHandler() {
		/* This class is not intended to make instances of. */
	}

	public static String getSqLiteType(Field field) {
		final String fieldName = field.getType().getName();

		if (field.getType().isEnum() || "java.lang.String".equals(fieldName)) {
			return "TEXT";
		} else if ("boolean".equals(fieldName) || "int".equals(fieldName)) {
			return "INTEGER";
		} else if ("float".equals(fieldName) || "double".equals(fieldName)) {
			return "REAL";
		}

		throw new IllegalArgumentException(String.format("Type %s is not supported.", fieldName));
	}

	public static Object getObjectFromCursor(Field field, Cursor cursor, int column) {
		final String fieldName = field.getType().getName();

		if ("boolean".equals(fieldName)) {
			return cursor.getInt(column) == 0 ? false : true;
		} else if ("int".equals(fieldName)) {
			return cursor.getInt(column);
		} else if (field.getType().isEnum() || "java.lang.String".equals(fieldName)) {
			return cursor.getString(column);
		} else if ("float".equals(fieldName)) {
			return cursor.getFloat(column);
		} else if ("double".equals(fieldName)) {
			return cursor.getDouble(column);
		}

		throw new IllegalArgumentException(String.format("Type %s is not supported.", fieldName));
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public static void putContentValue(Field field, ContentValues contentValues, Object value) {
		final String fieldName = field.getType().getName();
		final String columnName = field.getAnnotation(Column.class).name();

		if (field.getType().isEnum() || "java.lang.String".equals(fieldName)) {
			contentValues.put(columnName, (String) value);
		} else if ("boolean".equals(fieldName)) {
			contentValues.put(columnName, ((Boolean) value) ? 1 : 0);
		} else if ("int".equals(fieldName)) {
			contentValues.put(columnName, (Integer) value);
		} else if ("float".equals(fieldName)) {
			contentValues.put(columnName, (Float) value);
		} else if ("double".equals(fieldName)) {
			contentValues.put(columnName, (Double) value);
		} else {
			throw new IllegalArgumentException(String.format("Type %s is not supported.", fieldName));
		}
	}
}
