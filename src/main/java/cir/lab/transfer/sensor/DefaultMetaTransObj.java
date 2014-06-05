package cir.lab.transfer.sensor;

import cir.lab.csn.metadata.sensor.DefaultMetadata;

public class DefaultMetaTransObj {
	private DefaultMetadata def_meta;

	public DefaultMetadata getDef_meta() {
		return def_meta;
	}

	public void setDef_meta(DefaultMetadata def_meta) {
		this.def_meta = def_meta;
	}

	@Override
	public String toString() {
		return "DefaultMetaTransObj [def_meta=" + def_meta + "]";
	}
}
