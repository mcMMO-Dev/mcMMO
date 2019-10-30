package com.gmail.nossr50.core.adapters.NMS_114;

import com.gmail.nossr50.core.adapters.NBTAdapter;
import com.gmail.nossr50.core.nbt.NBTBase;
import com.gmail.nossr50.core.nbt.NBTList;
import com.gmail.nossr50.core.nbt.*;
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
                return asNativeNBTInt((NBTInt) nbtBase);
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
                return asNativeNBTIntArray((NBTIntArray) nbtBase);
            case LONG_ARRAY:
                return asNativeNBTLongArray((NBTLongArray) nbtBase);
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

    /**
     * Create a NBTTagShort (NMS Type) from our NBTShort representation
     * @param nbtShort target NBTShort
     * @return NBTTagShort copy of our NBTShort representation
     */
    private NBTTagShort asNativeNBTShort(NBTShort nbtShort) {
        return new NBTTagShort(nbtShort.getValue());
    }

    /**
     * Create a NBTTagInt (NMS Type) from our NBTInt representation
     * @param nbtInt target NBTInt
     * @return NBTTagInt copy of our NBTInt representation
     */
    private NBTTagInt asNativeNBTInt(NBTInt nbtInt) {
        return new NBTTagInt(nbtInt.getValue());
    }

    /**
     * Create a NBTTagLong (NMS Type) from our NBTLong representation
     * @param nbtLong target NBTLong
     * @return NBTTagLong copy of our NBTLong representation
     */
    private NBTTagLong asNativeNBTLong(NBTLong nbtLong) {
        return new NBTTagLong(nbtLong.getValue());
    }

    /**
     * Create a NBTTagFloat (NMS Type) from our NBTFloat representation
     * @param nbtFloat target NBTFloat
     * @return NBTTagFloat copy of our NBTFloat representation
     */
    private NBTTagFloat asNativeNBTFloat(NBTFloat nbtFloat) {
        return new NBTTagFloat(nbtFloat.getValue());
    }

    /**
     * Create a NBTTagDouble (NMS Type) from our NBTDouble representation
     * @param nbtDouble target NBTDouble
     * @return NBTTagDouble copy of our NBTDouble representation
     */
    private NBTTagDouble asNativeNBTDouble(NBTDouble nbtDouble) {
        return new NBTTagDouble(nbtDouble.getValue());
    }

    /**
     * Create a NBTTagByteArray (NMS Type) from our NBTByteArray representation
     * @param nbtByteArray target NBTByteArray
     * @return NBTTagByteArray copy of our NBTByteArray representation
     */
    private NBTTagByteArray asNativeNBTByteArray(NBTByteArray nbtByteArray) {
        return new NBTTagByteArray(nbtByteArray.getValues());
    }

    /**
     * Create a NBTTagString (NMS Type) from our NBTString representation
     * @param nbtString target NBTString
     * @return NBTTagString copy of our NBTString representation
     */
    private NBTTagString asNativeNBTString(NBTString nbtString) {
        return new NBTTagString(nbtString.getValue());
    }

    /**
     * Create a NBTTagList (NMS Type) from our NBTList representation
     * @param nbtList target NBTList
     * @return NBTTagList copy of our NBTList representation
     */
    private NBTTagList asNativeNBTList(NBTList nbtList) {
        NBTTagList nbtTagList = new NBTTagList();
        nbtList.setValues(nbtList.getValues());
        return nbtTagList;
    }

    /**
     * Create a NBTTagCompound (NMS Type) from our NBTCompound representation
     * @param nbtCompound target NBTCompound
     * @return NBTTagCompound copy of our NBTCompound representation
     */
    private NBTTagCompound asNativeNBTCompound(NBTCompound nbtCompound) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        nbtCompound
    }

    /**
     * Create a NBTTagIntArray (NMS Type) from our NBTIntArray representation
     * @param nbtIntArray target NBTIntArray
     * @return NBTTagIntArray copy of our NBTIntArray representation
     */
    private NBTTagIntArray asNativeNBTIntArray(NBTIntArray nbtIntArray) {
        return new NBTTagIntArray(nbtIntArray.getValues());
    }

    /**
     * Create a NBTTagLongArray (NMS Type) from our NBTLongArray representation
     * @param nbtLongArray target NBTLongArray
     * @return NBTTagLongArray copy of our NBTLongArray representation
     */
    private NBTTagLongArray asNativeNBTLongArray(NBTLongArray nbtLongArray) {
        return new NBTTagLongArray(nbtLongArray.getValues());
    }
}
