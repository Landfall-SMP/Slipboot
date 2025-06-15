package world.landfall.slipboot.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import world.landfall.slipboot.Slipboot;

import java.util.Set;

public record WarpPacket(String playerName, Vector3f pos, String dimension) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WarpPacket> TYPE = new CustomPacketPayload.Type<WarpPacket>(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "warp_packet"));

    public static final StreamCodec<ByteBuf, WarpPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            WarpPacket::playerName,
            ByteBufCodecs.VECTOR3F,
            WarpPacket::pos,
            ByteBufCodecs.STRING_UTF8,
            WarpPacket::dimension,
            WarpPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playBidirectional(
                    WarpPacket.TYPE,
                    WarpPacket.STREAM_CODEC,
                    new IPayloadHandler<WarpPacket>() {
                        @Override
                        public void handle(WarpPacket warpPacket, IPayloadContext iPayloadContext) {
                            System.out.println("WOWWOW");
                            iPayloadContext.player().getServer().getAllLevels().forEach((level) -> {
                                if (level.dimension().location().equals(ResourceLocation.parse(warpPacket.dimension))) {

                                    iPayloadContext.player().teleportTo(level, warpPacket.pos.x, warpPacket.pos.y, warpPacket.pos.z, Set.of(), 0, 0);
                                }
                            });
                        }
                    }
            );
        }
    }
}
