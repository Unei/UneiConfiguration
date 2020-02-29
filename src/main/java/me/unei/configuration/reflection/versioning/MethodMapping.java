package me.unei.configuration.reflection.versioning;

import java.lang.reflect.Method;

public class MethodMapping {

	private final String methodName;
	private final Class<?>[] parameters;

	public MethodMapping(String methName, Class<?>... paramsType) {
		this.methodName = methName;
		this.parameters = paramsType;
	}

	public Method getMethodOfClass(Class<?> clazz) {
		Method m = null;

		try {
			m = clazz.getMethod(this.methodName, this.parameters);
		} catch (NoSuchMethodException e) {
			/* IGNORED */ }

		try {
			m = clazz.getDeclaredMethod(this.methodName, this.parameters);
		} catch (NoSuchMethodException e) {
			/* IGNORED */ }
		return m;
	}

	public Method getMethod(Object instance) {
		Method m = null;

		try {
			m = instance.getClass().getMethod(this.methodName, this.parameters);
		} catch (NoSuchMethodException e) {
			/* IGNORED */ }

		try {
			m = instance.getClass().getDeclaredMethod(this.methodName, this.parameters);
		} catch (NoSuchMethodException e) {
			/* IGNORED */ }
		return m;
	}
}
