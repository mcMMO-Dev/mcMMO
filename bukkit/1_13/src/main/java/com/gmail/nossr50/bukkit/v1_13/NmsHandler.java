package com.gmail.nossr50.bukkit.v1_13;

import com.gmail.nossr50.VersionedHandler;
import com.gmail.nossr50.mcMMO;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.IBlockData;
import net.minecraft.server.v1_13_R2.IBlockState;
import net.minecraft.server.v1_13_R2.IRegistry;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.RegistryMaterials;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NmsHandler extends VersionedHandler {

    private static final IRegistry<net.minecraft.server.v1_13_R2.Block> BLOCK_REGISTRY = IRegistry.BLOCK;
    private static final IRegistry<EntityTypes<?>> ENTITY_REGISTRY = IRegistry.ENTITY_TYPE;
    /**
     * We use {@link RegistryMaterials} because we don't want to default to another block state,
     * if a {@link IBlockData} is not registered, we need to generate an ID and register that key to
     * that {@link IBlockData}. By default, this should be populated during plugin startup by
     * calling some init method, but that will be determined later.
     */
    private static final IRegistry<IBlockData> BLOCK_STATE_REGISTRY = new RegistryMaterials<>();

    NmsHandler(mcMMO plugin) {
        super(plugin);
    }

    @Override
    public String getIdFor(Block block) {
        // We have to get the underlying nms block by the block, which might as well just be the type id.
        final WorldServer world = ((CraftBlock) block).getCraftWorld().getHandle();
        // The registry.c(T) will return MinecraftKey, and the toString() returns the "$mod:$name", so "minecraft:stone"
        final IBlockData blockState = world.getType(((CraftBlock) block).getPosition());
        // Sadly, Mojang doesn't store the string id onto blocks, states, or any of their types, they just call the registry.
        final MinecraftKey key = BLOCK_REGISTRY.getKey(blockState.getBlock());
        // Sometimes, there's a rare case with mods where the key is going to be null, but should never happen in bukkit/spigot.
        return key == null ? "minecraft:air" : key.toString();
    }

    @Override
    public String getIdFor(Entity entity) {
        // Every entity knows it's "type". Here, we just use the right method getter to get the EntityTypes instance for that entity
        final EntityTypes<?> entityType = ((CraftEntity) entity).getHandle().P();
        // And then... well. we get the string id from the registry!
        final MinecraftKey key = ENTITY_REGISTRY.getKey(entityType);
        // If the key is null, well, we've got bigger problems...
        return key == null ? "minecraft:slime" : key.toString();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String getIdFor(BlockState block) {
        // Much like #getIdFor(Block) except here we have the "state" by numerical id. So.. we have to grab it from
        // the world.
        final CraftBlockState craftState = (CraftBlockState) block;
        final IBlockData nmsState = craftState.getHandle();
        final net.minecraft.server.v1_13_R2.Block nmsBlock = nmsState.getBlock();
        final MinecraftKey blockKey = BLOCK_REGISTRY.getKey(nmsBlock);
        // Now we can check if our blockstate registry actually has the block state.
        final MinecraftKey stateKey = BLOCK_STATE_REGISTRY.getKey(nmsState);
        if (stateKey != null) { // If the state has been registered by the handler, then by all means.
            return stateKey.toString();
        }
        // If not, well, it needs to have a key generated and registered.
        final String nameSpace = blockKey.getKey();
        StringBuilder builder = new StringBuilder();
        builder.append(nameSpace); // We only want to get the block id, not the domain id, since that's going to be appended later

        // Get the full Property -> Value mapping for the block state
        final ImmutableMap<IBlockState<?>, Comparable<?>> properties = nmsState.getStateMap();
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
