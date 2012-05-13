package com.ejwa.milliorm.test;

import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class CreateObjectTest extends DatabaseTestBase {
	@MediumTest
	public void testCreateObject() {
		database.getDatabaseObjectManager().create(DatabaseRow.class);

		try {
			database.getDatabaseObjectManager().create(DatabaseTestBase.class);
			fail("Getting a class without the @Table annotation should throw an exception.");
		} catch(Exception ex) {
			Log.e(DatabaseTestBase.class.getName(), ex.getMessage());
		}
	}
}
