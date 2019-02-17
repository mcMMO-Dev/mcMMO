package com.gmail.nossr50.bukkit.v1_8;

import com.gmail.nossr50.VersionedHandler;
import com.gmail.nossr50.mcMMO;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.IBlockState;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.RegistryBlocks;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "Duplicates"}) // We use reflection to load this handler on 1.8 versions
public class NmsHandler extends VersionedHandler {

    /**
     * Static reference to the block registry. Don't really care about the fact that
     * it is being stored as a reference here, but for imports reasons, it's smaller
     * line length makes it optimal to read for method usages.
     */
    private static final RegistryBlocks<MinecraftKey, net.minecraft.server.v1_8_R3.Block> BLOCK_REGISTRY = net.minecraft.server.v1_8_R3.Block.REGISTRY;
    /**
     * We can keep track of block state id's by generating them on demand, i.e., when
     * they're being requested from Blocks, or BlockDatas.
     */
    private static final RegistryBlocks<MinecraftKey, IBlockData> BLOCK_STATE_REGISTRY = new RegistryBlocks<>(BLOCK_REGISTRY.c(Blocks.AIR)); // Default to AIR

    NmsHandler(mcMMO plugin) {
        super(plugin);
    }

    @Override
    public String getIdFor(Block block) {
        // We have to get the underlying nms block by the block, which might as well just be the type id.
        final net.minecraft.server.v1_8_R3.Block block1 = CraftMagicNumbers.getBlock(block);
        // The registry.c(T) will return MinecraftKey, and the toString() returns the "$mod:$name", so "minecraft:stone"
        final MinecraftKey key = BLOCK_REGISTRY.c(block1);
        return key.toString();
    }

    @Override
    public String getIdFor(Entity entity) {
        // EntityTypes maintains the maps of Class<? extends nms.Entity> to String for id's and to numerical id numbers.
        // We of course are using the method that gets us the String id of the entity, such as "minecraft:creeper"
        return EntityTypes.b(((CraftEntity) entity).getHandle());
    }

    @Override
    public String getIdFor(BlockState block) {
        // Much like #getIdFor(Block) except here we have the "state" by numerical id. So.. we have to grab it from
        // the world.
        final net.minecraft.server.v1_8_R3.Block nmsBlock = net.minecraft.server.v1_8_R3.Block.REGISTRY.a(block.getTypeId());
        final MinecraftKey blockKey = net.minecraft.server.v1_8_R3.Block.REGISTRY.c(nmsBlock);
        final IBlockData blockState = nmsBlock.fromLegacyData(block.getRawData());
        // Now we can check if our blockstate registry actually has the block state.
        final MinecraftKey stateKey = BLOCK_STATE_REGISTRY.c(blockState);
        if (stateKey != null) { // If the state has been registered by the handler, then by all means.
            return stateKey.toString();
        }
        // If not, well, it needs to have a key generated and registered.
        final String nameSpace = blockKey.a();
        StringBuilder builder = new StringBuilder();
        builder.append(blockKey.a()); // We only want to get the block id, not the domain id, since that's going to be appended later

        // Get the full Property -> Value mapping for the block state
        final ImmutableMap<IBlockState, Comparable> properties = blockState.b();
        if (!properties.isEmpty()) {
            builder.append('[');
            Joiner joiner = Joiner.on(',');
            List<String> propertyValues = new ArrayList<>();
            for (Map.Entry<IBlockState, Comparable> entry : properties.entrySet()) {
                // a() gets the inputted "name" for the state, the value, well, that gets the value, which is always toStringable.
                propertyValues.add(entry.getKey().a() + "=" + entry.getValue());
            }
            builder.append(joiner.join(propertyValues));
            builder.append(']');
        }
        // Now we can make the MinecraftKey...
        final MinecraftKey newKey = new MinecraftKey(builder.toString());
        BLOCK_STATE_REGISTRY.a(newKey, blockState);
        return newKey.toString();
    }
}
