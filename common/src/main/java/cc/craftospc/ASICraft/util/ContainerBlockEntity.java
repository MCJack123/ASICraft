package cc.craftospc.ASICraft.util;

import cc.craftospc.ASICraft.ASICraftExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class ContainerBlockEntity extends BaseContainerBlockEntity {
    protected ContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected final Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public boolean stillValid(Player player) {
        Level level = getLevel();
        BlockPos pos = getBlockPos();
        double range = Math.max(8.0, ASICraftExpectPlatform.getReachDistance(player));
        return player.isAlive() && player.getCommandSenderWorld() == level &&
                !isRemoved() && level.getBlockEntity(pos) == this &&
                player.distanceToSqr(Vec3.atCenterOf(pos)) <= range * range;
    }
}
