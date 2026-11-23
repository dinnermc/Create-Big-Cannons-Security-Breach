package dinner.dev;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final String LANG = CreateBigCannonsSecurityBreach.MODID + ".configuration.";

    // General
    private static final ModConfigSpec.BooleanValue ENABLED;
    private static final ModConfigSpec.DoubleValue EXPLOSION_DAMAGE_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue MIN_EXPLOSION_POWER;
    private static final ModConfigSpec.DoubleValue DIRECT_HIT_MULTIPLIER;
    private static final ModConfigSpec.BooleanValue DROP_BLOCKS_ON_BREAK;
    private static final ModConfigSpec.BooleanValue AFFECT_OWNABLE_MACHINES;
    private static final ModConfigSpec.BooleanValue OVERRIDE_EXPLOSION_RESISTANCE;

    // Munition multipliers
    private static final ModConfigSpec.DoubleValue HE_SHELL_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue AP_SHELL_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue AP_SHOT_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue SOLID_SHOT_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue MORTAR_STONE_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue DROP_MORTAR_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue AUTOCANNON_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue SHRAPNEL_MULTIPLIER;

    // Damage decay
    private static final ModConfigSpec.BooleanValue DAMAGE_DECAY_ENABLED;
    private static final ModConfigSpec.IntValue DECAY_GRACE_PERIOD_TICKS;
    private static final ModConfigSpec.IntValue DAMAGE_DECAY_INTERVAL_MIN;
    private static final ModConfigSpec.IntValue DAMAGE_DECAY_INTERVAL_MAX;
    private static final ModConfigSpec.DoubleValue DECAY_PERCENT_PER_STEP;

    // Manual repair
    private static final ModConfigSpec.BooleanValue REPAIR_ENABLED;
    private static final ModConfigSpec.BooleanValue REPAIR_OWNER_ONLY;
    private static final ModConfigSpec.DoubleValue REPAIR_AMOUNT_PER_CLICK;
    private static final ModConfigSpec.IntValue REPAIR_COOLDOWN_TICKS;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> REPAIR_ITEMS;
    private static final ModConfigSpec.BooleanValue CONSUME_REPAIR_ITEM;

    // Feedback & HUD
    private static final ModConfigSpec.BooleanValue GOGGLES_HUD_ENABLED;
    private static final ModConfigSpec.BooleanValue IMPACT_PARTICLES_ENABLED;
    private static final ModConfigSpec.BooleanValue IMPACT_SOUNDS_ENABLED;

    // Material HP (proper DoubleValue with range validation)
    private static final ModConfigSpec.DoubleValue REINFORCED_GLASS_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_POWDER_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_WOOD_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_STONE_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_COPPER_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_WOOL_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_IRON_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_STEEL_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_DIAMOND_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_OBSIDIAN_HP;
    private static final ModConfigSpec.DoubleValue REINFORCED_NETHERITE_HP;
    private static final ModConfigSpec.DoubleValue FALLBACK_HP;

    // Material resistance
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

    // Overrides & Blacklist
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_BLOCK_HP;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_BLOCKS;

    public static final ModConfigSpec SPEC;

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
                .defineInRange("explosionDamageMultiplier", 1.0, 0.0, 100.0);
        MIN_EXPLOSION_POWER = BUILDER
                .comment("Minimum explosion power to damage an SC block")
                .translation(LANG + "minExplosionPower")
                .defineInRange("minExplosionPower", 2.0, 0.1, 20.0);
        DIRECT_HIT_MULTIPLIER = BUILDER
                .comment("Global multiplier for direct projectile hits (solid shot, AP, autocannon, shrapnel)")
                .translation(LANG + "directHitMultiplier")
                .defineInRange("directHitMultiplier", 1.5, 0.0, 100.0);
        DROP_BLOCKS_ON_BREAK = BUILDER
                .comment("Whether broken Security Craft blocks drop themselves as items (false by default for Rust-style PvP)")
                .translation(LANG + "dropBlocksOnBreak")
                .define("dropBlocksOnBreak", false);
        AFFECT_OWNABLE_MACHINES = BUILDER
                .comment("Allow cannons to damage functional Security Craft blocks (keypads, scanners, keypad doors, etc.)")
                .translation(LANG + "affectOwnableMachines")
                .define("affectOwnableMachines", true);
        OVERRIDE_EXPLOSION_RESISTANCE = BUILDER
                .comment("Override SC block explosion resistance for CBC armor inspection (disable if using nuke mods)")
                .translation(LANG + "overrideExplosionResistance")
                .define("overrideExplosionResistance", true);
        BUILDER.pop();

        // Munition multipliers
        BUILDER.push("munition_multipliers").translation(LANG + "category.munition_multipliers");
        HE_SHELL_MULTIPLIER = BUILDER
                .comment("Damage multiplier for HE shells")
                .translation(LANG + "heShellMultiplier")
                .defineInRange("heShellMultiplier", 1.25, 0.0, 50.0);
        AP_SHELL_MULTIPLIER = BUILDER
                .comment("Damage multiplier for AP (Armor-Piercing) shells")
                .translation(LANG + "apShellMultiplier")
                .defineInRange("apShellMultiplier", 1.6, 0.0, 50.0);
        AP_SHOT_MULTIPLIER = BUILDER
                .comment("Damage multiplier for AP solid shots")
                .translation(LANG + "apShotMultiplier")
                .defineInRange("apShotMultiplier", 1.8, 0.0, 50.0);
        SOLID_SHOT_MULTIPLIER = BUILDER
                .comment("Damage multiplier for standard Solid Shots")
                .translation(LANG + "solidShotMultiplier")
                .defineInRange("solidShotMultiplier", 1.2, 0.0, 50.0);
        MORTAR_STONE_MULTIPLIER = BUILDER
                .comment("Damage multiplier for Mortar Stones")
                .translation(LANG + "mortarStoneMultiplier")
                .defineInRange("mortarStoneMultiplier", 0.85, 0.0, 50.0);
        DROP_MORTAR_MULTIPLIER = BUILDER
                .comment("Damage multiplier for Drop Mortar shells")
                .translation(LANG + "dropMortarMultiplier")
                .defineInRange("dropMortarMultiplier", 1.1, 0.0, 50.0);
        AUTOCANNON_MULTIPLIER = BUILDER
                .comment("Damage multiplier for Autocannon rounds (AP, Flak, Machine Gun)")
                .translation(LANG + "autocannonMultiplier")
                .defineInRange("autocannonMultiplier", 0.25, 0.0, 50.0);
        SHRAPNEL_MULTIPLIER = BUILDER
                .comment("Damage multiplier for Grapeshot, Shrapnel, and Flak sub-projectiles")
                .translation(LANG + "shrapnelMultiplier")
                .defineInRange("shrapnelMultiplier", 0.3, 0.0, 50.0);
        BUILDER.pop();

        // Damage decay
        BUILDER.push("damage_decay").translation(LANG + "category.damage_decay");
        DAMAGE_DECAY_ENABLED = BUILDER
                .comment("Cracks slowly heal over time")
                .translation(LANG + "damageDecayEnabled")
                .define("damageDecayEnabled", true);
        DECAY_GRACE_PERIOD_TICKS = BUILDER
                .comment("Ticks a block must remain undamaged before automatic decay begins (20 = 1s, 1200 = 60s)")
                .translation(LANG + "decayGracePeriodTicks")
                .defineInRange("decayGracePeriodTicks", 1200, 0, 72000);
        DAMAGE_DECAY_INTERVAL_MIN = BUILDER
                .comment("Min ticks between crack reductions (20 = 1s, 600 = 30s)")
                .translation(LANG + "damageDecayIntervalMin")
                .defineInRange("damageDecayIntervalMin", 600, 20, 72000);
        DAMAGE_DECAY_INTERVAL_MAX = BUILDER
                .comment("Max ticks between crack reductions (20 = 1s, 1200 = 60s)")
                .translation(LANG + "damageDecayIntervalMax")
                .defineInRange("damageDecayIntervalMax", 1200, 20, 72000);
        DECAY_PERCENT_PER_STEP = BUILDER
                .comment("Fraction of max HP healed per decay tick (0.05 = 5%)")
                .translation(LANG + "decayPercentPerStep")
                .defineInRange("decayPercentPerStep", 0.05, 0.01, 1.0);
        BUILDER.pop();

        // Manual repair
        BUILDER.push("repair").translation(LANG + "category.repair");
        REPAIR_ENABLED = BUILDER
                .comment("Allow players to manually repair damaged Security Craft blocks (Shift + Right-Click)")
                .translation(LANG + "repairEnabled")
                .define("repairEnabled", true);
        REPAIR_OWNER_ONLY = BUILDER
                .comment("Only the Security Craft block owner (or Creative/OP) can repair a block")
                .translation(LANG + "repairOwnerOnly")
                .define("repairOwnerOnly", true);
        REPAIR_AMOUNT_PER_CLICK = BUILDER
                .comment("HP restored per Shift + Right-Click repair action")
                .translation(LANG + "repairAmountPerClick")
                .defineInRange("repairAmountPerClick", 100.0, 1.0, 100000.0);
        REPAIR_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown in ticks between manual repairs per player (20 = 1s)")
                .translation(LANG + "repairCooldownTicks")
                .defineInRange("repairCooldownTicks", 10, 0, 200);
        REPAIR_ITEMS = BUILDER
                .comment("List of item IDs allowed to repair damaged SC blocks (e.g. create:wrench, minecraft:iron_ingot)")
                .translation(LANG + "repairItems")
                .defineListAllowEmpty(
                        "repairItems",
                        List.of("create:wrench", "minecraft:iron_ingot"),
                        () -> "create:wrench",
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null
                );
        CONSUME_REPAIR_ITEM = BUILDER
                .comment("Consume 1 item (or 1 durability if damageable) per repair action")
                .translation(LANG + "consumeRepairItem")
                .define("consumeRepairItem", false);
        BUILDER.pop();

        // Feedback & HUD
        BUILDER.push("feedback").translation(LANG + "category.feedback");
        GOGGLES_HUD_ENABLED = BUILDER
                .comment("Show block HP and structural integrity HUD when wearing Create Engineer's Goggles")
                .translation(LANG + "gogglesHudEnabled")
                .define("gogglesHudEnabled", true);
        IMPACT_PARTICLES_ENABLED = BUILDER
                .comment("Spawn block crack and breach particles when SC blocks take cannon damage")
                .translation(LANG + "impactParticlesEnabled")
                .define("impactParticlesEnabled", true);
        IMPACT_SOUNDS_ENABLED = BUILDER
                .comment("Play impact and breach sound effects when SC blocks take cannon damage")
                .translation(LANG + "impactSoundsEnabled")
                .define("impactSoundsEnabled", true);
        BUILDER.pop();

        // Material HP
        BUILDER.push("material_hp").translation(LANG + "category.material_hp");
        REINFORCED_GLASS_HP = BUILDER
                .translation(LANG + "reinforcedGlassHp")
                .defineInRange("reinforcedGlassHp", 50.0, 1.0, 1_000_000.0);
        REINFORCED_POWDER_HP = BUILDER
                .translation(LANG + "reinforcedPowderHp")
                .defineInRange("reinforcedPowderHp", 75.0, 1.0, 1_000_000.0);
        REINFORCED_WOOD_HP = BUILDER
                .translation(LANG + "reinforcedWoodHp")
                .defineInRange("reinforcedWoodHp", 200.0, 1.0, 1_000_000.0);
        REINFORCED_STONE_HP = BUILDER
                .translation(LANG + "reinforcedStoneHp")
                .defineInRange("reinforcedStoneHp", 500.0, 1.0, 1_000_000.0);
        REINFORCED_COPPER_HP = BUILDER
                .translation(LANG + "reinforcedCopperHp")
                .defineInRange("reinforcedCopperHp", 500.0, 1.0, 1_000_000.0);
        REINFORCED_WOOL_HP = BUILDER
                .translation(LANG + "reinforcedWoolHp")
                .defineInRange("reinforcedWoolHp", 100.0, 1.0, 1_000_000.0);
        REINFORCED_IRON_HP = BUILDER
                .translation(LANG + "reinforcedIronHp")
                .defineInRange("reinforcedIronHp", 1500.0, 1.0, 1_000_000.0);
        REINFORCED_STEEL_HP = BUILDER
                .translation(LANG + "reinforcedSteelHp")
                .defineInRange("reinforcedSteelHp", 3000.0, 1.0, 1_000_000.0);
        REINFORCED_DIAMOND_HP = BUILDER
                .translation(LANG + "reinforcedDiamondHp")
                .defineInRange("reinforcedDiamondHp", 3000.0, 1.0, 1_000_000.0);
        REINFORCED_OBSIDIAN_HP = BUILDER
                .translation(LANG + "reinforcedObsidianHp")
                .defineInRange("reinforcedObsidianHp", 2500.0, 1.0, 1_000_000.0);
        REINFORCED_NETHERITE_HP = BUILDER
                .translation(LANG + "reinforcedNetheriteHp")
                .defineInRange("reinforcedNetheriteHp", 4000.0, 1.0, 1_000_000.0);
        FALLBACK_HP = BUILDER
                .translation(LANG + "fallbackHp")
                .defineInRange("fallbackHp", 600.0, 1.0, 1_000_000.0);
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

        // Overrides & Blacklist
        BUILDER.push("overrides").translation(LANG + "category.overrides");
        CUSTOM_BLOCK_HP = BUILDER
                .comment("Custom per-block HP overrides in format 'modid:block_id=hp' (e.g. 'securitycraft:reinforced_door=1200')")
                .translation(LANG + "customBlockHp")
                .defineListAllowEmpty(
                        "customBlockHp",
                        List.of(),
                        () -> "securitycraft:reinforced_door=1200",
                        Config::isValidCustomHpEntry
                );
        BLACKLISTED_BLOCKS = BUILDER
                .comment("Block IDs that can NEVER be damaged by cannons (e.g. laser fields, fake fluids, block_pocket_manager)")
                .translation(LANG + "blacklistedBlocks")
                .defineListAllowEmpty(
                        "blacklistedBlocks",
                        List.of(
                                "securitycraft:laser",
                                "securitycraft:inventory_scanner_field",
                                "securitycraft:fake_water_block",
                                "securitycraft:fake_lava_block"
                        ),
                        () -> "securitycraft:block_pocket_manager",
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null
                );
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static boolean isValidCustomHpEntry(Object obj) {
        if (!(obj instanceof String s)) return false;
        int eq = s.indexOf('=');
        if (eq <= 0 || eq == s.length() - 1) return false;
        String idPart = s.substring(0, eq).trim();
        String hpPart = s.substring(eq + 1).trim();
        if (ResourceLocation.tryParse(idPart) == null) return false;
        try {
            double val = Double.parseDouble(hpPart);
            return Double.isFinite(val) && val >= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Runtime values (volatile for cross-thread visibility between server/client/config watcher)
    public static volatile boolean enabled = true;
    public static volatile double explosionDamageMultiplier = 1.0;
    public static volatile double minExplosionPower = 2.0;
    public static volatile double directHitMultiplier = 1.5;
    public static volatile boolean dropBlocksOnBreak = false;
    public static volatile boolean affectOwnableMachines = true;
    public static volatile boolean overrideExplosionResistance = true;

    public static volatile double heShellMultiplier = 1.25;
    public static volatile double apShellMultiplier = 1.6;
    public static volatile double apShotMultiplier = 1.8;
    public static volatile double solidShotMultiplier = 1.2;
    public static volatile double mortarStoneMultiplier = 0.85;
    public static volatile double dropMortarMultiplier = 1.1;
    public static volatile double autocannonMultiplier = 0.25;
    public static volatile double shrapnelMultiplier = 0.3;

    public static volatile boolean damageDecayEnabled = true;
    public static volatile int decayGracePeriodTicks = 1200;
    public static volatile int damageDecayIntervalMin = 600;
    public static volatile int damageDecayIntervalMax = 1200;
    public static volatile double decayPercentPerStep = 0.05;

    public static volatile boolean repairEnabled = true;
    public static volatile boolean repairOwnerOnly = true;
    public static volatile double repairAmountPerClick = 100.0;
    public static volatile int repairCooldownTicks = 10;
    public static volatile boolean consumeRepairItem = false;

    public static volatile boolean gogglesHudEnabled = true;
    public static volatile boolean impactParticlesEnabled = true;
    public static volatile boolean impactSoundsEnabled = true;

    public static volatile double fallbackHp = 600.0;
    public static volatile double fallbackResistance = 4500.0;

    private static volatile double glassHp = 50.0, powderHp = 75.0, woolHp = 100.0, woodHp = 200.0;
    private static volatile double stoneHp = 500.0, copperHp = 500.0, ironHp = 1500.0, diamondHp = 3000.0;
    private static volatile double obsidianHp = 2500.0, steelHp = 3000.0, netheriteHp = 4000.0;

    private static volatile double glassRes = 2400.0, powderRes = 2400.0, woolRes = 2400.0, woodRes = 3000.0;
    private static volatile double stoneRes = 4500.0, copperRes = 4500.0, ironRes = 6000.0, diamondRes = 8000.0;
    private static volatile double obsidianRes = 7500.0, steelRes = 8000.0, netheriteRes = 10000.0;

    private static volatile Map<ResourceLocation, Double> customHpMap = Map.of();
    private static volatile Set<ResourceLocation> blacklistSet = Set.of();
    private static volatile Set<ResourceLocation> repairItemSet = Set.of();

    // Thread-safe per-block caches, atomically swapped on config reload.
    private static volatile ConcurrentHashMap<Block, Float> resistanceCache = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Block, Double> hpCache = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Block, Boolean> targetableCache = new ConcurrentHashMap<>();

    public enum Group {
        GLASS("glass"),
        POWDER("powder"),
        WOOL("wool"),
        WOOD("wood"),
        STONE("stone"),
        COPPER("copper"),
        IRON("iron"),
        DIAMOND("diamond"),
        OBSIDIAN("obsidian"),
        STEEL("steel"),
        NETHERITE("netherite");

        private final String key;

        Group(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }
    }

    private static final Map<String, Group> MATERIAL_MAP = new LinkedHashMap<>();

    static {
        // Checked in order: specific overrides that would otherwise match a shorter key come first.
        put("sandstone", Group.STONE);
        put("sea_lantern", Group.GLASS);
        put("glowstone", Group.GLASS);
        put("tinted_glass", Group.GLASS);
        put("shroomlight", Group.GLASS);
        put("froglight", Group.GLASS);

        put("netherite", Group.NETHERITE);
        put("diamond", Group.DIAMOND);
        put("emerald", Group.DIAMOND);
        put("obsidian", Group.OBSIDIAN);
        put("steel", Group.STEEL);
        put("copper", Group.COPPER);
        put("gold", Group.IRON);
        put("iron", Group.IRON);
        put("anvil", Group.IRON);
        put("lantern", Group.IRON);
        put("chain", Group.IRON);
        put("cauldron", Group.IRON);
        put("hopper", Group.IRON);
        put("lightning_rod", Group.COPPER);

        put("glass", Group.GLASS);
        put("ice", Group.GLASS);
        put("amethyst", Group.GLASS);
        put("wool", Group.WOOL);
        put("carpet", Group.WOOL);
        put("sponge", Group.WOOL);
        put("cobweb", Group.WOOL);
        put("leaves", Group.WOOL);
        put("hay", Group.WOOL);
        put("nether_wart", Group.WOOL);
        put("honey", Group.POWDER);
        put("slime", Group.POWDER);

        put("bricks", Group.STONE);
        put("brick", Group.STONE);
        put("blackstone", Group.STONE);
        put("stone", Group.STONE);
        put("deepslate", Group.STONE);
        put("granite", Group.STONE);
        put("diorite", Group.STONE);
        put("andesite", Group.STONE);
        put("basalt", Group.STONE);
        put("tuff", Group.STONE);
        put("calcite", Group.STONE);
        put("dripstone", Group.STONE);
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
        put("observer", Group.STONE);
        put("dispenser", Group.STONE);
        put("dropper", Group.STONE);
        put("furnace", Group.STONE);
        put("smoker", Group.STONE);

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
        put("planks", Group.WOOD);
        put("log", Group.WOOD);
        put("wood", Group.WOOD);
        put("ladder", Group.WOOD);
        put("scaffolding", Group.WOOD);
        put("bookshelf", Group.WOOD);
        put("lectern", Group.WOOD);
        put("barrel", Group.WOOD);
        put("chest", Group.WOOD);
        put("melon", Group.WOOD);
        put("pumpkin", Group.WOOD);

        // Reinforced door/trapdoor without a wood prefix (e.g. securitycraft:reinforced_door) is iron-tier
        put("door", Group.IRON);
        put("keypad", Group.IRON);
        put("scanner", Group.IRON);
        put("laser_block", Group.IRON);
        put("turret", Group.IRON);
        put("protecto", Group.IRON);
        put("trophy_system", Group.IRON);
    }

    private static void put(String key, Group group) {
        MATERIAL_MAP.put(key, group);
    }

    static void onLoad(final ModConfigEvent event) {
        if (event instanceof ModConfigEvent.Unloading) return;
        if (event.getConfig().getSpec() != SPEC) return;
        bake();
    }

    public static void bake() {
        enabled = ENABLED.get();
        explosionDamageMultiplier = EXPLOSION_DAMAGE_MULTIPLIER.get();
        minExplosionPower = MIN_EXPLOSION_POWER.get();
        directHitMultiplier = DIRECT_HIT_MULTIPLIER.get();
        dropBlocksOnBreak = DROP_BLOCKS_ON_BREAK.get();
        affectOwnableMachines = AFFECT_OWNABLE_MACHINES.get();
        overrideExplosionResistance = OVERRIDE_EXPLOSION_RESISTANCE.get();

        heShellMultiplier = HE_SHELL_MULTIPLIER.get();
        apShellMultiplier = AP_SHELL_MULTIPLIER.get();
        apShotMultiplier = AP_SHOT_MULTIPLIER.get();
        solidShotMultiplier = SOLID_SHOT_MULTIPLIER.get();
        mortarStoneMultiplier = MORTAR_STONE_MULTIPLIER.get();
        dropMortarMultiplier = DROP_MORTAR_MULTIPLIER.get();
        autocannonMultiplier = AUTOCANNON_MULTIPLIER.get();
        shrapnelMultiplier = SHRAPNEL_MULTIPLIER.get();

        damageDecayEnabled = DAMAGE_DECAY_ENABLED.get();
        decayGracePeriodTicks = DECAY_GRACE_PERIOD_TICKS.get();
        int rawMin = DAMAGE_DECAY_INTERVAL_MIN.get();
        int rawMax = DAMAGE_DECAY_INTERVAL_MAX.get();
        damageDecayIntervalMin = Math.min(rawMin, rawMax);
        damageDecayIntervalMax = Math.max(rawMin, rawMax);
        decayPercentPerStep = DECAY_PERCENT_PER_STEP.get();

        repairEnabled = REPAIR_ENABLED.get();
        repairOwnerOnly = REPAIR_OWNER_ONLY.get();
        repairAmountPerClick = REPAIR_AMOUNT_PER_CLICK.get();
        repairCooldownTicks = REPAIR_COOLDOWN_TICKS.get();
        consumeRepairItem = CONSUME_REPAIR_ITEM.get();

        gogglesHudEnabled = GOGGLES_HUD_ENABLED.get();
        impactParticlesEnabled = IMPACT_PARTICLES_ENABLED.get();
        impactSoundsEnabled = IMPACT_SOUNDS_ENABLED.get();

        fallbackHp = FALLBACK_HP.get();
        fallbackResistance = FALLBACK_RESISTANCE.get();

        glassHp = REINFORCED_GLASS_HP.get();
        powderHp = REINFORCED_POWDER_HP.get();
        woolHp = REINFORCED_WOOL_HP.get();
        woodHp = REINFORCED_WOOD_HP.get();
        stoneHp = REINFORCED_STONE_HP.get();
        copperHp = REINFORCED_COPPER_HP.get();
        ironHp = REINFORCED_IRON_HP.get();
        diamondHp = REINFORCED_DIAMOND_HP.get();
        obsidianHp = REINFORCED_OBSIDIAN_HP.get();
        steelHp = REINFORCED_STEEL_HP.get();
        netheriteHp = REINFORCED_NETHERITE_HP.get();

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

        // Parse custom HP overrides
        Map<ResourceLocation, Double> parsedCustomHp = new HashMap<>();
        for (String entry : CUSTOM_BLOCK_HP.get()) {
            int eq = entry.indexOf('=');
            if (eq <= 0 || eq == entry.length() - 1) continue;
            ResourceLocation id = ResourceLocation.tryParse(entry.substring(0, eq).trim());
            if (id == null) continue;
            try {
                double hp = Double.parseDouble(entry.substring(eq + 1).trim());
                if (Double.isFinite(hp) && hp >= 1.0) {
                    parsedCustomHp.put(id, hp);
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("Ignoring invalid customBlockHp entry '{}'", entry);
            }
        }
        customHpMap = Map.copyOf(parsedCustomHp);

        // Parse blacklist
        Set<ResourceLocation> parsedBlacklist = new HashSet<>();
        for (String entry : BLACKLISTED_BLOCKS.get()) {
            ResourceLocation id = ResourceLocation.tryParse(entry.trim());
            if (id != null) parsedBlacklist.add(id);
        }
        blacklistSet = Set.copyOf(parsedBlacklist);

        // Parse repair items
        Set<ResourceLocation> parsedRepairItems = new HashSet<>();
        for (String entry : REPAIR_ITEMS.get()) {
            ResourceLocation id = ResourceLocation.tryParse(entry.trim());
            if (id != null) parsedRepairItems.add(id);
        }
        repairItemSet = Set.copyOf(parsedRepairItems);

        // Atomically reset caches
        clearCaches();
    }

    public static void clearCaches() {
        resistanceCache = new ConcurrentHashMap<>();
        hpCache = new ConcurrentHashMap<>();
        targetableCache = new ConcurrentHashMap<>();
    }

    public static Float getCachedResistance(Block block) {
        return resistanceCache.get(block);
    }

    public static void cacheResistance(Block block, float resistance) {
        resistanceCache.put(block, resistance);
    }

    public static Boolean getCachedTargetable(Block block) {
        return targetableCache.get(block);
    }

    public static void cacheTargetable(Block block, boolean targetable) {
        targetableCache.put(block, targetable);
    }

    public static boolean isBlacklisted(ResourceLocation blockId) {
        return blacklistSet.contains(blockId);
    }

    public static boolean hasCustomHp(ResourceLocation blockId) {
        return customHpMap.containsKey(blockId);
    }

    public static boolean isRepairItem(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return repairItemSet.contains(id);
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

    public static double getBlockHp(Block block) {
        Double cached = hpCache.get(block);
        if (cached != null) return cached;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        Double custom = customHpMap.get(blockId);
        double result;
        if (custom != null) {
            result = custom;
        } else {
            Group g = matchGroup(blockId.getPath());
            result = g != null ? groupHp(g) : fallbackHp;
        }
        hpCache.put(block, result);
        return result;
    }

    public static double getBlockHp(String blockPath) {
        Group g = matchGroup(blockPath);
        return g != null ? groupHp(g) : fallbackHp;
    }

    public static double getBlockResistance(String blockPath) {
        Group g = matchGroup(blockPath);
        return g != null ? groupResistance(g) : fallbackResistance;
    }

    public static Group matchGroup(String path) {
        if (path == null) return null;
        String name = path.startsWith("reinforced_") ? path.substring("reinforced_".length()) : path;
        for (Map.Entry<String, Group> e : MATERIAL_MAP.entrySet()) {
            if (name.contains(e.getKey())) return e.getValue();
        }
        return null;
    }
}
