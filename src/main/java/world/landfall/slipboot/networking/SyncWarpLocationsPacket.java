package world.landfall.slipboot.networking;

import io.netty.buffer.ByteBuf;
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
import world.landfall.slipboot.WarpLocations;
import world.landfall.slipboot.client.ClientWarpData;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("override.return")
public record SyncWarpLocationsPacket(List<WarpLocationData> locations) implements CustomPacketPayload {
    
    public record WarpLocationData(
        int id,
        String name,
        BlockPos pos,
        boolean active,
        String level
    ) {
        public static final StreamCodec<ByteBuf, WarpLocationData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, WarpLocationData::id,
            ByteBufCodecs.STRING_UTF8, WarpLocationData::name,
            BlockPos.STREAM_CODEC, WarpLocationData::pos,
            ByteBufCodecs.BOOL, WarpLocationData::active,
            ByteBufCodecs.STRING_UTF8, WarpLocationData::level,
            WarpLocationData::new
        );
    }
    
    public static final CustomPacketPayload.Type<SyncWarpLocationsPacket> TYPE = 
        new CustomPacketPayload.Type<>(Constants.modResource("sync_warp_locations"));
    
    public static final StreamCodec<ByteBuf, SyncWarpLocationsPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, WarpLocationData.STREAM_CODEC),
        SyncWarpLocationsPacket::locations,
        SyncWarpLocationsPacket::new
    );
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static SyncWarpLocationsPacket create(WarpLocations serverData) {
        List<WarpLocationData> locations = new ArrayList<>();
        if (serverData != null && serverData.getLocations() != null) {
            serverData.getLocations().forEach((id, loc) -> {
                locations.add(new WarpLocationData(
                    loc.id,
                    loc.name,
                    loc.pos,
                    loc.active,
                    loc.level
                ));
            });
        }
        return new SyncWarpLocationsPacket(locations);
    }
    
    @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class PacketHandler {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            
            // Server to Client
            registrar.playToClient(
                SyncWarpLocationsPacket.TYPE,
                SyncWarpLocationsPacket.STREAM_CODEC,
                    (packet, context) -> context.enqueueWork(() -> {
                        // Handle on client side
                        if (context.player().level().isClientSide()) {
                            ClientWarpData.updateAllLocations(packet.locations());
                            Slipboot.LOGGER.info("Received sync packet with {} warp locations", packet.locations().size());
                        }
                    })
            );
        }
    }
}