package world.landfall.slipboot;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import world.landfall.slipboot.blocks.WarpBlock;
import world.landfall.slipboot.networking.SyncWarpLocationsPacket;
import net.minecraft.server.level.ServerPlayer;

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
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.slipboot"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.WARP_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.WARP_BLOCK_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            })
            .build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Slipboot(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerScreens);
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

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    private void registerScreens(RegisterMenuScreensEvent event) {
        
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Common setup");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        //if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // Sync warp locations to client when player joins
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (locationData != null) {
                LOGGER.info("Syncing warp locations to player: {}", serverPlayer.getName().getString());
                PacketDistributor.sendToPlayer(serverPlayer, SyncWarpLocationsPacket.create(locationData));
            }
        }
    }
    
    // Note: Client disconnect cleanup is handled in ClientModEvents
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Setting up server");
        if (Config.doBluemapIntegration) {
            try {
                BlueMapIntegration.init(event.getServer().getAllLevels());
            } catch (NoClassDefFoundError | Exception e) {
                LOGGER.warn("BlueMap integration failed - BlueMap API not available or error occurred: " + e.getMessage());
            }
        }
        ModCommands.register(event.getServer().createCommandSourceStack().dispatcher());

        DimensionDataStorage dataStorage = Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD)).getDataStorage();
        SavedData.Factory<WarpLocations> factory = new SavedData.Factory<>(WarpLocations::create, WarpLocations::load);
        locationData = dataStorage.computeIfAbsent(factory, "warp_locations");
        WarpBlock.setLocationData(locationData);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Initialize client-side components
            LOGGER.info("Client setup - initializing client-side components");
            
            // Ensure client-side initialization is complete
            event.enqueueWork(() -> LOGGER.info("Client setup work enqueued - client initialization complete"));
        }
    }
    
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientDisconnect(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
            // Clear client-side warp data when disconnecting from server
            world.landfall.slipboot.client.ClientWarpData.clear();
            LOGGER.info("Cleared client warp data on disconnect");
        }
    }
}
