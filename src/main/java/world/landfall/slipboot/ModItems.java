package world.landfall.slipboot;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(Slipboot.MODID);
    public static DeferredItem<BlockItem> REPAIRABLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("repairable_block", ModBlocks.REPAIRABLE_BLOCK);
    public static DeferredItem<BlockItem> WARP_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("warp", ModBlocks.WARP);
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
