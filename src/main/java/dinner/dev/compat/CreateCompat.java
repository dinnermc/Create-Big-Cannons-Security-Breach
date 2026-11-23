package dinner.dev.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Reflection-based compatibility layer for Create Mod (Engineer's Goggles).
 * Supports both Helmet slot and Curios via Create's GogglesItem#isWearingGoggles.
 */
public final class CreateCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CREATE_GOGGLES_ID = ResourceLocation.fromNamespaceAndPath("create", "goggles");

    private static volatile boolean initialized = false;
    private static Method isWearingGogglesMethod;

    private CreateCompat() {}

    private static void ensureInitialized() {
        if (initialized) return;
        synchronized (CreateCompat.class) {
            if (initialized) return;
            try {
                Class<?> gogglesClass = Class.forName(
                        "com.simibubi.create.content.equipment.goggles.GogglesItem",
                        false,
                        CreateCompat.class.getClassLoader()
                );
                isWearingGogglesMethod = gogglesClass.getMethod("isWearingGoggles", Player.class);
            } catch (ClassNotFoundException e) {
                LOGGER.debug("Create GogglesItem not present; falling back to item ID check");
            } catch (NoSuchMethodException e) {
                LOGGER.debug("Create GogglesItem#isWearingGoggles(Player) not found; falling back to item ID check");
            }
            initialized = true;
        }
    }

    public static boolean isWearingGoggles(Player player) {
        if (player == null) return false;
        ensureInitialized();

        if (isWearingGogglesMethod != null) {
            try {
                Object res = isWearingGogglesMethod.invoke(null, player);
                if (Boolean.TRUE.equals(res)) return true;
            } catch (Exception e) {
                LOGGER.debug("Error invoking Create GogglesItem#isWearingGoggles", e);
            }
        }

        // Fallback: direct helmet slot check for create:goggles
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!head.isEmpty()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(head.getItem());
            return CREATE_GOGGLES_ID.equals(id);
        }
        return false;
    }
}
