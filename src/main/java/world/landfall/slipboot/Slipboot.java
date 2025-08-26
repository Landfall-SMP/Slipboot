package world.landfall.slipboot;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import world.landfall.slipboot.blocks.WarpBlock;
import world.landfall.slipboot.integration.BlueMapIntegration;
import world.landfall.slipboot.networking.LocationListPacket;

import java.util.Objects;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Slipboot.MODID)
public class Slipboot {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "slipboot";
    public static WarpLocations locationData;
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "slipboot" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a creative tab with the id "slipboot:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.slipboot")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.REPAIRABLE_BLOCK_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModItems.WARP_BLOCK_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Slipboot(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Slipboot) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Common setup");
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Setting up server");
        if (Config.doBluemapIntegration)
            BlueMapIntegration.init(event.getServer().getAllLevels());
        ModCommands.register(event.getServer().createCommandSourceStack().dispatcher());

        DimensionDataStorage dataStorage = Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD)).getDataStorage();
        SavedData.Factory<WarpLocations> factory = new SavedData.Factory<>(WarpLocations::create, WarpLocations::load);
        locationData = dataStorage.computeIfAbsent(factory, "warp_locations");
        WarpBlock.setLocationData(locationData);
    }
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        var checkRate = 100;
        if (Minecraft.getInstance().level.getServer() == null) return;
        if (Minecraft.getInstance().level.getServer().getTickCount() % checkRate == 0)
            PacketDistributor.sendToServer(new LocationListPacket(Minecraft.getInstance().player.getName().getString()));

    }

}
