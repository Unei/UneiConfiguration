package me.unei.configuration.reflection.versioning;

import java.lang.reflect.Field;

public class FieldMapping {

	private final String fieldName;

	public FieldMapping(String fieldName) {
		this.fieldName = fieldName;
	}

	public Field getFieldOfClass(Class<?> clazz) {
		Field m = null;

		try {
			m = clazz.getField(this.fieldName);
		} catch (NoSuchFieldException e) {
			/* IGNORED */ }

		try {
			m = clazz.getDeclaredField(this.fieldName);
		} catch (NoSuchFieldException e) {
			/* IGNORED */ }
		return m;
	}

	public Field getField(Object instance) {
		Field m = null;

		try {
			m = instance.getClass().getField(this.fieldName);
		} catch (NoSuchFieldException e) {
			/* IGNORED */ }

		try {
			m = instance.getClass().getDeclaredField(this.fieldName);
		} catch (NoSuchFieldException e) {
			/* IGNORED */ }
		return m;
	}
}
