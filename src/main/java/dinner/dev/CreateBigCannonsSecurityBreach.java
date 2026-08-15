package dinner.dev;

import com.mojang.logging.LogUtils;
import dinner.dev.handler.DamageDecayHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CreateBigCannonsSecurityBreach.MODID)
public class CreateBigCannonsSecurityBreach {
    public static final String MODID = "create_big_cannons_security_breach";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateBigCannonsSecurityBreach(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(Config::onLoad);

        NeoForge.EVENT_BUS.register(new DamageDecayHandler());

        if (FMLLoader.getDist().isClient()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        LOGGER.info("Create Big Cannons: Security Breach loaded - Rust siege mechanics enabled");
    }
}
