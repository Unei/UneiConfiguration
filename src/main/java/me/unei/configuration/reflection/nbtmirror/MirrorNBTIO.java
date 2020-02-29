package me.unei.configuration.reflection.nbtmirror;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.unei.configuration.reflection.NBTCompressedStreamToolsReflection;

public final class MirrorNBTIO {

	public static MirrorTagCompound readCompressed(InputStream input) throws IOException {
		return new MirrorTagCompound(NBTCompressedStreamToolsReflection.read(input));
	}

	public static void writeCompressed(MirrorTagCompound compound, OutputStream stream) throws IOException {
		NBTCompressedStreamToolsReflection.write(compound.getNMS(), stream);
	}
}
