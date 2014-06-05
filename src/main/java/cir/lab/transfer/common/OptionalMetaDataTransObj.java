package cir.lab.transfer.common;

import cir.lab.csn.metadata.common.OptionalMetadata;

public class OptionalMetaDataTransObj {
	private OptionalMetadata opt_meta;

	public OptionalMetadata getOpt_meta() {
		return opt_meta;
	}

	public void setOpt_meta(OptionalMetadata opt_meta) {
		this.opt_meta = opt_meta;
	}

	@Override
	public String toString() {
		return "OptionalMetaDataTransObj [opt_meta=" + opt_meta + "]";
	}
}
