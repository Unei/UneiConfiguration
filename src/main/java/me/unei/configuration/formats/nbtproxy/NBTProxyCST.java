package me.unei.configuration.formats.nbtproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.unei.configuration.formats.nbtlib.NBTIO;
import me.unei.configuration.formats.nbtproxy.NBTProxyTag.LibType;
import me.unei.configuration.reflection.NBTCompressedStreamToolsReflection;

public class NBTProxyCST {

    public static void writeCompressed(NBTProxyCompound compound, OutputStream stream) throws IOException {
        switch(compound.unei_type) {
            case NMS:
                NBTCompressedStreamToolsReflection.write(compound.getNMSObject(), stream);
                break;
            case UNEI:
                NBTIO.writeCompressed(compound.getUNEIObject(), stream);
                break;
        }
    }

    public static NBTProxyCompound readCompressed(InputStream stream) throws IOException {
        NBTProxyCompound cmp = NBTProxyCST.readCompressed(stream, NBTProxyTag.getLibType());
        if (cmp == null) {
            return new NBTProxyCompound();
        }
        return cmp;
    }

    public static NBTProxyCompound readCompressed(InputStream stream, LibType type) throws IOException {
        switch(type) {
            case NMS:
                return new NBTProxyCompound(NBTCompressedStreamToolsReflection.read(stream), 0);
            case UNEI:
                return new NBTProxyCompound(NBTIO.readCompressed(stream));
        }
        return null;
    }
}