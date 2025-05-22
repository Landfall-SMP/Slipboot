package world.landfall.slipboot.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class WarpScreen extends Screen {
    private static final class Layout {
        static final int BUTTON_HEIGHT = 20;
        static final int LIST_ITEM_HEIGHT = 25;
        static final int MAX_VISIBLE_ITEMS = 5;
        static final int CREATE_BUTTON_SIZE = 20;
        static final int LIST_WIDTH = 300;
        static final int LIST_HEIGHT = (MAX_VISIBLE_ITEMS + 1) * LIST_ITEM_HEIGHT;

        // Button positioning
        static final int BUTTON_Y_OFFSET = 2;
        static final int SWITCH_BUTTON_X_WITH_DELETE = 239;
        static final int SWITCH_BUTTON_X_WITHOUT_DELETE = 264;
        static final int DELETE_BUTTON_X = 264;

        // Scrollbar
        static final int SCROLLBAR_WIDTH = 8;
        static final int SCROLLBAR_MARGIN = 3;
        static final int MIN_THUMB_HEIGHT = 15;

        // Panel
        static final int PANEL_PADDING = 5;
        static final int TITLE_Y_OFFSET = 20;
        static final int NAME_Y_OFFSET = 6;
    }
    // UI Colors
    private static final class Colors {
        static final int PANEL_BACKGROUND = 0x80000000;
        static final int SCROLLBAR_TRACK = 0x80333333;
        static final int SCROLLBAR_THUMB = 0xFFBBBBBB;
        static final int TEXT_COLOR = 0xFFFFFF;
        static final int ACTIVE_CHARACTER = 0x00FF00;
        static final int SWITCH_BUTTON = 0x66CCFF;
        static final int DELETE_BUTTON = 0xFF0000;
        static final int CREATE_BUTTON = 0x00FF00;
    }
    private final Player player;
    private final BlockPos pos;
    public WarpScreen(BlockPos pos, Player player) {
        super(Component.translatable("screen.slipboot.warp"));
        this.minecraft = Minecraft.getInstance();
        this.pos = pos;
        this.player = player;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

    }
}
