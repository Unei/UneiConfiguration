package me.unei.configuration.reflection.nbtmirror;

import java.util.function.Predicate;

import me.unei.configuration.reflection.NMSReflection;

public abstract class MirrorTagArray extends MirrorTag {

	private static Class<?> originalNBTListClass = NMSReflection.getNMSClass("NBTList", false);
	
	protected MirrorTagArray(Object tag, Predicate<Object> checker) {
		super(tag, checker);
	}

}
