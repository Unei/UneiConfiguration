package me.unei.configuration.reflection.versioning;

import java.lang.reflect.InvocationTargetException;

public enum NMSNBTVersioning
{
	M_NBTBASE_GETTYPEID(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("a")),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("getTypeId"))
	}),
	@Deprecated
	M_NBTBASE_SETNAME(MinecraftVersion.MC1_0, MinecraftVersion.MC1_7_R1, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("a", String.class)),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("setName", String.class))
	}),
	@Deprecated
	M_NBTBASE_GETNAME(MinecraftVersion.MC1_0, MinecraftVersion.MC1_7_R1, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("c")),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("getName"))
	}),
	M_NBTBASE_CLONE(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("b")),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("clone"))
	}),
	M_NBTBASE_CREATETAG(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("a", byte.class, String.class)),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("createTag", byte.class, String.class)),
			new Since(MinecraftVersion.MC1_7_R1, new MethodMapping("createTag", byte.class))
	}),
	M_NBTBASE_GETNAMEBYID(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new MethodMapping("a", byte.class)),
			new Since(MinecraftVersion.MC1_1, new MethodMapping("getTagName", byte.class)),
			new Since(MinecraftVersion.MC1_7_R1, new MethodMapping("getTagName", int.class)),
			new Since(MinecraftVersion.MC1_8_R1),
			new Since(MinecraftVersion.MC1_12_R1, new MethodMapping("j", int.class)),
			new Since(MinecraftVersion.MC1_13_R1, new MethodMapping("n", int.class)),
			new Since(MinecraftVersion.MC1_14_R1, new MethodMapping("l", int.class))
	}),
	M_NBTBASE_ASSTRING(MinecraftVersion.MC1_7_R1, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_7_R1, new MethodMapping("a_")),
			new Since(MinecraftVersion.MC1_10_R1, new MethodMapping("c_")),
			new Since(MinecraftVersion.MC1_13_R1, new MethodMapping("asString")),
	}),
	M_NBTBASE_ISEMPTY(MinecraftVersion.MC1_8_R1, MinecraftVersion.MC1_13_R1, new Since[] {
			new Since(MinecraftVersion.MC1_8_R1, new MethodMapping("isEmpty")),
	}),
	M_NBTBYTEARRAY_GETDATA(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new FieldMapping("a")),
			new Since(MinecraftVersion.MC1_1, new FieldMapping("data")),
			new Since(MinecraftVersion.MC1_7_R1, new MethodMapping("c")),
			new Since(MinecraftVersion.MC1_13_R1, new MethodMapping("getBytes")),
	}),
	M_NBTINTARRAY_GETDATA(MinecraftVersion.MC1_2, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_2, new FieldMapping("data")),
			new Since(MinecraftVersion.MC1_7_R1, new MethodMapping("c")),
			new Since(MinecraftVersion.MC1_13_R1, new MethodMapping("getInts")),
	}),
	@Deprecated
	F_NBTBASE_NAME(MinecraftVersion.MC1_0, MinecraftVersion.MC1_7_R1, new Since[] {
			new Since(MinecraftVersion.MC1_0, new FieldMapping("a")),
			new Since(MinecraftVersion.MC1_1, new FieldMapping("name"))
	}),
	F_NBTBASE_TYPENAMES(MinecraftVersion.MC1_4, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_4, new FieldMapping("b")),
			new Since(MinecraftVersion.MC1_7_R1, new FieldMapping("a"))
	}),
	F_ANYTAG_DATA(MinecraftVersion.MC1_0, MinecraftVersion.Unknown, new Since[] {
			new Since(MinecraftVersion.MC1_0, new FieldMapping("a")),
			new Since(MinecraftVersion.MC1_1, new FieldMapping("data")),
	}),
	;
	
	private Since implVersion;
	
	private NMSNBTVersioning(final MinecraftVersion firstImpl, final MinecraftVersion lastImpl, final Since[] methods) {
		
		MinecraftVersion current = MinecraftVersion.getActualVersion();
		if (current.compareTo(firstImpl) < 0) { // Method not yet implemented
			return;
		}
		if (current.compareTo(lastImpl) >= 0) { // Method has been removed
			return;
		}
		
		Since target = methods[0];
		for (final Since s : methods) {
			if (s.version.getVersionId() <= current.getVersionId()
					&& target.version.getVersionId() < s.version.getVersionId()) {
				target = s;
			}
		}
		
		implVersion = target;
		
	}
	
	public Object callOrGet(Class<?> clz, Object instance, Object...params) {
		if (implVersion == null) return null;
		return implVersion.callOrGet(clz, instance, params);
	}
	
	public static class Since {
		private final MinecraftVersion version;
		private final MethodMapping method;
		private final FieldMapping field;
		
		public Since(MinecraftVersion version, MethodMapping meth) {
			this.version = version;
			this.method = meth;
			this.field = null;
		}

		public Since(MinecraftVersion version, FieldMapping field) {
			this.version = version;
			this.method = null;
			this.field = field;
		}

		public Since(MinecraftVersion version) {
			this.version = version;
			this.method = null;
			this.field = null;
		}
		
		public MinecraftVersion getMCVersion() {
			return this.version;
		}

		public Object callOrGet(Class<?> clz, Object instance, Object...params) {
			try {
				if (this.field != null) {
					return this.field.getFieldOfClass(clz).get(instance);
				} else if (this.method != null) {
					return this.method.getMethodOfClass(clz).invoke(instance, params);
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}