package world.landfall.slipboot;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    public static final ModConfigSpec.BooleanValue DO_BLUEMAP_INTEGRATION = BUILDER
            .comment("Show markers on Bluemap for warps")
            .define("doBluemapIntegration", true);
    
    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean doBluemapIntegration = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        doBluemapIntegration = DO_BLUEMAP_INTEGRATION.get();
    }
}
