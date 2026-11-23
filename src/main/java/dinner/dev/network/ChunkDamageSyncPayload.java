package dinner.dev.network;

import dinner.dev.CreateBigCannonsSecurityBreach;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * S2C packet sent when a player begins watching a chunk containing damaged blocks,
 * synchronizing their client cache for the Create Goggles HUD.
 */
public record ChunkDamageSyncPayload(List<Entry> entries) implements CustomPacketPayload {
    public record Entry(BlockPos pos, float damage) {
        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Entry::pos,
                ByteBufCodecs.FLOAT, Entry::damage,
                Entry::new
        );
    }

    public static final CustomPacketPayload.Type<ChunkDamageSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    CreateBigCannonsSecurityBreach.MODID, "chunk_damage_sync"));

    public static final StreamCodec<ByteBuf, ChunkDamageSyncPayload> STREAM_CODEC = StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ChunkDamageSyncPayload::entries,
            ChunkDamageSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
