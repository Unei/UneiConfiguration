package me.unei.configuration.reflection.nbtmirror;

import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.api.format.TagType.ATagCreator;

public class TagCreatorImpl extends ATagCreator {

	static {
		new TagCreatorImpl();
	}

	public static void init() {
		/* Call static {} */ }

	private TagCreatorImpl() {
		setInstance();
	}

	@Override
	protected INBTTag internal_createTag(TagType type) {
		return MirrorTag.createTag(type.getId());
	}
}
