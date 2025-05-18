package com.landfall.slipboot;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlipBoot.MODID);
    public static final RegistryObject<Item> REPAIR_ITEM = ITEMS.register("repair_item", () -> new Item(new Item.Properties().stacksTo(16)));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("repairable_block", () -> new BlockItem(ModBlocks.BREAKABLE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> WARP_ITEM = ITEMS.register("warp", () -> new BlockItem(ModBlocks.WARP.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
