package dinner.dev.handler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.data.CannonDamageData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Map;

/**
 * Registers the /cbcbreach admin and inspection command tree.
 */
public class BreachCommands {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("cbcbreach")
                        .then(Commands.literal("inspect")
                                .executes(ctx -> inspectLookedAt(ctx.getSource()))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .requires(src -> src.hasPermission(2))
                                        .executes(ctx -> inspectPos(
                                                ctx.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                                        ))))
                        .then(Commands.literal("clear")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 256))
                                        .executes(ctx -> clearRadius(
                                                ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "radius")
                                        ))))
                        .then(Commands.literal("clearall")
                                .requires(src -> src.hasPermission(2))
                                .executes(ctx -> clearAll(ctx.getSource())))
                        .then(Commands.literal("status")
                                .requires(src -> src.hasPermission(2))
                                .executes(ctx -> showStatus(ctx.getSource())))
                        .then(Commands.literal("reload")
                                .requires(src -> src.hasPermission(2))
                                .executes(ctx -> reloadCaches(ctx.getSource())))
        );
    }

    private static int inspectLookedAt(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("create_big_cannons_security_breach.command.player_only"));
            return 0;
        }

        Vec3 eyePos = player.getEyePosition();
        Vec3 endPos = eyePos.add(player.getLookAngle().scale(16.0));
        BlockHitResult hit = player.level().clip(new ClipContext(
                eyePos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
        ));
        if (hit.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.translatable("create_big_cannons_security_breach.command.no_block"));
            return 0;
        }

        return inspectPos(source, hit.getBlockPos());
    }

    private static int inspectPos(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);

        if (!SecurityCraftCompat.isTargetableBlock(block)) {
            source.sendFailure(Component.translatable(
                    "create_big_cannons_security_breach.command.not_targetable",
                    id.toString()
            ));
            return 0;
        }

        CannonDamageData data = CannonDamageData.get(level);
        float maxHp = (float) Math.max(1.0, Config.getBlockHp(block));
        float damage = data.getDamage(pos);
        float currentHp = Math.max(0.0f, maxHp - damage);
        int pct = Math.max(0, Math.min(100, Math.round(currentHp / maxHp * 100.0f)));

        Config.Group group = Config.matchGroup(id.getPath());
        String groupName = group != null ? group.key() : "fallback";
        String owner = SecurityCraftCompat.getOwnerName(level, pos);

        source.sendSuccess(() -> Component.translatable(
                "create_big_cannons_security_breach.command.inspect_result",
                id.toString(),
                pos.toShortString(),
                Math.round(currentHp),
                Math.round(maxHp),
                pct,
                groupName,
                owner != null ? owner : "-"
        ).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int clearRadius(CommandSourceStack source, int radius) {
        ServerLevel level = source.getLevel();
        BlockPos center = BlockPos.containing(source.getPosition());
        double radiusSq = (double) radius * (double) radius;

        CannonDamageData data = CannonDamageData.get(level);
        int cleared = 0;
        for (Map.Entry<BlockPos, Float> entry : data.copyEntries().entrySet()) {
            BlockPos pos = entry.getKey();
            if (pos.distSqr(center) <= radiusSq) {
                CannonDamageService.clearDamageAndSync(level, pos, data);
                cleared++;
            }
        }

        final int count = cleared;
        source.sendSuccess(() -> Component.translatable(
                "create_big_cannons_security_breach.command.cleared_radius",
                count,
                radius
        ).withStyle(ChatFormatting.GREEN), true);
        return count;
    }

    private static int clearAll(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        CannonDamageData data = CannonDamageData.get(level);
        int cleared = 0;
        for (BlockPos pos : data.copyEntries().keySet()) {
            CannonDamageService.clearDamageAndSync(level, pos, data);
            cleared++;
        }

        final int count = cleared;
        source.sendSuccess(() -> Component.translatable(
                "create_big_cannons_security_breach.command.cleared_all",
                count,
                level.dimension().location().toString()
        ).withStyle(ChatFormatting.GREEN), true);
        return count;
    }

    private static int showStatus(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        CannonDamageData data = CannonDamageData.get(level);
        source.sendSuccess(() -> Component.translatable(
                "create_big_cannons_security_breach.command.status",
                Config.enabled,
                data.size(),
                Config.damageDecayEnabled,
                Config.repairEnabled,
                Config.dropBlocksOnBreak
        ).withStyle(ChatFormatting.YELLOW), false);
        return 1;
    }

    private static int reloadCaches(CommandSourceStack source) {
        Config.bake();
        source.sendSuccess(() -> Component.translatable(
                "create_big_cannons_security_breach.command.reloaded"
        ).withStyle(ChatFormatting.GREEN), true);
        return 1;
    }
}
