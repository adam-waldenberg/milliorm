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
import android.util.Log;

/**
 * The base class describing a Database instance, used whenever a new database is to be opened.
 *
 * @author Adam Waldenberg <adam.waldenberg@ejwa.se>
 * @since 0.0.1
 */
public class Database {
	private final Context context;
	private final String databaseName;
	private final SQLiteDatabase sqLiteDatabase;
	private final DatabaseObjectManager databaseObjectManager;

	/**
	 * Creates a new database object with the given properties. This constructor requires the specification of a database
	 * version, there is also a constructor available that does not require you to set a version.
	 *
	 * @param context An android context.
	 * @param databaseName The name of the database. Usually set to something like "databaseName.db".
	 * @param version The version of the database. Used when converting between different database versions.
	 * @param tables A list of classes that will be used as the actual tables in the database.
	 *
	 * @since 0.0.1
	 */
	public Database(Context context, String databaseName, int version, Class<?> ...tables) {
		final DatabaseOpener databaseOpenHelper = new DatabaseOpener(context, databaseName, version, tables);

		this.context = context;
		this.databaseName = databaseName;
		sqLiteDatabase = databaseOpenHelper.getWritableDatabase();
		this.databaseObjectManager = new DatabaseObjectManager(sqLiteDatabase);
	}

	/**
	 * Creates a new database object with the given properties, not specifying a version. As a consequence, the database
	 * will not support upgrades.
	 *
	 * @since 0.0.1
	 */
	public Database(Context context, String databaseName, Class<?> ...tables) {
		this(context, databaseName, 1, tables);
	}

	/**
	 * @return An instance of the database object-manager associated with this database.
	 *
	 * @since 0.0.1
	 */
	public DatabaseObjectManager getDatabaseObjectManager() {
		return databaseObjectManager;
	}

	/**
	 * Closes the database.
	 *
	 * @since 0.0.1
	 */
	public void close() {
		sqLiteDatabase.close();
	}

	/**
	 * Deletes the database and removes all tables. If the database is open, it will be closed before being deleted.
	 *
	 * @since 0.0.1
	 */
	public void purge() {
		if (databaseObjectManager.hasPendingObjects()) {
			Log.w(Database.class.getName(), "Database has pending objects that have not been synchronized.");
		}
		if (sqLiteDatabase.isOpen()) {
			close();
		}
		context.deleteDatabase(databaseName);
	}
}
