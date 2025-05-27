package world.landfall.slipboot;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mojang.brigadier.arguments.IntegerArgumentType.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ModCommands {
    private static void sendSystemMessage(LocalPlayer player, String message) {
        if (player != null)
            player.sendSystemMessage(Component.literal(message));
    }
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        final LiteralCommandNode<CommandSourceStack> a = dispatcher.register(
                literal("slipboot")

                        .then(literal("rename").then(argument("id", integer()).then(argument("name", StringArgumentType.string())
                                .executes(c -> {
                                    boolean result = Slipboot.locationData.setName(getInteger(c, "id"), StringArgumentType.getString(c, "name"));
                                    sendSystemMessage(Minecraft.getInstance().player, result ?
                                            "Successfuly set name of warp" :
                                            "Could not find warp"
                                    );
                                    return result ? 1 : 0;
                                }))))
                        .then(literal("search").then(argument("name", StringArgumentType.string())
                                .executes(c -> {
                                    int id = Slipboot.locationData.search(StringArgumentType.getString(c, "name"));
                                    sendSystemMessage(Minecraft.getInstance().player, "Got id " + id);
                                    return 1;
                                })))
                        .executes(c -> {
                            sendSystemMessage(Minecraft.getInstance().player, "Command used without parameters");
                            return 0;
                        })

        );
    }
}
