package dinner.dev.network;

import dinner.dev.CreateBigCannonsSecurityBreach;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C packet notifying tracking clients of a single block's updated damage value
 * (0.0f means the block is intact or destroyed and should be removed from the client cache).
 */
public record BlockDamageSyncPayload(BlockPos pos, float damage) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BlockDamageSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    CreateBigCannonsSecurityBreach.MODID, "block_damage_sync"));

    public static final StreamCodec<ByteBuf, BlockDamageSyncPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockDamageSyncPayload::pos,
            ByteBufCodecs.FLOAT, BlockDamageSyncPayload::damage,
            BlockDamageSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
