package world.landfall.slipboot.ui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import world.landfall.slipboot.ModBlocks;
import world.landfall.slipboot.ModMenuTypes;

public class WarpMenu extends AbstractContainerMenu {
    public WarpMenu(int containerId, Inventory inv) {
        this(containerId, inv, new SimpleContainerData(3));
        //super(ModMenuTypes.WARP_MENU.get(), containerId);
    }
    public WarpMenu(int containerId, Inventory inv, ContainerData data) {
        super(ModMenuTypes.WARP_MENU.get(), containerId);
        checkContainerDataCount(data, 3);
        this.addDataSlots(data);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.NULL, player, ModBlocks.WARP.get());
    }
}
