/*
 * Copyright © 2011 Ejwa Software. All rights reserved.
 * 
 * This file is part of milliorm. milliorm is a lightweight
 * object-relational-mapping library specifically developed for the
 * Android platform.
 *
 * milliorm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * milliorm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with milliorm.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ejwa.milliorm.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import com.ejwa.milliorm.Database;
import com.ejwa.milliorm.criteria.Criteria;
import com.ejwa.milliorm.criteria.CriteriaBuilder;
import java.util.List;

public class DatabaseTest extends AndroidTestCase {
	private Database database;

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected void setUp() throws Exception {
		super.setUp();
		database = new Database(getContext(), "test.db", DatabaseRow.class);
	}

	@Override
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	protected void tearDown() throws Exception {
		database.purge();
		super.tearDown();
	}

	@MediumTest
	public void testCreateObject() {
		database.getDatabaseObjectManager().create(DatabaseRow.class);

		try {
			database.getDatabaseObjectManager().create(DatabaseTest.class);
			fail("Getting a class without the @Table annotation should throw an exception.");
		} catch(Exception ex) {
			Log.e(DatabaseTest.class.getName(), ex.getMessage());
		}
	}

	@MediumTest
	public void testSyncAndFetchSingleObject() {
		final DatabaseRow firstRow = database.getDatabaseObjectManager().create(DatabaseRow.class);
		firstRow.set(true, 5.0, 10, "Testing");

		database.getDatabaseObjectManager().synchronize();

		final Criteria criteria = new CriteriaBuilder().where().eq(DatabaseRow.SOME_DOUBLE, 5.0).build();
		final DatabaseRow secondRow = database.getDatabaseObjectManager().fetch(DatabaseRow.class, criteria);

		assertEquals("Database boolean mismatch.", firstRow.isSomeBoolean(), secondRow.isSomeBoolean());
		assertEquals("Database double mismatch.", firstRow.getSomeDouble(), secondRow.getSomeDouble());
		assertEquals("Database integer mismatch.", firstRow.getSomeKey(), secondRow.getSomeKey());
		assertEquals("Database string mismatch.", firstRow.getSomeString(), secondRow.getSomeString());
	}

	@MediumTest
	public void testSyncAndFetchMultipleObjects() {
		final DatabaseRow firstRow = database.getDatabaseObjectManager().create(DatabaseRow.class);
		final DatabaseRow secondRow = database.getDatabaseObjectManager().create(DatabaseRow.class);
		final DatabaseRow thirdRow = database.getDatabaseObjectManager().create(DatabaseRow.class);

		firstRow.set(true, 5.0, 10, "Testing");
		secondRow.set(true, 10.0, 20, "Double-up");
		thirdRow.set(true, 2.5, 5, "Halfsies");

		database.getDatabaseObjectManager().synchronize();

		final Criteria criteria = new CriteriaBuilder().where().mt(DatabaseRow.SOME_DOUBLE, 3.0).build();
		final List<DatabaseRow> rows = database.getDatabaseObjectManager().fetchList(DatabaseRow.class, criteria);

		assertEquals(String.format("Expected 2 results from fetchList() but got %d.", rows.size()), 2, rows.size());
	}
}
