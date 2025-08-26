package world.landfall.slipboot.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.WarpLocations;

public record LocationListPacket(String playerName) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LocationListPacket> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "location_list_packet"));
    public static final StreamCodec<ByteBuf, LocationListPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            LocationListPacket::playerName,
            LocationListPacket::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playBidirectional(
                    LocationListPacket.TYPE,
                    LocationListPacket.STREAM_CODEC,
                    new IPayloadHandler<LocationListPacket>() {
                        @Override
                        public void handle(LocationListPacket locationListPacket, IPayloadContext iPayloadContext) {
                            iPayloadContext.reply(new LocationListReplyPacket(
                                    WarpLocations.serialize(Slipboot.locationData)
                            ));
                        }
                    }
            );
        }
    }
    public record LocationListReplyPacket(String serializedLocationData) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<LocationListReplyPacket> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Slipboot.MODID, "location_list_reply_packet"));
        public static final StreamCodec<ByteBuf, LocationListReplyPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                LocationListReplyPacket::serializedLocationData,
                LocationListReplyPacket::new
        );
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
        @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
        public static class ModEvents {
            @SubscribeEvent
            public static void register(final RegisterPayloadHandlersEvent event) {
                final PayloadRegistrar registrar = event.registrar("1");
                registrar.playToClient(
                        LocationListReplyPacket.TYPE,
                        LocationListReplyPacket.STREAM_CODEC,
                        new IPayloadHandler<LocationListReplyPacket>() {
                            @Override
                            public void handle(LocationListReplyPacket locationListReplyPacket, IPayloadContext iPayloadContext) {
                                Slipboot.LOGGER.info("Got location list from server");
                                Slipboot.LOGGER.info(locationListReplyPacket.serializedLocationData);
                                Slipboot.locationData = WarpLocations.unserialize(locationListReplyPacket.serializedLocationData);
                            }
                        }

                );
            }
        }
    }
}
