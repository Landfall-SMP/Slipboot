package world.landfall.slipboot;

import com.ibm.icu.impl.ValidIdentifiers;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import world.landfall.slipboot.blocks.RepairableBlock;
import world.landfall.slipboot.blocks.RepairableBlockEntity;
import world.landfall.slipboot.blocks.WarpBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Slipboot.MODID);
    public static final Supplier<BlockEntityType<RepairableBlockEntity>> REPAIRABLE_BLOCK_ENTITY = BLOCKENTITIES.register("repairable_block",
            () -> BlockEntityType.Builder.of(RepairableBlockEntity::new, ModBlocks.REPAIRABLE_BLOCK.get()).build(null)
    );
    public static final Supplier<BlockEntityType<WarpBlockEntity>> WARP_BLOCK_ENTITY = BLOCKENTITIES.register("warp",
            () -> BlockEntityType.Builder.of(
                    WarpBlockEntity::new,
                    ModBlocks.WARP.get()
            ).build(null)
    );
    public static void register(IEventBus eventBus) {
        System.out.println("Registering ModBlockEntities");
        BLOCKENTITIES.register(eventBus);

    }
}
