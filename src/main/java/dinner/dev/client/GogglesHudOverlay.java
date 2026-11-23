package dinner.dev.client;

import dinner.dev.Config;
import dinner.dev.compat.CreateCompat;
import dinner.dev.compat.SecurityCraftCompat;
import dinner.dev.network.ClientDamageCache;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Engineer's Goggles HUD overlay displaying structural integrity,
 * HP progress bar, material tier, owner, and repair hint when looking at
 * a targetable SecurityCraft block.
 */
public final class GogglesHudOverlay implements LayeredDraw.Layer {

    public static final GogglesHudOverlay INSTANCE = new GogglesHudOverlay();

    private static final int BG_COLOR = 0xE8100010;
    private static final int BORDER_TOP = 0xA05060FF;
    private static final int BORDER_BOTTOM = 0xA0283080;
    private static final int BAR_BG = 0xFF222228;
    private static final int BAR_BORDER = 0xFF444455;

    private GogglesHudOverlay() {}

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!Config.enabled || !Config.gogglesHudEnabled) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.screen != null) return;

        Player player = mc.player;
        Level level = mc.level;
        if (player == null || level == null) return;
        if (!CreateCompat.isWearingGoggles(player)) return;

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = blockHit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!SecurityCraftCompat.isTargetableBlock(block)) return;

        float maxHp = (float) Math.max(1.0, Config.getBlockHp(block));
        float damage = ClientDamageCache.getDamage(pos);
        float currentHp = Math.max(0.0f, maxHp - damage);
        float ratio = Math.max(0.0f, Math.min(1.0f, currentHp / maxHp));
        int pct = Math.round(ratio * 100.0f);

        Font font = mc.font;
        List<Component> lines = new ArrayList<>();

        // Title line: block name
        lines.add(block.getName().copy().withStyle(ChatFormatting.WHITE));

        // Integrity HP line
        ChatFormatting hpColor = colorForRatio(ratio);
        lines.add(Component.translatable(
                "create_big_cannons_security_breach.hud.integrity",
                Math.round(currentHp),
                Math.round(maxHp),
                pct
        ).withStyle(hpColor));

        // Material group line
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        Config.Group group = Config.matchGroup(blockId.getPath());
        String groupKey = group != null
                ? "create_big_cannons_security_breach.material." + group.key()
                : "create_big_cannons_security_breach.material.fallback";
        lines.add(Component.translatable(
                "create_big_cannons_security_breach.hud.material",
                Component.translatable(groupKey)
        ).withStyle(ChatFormatting.GRAY));

        // Optional owner line (if synced by SecurityCraft)
        String ownerName = SecurityCraftCompat.getOwnerName(level, pos);
        if (ownerName != null) {
            lines.add(Component.translatable(
                    "create_big_cannons_security_breach.hud.owner",
                    ownerName
            ).withStyle(ChatFormatting.DARK_GRAY));
        }

        // Repair hint when damaged
        if (damage > 0.0f && Config.repairEnabled) {
            lines.add(Component.translatable("create_big_cannons_security_breach.hud.repair_hint")
                    .withStyle(ChatFormatting.YELLOW));
        }

        // Compute panel geometry
        int minBarWidth = 110;
        int maxTextWidth = minBarWidth;
        for (Component line : lines) {
            maxTextWidth = Math.max(maxTextWidth, font.width(line));
        }

        int padding = 6;
        int lineHeight = font.lineHeight + 2;
        int barHeight = 5;
        int barGap = 4;

        int panelWidth = maxTextWidth + padding * 2;
        int panelHeight = padding * 2 + lines.size() * lineHeight + barHeight + barGap;

        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        int x = screenWidth / 2 + 22;
        int y = screenHeight / 2 - panelHeight / 2;

        // Clamp inside screen bounds
        if (x + panelWidth > screenWidth - 6) {
            x = screenWidth - panelWidth - 6;
        }
        y = Math.max(6, Math.min(screenHeight - panelHeight - 6, y));

        // Render block icon to the left of the panel (Create Goggles signature look)
        ItemStack icon = block.asItem().getDefaultInstance();
        if (!icon.isEmpty()) {
            graphics.renderItem(icon, x - 20, y + 4);
        }

        // Draw Create-style tooltip background & gradient border
        graphics.fill(x - 1, y, x + panelWidth + 1, y + panelHeight, BG_COLOR);
        graphics.fill(x, y - 1, x + panelWidth, y + panelHeight + 1, BG_COLOR);
        graphics.fillGradient(x, y, x + 1, y + panelHeight, BORDER_TOP, BORDER_BOTTOM);
        graphics.fillGradient(x + panelWidth - 1, y, x + panelWidth, y + panelHeight, BORDER_TOP, BORDER_BOTTOM);
        graphics.fill(x, y, x + panelWidth, y + 1, BORDER_TOP);
        graphics.fill(x, y + panelHeight - 1, x + panelWidth, y + panelHeight, BORDER_BOTTOM);

        // Draw text lines with progress bar right under the HP line (index 1)
        int textX = x + padding;
        int cursorY = y + padding;

        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(font, lines.get(i), textX, cursorY, 0xFFFFFF, true);
            cursorY += lineHeight;

            if (i == 1) {
                int barWidth = panelWidth - padding * 2;
                int filled = Math.round(barWidth * ratio);
                int barColor = argbForRatio(ratio);

                graphics.fill(textX - 1, cursorY - 1, textX + barWidth + 1, cursorY + barHeight + 1, BAR_BORDER);
                graphics.fill(textX, cursorY, textX + barWidth, cursorY + barHeight, BAR_BG);
                if (filled > 0) {
                    graphics.fill(textX, cursorY, textX + filled, cursorY + barHeight, barColor);
                }
                cursorY += barHeight + barGap;
            }
        }
    }

    private static ChatFormatting colorForRatio(float ratio) {
        if (ratio >= 0.75f) return ChatFormatting.GREEN;
        if (ratio >= 0.40f) return ChatFormatting.YELLOW;
        if (ratio >= 0.15f) return ChatFormatting.GOLD;
        return ChatFormatting.RED;
    }

    private static int argbForRatio(float ratio) {
        if (ratio >= 0.75f) return 0xFF44D060;
        if (ratio >= 0.40f) return 0xFFE6C832;
        if (ratio >= 0.15f) return 0xFFF08020;
        return 0xFFE03838;
    }
}
