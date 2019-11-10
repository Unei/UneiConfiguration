package me.unei.configuration.reflection.nbtmirror;

import java.util.function.Predicate;

public abstract class MirrorTagNumber extends MirrorTag {

	protected MirrorTagNumber(Object tag, Predicate<Object> checker) {
		super(tag, checker);
	}
	
	public long asLong() {
		return asJLNumber().longValue();
	}

    public int asInt() {
    	return asJLNumber().intValue();
    }

    public short asShort() {
    	return asJLNumber().shortValue();
    }

    public byte asByte() {
    	return asJLNumber().byteValue();
    }

    public double asDouble() {
    	return asJLNumber().doubleValue();
    }

    public float asFloat() {
    	return asJLNumber().floatValue();
    }
	
	public abstract Number asJLNumber();
}
