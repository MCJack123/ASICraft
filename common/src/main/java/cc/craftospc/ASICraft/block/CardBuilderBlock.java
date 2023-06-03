package cc.craftospc.ASICraft.block;

import cc.craftospc.ASICraft.blockentity.CardBuilderBlockEntity;
import cc.craftospc.ASICraft.blockentity.MicrochipDesignerBlockEntity;
import cc.craftospc.ASICraft.util.ContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class CardBuilderBlock extends ContainerBlock {
    public CardBuilderBlock() {
        super(Properties.of(Material.METAL).strength(4.0f).requiresCorrectToolForDrops());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CardBuilderBlockEntity(blockPos, blockState);
    }
}
