package cc.craftospc.ASICraft.algorithms;

import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class ExpansionBusPeripheral implements IPeripheral {
    ExpansionBusBlockEntity blockEntity;

    public ExpansionBusPeripheral(ExpansionBusBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void attach(IComputerAccess computer) {
        IPeripheral.super.attach(computer);
    }

    @Override
    public void detach(IComputerAccess computer) {
        IPeripheral.super.detach(computer);
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof ExpansionBusBlockEntity && blockEntity.getBlockPos().equals(((ExpansionBusBlockEntity) other).getBlockPos());
    }

    @Override
    public String getType() {
        return "asicraft:expansion_bus";
    }

    @LuaFunction
    public final String getCardType(int slot) {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            return blockEntity.algorithms[slot-1].getType();
        else return null;
    }

    @LuaFunction
    public final String[][] getAvailableProperties(int slot) {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            return blockEntity.algorithms[slot-1].getAvailableProperties();
        else return null;
    }

    @LuaFunction
    public final Object getProperty(int slot, String name) throws LuaException {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            return blockEntity.algorithms[slot-1].getProperty(name);
        else throw new LuaException("Slot does not have a card inserted");
    }

    @LuaFunction
    public final void setProperty(IArguments args) throws LuaException {
        int slot = args.getInt(0);
        String name = args.getString(1);
        if (args.count() < 3) throw new LuaException("bad argument #3 (missing value)");
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            blockEntity.algorithms[slot-1].setProperty(name, args);
        else throw new LuaException("Slot does not have a card inserted");
    }

    @LuaFunction
    public final void input(int slot, ByteBuffer data) throws LuaException {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null) {
            byte[] bytes = new byte[data.capacity()];
            data.get(bytes);
            blockEntity.algorithms[slot - 1].input(bytes);
        }
        else throw new LuaException("Slot does not have a card inserted");
    }

    @LuaFunction
    public final void finish(IComputerAccess computer, int slot) throws LuaException {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            blockEntity.algorithms[slot-1].finish(computer, slot);
        else throw new LuaException("Slot does not have a card inserted");
    }
}
