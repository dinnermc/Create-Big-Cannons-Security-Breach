package dinner.dev;

import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final String LANG = CreateBigCannonsSecurityBreach.MODID + ".configuration.";

    private static final ModConfigSpec.BooleanValue ENABLED;
    private static final ModConfigSpec.DoubleValue EXPLOSION_DAMAGE_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue MIN_EXPLOSION_POWER;
    private static final ModConfigSpec.DoubleValue DIRECT_HIT_MULTIPLIER;
    private static final ModConfigSpec.BooleanValue DAMAGE_DECAY_ENABLED;
    private static final ModConfigSpec.IntValue DAMAGE_DECAY_INTERVAL_MIN;
    private static final ModConfigSpec.IntValue DAMAGE_DECAY_INTERVAL_MAX;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_GLASS_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_POWDER_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_WOOD_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_STONE_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_COPPER_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_WOOL_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_IRON_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_STEEL_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_DIAMOND_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_OBSIDIAN_HP;
    private static final ModConfigSpec.ConfigValue<String> REINFORCED_NETHERITE_HP;
    private static final ModConfigSpec.ConfigValue<String> FALLBACK_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_GLASS_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_POWDER_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_WOOD_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_STONE_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_COPPER_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_WOOL_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_IRON_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_STEEL_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_DIAMOND_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_OBSIDIAN_RESISTANCE;
    private static final ModConfigSpec.DoubleValue REINFORCED_NETHERITE_RESISTANCE;
    private static final ModConfigSpec.DoubleValue FALLBACK_RESISTANCE;
    static final ModConfigSpec SPEC;

    static {
        // General
        BUILDER.push("general").translation(LANG + "category.general");
        ENABLED = BUILDER
                .comment("Enable CBC damage to Security Craft blocks globally")
                .translation(LANG + "enabled")
                .define("enabled", true);
        EXPLOSION_DAMAGE_MULTIPLIER = BUILDER
                .comment("Global multiplier for explosive damage to SC blocks")
                .translation(LANG + "explosionDamageMultiplier")
                .defineInRange("explosionDamageMultiplier", 0.5, 0.0, 100.0);
        MIN_EXPLOSION_POWER = BUILDER
                .comment("Minimum explosion power to damage an SC block")
                .translation(LANG + "minExplosionPower")
                .defineInRange("minExplosionPower", 2.0, 0.1, 20.0);
        DIRECT_HIT_MULTIPLIER = BUILDER
                .comment("Multiplier for direct projectile hits (solid shot, grapeshot)")
                .translation(LANG + "directHitMultiplier")
                .defineInRange("directHitMultiplier", 1.5, 0.0, 100.0);
        BUILDER.pop();

        // Damage decay
        BUILDER.push("damage_decay").translation(LANG + "category.damage_decay");
        DAMAGE_DECAY_ENABLED = BUILDER
                .comment("Cracks slowly heal over time")
                .translation(LANG + "damageDecayEnabled")
                .define("damageDecayEnabled", true);
        DAMAGE_DECAY_INTERVAL_MIN = BUILDER
                .comment("Min ticks between crack reductions (20 = 1s)")
                .translation(LANG + "damageDecayIntervalMin")
                .defineInRange("damageDecayIntervalMin", 140, 20, 1200);
        DAMAGE_DECAY_INTERVAL_MAX = BUILDER
                .comment("Max ticks between crack reductions (20 = 1s)")
                .translation(LANG + "damageDecayIntervalMax")
                .defineInRange("damageDecayIntervalMax", 300, 20, 1200);
        BUILDER.pop();

        // Material HP
        BUILDER.push("material_hp").translation(LANG + "category.material_hp");
        REINFORCED_GLASS_HP = BUILDER
                .translation(LANG + "reinforcedGlassHp")
                .define("reinforcedGlassHp", "50.0");
        REINFORCED_POWDER_HP = BUILDER
                .translation(LANG + "reinforcedPowderHp")
                .define("reinforcedPowderHp", "75.0");
        REINFORCED_WOOD_HP = BUILDER
                .translation(LANG + "reinforcedWoodHp")
                .define("reinforcedWoodHp", "200.0");
        REINFORCED_STONE_HP = BUILDER
                .translation(LANG + "reinforcedStoneHp")
                .define("reinforcedStoneHp", "500.0");
        REINFORCED_COPPER_HP = BUILDER
                .translation(LANG + "reinforcedCopperHp")
                .define("reinforcedCopperHp", "500.0");
        REINFORCED_WOOL_HP = BUILDER
                .translation(LANG + "reinforcedWoolHp")
                .define("reinforcedWoolHp", "100.0");
        REINFORCED_IRON_HP = BUILDER
                .translation(LANG + "reinforcedIronHp")
                .define("reinforcedIronHp", "1500.0");
        REINFORCED_STEEL_HP = BUILDER
                .translation(LANG + "reinforcedSteelHp")
                .define("reinforcedSteelHp", "3000.0");
        REINFORCED_DIAMOND_HP = BUILDER
                .translation(LANG + "reinforcedDiamondHp")
                .define("reinforcedDiamondHp", "3000.0");
        REINFORCED_OBSIDIAN_HP = BUILDER
                .translation(LANG + "reinforcedObsidianHp")
                .define("reinforcedObsidianHp", "2500.0");
        REINFORCED_NETHERITE_HP = BUILDER
                .translation(LANG + "reinforcedNetheriteHp")
                .define("reinforcedNetheriteHp", "4000.0");
        FALLBACK_HP = BUILDER
                .translation(LANG + "fallbackHp")
                .define("fallbackHp", "600.0");
        BUILDER.pop();

        // Material explosion resistance
        BUILDER.push("material_resistance").translation(LANG + "category.material_resistance");
        REINFORCED_GLASS_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedGlassResistance")
                .defineInRange("reinforcedGlassResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_POWDER_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedPowderResistance")
                .defineInRange("reinforcedPowderResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_WOOD_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedWoodResistance")
                .defineInRange("reinforcedWoodResistance", 3000.0, 1.0, 50000.0);
        REINFORCED_STONE_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedStoneResistance")
                .defineInRange("reinforcedStoneResistance", 4500.0, 1.0, 50000.0);
        REINFORCED_COPPER_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedCopperResistance")
                .defineInRange("reinforcedCopperResistance", 4500.0, 1.0, 50000.0);
        REINFORCED_WOOL_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedWoolResistance")
                .defineInRange("reinforcedWoolResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_IRON_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedIronResistance")
                .defineInRange("reinforcedIronResistance", 6000.0, 1.0, 50000.0);
        REINFORCED_STEEL_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedSteelResistance")
                .defineInRange("reinforcedSteelResistance", 8000.0, 1.0, 50000.0);
        REINFORCED_DIAMOND_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedDiamondResistance")
                .defineInRange("reinforcedDiamondResistance", 8000.0, 1.0, 50000.0);
        REINFORCED_OBSIDIAN_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedObsidianResistance")
                .defineInRange("reinforcedObsidianResistance", 7500.0, 1.0, 50000.0);
        REINFORCED_NETHERITE_RESISTANCE = BUILDER
                .translation(LANG + "reinforcedNetheriteResistance")
                .defineInRange("reinforcedNetheriteResistance", 10000.0, 1.0, 50000.0);
        FALLBACK_RESISTANCE = BUILDER
                .translation(LANG + "fallbackResistance")
                .defineInRange("fallbackResistance", 4500.0, 1.0, 50000.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // Runtime values
    public static boolean enabled;
    public static double explosionDamageMultiplier;
    public static double minExplosionPower;
    public static double directHitMultiplier;
    public static boolean damageDecayEnabled;
    public static int damageDecayIntervalMin;
    public static int damageDecayIntervalMax;
    public static double fallbackHp;
    public static double fallbackResistance;

    // Per-block resistance cache, cleared when the config reloads.
    // Non-Security Craft blocks are cached as NaN so the registry is only hit once per block.
    private static final Map<Block, Float> RESISTANCE_CACHE = new IdentityHashMap<>();
    private static boolean cacheDirty = true;

    public static Float getCachedResistance(Block block) {
        if (cacheDirty) {
            RESISTANCE_CACHE.clear();
            cacheDirty = false;
        }
        return RESISTANCE_CACHE.get(block);
    }

    public static void cacheResistance(Block block, float resistance) {
        RESISTANCE_CACHE.put(block, resistance);
    }

    enum Group {
        GLASS, POWDER, WOOL, WOOD, STONE, COPPER, IRON, DIAMOND, OBSIDIAN, STEEL, NETHERITE
    }

    private static final Map<String, Group> MATERIAL_MAP = new LinkedHashMap<>();
    private static double glassHp, powderHp, woolHp, woodHp, stoneHp, copperHp;
    private static double ironHp, diamondHp, obsidianHp, steelHp, netheriteHp;
    private static double glassRes, powderRes, woolRes, woodRes, stoneRes, copperRes;
    private static double ironRes, diamondRes, obsidianRes, steelRes, netheriteRes;

    static {
        // These get checked in order, so the "odd ones out" that would be
        // matched wrong by a shorter key have to come first.
        put("sandstone", Group.STONE);
        put("sea_lantern", Group.GLASS);
        put("glowstone", Group.GLASS);

        put("netherite", Group.NETHERITE);
        put("diamond", Group.DIAMOND);
        put("emerald", Group.DIAMOND);
        put("obsidian", Group.OBSIDIAN);
        put("gold", Group.IRON);
        put("iron", Group.IRON);
        put("steel", Group.STEEL);
        put("copper", Group.STONE);

        put("glass", Group.GLASS);
        put("ice", Group.GLASS);
        put("wool", Group.WOOL);
        put("carpet", Group.WOOL);
        put("sponge", Group.WOOL);
        put("cobweb", Group.WOOL);
        put("lantern", Group.IRON);
        put("chain", Group.IRON);
        put("cauldron", Group.IRON);
        put("hopper", Group.IRON);

        put("bricks", Group.STONE);
        put("brick", Group.STONE);
        put("stone", Group.STONE);
        put("deepslate", Group.STONE);
        put("granite", Group.STONE);
        put("diorite", Group.STONE);
        put("andesite", Group.STONE);
        put("basalt", Group.STONE);
        put("tuff", Group.STONE);
        put("calcite", Group.STONE);
        put("netherrack", Group.STONE);
        put("quartz", Group.STONE);
        put("prismarine", Group.STONE);
        put("purpur", Group.STONE);
        put("terracotta", Group.STONE);
        put("concrete", Group.STONE);
        put("magma", Group.STONE);
        put("bone", Group.STONE);
        put("coal", Group.STONE);
        put("lapis", Group.STONE);
        put("redstone", Group.STONE);
        put("piston", Group.STONE);

        put("sand", Group.POWDER);
        put("gravel", Group.POWDER);
        put("dirt", Group.POWDER);
        put("grass", Group.POWDER);
        put("mud", Group.POWDER);
        put("clay", Group.POWDER);
        put("moss", Group.POWDER);
        put("snow", Group.POWDER);
        put("soul", Group.POWDER);
        put("suspicious", Group.POWDER);
        put("mycelium", Group.POWDER);
        put("nylium", Group.POWDER);
        put("podzol", Group.POWDER);

        put("oak", Group.WOOD);
        put("spruce", Group.WOOD);
        put("birch", Group.WOOD);
        put("jungle", Group.WOOD);
        put("acacia", Group.WOOD);
        put("cherry", Group.WOOD);
        put("mangrove", Group.WOOD);
        put("crimson", Group.WOOD);
        put("warped", Group.WOOD);
        put("bamboo", Group.WOOD);
        put("ladder", Group.WOOD);
        put("scaffolding", Group.WOOD);
        put("bookshelf", Group.WOOD);
        put("lectern", Group.WOOD);
    }

    private static void put(String key, Group group) {
        MATERIAL_MAP.put(key, group);
    }

    static void onLoad(final ModConfigEvent event) {
        cacheDirty = true;
        enabled = ENABLED.get();
        explosionDamageMultiplier = EXPLOSION_DAMAGE_MULTIPLIER.get();
        minExplosionPower = MIN_EXPLOSION_POWER.get();
        directHitMultiplier = DIRECT_HIT_MULTIPLIER.get();
        damageDecayEnabled = DAMAGE_DECAY_ENABLED.get();
        damageDecayIntervalMin = DAMAGE_DECAY_INTERVAL_MIN.get();
        damageDecayIntervalMax = DAMAGE_DECAY_INTERVAL_MAX.get();
        fallbackHp = Double.parseDouble(FALLBACK_HP.get());
        fallbackResistance = FALLBACK_RESISTANCE.get();

        glassHp = Double.parseDouble(REINFORCED_GLASS_HP.get());
        powderHp = Double.parseDouble(REINFORCED_POWDER_HP.get());
        woolHp = Double.parseDouble(REINFORCED_WOOL_HP.get());
        woodHp = Double.parseDouble(REINFORCED_WOOD_HP.get());
        stoneHp = Double.parseDouble(REINFORCED_STONE_HP.get());
        copperHp = Double.parseDouble(REINFORCED_COPPER_HP.get());
        ironHp = Double.parseDouble(REINFORCED_IRON_HP.get());
        diamondHp = Double.parseDouble(REINFORCED_DIAMOND_HP.get());
        obsidianHp = Double.parseDouble(REINFORCED_OBSIDIAN_HP.get());
        steelHp = Double.parseDouble(REINFORCED_STEEL_HP.get());
        netheriteHp = Double.parseDouble(REINFORCED_NETHERITE_HP.get());

        glassRes = REINFORCED_GLASS_RESISTANCE.get();
        powderRes = REINFORCED_POWDER_RESISTANCE.get();
        woolRes = REINFORCED_WOOL_RESISTANCE.get();
        woodRes = REINFORCED_WOOD_RESISTANCE.get();
        stoneRes = REINFORCED_STONE_RESISTANCE.get();
        copperRes = REINFORCED_COPPER_RESISTANCE.get();
        ironRes = REINFORCED_IRON_RESISTANCE.get();
        diamondRes = REINFORCED_DIAMOND_RESISTANCE.get();
        obsidianRes = REINFORCED_OBSIDIAN_RESISTANCE.get();
        steelRes = REINFORCED_STEEL_RESISTANCE.get();
        netheriteRes = REINFORCED_NETHERITE_RESISTANCE.get();
    }

    private static double groupHp(Group g) {
        return switch (g) {
            case GLASS -> glassHp;
            case POWDER -> powderHp;
            case WOOL -> woolHp;
            case WOOD -> woodHp;
            case STONE -> stoneHp;
            case COPPER -> copperHp;
            case IRON -> ironHp;
            case DIAMOND -> diamondHp;
            case OBSIDIAN -> obsidianHp;
            case STEEL -> steelHp;
            case NETHERITE -> netheriteHp;
        };
    }

    private static double groupResistance(Group g) {
        return switch (g) {
            case GLASS -> glassRes;
            case POWDER -> powderRes;
            case WOOL -> woolRes;
            case WOOD -> woodRes;
            case STONE -> stoneRes;
            case COPPER -> copperRes;
            case IRON -> ironRes;
            case DIAMOND -> diamondRes;
            case OBSIDIAN -> obsidianRes;
            case STEEL -> steelRes;
            case NETHERITE -> netheriteRes;
        };
    }

    public static double getBlockHp(String blockPath) {
        Group g = matchGroup(blockPath);
        return g != null ? groupHp(g) : fallbackHp;
    }

    public static double getBlockResistance(String blockPath) {
        Group g = matchGroup(blockPath);
        return g != null ? groupResistance(g) : fallbackResistance;
    }

    private static Group matchGroup(String path) {
        if (path == null || !path.startsWith("reinforced_")) return null;
        String name = path.substring("reinforced_".length());
        for (Map.Entry<String, Group> e : MATERIAL_MAP.entrySet()) {
            if (name.contains(e.getKey())) return e.getValue();
        }
        return null;
    }
}
