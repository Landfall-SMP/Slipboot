package world.landfall.slipboot;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(Slipboot.MODID);
    public static DeferredItem<Item> REPAIRABLE_BLOCK_ITEM = ITEMS.registerItem("repairable_block",(registryName) -> new BlockItem(ModBlocks.REPAIRABLE_BLOCK.get(), new Item.Properties()));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
