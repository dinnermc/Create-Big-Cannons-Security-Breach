package dinner.dev.data;

import dinner.dev.CreateBigCannonsSecurityBreach;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class CannonDamageData extends SavedData {
    private static final String DATA_NAME = CreateBigCannonsSecurityBreach.MODID + "_damage";
    private static final String TAG_DAMAGE_MAP = "block_damage";

    private final Map<BlockPos, Float> damageMap = new HashMap<>();
    private final Map<BlockPos, Long> lastDamagedTick = new HashMap<>();
    /** Transient in-memory cache of last broadcast visual crack stage (-1..9) per position. */
    private final Map<BlockPos, Integer> lastCrackStage = new HashMap<>();

    public CannonDamageData() {}

    public static CannonDamageData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CannonDamageData::new, CannonDamageData::load),
                DATA_NAME
        );
    }

    /**
     * Generates a guaranteed-negative animation ID so it never collides with
     * Minecraft entity IDs (which are always non-negative).
     */
    public static int animId(BlockPos pos) {
        return -(Math.abs(pos.hashCode() & 0x3FFFFFFF) + 1);
    }

    /**
     * Computes vanilla break-progress stage (-1 for intact, 0..9 for damaged).
     */
    public static int computeStage(float damage, float maxHp) {
        if (damage <= 0.0f) return -1;
        float safeMax = Math.max(1.0f, maxHp);
        return Math.min(9, Math.max(0, (int) (damage / safeMax * 10.0f)));
    }

    public static void sendBreakProgressToPlayer(ServerPlayer player, BlockPos pos, int stage) {
        int clamped = stage < 0 ? -1 : Math.min(9, stage);
        player.connection.send(new ClientboundBlockDestructionPacket(animId(pos), pos, clamped));
    }

    /**
     * Broadcasts break progress only if the visual stage actually changed (or forced).
     */
    public void updateBreakProgress(ServerLevel level, BlockPos pos, int stage) {
        int normalized = stage < 0 ? -1 : Math.min(9, stage);
        Integer prev = lastCrackStage.get(pos);
        if (prev != null && prev == normalized) {
            return;
        }
        if (normalized < 0) {
            lastCrackStage.remove(pos);
        } else {
            lastCrackStage.put(pos.immutable(), normalized);
        }
        level.destroyBlockProgress(animId(pos), pos, normalized);
    }

    public static void clearBreakProgressImmediate(ServerLevel level, BlockPos pos) {
        level.destroyBlockProgress(animId(pos), pos, -1);
    }

    public static CannonDamageData load(CompoundTag tag, HolderLookup.Provider registries) {
        CannonDamageData data = new CannonDamageData();
        ListTag list = tag.getList(TAG_DAMAGE_MAP, Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag compound = (CompoundTag) entry;
            BlockPos pos = BlockPos.of(compound.getLong("pos"));
            float damage = compound.getFloat("damage");
            if (damage > 0.0f && Float.isFinite(damage)) {
                data.damageMap.put(pos, damage);
                if (compound.contains("last_damaged", Tag.TAG_LONG)) {
                    data.lastDamagedTick.put(pos, compound.getLong("last_damaged"));
                }
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, Float> entry : damageMap.entrySet()) {
            BlockPos pos = entry.getKey();
            CompoundTag compound = new CompoundTag();
            compound.putLong("pos", pos.asLong());
            compound.putFloat("damage", entry.getValue());
            Long tick = lastDamagedTick.get(pos);
            if (tick != null) {
                compound.putLong("last_damaged", tick);
            }
            list.add(compound);
        }
        tag.put(TAG_DAMAGE_MAP, list);
        return tag;
    }

    public boolean isEmpty() {
        return damageMap.isEmpty();
    }

    public int size() {
        return damageMap.size();
    }

    public float getDamage(BlockPos pos) {
        return damageMap.getOrDefault(pos, 0.0f);
    }

    public void setDamage(BlockPos pos, float amount) {
        if (amount <= 0.0f || !Float.isFinite(amount)) {
            clearDamage(pos);
            return;
        }
        BlockPos key = pos.immutable();
        Float prev = damageMap.put(key, amount);
        if (prev == null || Float.compare(prev, amount) != 0) {
            setDirty();
        }
    }

    public void markDamaged(BlockPos pos, long gameTime) {
        lastDamagedTick.put(pos.immutable(), gameTime);
        setDirty();
    }

    public long getLastDamagedTick(BlockPos pos) {
        return lastDamagedTick.getOrDefault(pos, 0L);
    }

    public boolean clearDamage(BlockPos pos) {
        lastCrackStage.remove(pos);
        lastDamagedTick.remove(pos);
        if (damageMap.remove(pos) != null) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean hasDamage(BlockPos pos) {
        return damageMap.containsKey(pos);
    }

    public Map<BlockPos, Float> copyEntries() {
        if (damageMap.isEmpty()) return Map.of();
        return new HashMap<>(damageMap);
    }

    public Map<BlockPos, Float> getEntriesInChunk(ChunkPos chunkPos) {
        if (damageMap.isEmpty()) return Map.of();
        Map<BlockPos, Float> result = null;
        for (Map.Entry<BlockPos, Float> entry : damageMap.entrySet()) {
            BlockPos pos = entry.getKey();
            if ((pos.getX() >> 4) == chunkPos.x && (pos.getZ() >> 4) == chunkPos.z) {
                if (result == null) result = new HashMap<>();
                result.put(pos, entry.getValue());
            }
        }
        return result != null ? result : Map.of();
    }
}
