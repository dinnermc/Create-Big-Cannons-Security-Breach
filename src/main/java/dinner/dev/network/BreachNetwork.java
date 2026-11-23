package dinner.dev.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BreachNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private BreachNetwork() {}

    public static void register(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION).optional();

        registrar.playToClient(
                BlockDamageSyncPayload.TYPE,
                BlockDamageSyncPayload.STREAM_CODEC,
                BreachNetwork::handleBlockDamageSync
        );

        registrar.playToClient(
                ChunkDamageSyncPayload.TYPE,
                ChunkDamageSyncPayload.STREAM_CODEC,
                BreachNetwork::handleChunkDamageSync
        );
    }

    private static void handleBlockDamageSync(BlockDamageSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientDamageCache.update(payload.pos(), payload.damage()));
    }

    private static void handleChunkDamageSync(ChunkDamageSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (ChunkDamageSyncPayload.Entry entry : payload.entries()) {
                ClientDamageCache.update(entry.pos(), entry.damage());
            }
        });
    }

    public static void syncBlockDamage(ServerLevel level, BlockPos pos, float damage) {
        PacketDistributor.sendToPlayersTrackingChunk(
                level,
                new ChunkPos(pos),
                new BlockDamageSyncPayload(pos.immutable(), Math.max(0.0f, damage))
        );
    }

    public static void syncChunkDamageToPlayer(ServerPlayer player, Map<BlockPos, Float> entries) {
        if (entries.isEmpty()) return;
        List<ChunkDamageSyncPayload.Entry> list = new ArrayList<>(entries.size());
        for (Map.Entry<BlockPos, Float> e : entries.entrySet()) {
            list.add(new ChunkDamageSyncPayload.Entry(e.getKey().immutable(), e.getValue()));
        }
        PacketDistributor.sendToPlayer(player, new ChunkDamageSyncPayload(list));
    }
}
