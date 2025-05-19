package com.landfall.slipboot.blocks;

import javax.annotation.Nullable;

import com.landfall.slipboot.ModBlockEntities;
import com.landfall.slipboot.ui.WarpMenu;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class WarpBlockEntity extends BlockEntity implements MenuProvider {
    public WarpBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.WARP_ENTITY.get(), pos, state);
    }

    public WarpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        //TODO Auto-generated constructor stub
    }
    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // TODO Auto-generated method stub
        return new WarpMenu(containerId, playerInventory, this);
    }
    @Override
    public Component getDisplayName() {
        // TODO Auto-generated method stub
        return Component.translatable("menu.warp");
    }
    public static class WarpRenderer implements BlockEntityRenderer {
        public WarpRenderer(BlockEntityRendererProvider.Context context) {

        }
        @Override
        public void render(BlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource bufferSource,
                int packedLight, int packedOverlay) {
            // TODO Auto-generated method stub
            // BlockState state = entity.getBlockState();
            // Level level = entity.getLevel();
            // BlockPos pos = entity.getBlockPos();

            // BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            // RandomSource randomSource = RandomSource.create();
            // stack.pushPose();
            // if (level != null) {
            //     // This ensures we get the correct light value from the world
            //     packedLight = LevelRenderer.getLightColor(level, pos);
            // }
            // BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            // for (RenderType renderType : model.getRenderTypes(state, randomSource, ModelData.EMPTY)) {
            //     dispatcher.getModelRenderer().renderModel(
            //         stack.last(),
            //         bufferSource.getBuffer(RenderType.solid()),
            //         state,
            //         model,
                    
            //         // RGB values (typically 1.0f each unless you want tinting)
            //         1.0F, 1.0F, 1.0F,
                    
            //         packedLight,
            //         packedOverlay,
            //         ModelData.EMPTY,
            //         renderType
            //     );
            // }
            // stack.popPose();
        }

    }
}
