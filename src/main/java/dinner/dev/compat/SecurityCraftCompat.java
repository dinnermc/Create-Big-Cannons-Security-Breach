package dinner.dev.compat;

import com.mojang.logging.LogUtils;
import dinner.dev.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflection-based compatibility layer for SecurityCraft so the mod builds
 * with zero compile-time dependency on SecurityCraft.
 */
public final class SecurityCraftCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile boolean initialized = false;
    private static Class<?> iReinforcedBlockClass;
    private static Class<?> ownableBlockClass;
    private static Class<?> iOwnableClass;
    private static Class<?> laserFieldBlockClass;
    private static Class<?> inventoryScannerFieldBlockClass;
    private static Method isOwnedByEntityMethod;
    private static Method getOwnerMethod;
    private static Method ownerGetNameMethod;

    private SecurityCraftCompat() {}

    private static void ensureInitialized() {
        if (initialized) return;
        synchronized (SecurityCraftCompat.class) {
            if (initialized) return;
            iReinforcedBlockClass = loadClass("net.geforcemods.securitycraft.api.IReinforcedBlock");
            ownableBlockClass = loadClass("net.geforcemods.securitycraft.blocks.OwnableBlock");
            iOwnableClass = loadClass("net.geforcemods.securitycraft.api.IOwnable");
            laserFieldBlockClass = loadClass("net.geforcemods.securitycraft.blocks.LaserFieldBlock");
            inventoryScannerFieldBlockClass = loadClass("net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock");

            if (iOwnableClass != null) {
                try {
                    isOwnedByEntityMethod = iOwnableClass.getMethod("isOwnedBy", Entity.class);
                } catch (NoSuchMethodException e) {
                    LOGGER.debug("SecurityCraft IOwnable#isOwnedBy(Entity) not found", e);
                }
                try {
                    getOwnerMethod = iOwnableClass.getMethod("getOwner");
                } catch (NoSuchMethodException e) {
                    LOGGER.debug("SecurityCraft IOwnable#getOwner() not found", e);
                }
            }

            Class<?> ownerClass = loadClass("net.geforcemods.securitycraft.api.Owner");
            if (ownerClass != null) {
                try {
                    ownerGetNameMethod = ownerClass.getMethod("getName");
                } catch (NoSuchMethodException e) {
                    LOGGER.debug("SecurityCraft Owner#getName() not found", e);
                }
            }
            initialized = true;
        }
    }

    private static Class<?> loadClass(String name) {
        try {
            return Class.forName(name, false, SecurityCraftCompat.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Checks whether a block should be managed by CBC: Security Breach.
     * Result is cached per-block in {@link Config}.
     */
    public static boolean isTargetableBlock(Block block) {
        if (block == null) return false;
        Boolean cached = Config.getCachedTargetable(block);
        if (cached != null) return cached;

        boolean result = computeTargetable(block);
        Config.cacheTargetable(block, result);
        return result;
    }

    private static boolean computeTargetable(Block block) {
        if (block instanceof LiquidBlock) return false;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (Config.isBlacklisted(blockId)) return false;

        // Explicitly configured custom HP overrides always opt a block in (unless blacklisted).
        if (Config.hasCustomHp(blockId)) return true;

        if (!"securitycraft".equals(blockId.getNamespace())) return false;

        ensureInitialized();

        // Never target intangible security fields (laser beams, inventory scanner fields).
        if (laserFieldBlockClass != null && laserFieldBlockClass.isInstance(block)) return false;
        if (inventoryScannerFieldBlockClass != null && inventoryScannerFieldBlockClass.isInstance(block)) return false;

        String path = blockId.getPath();

        // Exclude mines/traps even if ownable, and reinforced_* always qualifies.
        if (iReinforcedBlockClass != null && iReinforcedBlockClass.isInstance(block)) {
            return true;
        }
        if (path.startsWith("reinforced_")) {
            return true;
        }

        if (!Config.affectOwnableMachines) {
            return false;
        }

        // Functional indestructible SecurityCraft blocks (keypads, scanners, keypad doors, etc.)
        // extend OwnableBlock and have destroyTime == -1.0f for non-owners.
        if (ownableBlockClass != null && ownableBlockClass.isInstance(block)) {
            // Skip breakable/explosive mines & traps.
            if (path.endsWith("_mine") || path.equals("claymore") || path.equals("bouncing_betty")
                    || path.equals("mine") || path.equals("track_mine") || path.equals("ims")) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Returns true if the player owns the SecurityCraft block at {@code pos},
     * or if the block has no SecurityCraft owner.
     */
    public static boolean isOwnedBy(Level level, BlockPos pos, Player player) {
        if (player.isCreative() || player.hasPermissions(2)) return true;

        ensureInitialized();
        if (iOwnableClass == null || isOwnedByEntityMethod == null) return true;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || !iOwnableClass.isInstance(be)) return true;

        try {
            Object res = isOwnedByEntityMethod.invoke(be, player);
            return Boolean.TRUE.equals(res);
        } catch (Exception e) {
            LOGGER.debug("Failed to check SecurityCraft ownership at {}", pos, e);
            return true;
        }
    }

    /**
     * Returns the owner's display name for a SecurityCraft block, or null if unavailable.
     */
    public static String getOwnerName(Level level, BlockPos pos) {
        ensureInitialized();
        if (iOwnableClass == null || getOwnerMethod == null || ownerGetNameMethod == null) return null;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || !iOwnableClass.isInstance(be)) return null;

        try {
            Object owner = getOwnerMethod.invoke(be);
            if (owner == null) return null;
            Object name = ownerGetNameMethod.invoke(owner);
            if (name instanceof String s && !s.isBlank() && !"owner".equalsIgnoreCase(s)) {
                return s;
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to read SecurityCraft owner name at {}", pos, e);
        }
        return null;
    }
}
