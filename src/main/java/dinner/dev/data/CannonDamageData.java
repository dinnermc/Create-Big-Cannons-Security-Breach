package dinner.dev.data;

import dinner.dev.CreateBigCannonsSecurityBreach;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CannonDamageData extends SavedData {
    private static final String DATA_NAME = CreateBigCannonsSecurityBreach.MODID + "_damage";
    private static final String TAG_DAMAGE_MAP = "block_damage";

    public static final int ANIM_BASE = 0x1CB00000;

    private final Map<BlockPos, Float> damageMap = new HashMap<>();

    public CannonDamageData() {}

    public static CannonDamageData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<CannonDamageData>(CannonDamageData::new, CannonDamageData::load),
                DATA_NAME
        );
    }

    public static int animId(BlockPos pos) {
        return ANIM_BASE + pos.hashCode();
    }

    public static void setBreakProgress(ServerLevel level, BlockPos pos, int stage) {
        if (stage <= 0) {
            level.destroyBlockProgress(animId(pos), pos, -1);
        } else {
            level.destroyBlockProgress(animId(pos), pos, Math.min(9, stage));
        }
    }

    public static CannonDamageData load(CompoundTag tag, HolderLookup.Provider registries) {
        CannonDamageData data = new CannonDamageData();
        ListTag list = tag.getList(TAG_DAMAGE_MAP, Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag compound = (CompoundTag) entry;
            BlockPos pos = BlockPos.of(compound.getLong("pos"));
            float damage = compound.getFloat("damage");
            data.damageMap.put(pos, damage);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, Float> entry : damageMap.entrySet()) {
            CompoundTag compound = new CompoundTag();
            compound.putLong("pos", entry.getKey().asLong());
            compound.putFloat("damage", entry.getValue());
            list.add(compound);
        }
        tag.put(TAG_DAMAGE_MAP, list);
        return tag;
    }

    public float getDamage(BlockPos pos) {
        return damageMap.getOrDefault(pos, 0.0f);
    }

    public void addDamage(BlockPos pos, float amount) {
        float current = getDamage(pos);
        damageMap.put(pos, current + amount);
        setDirty();
    }

    public void setDamage(BlockPos pos, float amount) {
        if (amount <= 0) {
            damageMap.remove(pos);
        } else {
            damageMap.put(pos, amount);
        }
        setDirty();
    }

    public void clearDamage(BlockPos pos) {
        damageMap.remove(pos);
        setDirty();
    }

    public boolean hasDamage(BlockPos pos) {
        return damageMap.containsKey(pos);
    }

    public Map<BlockPos, Float> copyEntries() {
        return new HashMap<>(damageMap);
    }
}
