package world.landfall.slipboot.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import world.landfall.slipboot.Slipboot;
import world.landfall.slipboot.Constants;
import world.landfall.slipboot.client.ClientWarpData;

@MethodsReturnNonnullByDefault
public record UpdateWarpLocationPacket(
    UpdateType updateType,
    int id,
    String name,
    BlockPos pos,
    boolean active,
    String level
) implements CustomPacketPayload {
    
    public enum UpdateType {
        ADD,
        REMOVE,
        UPDATE_NAME,
        UPDATE_ACTIVE
    }
    
    public static final CustomPacketPayload.Type<UpdateWarpLocationPacket> TYPE = 
        new CustomPacketPayload.Type<>(Constants.modResource("update_warp_location"));
    
    public static final StreamCodec<ByteBuf, UpdateType> UPDATE_TYPE_CODEC = new StreamCodec<>() {
        @Override
        public UpdateType decode(ByteBuf buffer) {
            byte ordinal = buffer.readByte();
            UpdateType[] values = UpdateType.values();
            if (ordinal < 0 || ordinal >= values.length) {
                Slipboot.LOGGER.error("Invalid UpdateType ordinal received: {}", ordinal);
                return UpdateType.REMOVE; // Safe default
            }
            return values[ordinal];
        }

        @Override
        public void encode(ByteBuf buffer, UpdateType value) {
            buffer.writeByte(value.ordinal());
        }
    };
    
    public static final StreamCodec<ByteBuf, UpdateWarpLocationPacket> STREAM_CODEC = StreamCodec.composite(
        UPDATE_TYPE_CODEC, UpdateWarpLocationPacket::updateType,
        ByteBufCodecs.VAR_INT, UpdateWarpLocationPacket::id,
        ByteBufCodecs.STRING_UTF8, UpdateWarpLocationPacket::name,
        BlockPos.STREAM_CODEC, UpdateWarpLocationPacket::pos,
        ByteBufCodecs.BOOL, UpdateWarpLocationPacket::active,
        ByteBufCodecs.STRING_UTF8, UpdateWarpLocationPacket::level,
        UpdateWarpLocationPacket::new
    );
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static UpdateWarpLocationPacket createAdd(int id, String name, BlockPos pos, boolean active, String level) {
        return new UpdateWarpLocationPacket(UpdateType.ADD, id, name, pos, active, level);
    }
    
    public static UpdateWarpLocationPacket createRemove(int id) {
        return new UpdateWarpLocationPacket(UpdateType.REMOVE, id, "", BlockPos.ZERO, false, "");
    }
    
    public static UpdateWarpLocationPacket createUpdateName(int id, String name) {
        return new UpdateWarpLocationPacket(UpdateType.UPDATE_NAME, id, name, BlockPos.ZERO, false, "");
    }
    
    public static UpdateWarpLocationPacket createUpdateActive(int id, boolean active) {
        return new UpdateWarpLocationPacket(UpdateType.UPDATE_ACTIVE, id, "", BlockPos.ZERO, active, "");
    }
    
    @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class PacketHandler {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            
            // Server to Client
            registrar.playToClient(
                UpdateWarpLocationPacket.TYPE,
                UpdateWarpLocationPacket.STREAM_CODEC,
                    (packet, context) -> context.enqueueWork(() -> {
                        // Handle on client side
                        if (context.player().level().isClientSide()) {
                            switch (packet.updateType()) {
                                case ADD:
                                    ClientWarpData.addLocation(packet.id(), packet.name(), packet.pos(), packet.active(), packet.level());
                                    break;
                                case REMOVE:
                                    ClientWarpData.removeLocation(packet.id());
                                    break;
                                case UPDATE_NAME:
                                    ClientWarpData.updateName(packet.id(), packet.name());
                                    break;
                                case UPDATE_ACTIVE:
                                    ClientWarpData.updateActive(packet.id(), packet.active());
                                    break;
                            }
                            Slipboot.LOGGER.debug("Processed {} update for warp location {}", packet.updateType(), packet.id());
                        }
                    })
            );
        }
    }
}