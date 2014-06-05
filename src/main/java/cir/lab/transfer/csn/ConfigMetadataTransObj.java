package cir.lab.transfer.csn;

import cir.lab.csn.metadata.csn.CSNConfigMetadata;

public class ConfigMetadataTransObj {
	private CSNConfigMetadata config;

	public CSNConfigMetadata getConfig() {
		return config;
	}

	public void setConfig(CSNConfigMetadata config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return "ConfigMetadataTransObj [config=" + config + "]";
	}
}
