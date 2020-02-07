package com.gmail.nossr50.util.nbt;

import com.gmail.nossr50.core.nbt.NBTByte;
import com.gmail.nossr50.core.nbt.NBTCompound;
import net.minecraft.server.v1_14_R1.NBTTagByte;

public class NBTFactory {
    //TODO: Finish
    /**
     * Converts NMS NBT types into our own NBT type representation
     * @param nmsNBT target NMS Compound
     * @return NMS Representation of our NBT
     */
//    public NBTCompound asNBT(net.minecraft.server.v1_14_R1.NBTTagCompound nmsNBT) {
//        NBTCompound nbtCompound = new NBTCompound("");
//
//        //Traverse the NMS Map
//        for(String key : nmsNBT.getKeys()) {
//
//        }
//    }

    //TODO: Finish
//    /**
//     * Convert our NBT type into the NMS NBT Type equivalent
//     * @param nbtCompound target nbt compound
//     * @return NMS NBT copy of our NBT type
//     */
//    public net.minecraft.server.v1_14_R1.NBTTagCompound asNMSCopy(NBTCompound nbtCompound) {
//
//    }

    /**
     * Create a new NMS NBT tag compound with only 1 tag compound named "tag"
     * @return new NMS NBT tag compound
     */
    private net.minecraft.server.v1_14_R1.NBTTagCompound makeNewNMSNBT() {
        net.minecraft.server.v1_14_R1.NBTTagCompound nbtTagCompound = new net.minecraft.server.v1_14_R1.NBTTagCompound();

        //Add the 'tag' compound where arbitrary data persists
        nbtTagCompound.set("tag", new net.minecraft.server.v1_14_R1.NBTTagCompound());
        return nbtTagCompound;
    }

    //TODO: Finish
//    private NBTCompound deepCopy(NBTCompound target, String key, net.minecraft.server.v1_14_R1.NBTBase nbtBase) {
//        switch (nbtBase.getTypeId()) {
//            case 0:
//                return new NBTCompound();
//        }
//    }

    /**
     * Create a NBTByte representation of NBTTagByte (NMS Type)
     * @param nmsNBTByte target NMS NBTTagByte
     * @return NBTByte representation of the targeted NMS nbt-type
     */
    private NBTByte asNBTByte(NBTTagByte nmsNBTByte) {
        NBTByte nbtByte = new NBTByte(nmsNBTByte.asByte());
        return nbtByte;
    }

    /**
     * Create a NBTTagByte (NMS Type) from our NBTByte representation
     * @param nbtByte target NBTByte
     * @return NBTTagByte copy of our NBTByte representation
     */
    private NBTTagByte asNBTTagByte(NBTByte nbtByte) {
        NBTTagByte nbtTagByte = new NBTTagByte(nbtByte.getValue());
        return nbtTagByte;
    }
}
