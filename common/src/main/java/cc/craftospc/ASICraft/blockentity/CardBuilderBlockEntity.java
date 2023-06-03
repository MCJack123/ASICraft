package cc.craftospc.ASICraft.blockentity;

import cc.craftospc.ASICraft.menu.CardBuilderMenu;
import cc.craftospc.ASICraft.menu.MicrochipDesignerMenu;
import cc.craftospc.ASICraft.util.ContainerBlockEntity;
import cc.craftospc.ASICraft.util.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CardBuilderBlockEntity extends ContainerBlockEntity {
    public CardBuilderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registry.BlockEntities.CARD_BUILDER.get(), blockPos, blockState);
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new CardBuilderMenu(i, inventory);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public void clearContent() {

    }
}
