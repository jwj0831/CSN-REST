package cir.lab.transfer.common;

import cir.lab.csn.metadata.common.TagMetadata;

public class TagTransObj {
	private TagMetadata tag_meta;

	public TagMetadata getTag_meta() {
		return tag_meta;
	}

	public void setTag_meta(TagMetadata tag_meta) {
		this.tag_meta = tag_meta;
	}

	@Override
	public String toString() {
		return "TagTransObj [tag_meta=" + tag_meta + "]";
	}
}
