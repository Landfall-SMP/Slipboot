package com.landfall.slipboot;

import com.landfall.slipboot.ui.WarpMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(Registries.MENU, SlipBoot.MODID);
    public static final RegistryObject<MenuType<WarpMenu>> WARP_MENU = MENUS.register("warp_menu", () -> IForgeMenuType.create(WarpMenu::new));
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}