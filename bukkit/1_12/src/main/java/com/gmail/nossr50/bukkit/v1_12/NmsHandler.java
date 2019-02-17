package com.gmail.nossr50.bukkit.v1_12;

import com.gmail.nossr50.VersionedHandler;
import com.gmail.nossr50.mcMMO;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.IBlockState;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryBlocks;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NmsHandler extends VersionedHandler {
    /**
     * Static reference to the block registry. Don't really care about the fact that
     * it is being stored as a reference here, but for imports reasons, it's smaller
     * line length makes it optimal to read for method usages.
     */
    private static final RegistryBlocks<MinecraftKey, net.minecraft.server.v1_12_R1.Block> BLOCK_REGISTRY = net.minecraft.server.v1_12_R1.Block.REGISTRY;
    /**
     * We can keep track of block state id's by generating them on demand, i.e., when
     * they're being requested from Blocks, or BlockDatas.
     */
    private static final RegistryMaterials<MinecraftKey, IBlockData> BLOCK_STATE_REGISTRY = new RegistryMaterials<>();

    NmsHandler(mcMMO plugin) {
        super(plugin);
    }

    @Override
    public String getIdFor(Block block) {
        // We have to get the underlying nms block by the block, which might as well just be the type id.
        final Chunk chunk = ((CraftChunk) block.getChunk()).getHandle();
        // Get the block state from the underlying chunk (stored by reference in CraftBlock)
        final IBlockData blockState = chunk.a(block.getX(), block.getY(), block.getZ());
        // Sadly, Mojang doesn't store the string id onto blocks, states, or any of their types, they just call the registry.
        final MinecraftKey key = BLOCK_REGISTRY.b(blockState.getBlock());
        return key.toString();
    }

    @Override
    public String getIdFor(Entity entity) {
        // Every entity knows it's "type". Here, we just use the right method getter to get the EntityTypes instance for that entity
        final net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        // And then... well. we get the string id from the registry, based on the entity class to id mapping in EntityTypes.
        final MinecraftKey key = EntityTypes.a(nmsEntity);
        // If the key is null, well, we've got bigger problems...
        return key == null ? "minecraft:slime" : key.toString();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String getIdFor(BlockState block) {
        // Since we don't really want to trust what "BlockState" gives us, and the API doesn't give us anything but
        // what the API wants to give us, we have to dig into internals to get the true BlockState representation
        final CraftBlockState craftState = (CraftBlockState) block;
        // Because CraftBlock also stores the chunk reference, we can short cut in to use the chunk, instead of pinging
        // the chunk map on WorldServer
        final CraftChunk craftChunk = (CraftChunk) craftState.getChunk();
        // Then query for the block state from the chunk.
        final IBlockData nmsState = craftChunk.getHandle().a(block.getX(), block.getY(), block.getZ());
        // And then we're gucci
        final net.minecraft.server.v1_12_R1.Block nmsBlock = nmsState.getBlock();
        final MinecraftKey blockKey = BLOCK_REGISTRY.b(nmsBlock);
        // Now we can check if our blockstate registry actually has the block state.
        final MinecraftKey stateKey = BLOCK_STATE_REGISTRY.b(nmsState);
        if (stateKey != null) { // If the state has been registered by the handler, then by all means.
            return stateKey.toString();
        }
        // If not, well, it needs to have a key generated and registered.
        final String nameSpace = blockKey.getKey();
        StringBuilder builder = new StringBuilder();
        builder.append(nameSpace); // We only want to get the block id, not the domain id, since that's going to be appended later

        // Get the full Property -> Value mapping for the block state
        final ImmutableMap<IBlockState<?>, Comparable<?>> properties = nmsState.t();
        if (!properties.isEmpty()) {
            builder.append('[');
            Joiner joiner = Joiner.on(',');
            List<String> propertyValues = new ArrayList<>();
            // Yadadadada, go through all property entries and add each as a string for the "propertyName=value" (like "variant=oak")
            for (Map.Entry<IBlockState<?>, Comparable<?>> entry : properties.entrySet()) {
                // a() gets the inputted "name" for the state, the value, well, that gets the value, which is always toStringable.
                propertyValues.add(entry.getKey().a() + "=" + entry.getValue());
            }
            builder.append(joiner.join(propertyValues));
            builder.append(']');
        }
        // Now we can make the MinecraftKey...
        final MinecraftKey newKey = new MinecraftKey(builder.toString());
        BLOCK_STATE_REGISTRY.a(newKey, nmsState);
        return newKey.toString();
    }
}
