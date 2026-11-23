package dinner.dev.network;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache of synced block damage values used by the Create Goggles HUD.
 */
public final class ClientDamageCache {
    private static final Map<BlockPos, Float> DAMAGE = new ConcurrentHashMap<>();

    private ClientDamageCache() {}

    public static void update(BlockPos pos, float damage) {
        if (damage <= 0.0f || !Float.isFinite(damage)) {
            DAMAGE.remove(pos);
        } else {
            DAMAGE.put(pos.immutable(), damage);
        }
    }

    public static float getDamage(BlockPos pos) {
        return DAMAGE.getOrDefault(pos, 0.0f);
    }

    public static void clear() {
        DAMAGE.clear();
    }
}
