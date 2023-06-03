package cc.craftospc.ASICraft.block;

import cc.craftospc.ASICraft.blockentity.CardBuilderBlockEntity;
import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import cc.craftospc.ASICraft.util.ContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class ExpansionBusBlock extends ContainerBlock {
    public int tier;
    public ExpansionBusBlock(int tier) {
        super(Properties.of(Material.METAL).strength(4.0f).requiresCorrectToolForDrops());
        this.tier = tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ExpansionBusBlockEntity(tier, blockPos, blockState);
    }
}
