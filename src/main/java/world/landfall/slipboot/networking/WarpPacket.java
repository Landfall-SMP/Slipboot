package world.landfall.slipboot.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import world.landfall.slipboot.Slipboot;

import java.util.Objects;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import world.landfall.slipboot.Constants;

public record WarpPacket(String playerName, Vector3f pos, String dimension) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WarpPacket> TYPE = new CustomPacketPayload.Type<>(Constants.modResource("warp_packet"));

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
    @EventBusSubscriber(modid = Slipboot.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class PacketHandler {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    WarpPacket.TYPE,
                    WarpPacket.STREAM_CODEC,
                    (warpPacket, iPayloadContext) -> iPayloadContext.enqueueWork(() -> {
                        Player player = iPayloadContext.player();
                        if (!(player instanceof ServerPlayer serverPlayer)) {
                            Slipboot.LOGGER.warn("Received warp packet from non-server player");
                            return;
                        }

                        // Validate player name matches sender
                        if (!serverPlayer.getName().getString().equals(warpPacket.playerName)) {
                            Slipboot.LOGGER.warn("Player {} tried to teleport as {}", serverPlayer.getName().getString(), warpPacket.playerName);
                            return;
                        }

                        // Validate dimension
                        ResourceLocation dimLocation;
                        try {
                            dimLocation = ResourceLocation.parse(warpPacket.dimension);
                        } catch (Exception e) {
                            Slipboot.LOGGER.warn("Invalid dimension in warp packet: {}", warpPacket.dimension);
                            return;
                        }

                        // Validate coordinates (prevent teleporting outside world border)
                        if (Math.abs(warpPacket.pos.x) > Constants.MAX_WORLD_COORDINATE ||
                            Math.abs(warpPacket.pos.z) > Constants.MAX_WORLD_COORDINATE ||
                            warpPacket.pos.y < Constants.MIN_Y_COORDINATE ||
                            warpPacket.pos.y > Constants.MAX_Y_COORDINATE) {
                            Slipboot.LOGGER.warn("Invalid coordinates in warp packet: {}", warpPacket.pos);
                            return;
                        }

                        // Check if player has permission (could add custom permission system here)
                        if (!serverPlayer.hasPermissions(Constants.DEFAULT_PLAYER_PERMISSION_LEVEL)) {
                            Slipboot.LOGGER.warn("Player {} lacks permission to warp", serverPlayer.getName().getString());
                            return;
                        }

                        // Perform teleportation
                        Objects.requireNonNull(serverPlayer.getServer()).getAllLevels().forEach((level) -> {
                            if (level.dimension().location().equals(dimLocation)) {
                                Slipboot.LOGGER.info("Teleporting {} to {} at {},{},{}",
                                    serverPlayer.getName().getString(),
                                    dimLocation,
                                    warpPacket.pos.x, warpPacket.pos.y, warpPacket.pos.z);
                                serverPlayer.teleportTo(level, warpPacket.pos.x, warpPacket.pos.y, warpPacket.pos.z, Set.of(), 0, 0);
                            }
                        });
                    })
            );
        }
    }
}
