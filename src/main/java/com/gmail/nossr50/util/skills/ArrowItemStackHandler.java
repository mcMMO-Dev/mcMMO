package com.gmail.nossr50.util.skills;

import com.gmail.nossr50.mcMMO;
import java.lang.reflect.Method;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles copying the full pickup item between arrows to preserve tipped-arrow state
 * (item type, {@code POTION_DURATION_SCALE}, potion contents, custom effects, and color)
 * across Trickshot ricochets.
 *
 * <p>Uses a three-tier strategy, resolved once at class load via cached reflection:</p>
 * <ol>
 *   <li><strong>Paper</strong> — {@code AbstractArrow.getItemStack()} / {@code setItemStack()}.
 *       Single call copies everything including color.</li>
 *   <li><strong>Spigot</strong> — {@code AbstractArrow.getItem()} / {@code setItem()}
 *       ({@code @ApiStatus.Experimental}). Copies the pickup item stack (fixing duration scale),
 *       then calls {@code setBasePotionType()} to trigger the internal {@code updateColor()}
 *       so clients see the correct tipped-arrow particles.</li>
 *   <li><strong>Fallback</strong> — manual copy of base potion type, custom effects, and color
 *       via standard Bukkit API. Does <em>not</em> fix the duration scale issue (pickup item
 *       remains {@code Items.ARROW}), but preserves as much potion state as possible.</li>
 * </ol>
 */
public final class ArrowItemStackHandler {

    // Tier 1: Paper API — AbstractArrow.getItemStack() / setItemStack(ItemStack)
    private static final @Nullable Method PAPER_GET_ITEM_STACK;
    private static final @Nullable Method PAPER_SET_ITEM_STACK;

    // Tier 2: Spigot Bukkit API — AbstractArrow.getItem() / setItem(ItemStack)
    private static final @Nullable Method SPIGOT_GET_ITEM;
    private static final @Nullable Method SPIGOT_SET_ITEM;

    static {
        // Tier 1: probe for Paper API
        Method paperGetter = null;
        Method paperSetter = null;
        try {
            paperGetter = Arrow.class.getMethod("getItemStack");
            paperSetter = Arrow.class.getMethod("setItemStack", ItemStack.class);
        } catch (final NoSuchMethodException ignored) {
            // Not on Paper
        }
        PAPER_GET_ITEM_STACK = paperGetter;
        PAPER_SET_ITEM_STACK = paperSetter;

        // Tier 2: probe for Spigot's experimental getItem/setItem
        Method spigotGetter = null;
        Method spigotSetter = null;
        try {
            spigotGetter = Arrow.class.getMethod("getItem");
            spigotSetter = Arrow.class.getMethod("setItem", ItemStack.class);
        } catch (final NoSuchMethodException ignored) {
            // Not available on this Spigot version
        }
        SPIGOT_GET_ITEM = spigotGetter;
        SPIGOT_SET_ITEM = spigotSetter;
    }

    private ArrowItemStackHandler() {
    }

    /**
     * Copies the full pickup item from one arrow to another using the best available API,
     * preserving the tipped-arrow item type and its {@code POTION_DURATION_SCALE} component.
     *
     * @param sourceArrow the original arrow to copy from
     * @param targetArrow the newly spawned arrow to copy to
     */
    public static void copyArrowItemStack(@NotNull final Arrow sourceArrow,
            @NotNull final Arrow targetArrow) {
        if (PAPER_GET_ITEM_STACK != null && PAPER_SET_ITEM_STACK != null) {
            copyItemStackViaPaper(sourceArrow, targetArrow);
        } else if (SPIGOT_GET_ITEM != null && SPIGOT_SET_ITEM != null) {
            copyItemViaSpigot(sourceArrow, targetArrow);
        } else {
            copyPotionDataFallback(sourceArrow, targetArrow);
        }
    }

    /**
     * Tier 1 — Paper: {@code getItemStack()} / {@code setItemStack()}.
     * Copies the full item stack. Follow up with {@code setBasePotionType()} to ensure
     * the internal {@code updateColor()} fires for client-side particle sync.
     */
    private static void copyItemStackViaPaper(@NotNull final Arrow sourceArrow,
            @NotNull final Arrow targetArrow) {
        try {
            final ItemStack itemStack = (ItemStack) PAPER_GET_ITEM_STACK.invoke(sourceArrow);
            if (itemStack != null) {
                PAPER_SET_ITEM_STACK.invoke(targetArrow, itemStack);
            }
            // Trigger updateColor() via setBasePotionType() in case Paper doesn't do it
            syncColor(sourceArrow, targetArrow);
        } catch (final ReflectiveOperationException e) {
            mcMMO.p.getLogger().warning(
                    "Paper arrow item-stack copy failed, falling back: " + e.getMessage());
            copyPotionDataFallback(sourceArrow, targetArrow);
        }
    }

    /**
     * Tier 2 — Spigot: {@code getItem()} / {@code setItem()}.
     * Copies the pickup item stack (fixing duration scale and item type), then calls
     * {@code setBasePotionType()} to trigger the NMS {@code updateColor()} side-effect
     * so clients see the correct tipped-arrow color and particles.
     */
    private static void copyItemViaSpigot(@NotNull final Arrow sourceArrow,
            @NotNull final Arrow targetArrow) {
        try {
            final ItemStack item = (ItemStack) SPIGOT_GET_ITEM.invoke(sourceArrow);
            if (item != null) {
                SPIGOT_SET_ITEM.invoke(targetArrow, item);
            }
            // setItem() writes pickupItemStack directly but does NOT call updateColor().
            // Re-applying the base potion type triggers setPotionContents() → updateColor().
            syncColor(sourceArrow, targetArrow);
        } catch (final ReflectiveOperationException e) {
            mcMMO.p.getLogger().warning(
                    "Spigot arrow item copy failed, falling back: " + e.getMessage());
            copyPotionDataFallback(sourceArrow, targetArrow);
        }
    }

    /**
     * Re-applies the base potion type to trigger the internal {@code updateColor()} call,
     * ensuring clients see the correct tipped-arrow color and particles.
     */
    private static void syncColor(@NotNull final Arrow sourceArrow,
            @NotNull final Arrow targetArrow) {
        if (sourceArrow.getBasePotionType() != null) {
            targetArrow.setBasePotionType(sourceArrow.getBasePotionType());
        }
    }

    /**
     * Tier 3 — Fallback: copies potion base type, custom effects, and color individually
     * via standard Bukkit API. Does <strong>not</strong> fix the {@code POTION_DURATION_SCALE}
     * issue (the pickup item remains {@code Items.ARROW} with a default scale of 1.0),
     * but preserves as much potion state as the API allows.
     */
    private static void copyPotionDataFallback(@NotNull final Arrow sourceArrow,
            @NotNull final Arrow targetArrow) {
        if (sourceArrow.getBasePotionType() != null) {
            targetArrow.setBasePotionType(sourceArrow.getBasePotionType());
        }

        if (sourceArrow.hasCustomEffects()) {
            for (final var effect : sourceArrow.getCustomEffects()) {
                targetArrow.addCustomEffect(effect, true);
            }
        }

        if (sourceArrow.getColor() != null) {
            targetArrow.setColor(sourceArrow.getColor());
        }
    }
}

