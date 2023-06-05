package cc.craftospc.ASICraft.blockentity;

import cc.craftospc.ASICraft.ASICraft;
import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.algorithms.ExpansionBusPeripheral;
import cc.craftospc.ASICraft.algorithms.IAlgorithm;
import cc.craftospc.ASICraft.item.AcceleratorCardItem;
import cc.craftospc.ASICraft.menu.ExpansionBusMenu;
import cc.craftospc.ASICraft.util.ContainerBlockEntity;
import cc.craftospc.ASICraft.util.Registry;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExpansionBusBlockEntity extends ContainerBlockEntity implements WorldlyContainer {
    public int tier;
    public IAlgorithm[] algorithms;
    public boolean[] forward;
    public ExpansionBusPeripheral peripheral = new ExpansionBusPeripheral(this);
    public ContainerData forwardData = new ContainerData() {
        @Override
        public int get(int i) {
            return i < forward.length ? (forward[i] ? 8 : 0) : 0;
        }

        @Override
        public void set(int i, int j) {
            if (i >= forward.length - 1) return;
            forward[i] = j != 0;
            setChanged();
        }

        @Override
        public int getCount() {
            return forward.length;
        }
    };

    public ExpansionBusBlockEntity(int tier, BlockPos blockPos, BlockState blockState) {
        super(Registry.BlockEntities.EXPANSION_BUS.get(), blockPos, blockState);
        this.tier = tier;
        algorithms = new IAlgorithm[getContainerSize()];
        forward = new boolean[getContainerSize()];
        clearContent();
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new ExpansionBusMenu(tier, i, inventory, this, this.forwardData);
    }

    @Override
    public int getContainerSize() {
        return switch (tier) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 6;
            default -> 0;
        };
    }

    @Override
    public boolean isEmpty() {
        for (IAlgorithm a : algorithms) if (a != null) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        if (i >= 0 && i < algorithms.length && algorithms[i] != null) {
            ItemStack stack = new ItemStack(Registry.Items.ACCELERATOR_CARD.get());
            AcceleratorCardItem.setAlgorithm(stack, algorithms[i].getType());
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        if (i >= 0 && i < algorithms.length && algorithms[i] != null) {
            ItemStack stack = new ItemStack(Registry.Items.ACCELERATOR_CARD.get());
            AcceleratorCardItem.setAlgorithm(stack, algorithms[i].getType());
            algorithms[i] = null;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return getItem(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        if (i >= 0 && i < algorithms.length && algorithms[i] == null && itemStack.is(Registry.Items.ACCELERATOR_CARD.get())) {
            ASICraft.LOG.info(AcceleratorCardItem.getAlgorithm(itemStack));
            algorithms[i] = AlgorithmRegistry.createAlgorithm(AcceleratorCardItem.getAlgorithm(itemStack));
            itemStack.setCount(itemStack.getCount()-1);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return itemStack.is(Registry.Items.ACCELERATOR_CARD.get());
    }

    @Override
    public void clearContent() {
        Arrays.fill(algorithms, null);
        Arrays.fill(forward, false);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return switch (tier) {
            case 1 -> new int[] {0};
            case 2 -> new int[] {0, 1, 2};
            case 3 -> new int[] {0, 1, 2, 3, 4, 5};
            default -> new int[0];
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return false;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        ListTag cardList = (ListTag)compoundTag.get("Cards");
        if (cardList != null) for (int i = 0; i < cardList.size() && i < algorithms.length; i++) algorithms[i] = AlgorithmRegistry.createAlgorithm(cardList.getString(i));
        byte fwd = compoundTag.getByte("Forwarding");
        for (int i = 0; i < forward.length - 1; i++) forward[i] = (fwd & (1 << i)) != 0;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        ListTag cardList = new ListTag();
        for (IAlgorithm algo : algorithms) cardList.add(StringTag.valueOf(algo == null ? "" : algo.getType()));
        compoundTag.put("Cards", cardList);
        byte fwd = 0;
        for (int i = 0; i < forward.length - 1; i++) fwd |= forward[i] ? 1 << i : 0;
        compoundTag.putByte("Forwarding", fwd);
        super.saveAdditional(compoundTag);
    }
}
