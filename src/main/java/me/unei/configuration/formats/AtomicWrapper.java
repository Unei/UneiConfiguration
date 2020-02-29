package me.unei.configuration.formats;

import java.util.concurrent.atomic.AtomicInteger;

final class AtomicWrapper<A> {
	public final AtomicInteger index;
	public A element;

	public AtomicWrapper(AtomicInteger idx, A element) {
		this.index = idx;
		this.element = element;
	}

	public AtomicWrapper(int index, A element) {
		this.index = new AtomicInteger(index);
		this.element = element;
	}

	public A invalidate() {
		this.index.set(-1);
		return this.element;
	}
}
