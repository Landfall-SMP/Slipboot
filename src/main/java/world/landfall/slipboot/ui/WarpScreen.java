package world.landfall.slipboot.ui;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;
import world.landfall.slipboot.client.ClientWarpData;
import world.landfall.slipboot.networking.WarpPacket;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
        static final int GRAY_TEXT_COLOR = 0xAAAAAA;
        static final int ACTIVE_CHARACTER = 0x00FF00;
        static final int SWITCH_BUTTON = 0x66CCFF;
        static final int DELETE_BUTTON = 0xFF0000;
        static final int CREATE_BUTTON = 0x00FF00;
    }
    private final Player player;
    private final BlockPos pos;
    private Map<Integer, WarpLocations.WarpLocation> locations;
    private LocationListWidget locationListWidget;
    public WarpScreen(BlockPos pos, Player player) {
        super(Component.translatable("screen.slipboot.warp"));
        this.minecraft = Minecraft.getInstance();
        this.pos = pos;
        this.player = player;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Remove the client-side check - render methods only run on client
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // Render background if needed in the future
    }


    @Override
    protected void init() {
        super.init();
        
        // Use client-side warp data
        locations = ClientWarpData.getLocations();
        if (locations.isEmpty()) {
            Slipboot.LOGGER.warn("WarpScreen: No warp locations available on client");
        }
        locationListWidget = new LocationListWidget(Minecraft.getInstance(), 300, 150, (this.width-300)/2, (this.height-200)/2, 20);
        this.addRenderableWidget(locationListWidget);
        
        if (locations != null) {
            for (Integer i : locations.keySet()) {
            WarpLocations.WarpLocation location = locations.get(i);
            if (!pos.equals(location.pos)) {
                    locationListWidget.addEntry(new LocationListWidget.Entry(location.id));
                }
            }
        }
        Button button = new Button.Builder(Component.translatable("gui.warp.button.warp"), new Button.OnPress() {
            @Override
            public void onPress(Button button) {

                for (WarpLocations.WarpLocation x : ClientWarpData.getLocations().values()) {
                    if (locationListWidget.getSelected() != null && x.id == locationListWidget.getSelected().locationID) {
                        if (locations != null && locations.get(locationListWidget.getSelected().locationID) != null &&
                            !locations.get(locationListWidget.getSelected().locationID).pos.equals(pos) && 
                            locations.get(locationListWidget.getSelected().locationID).active) {
                            var levelResourceLocation = ResourceLocation.parse(x.level);
                            var newPos = x.pos.above().above();

                            PacketDistributor.sendToServer(new WarpPacket(player.getName().getString(),new Vector3f(newPos.getX() + .5f, newPos.getY(), newPos.getZ() + .5f), levelResourceLocation.toString()));
                            onClose();
                        }
                    }
                }
            }
        }).build();
        button.setX((this.width-150)/2);
        button.setY((this.height-20)/2+70);
        this.addRenderableWidget(button);

    }
    private static class LocationListWidget extends ObjectSelectionList<LocationListWidget.Entry> {

        public LocationListWidget(Minecraft minecraft, int width, int height, int x, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
            this.setX(x);
        }

        public int addEntry(@NotNull Entry e) {
            return super.addEntry(e);

        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        }

        @Override
        protected void renderDecorations(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            super.renderDecorations(guiGraphics, mouseX, mouseY);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

        }
        protected static class Entry extends ObjectSelectionList.Entry<Entry> {
            public final int locationID;
            public Entry(int id) {
                super();
                locationID = id;
            }
            @Override
            public void render(GuiGraphics guiGraphics, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
                // Use client-side warp data
                WarpLocations.WarpLocation location = ClientWarpData.getLocation(locationID);
                
                if (location == null) {
                    // Fallback rendering if location data not available
                    guiGraphics.drawString(Minecraft.getInstance().font, "Warp #" + locationID, i2+2, i1+4, Colors.GRAY_TEXT_COLOR);
                    return;
                }
                
                guiGraphics.drawString(Minecraft.getInstance().font, location.name, i2+2, i1+4,
                        location.active ?
                        Colors.TEXT_COLOR :
                        Colors.GRAY_TEXT_COLOR
                );
            }

            @Override
            public Component getNarration() {
                return Component.literal("Womp womp");

            }
        }

    }
}
