package world.landfall.slipboot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import world.landfall.slipboot.blocks.FakeTop;
import world.landfall.slipboot.blocks.RepairableBlock;
import world.landfall.slipboot.blocks.WarpBlock;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Slipboot.MODID);
    public static final DeferredBlock<Block> REPAIRABLE_BLOCK = BLOCKS.registerBlock("repairable_block", (registryName) -> new RepairableBlock(BlockBehaviour.Properties.of(),"minecraft:redstone"));
    public static final DeferredBlock<Block> WARP = BLOCKS.registerBlock("warp", (registryName) -> {
        System.out.println("I swear it registered the warp");
        return new WarpBlock(BlockBehaviour.Properties.of());
    });
    public static final DeferredBlock<Block> FAKE_TOP = BLOCKS.registerBlock("fake_top", (registryName) -> new FakeTop(BlockBehaviour.Properties.of()));
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
