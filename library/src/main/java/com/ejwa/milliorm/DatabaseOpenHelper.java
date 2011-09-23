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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ejwa.milliorm.annotation.Column;
import com.ejwa.milliorm.annotation.Key;
import java.lang.reflect.Field;

class DatabaseOpenHelper extends SQLiteOpenHelper {
	private final Class<?>[] tables;

	protected DatabaseOpenHelper(Context context, String databaseName, int version, Class<?> ...tables) {
		super(context, databaseName, null, version);
		this.tables = tables;
	}

	private String getColumnString(Field field, boolean isKey) {
		final String sqLiteType = DatabaseTypeHandler.getSqLiteType(field);
		final String columnName = field.getAnnotation(Column.class).name();

		return String.format("%s %s%s", columnName, sqLiteType, isKey ? " PRIMARY KEY" : "");
	}

	@Override
	@SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.AvoidInstantiatingObjectsInLoops"})
	public void onCreate(SQLiteDatabase database) {
		for (Class<?> t : tables) {
			final StringBuilder tableMembers = new StringBuilder();
			boolean hasKey = false;

			for (Field f : t.getDeclaredFields()) {
				boolean foundKey = false;

				if (f.getAnnotation(Key.class) != null) {
					if (hasKey) {
						throw new IllegalArgumentException(String.format("%s can't have multiple key " +
						                                                 "definitions.", t.getName()));
					}

					hasKey = true;
					foundKey = true;
				}
				if (f.getAnnotation(Column.class) != null) {
					tableMembers.append(getColumnString(f, foundKey));

					if (!t.getDeclaredFields()[t.getDeclaredFields().length - 1].equals(f)) {
						tableMembers.append(", ");
					}
				}
			}

			final String create = "CREATE TABLE " + t.getSimpleName().toUpperCase() + "(" + tableMembers + ")";
			Log.i(DatabaseOpenHelper.class.getName() , create);
			database.execSQL(create);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		throw new UnsupportedOperationException("Not supported yet.");
	}	
}
