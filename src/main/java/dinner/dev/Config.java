package dinner.dev;

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
                .comment("HP to break reinforced glass")
                .translation(LANG + "reinforcedGlassHp")
                .define("reinforcedGlassHp", "50.0");
        REINFORCED_POWDER_HP = BUILDER
                .comment("HP to break reinforced sand/gravel/dirt")
                .translation(LANG + "reinforcedPowderHp")
                .define("reinforcedPowderHp", "75.0");
        REINFORCED_WOOD_HP = BUILDER
                .comment("HP to break reinforced wood")
                .translation(LANG + "reinforcedWoodHp")
                .define("reinforcedWoodHp", "200.0");
        REINFORCED_STONE_HP = BUILDER
                .comment("HP to break reinforced stone/brick/terracotta")
                .translation(LANG + "reinforcedStoneHp")
                .define("reinforcedStoneHp", "500.0");
        REINFORCED_COPPER_HP = BUILDER
                .comment("HP to break reinforced copper")
                .translation(LANG + "reinforcedCopperHp")
                .define("reinforcedCopperHp", "500.0");
        REINFORCED_WOOL_HP = BUILDER
                .comment("HP to break reinforced wool/carpet")
                .translation(LANG + "reinforcedWoolHp")
                .define("reinforcedWoolHp", "100.0");
        REINFORCED_IRON_HP = BUILDER
                .comment("HP to break reinforced iron/gold")
                .translation(LANG + "reinforcedIronHp")
                .define("reinforcedIronHp", "1500.0");
        REINFORCED_STEEL_HP = BUILDER
                .comment("HP to break reinforced steel")
                .translation(LANG + "reinforcedSteelHp")
                .define("reinforcedSteelHp", "3000.0");
        REINFORCED_DIAMOND_HP = BUILDER
                .comment("HP to break reinforced diamond/emerald")
                .translation(LANG + "reinforcedDiamondHp")
                .define("reinforcedDiamondHp", "3000.0");
        REINFORCED_OBSIDIAN_HP = BUILDER
                .comment("HP to break reinforced obsidian")
                .translation(LANG + "reinforcedObsidianHp")
                .define("reinforcedObsidianHp", "2500.0");
        REINFORCED_NETHERITE_HP = BUILDER
                .comment("HP to break reinforced netherite")
                .translation(LANG + "reinforcedNetheriteHp")
                .define("reinforcedNetheriteHp", "4000.0");
        FALLBACK_HP = BUILDER
                .translation(LANG + "fallbackHp")
                .define("fallbackHp", "600.0");
        BUILDER.pop();

        // Material explosion resistance
        BUILDER.push("material_resistance").translation(LANG + "category.material_resistance");
        REINFORCED_GLASS_RESISTANCE = BUILDER
                .comment("Resistance for reinforced glass")
                .translation(LANG + "reinforcedGlassResistance")
                .defineInRange("reinforcedGlassResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_POWDER_RESISTANCE = BUILDER
                .comment("Resistance for reinforced sand/gravel/dirt")
                .translation(LANG + "reinforcedPowderResistance")
                .defineInRange("reinforcedPowderResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_WOOD_RESISTANCE = BUILDER
                .comment("Resistance for reinforced wood")
                .translation(LANG + "reinforcedWoodResistance")
                .defineInRange("reinforcedWoodResistance", 3000.0, 1.0, 50000.0);
        REINFORCED_STONE_RESISTANCE = BUILDER
                .comment("Resistance for reinforced stone/brick/terracotta")
                .translation(LANG + "reinforcedStoneResistance")
                .defineInRange("reinforcedStoneResistance", 4500.0, 1.0, 50000.0);
        REINFORCED_COPPER_RESISTANCE = BUILDER
                .comment("Resistance for reinforced copper")
                .translation(LANG + "reinforcedCopperResistance")
                .defineInRange("reinforcedCopperResistance", 4500.0, 1.0, 50000.0);
        REINFORCED_WOOL_RESISTANCE = BUILDER
                .comment("Resistance for reinforced wool/carpet")
                .translation(LANG + "reinforcedWoolResistance")
                .defineInRange("reinforcedWoolResistance", 2400.0, 1.0, 50000.0);
        REINFORCED_IRON_RESISTANCE = BUILDER
                .comment("Resistance for reinforced iron/gold")
                .translation(LANG + "reinforcedIronResistance")
                .defineInRange("reinforcedIronResistance", 6000.0, 1.0, 50000.0);
        REINFORCED_STEEL_RESISTANCE = BUILDER
                .comment("Resistance for reinforced steel")
                .translation(LANG + "reinforcedSteelResistance")
                .defineInRange("reinforcedSteelResistance", 8000.0, 1.0, 50000.0);
        REINFORCED_DIAMOND_RESISTANCE = BUILDER
                .comment("Resistance for reinforced diamond/emerald")
                .translation(LANG + "reinforcedDiamondResistance")
                .defineInRange("reinforcedDiamondResistance", 8000.0, 1.0, 50000.0);
        REINFORCED_OBSIDIAN_RESISTANCE = BUILDER
                .comment("Resistance for reinforced obsidian")
                .translation(LANG + "reinforcedObsidianResistance")
                .defineInRange("reinforcedObsidianResistance", 7500.0, 1.0, 50000.0);
        REINFORCED_NETHERITE_RESISTANCE = BUILDER
                .comment("Resistance for reinforced netherite")
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

    // Cache resistance per block so we don't hit the registry on every call
    private static final Map<net.minecraft.world.level.block.Block, float[]> RESISTANCE_CACHE = new IdentityHashMap<>();
    private static boolean cacheDirty = true;

    public static boolean tryGetCachedResistance(net.minecraft.world.level.block.Block block, float[] out) {
        if (cacheDirty) {
            RESISTANCE_CACHE.clear();
            cacheDirty = false;
        }
        float[] cached = RESISTANCE_CACHE.get(block);
        if (cached != null) {
            out[0] = cached[0];
            return true;
        }
        return false;
    }

    public static void cacheResistance(net.minecraft.world.level.block.Block block, float resistance) {
        RESISTANCE_CACHE.put(block, new float[]{resistance});
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
        // Longer keys first, so "sandstone" is checked before "sand"
        put("crying_obsidian", Group.OBSIDIAN);
        put("red_sandstone", Group.STONE);
        put("sandstone", Group.STONE);
        put("end_stone", Group.STONE);
        put("mossy_cobblestone", Group.STONE);
        put("mossy_stone_bricks", Group.STONE);
        put("chiseled_stone_bricks", Group.STONE);
        put("cracked_stone_bricks", Group.STONE);
        put("stone_bricks", Group.STONE);
        put("stone_brick", Group.STONE);
        put("mud_bricks", Group.STONE);
        put("mud_brick", Group.STONE);
        put("nether_bricks", Group.STONE);
        put("nether_brick", Group.STONE);
        put("red_nether_bricks", Group.STONE);
        put("red_nether_brick", Group.STONE);
        put("end_stone_bricks", Group.STONE);
        put("end_stone_brick", Group.STONE);
        put("deepslate_bricks", Group.STONE);
        put("deepslate_brick", Group.STONE);
        put("deepslate_tiles", Group.STONE);
        put("deepslate_tile", Group.STONE);
        put("chiseled_deepslate", Group.STONE);
        put("chiseled_nether_bricks", Group.STONE);
        put("cracked_nether_bricks", Group.STONE);
        put("cracked_deepslate_bricks", Group.STONE);
        put("cracked_deepslate_tiles", Group.STONE);
        put("cracked_polished_blackstone_bricks", Group.STONE);
        put("polished_blackstone_bricks", Group.STONE);
        put("polished_blackstone_brick", Group.STONE);
        put("chiseled_polished_blackstone", Group.STONE);
        put("cobbled_deepslate", Group.STONE);
        put("polished_deepslate", Group.STONE);
        put("polished_granite", Group.STONE);
        put("polished_diorite", Group.STONE);
        put("polished_andesite", Group.STONE);
        put("polished_basalt", Group.STONE);
        put("smooth_basalt", Group.STONE);
        put("smooth_quartz", Group.STONE);
        put("chiseled_quartz", Group.STONE);
        put("quartz_bricks", Group.STONE);
        put("quartz_pillar", Group.STONE);
        put("quartz_block", Group.STONE);
        put("prismarine_bricks", Group.STONE);
        put("prismarine_brick", Group.STONE);
        put("dark_prismarine", Group.STONE);
        put("purpur_pillar", Group.STONE);
        put("purpur_block", Group.STONE);
        put("smooth_sandstone", Group.STONE);
        put("chiseled_sandstone", Group.STONE);
        put("cut_sandstone", Group.STONE);
        put("cut_red_sandstone", Group.STONE);
        put("chiseled_red_sandstone", Group.STONE);
        put("smooth_red_sandstone", Group.STONE);
        put("smooth_stone", Group.STONE);
        put("normal_stone_slab", Group.STONE);
        put("frosted_ice", Group.GLASS);
        put("stained_glass_pane", Group.GLASS);
        put("stained_glass", Group.GLASS);
        put("glass_pane", Group.GLASS);
        put("tinted_glass", Group.GLASS);
        put("glazed_terracotta", Group.STONE);
        put("soul_sand", Group.POWDER);
        put("soul_soil", Group.POWDER);
        put("rooted_dirt", Group.POWDER);
        put("coarse_dirt", Group.POWDER);
        put("grass_block", Group.POWDER);
        put("warped_wart_block", Group.POWDER);
        put("nether_wart_block", Group.POWDER);
        put("shroomlight", Group.POWDER);
        put("ochre_froglight", Group.POWDER);
        put("verdant_froglight", Group.POWDER);
        put("pearlescent_froglight", Group.POWDER);
        put("snow_block", Group.POWDER);
        put("magma_block", Group.STONE);
        put("sea_lantern", Group.GLASS);
        put("bone_block", Group.STONE);
        put("sponge", Group.WOOL);
        put("wet_sponge", Group.WOOL);
        put("cobweb", Group.WOOL);
        put("moss_block", Group.POWDER);
        put("moss_carpet", Group.WOOL);
        put("packed_mud", Group.STONE);
        put("packed_ice", Group.GLASS);
        put("blue_ice", Group.GLASS);
        put("iron_bars", Group.IRON);
        put("iron_door", Group.IRON);
        put("iron_trapdoor", Group.IRON);
        put("soul_lantern", Group.IRON);
        put("lantern", Group.IRON);
        put("chain", Group.IRON);
        put("raw_iron_block", Group.IRON);
        put("raw_gold_block", Group.IRON);
        put("raw_copper_block", Group.STONE);
        put("cut_copper", Group.STONE);
        put("exposed_copper", Group.STONE);
        put("weathered_copper", Group.STONE);
        put("oxidized_copper", Group.STONE);
        put("exposed_cut_copper", Group.STONE);
        put("weathered_cut_copper", Group.STONE);
        put("oxidized_cut_copper", Group.STONE);
        put("amethyst_block", Group.GLASS);
        put("budding_amethyst", Group.GLASS);
        put("redstone_block", Group.STONE);
        put("redstone_lamp", Group.STONE);
        put("lightning_rod", Group.IRON);
        put("stone_button", Group.STONE);
        put("stone_pressure_plate", Group.STONE);
        put("polished_blackstone_button", Group.STONE);
        put("polished_blackstone_pressure_plate", Group.STONE);
        put("bamboo_block", Group.WOOD);
        put("bamboo_planks", Group.WOOD);
        put("bamboo_mosaic", Group.WOOD);
        put("bamboo_slab", Group.WOOD);
        put("bamboo_stairs", Group.WOOD);
        put("bamboo_pressure_plate", Group.WOOD);
        put("bamboo_button", Group.WOOD);
        put("stripped_bamboo_block", Group.WOOD);
        put("end_rod", Group.GLASS);
        put("hopper", Group.IRON);
        put("dispenser", Group.STONE);
        put("dropper", Group.STONE);
        put("observer", Group.STONE);
        put("piston", Group.STONE);
        put("sticky_piston", Group.STONE);
        put("lectern", Group.WOOD);
        put("lever", Group.STONE);
        put("cauldron", Group.IRON);
        put("water_cauldron", Group.IRON);
        put("lava_cauldron", Group.IRON);
        put("powder_snow_cauldron", Group.IRON);
        put("scaffolding", Group.WOOD);
        put("ladder", Group.WOOD);

        // Single words
        put("netherite", Group.NETHERITE);
        put("diamond", Group.DIAMOND);
        put("emerald", Group.DIAMOND);
        put("gold", Group.IRON);
        put("coal", Group.STONE);
        put("lapis", Group.STONE);
        put("obsidian", Group.OBSIDIAN);
        put("glass", Group.GLASS);
        put("bricks", Group.STONE);
        put("brick", Group.STONE);
        put("quartz", Group.STONE);
        put("prismarine", Group.STONE);
        put("purpur", Group.STONE);
        put("terracotta", Group.STONE);
        put("concrete", Group.STONE);
        put("wool", Group.WOOL);
        put("carpet", Group.WOOL);
        put("ice", Group.GLASS);
        put("sand", Group.POWDER);
        put("gravel", Group.POWDER);
        put("suspicious", Group.POWDER);
        put("dirt", Group.POWDER);
        put("grass", Group.POWDER);
        put("podzol", Group.POWDER);
        put("mud", Group.POWDER);
        put("mycelium", Group.POWDER);
        put("nylium", Group.POWDER);
        put("clay", Group.POWDER);
        put("netherrack", Group.STONE);
        put("end_stone", Group.STONE);
        put("cobblestone", Group.STONE);
        put("glowstone", Group.GLASS);
        put("stone", Group.STONE);
        put("granite", Group.STONE);
        put("diorite", Group.STONE);
        put("andesite", Group.STONE);
        put("deepslate", Group.STONE);
        put("tuff", Group.STONE);
        put("calcite", Group.STONE);
        put("dripstone", Group.STONE);
        put("blackstone", Group.STONE);
        put("basalt", Group.STONE);
        put("copper", Group.STONE);
        put("iron", Group.IRON);
        put("steel", Group.STEEL);
        put("oak", Group.WOOD);
        put("spruce", Group.WOOD);
        put("birch", Group.WOOD);
        put("jungle", Group.WOOD);
        put("acacia", Group.WOOD);
        put("cherry", Group.WOOD);
        put("mangrove", Group.WOOD);
        put("crimson", Group.WOOD);
        put("warped", Group.WOOD);
        put("bookshelf", Group.WOOD);
        put("redstone", Group.STONE);
        put("cobweb", Group.WOOL);
        put("moss", Group.POWDER);
        put("packed", Group.STONE);
        put("smooth", Group.STONE);
        put("chiseled", Group.STONE);
        put("cracked", Group.STONE);
        put("polished", Group.STONE);
        put("cut", Group.STONE);
        put("exposed", Group.STONE);
        put("weathered", Group.STONE);
        put("oxidized", Group.STONE);
        put("stripped", Group.WOOD);
        put("stained", Group.GLASS);
        put("normal", Group.STONE);
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
