package com.ejwa.milliorm.test;

import android.test.suitebuilder.annotation.MediumTest;
import com.ejwa.milliorm.criteria.Criteria;
import com.ejwa.milliorm.criteria.CriteriaBuilder;

public class FetchSingleObject extends DatabaseTestBase {
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
}
