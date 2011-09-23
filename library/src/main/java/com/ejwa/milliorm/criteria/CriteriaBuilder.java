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
package com.ejwa.milliorm.criteria;

/**
 * The CriteriaBulder builds a Criteria object to be used when fetching rows from the database.
 * For example, to build a criteria object that holds true when the column "my_column" equals 5, the following can be used:
 * new CriteriaBuilder().where().eq("my_column", 5).build();
 */
@SuppressWarnings({"PMD.ShortMethodName", "PMD.TooManyMethods"})
public class CriteriaBuilder {
	private boolean isDistinct;
	private final StringBuilder whereString = new StringBuilder();

	public CriteriaBuilder distinct(boolean isDistinct) {
		this.isDistinct = isDistinct;
		return this;
	}

	public CriteriaBuilder where() {
		return this;
	}

	public CriteriaBuilder eq(Object a, Object b) {
		whereString.append(a).append(" = ").append(b);
		return this;
	}

	public CriteriaBuilder neq(Object a, Object b) {
		whereString.append(a).append(" != ").append(b);
		return this;
	}

	public CriteriaBuilder mt(Object a, Object b) {
		whereString.append(a).append(" > ").append(b);
		return this;
	}

	public CriteriaBuilder mte(Object a, Object b) {
		whereString.append(a).append(" >= ").append(b);
		return this;
	}

	public CriteriaBuilder lt(Object a, Object b) {
		whereString.append(a).append(" < ").append(b);
		return this;
	}

	public CriteriaBuilder lte(Object a, Object b) {
		whereString.append(a).append(" <= ").append(b);
		return this;
	}

	public CriteriaBuilder and() {
		whereString.append(" AND ");
		return this;
	}

	public CriteriaBuilder or() {
		whereString.append(" OR ");
		return this;
	}

	public Criteria build() {
		return new Criteria(isDistinct, whereString.toString());
	}
}
