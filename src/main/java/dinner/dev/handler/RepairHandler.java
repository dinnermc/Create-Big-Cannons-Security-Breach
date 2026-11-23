package dinner.dev.handler;

import dinner.dev.Config;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.data.CannonDamageData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Allows players (owners by default) to manually repair damaged SecurityCraft blocks
 * by Shift + Right-Clicking with a configured repair item (e.g. Create Wrench or Iron Ingot).
 */
public class RepairHandler {

    private final Map<UUID, Long> lastRepairTick = new HashMap<>();

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        lastRepairTick.clear();
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!Config.enabled || !Config.repairEnabled) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !Config.isRepairItem(stack.getItem())) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!SecurityCraftCompat.isTargetableBlock(state.getBlock())) return;

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        CannonDamageData data = CannonDamageData.get(serverLevel);
        if (!data.hasDamage(pos)) return;

        if (Config.repairOwnerOnly && !SecurityCraftCompat.isOwnedBy(serverLevel, pos, player)) {
            player.displayClientMessage(
                    Component.translatable("create_big_cannons_security_breach.message.repair_not_owner")
                            .withStyle(ChatFormatting.RED),
                    true
            );
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            return;
        }

        long now = serverLevel.getGameTime();
        Long last = lastRepairTick.get(player.getUUID());
        if (last != null && now - last < Config.repairCooldownTicks) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
            return;
        }
        lastRepairTick.put(player.getUUID(), now);

        float maxHp = (float) Math.max(1.0, Config.getBlockHp(state.getBlock()));
        float remainingDamage = CannonDamageService.repairBlock(serverLevel, pos, state, (float) Config.repairAmountPerClick);
        if (remainingDamage < 0.0f) return;

        // Consume item or durability if configured (skipped in Creative)
        if (Config.consumeRepairItem && !player.isCreative()) {
            if (stack.isDamageableItem()) {
                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            } else {
                stack.shrink(1);
            }
        }

        // Visual and audio feedback
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        if (Config.impactSoundsEnabled) {
            serverLevel.playSound(null, cx, cy, cz,
                    remainingDamage <= 0.0f ? SoundEvents.ANVIL_USE : SoundEvents.IRON_GOLEM_REPAIR,
                    SoundSource.BLOCKS, 0.7f, remainingDamage <= 0.0f ? 1.25f : 1.05f);
        }
        if (Config.impactParticlesEnabled) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, cx, cy, cz, 8, 0.45, 0.45, 0.45, 0.05);
        }

        if (remainingDamage <= 0.0f) {
            player.displayClientMessage(
                    Component.translatable("create_big_cannons_security_breach.message.repaired_full")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
        } else {
            int currentHp = Math.max(0, Math.round(maxHp - remainingDamage));
            int maxHpInt = Math.round(maxHp);
            int pct = Math.max(0, Math.min(100, Math.round((maxHp - remainingDamage) / maxHp * 100.0f)));
            player.displayClientMessage(
                    Component.translatable("create_big_cannons_security_breach.message.repaired_partial", currentHp, maxHpInt, pct)
                            .withStyle(ChatFormatting.GOLD),
                    true
            );
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
