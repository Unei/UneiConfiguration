package me.unei.configuration.reflection.versioning;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public enum MinecraftVersion {
	Unknown(Integer.MAX_VALUE),
	MC1_14_R1(2100),
	MC1_13_R1(2000),
	MC1_12_R1(1900),
	MC1_11_R1(1800),
	MC1_10_R1(1700),
	MC1_9_R1(1600),
	MC1_8_R1(1500),
	MC1_7_R4(1430),
	MC1_7_R3(1420),
	MC1_7_R2(1410),
	MC1_7_R1(1400),
	MC1_6_R1(1300),
	MC1_5_R1(1200), // First version with versioned package name
	MC1_4(1100),
	MC1_3(1000),
	MC1_2(900),
	MC1_1(800),
	MC1_0(700),
	;
	
	private final int versionId;
	
	private MinecraftVersion(int id) {
		this.versionId = id;
	}
	
	public int getVersionId() {
		return this.versionId;
	}
	
	public static MinecraftVersion getActualVersion() {
		return Unknown;
	}
	
	public static MinecraftVersion selectMinimal(MinecraftVersion...versions) {
		MinecraftVersion current = getActualVersion();
		MinecraftVersion selected = null;
		for (MinecraftVersion ver : versions) {
			if (ver == current) {
				return ver;
			}
			if (ver.getVersionId() < current.getVersionId()) {
				if (selected == null || (ver.getVersionId() > selected.getVersionId())) {
					selected = ver;
				}
			}
		}
		return selected;
	}
	
	public <T> T ifThen(MinecraftVersion version, Supplier<T> runnable) {
		if (this == version) {
			return runnable.get();
		}
		return null;
	}
	
	public <T> T selectToExec(Map<MinecraftVersion, Supplier<T>> methods) {
		Supplier<T> r = methods.get(this);
		if (r != null) {
			return r.get();
		}
		return null;
	}
	
	public <T> Map<MinecraftVersion, Supplier<T>> mapForExec(Supplier<T> method) {
		Map<MinecraftVersion, Supplier<T>> res = new EnumMap<>(MinecraftVersion.class);
		res.put(this, method);
		return res;
	}
}
