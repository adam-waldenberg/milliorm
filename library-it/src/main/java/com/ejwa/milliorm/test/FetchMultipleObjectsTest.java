package com.ejwa.milliorm.test;

import android.test.suitebuilder.annotation.MediumTest;
import com.ejwa.milliorm.criteria.Criteria;
import com.ejwa.milliorm.criteria.CriteriaBuilder;
import java.util.List;

public class FetchMultipleObjectsTest extends DatabaseTestBase {
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
