package com.landfall.slipboot.ui;

import java.util.ResourceBundle;

import com.landfall.slipboot.SlipBoot;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class WarpScreen extends AbstractContainerScreen<WarpMenu> {
    public static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SlipBoot.MODID, "textures/gui/warp/warp_gui");
    public WarpScreen(AbstractContainerMenu menu, Inventory inventory, Component name) {
        super((WarpMenu) menu, inventory, name);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
    
}
