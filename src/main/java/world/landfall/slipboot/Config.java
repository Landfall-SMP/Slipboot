package world.landfall.slipboot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config  {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static ModConfigSpec.BooleanValue DO_BLUEMAP_INTEGRATION;

    public static boolean doBluemapIntegration = true;
    private static final Pair<Config, ModConfigSpec> PAIR = BUILDER.configure(Config::new);
    static final ModConfigSpec SPEC = PAIR.getRight();
    static final Config CONFIG = PAIR.getLeft();

    private Config(ModConfigSpec.Builder builder) {
        DO_BLUEMAP_INTEGRATION = builder.define("Show markers on Bluemap for warps",true);
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        doBluemapIntegration = DO_BLUEMAP_INTEGRATION.get();
    }
}
