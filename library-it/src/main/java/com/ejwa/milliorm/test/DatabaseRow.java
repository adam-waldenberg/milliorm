package com.ejwa.milliorm.test;

import com.ejwa.milliorm.annotation.Column;
import com.ejwa.milliorm.annotation.Key;
import com.ejwa.milliorm.annotation.Table;

@Table
public class DatabaseRow {
	public final static String SOME_KEY = "some_key";
	public final static String SOME_STRING = "some_string";
	public final static String SOME_BOOLEAN = "some_boolean";
	public final static String SOME_DOUBLE = "some_double";

	@Key
	@Column(name = SOME_KEY)
	private int someKey;

	@Column(name = SOME_STRING)
	private String someString;

	@Column(name = SOME_BOOLEAN)
	private boolean someBoolean;

	@Column(name = SOME_DOUBLE)
	private double someDouble;

	public boolean isSomeBoolean() {
		return someBoolean;
	}

	public void setSomeBoolean(boolean someBoolean) {
		this.someBoolean = someBoolean;
	}

	public double getSomeDouble() {
		return someDouble;
	}

	public void setSomeDouble(double someDouble) {
		this.someDouble = someDouble;
	}

	public int getSomeKey() {
		return someKey;
	}

	public void setSomeKey(int someKey) {
		this.someKey = someKey;
	}

	public String getSomeString() {
		return someString;
	}

	public void setSomeString(String someString) {
		this.someString = someString;
	}

	public void set(boolean someBoolean, double someDouble, int someKey, String someString) {
		this.someBoolean = someBoolean;
		this.someDouble = someDouble;
		this.someKey = someKey;
		this.someString = someString;
	}
}
