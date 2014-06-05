package cir.lab.transfer.network;


import cir.lab.csn.metadata.network.SeedMetadata;

public class SeedTransObj {
	private SeedMetadata seed;

	public SeedMetadata getSeed() {
		return seed;
	}

	public void setSeed(SeedMetadata seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "SensorNetworkMetaDataObj [seed=" + seed + "]";
	}
	
}
