package com.gmail.nossr50.core.adapters.NMS_114;

import com.gmail.nossr50.core.adapters.NBTAdapter;
import com.gmail.nossr50.core.nbt.*;
import com.gmail.nossr50.core.nbt.NBTBase;
import com.gmail.nossr50.core.nbt.NBTList;
import com.gmail.nossr50.core.nbt.NBTCompound;
import net.minecraft.server.v1_14_R1.*;

public class BukkitNBTAdapter implements NBTAdapter {

    @Override
    public Object asNative(NBTBase nbtBase) {
        switch(nbtBase.getNBTType()) {
            case END:
                return new NBTTagEnd();
            case BYTE:
                return asNativeNBTByte((NBTByte) nbtBase);
            case SHORT:
                return asNativeNBTShort((NBTShort) nbtBase);
            case INT:
                return asNativeNBTInt((NBTInteger) nbtBase);
            case LONG:
                return asNativeNBTLong((NBTLong) nbtBase);
            case FLOAT:
                return asNativeNBTFloat((NBTFloat) nbtBase);
            case DOUBLE:
                return asNativeNBTDouble((NBTDouble) nbtBase);
            case BYTE_ARRAY:
                return asNativeNBTByteArray((NBTByteArray) nbtBase);
            case STRING:
                return asNativeNBTString((NBTString) nbtBase);
            case LIST:
                return asNativeNBTList((NBTList) nbtBase);
            case COMPOUND:
                return ;
            case INT_ARRAY:
                break;
            case LONG_ARRAY:
                break;
        }

        return null;
    }

    /**
     * Create a NBTTagByte (NMS Type) from our NBTByte representation
     * @param nbtByte target NBTByte
     * @return NBTTagByte copy of our NBTByte representation
     */
    private NBTTagByte asNativeNBTByte(NBTByte nbtByte) {
        return new NBTTagByte(nbtByte.getValue());
    }

    private NBTTagShort asNativeNBTShort(NBTShort nbtShort) {
        return new NBTTagShort(nbtShort.getValue());
    }

    private NBTTagInt asNativeNBTInt(NBTInteger nbtInteger) {
        return new NBTTagInt(nbtInteger.getValue());
    }

    private NBTTagLong asNativeNBTLong(NBTLong nbtLong) {
        return new NBTTagLong(nbtLong.getValue());
    }

    private NBTTagFloat asNativeNBTFloat(NBTFloat nbtFloat) {
        return new NBTTagFloat(nbtFloat.getValue());
    }

    private NBTTagDouble asNativeNBTDouble(NBTDouble nbtDouble) {
        return new NBTTagDouble(nbtDouble.getValue());
    }

    private NBTTagByteArray asNativeNBTByteArray(NBTByteArray nbtByteArray) {
        return new NBTTagByteArray(nbtByteArray.getValues());
    }

    private NBTTagString asNativeNBTString(NBTString nbtString) {
        return new NBTTagString(nbtString.getValue());
    }

    private NBTTagList asNativeNBTList(NBTList nbtList) {
        NBTTagList nbtTagList = new NBTTagList();
        nbtList.setValues(nbtList.getValues());
        return nbtTagList;
    }

    private NBTTagCompound asNativeNBTCompound(NBTCompound nbtCompound) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtCompound
    }
}
