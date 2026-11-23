package dinner.dev;

import com.mojang.logging.LogUtils;
import dinner.dev.handler.BlockLifecycleHandler;
import dinner.dev.handler.BreachCommands;
import dinner.dev.handler.DamageDecayHandler;
import dinner.dev.handler.RepairHandler;
import dinner.dev.network.BreachNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CreateBigCannonsSecurityBreach.MODID)
public class CreateBigCannonsSecurityBreach {
    public static final String MODID = "create_big_cannons_security_breach";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateBigCannonsSecurityBreach(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        modEventBus.addListener(Config::onLoad);
        modEventBus.addListener(BreachNetwork::register);

        NeoForge.EVENT_BUS.register(new DamageDecayHandler());
        NeoForge.EVENT_BUS.register(new BlockLifecycleHandler());
        NeoForge.EVENT_BUS.register(new RepairHandler());
        NeoForge.EVENT_BUS.register(new BreachCommands());

        LOGGER.info("Create Big Cannons: Security Breach loaded - Rust siege mechanics enabled");
    }
}
