package dinner.dev.client;

import dinner.dev.CreateBigCannonsSecurityBreach;
import dinner.dev.network.ClientDamageCache;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Client-only entrypoint keeping client classes isolated from dedicated servers.
 */
@Mod(value = CreateBigCannonsSecurityBreach.MODID, dist = Dist.CLIENT)
public class CreateBigCannonsSecurityBreachClient {

    private static final ResourceLocation GOGGLES_HUD_ID =
            ResourceLocation.fromNamespaceAndPath(CreateBigCannonsSecurityBreach.MODID, "goggles_integrity_hud");

    public CreateBigCannonsSecurityBreachClient(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onRegisterGuiLayers);

        NeoForge.EVENT_BUS.addListener(this::onClientLogout);
        NeoForge.EVENT_BUS.addListener(this::onClientLevelUnload);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, GOGGLES_HUD_ID, GogglesHudOverlay.INSTANCE);
    }

    private void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientDamageCache.clear();
    }

    private void onClientLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            ClientDamageCache.clear();
        }
    }
}
