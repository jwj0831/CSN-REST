package cir.lab.transfer.sensor;

import cir.lab.csn.metadata.sensor.SeedMetadata;

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
		return "SensorSeedTransObj [seed=" + seed + "]";
	}
}
