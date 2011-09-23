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

public class Criteria {
	private boolean distinct;
	private String whereString;

	public Criteria(boolean distinct, String whereString) {
		this.distinct = distinct;
		this.whereString = whereString;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public String getWhereString() {
		return whereString;
	}

	public void setWhereString(String whereString) {
		this.whereString = whereString;
	}
}
