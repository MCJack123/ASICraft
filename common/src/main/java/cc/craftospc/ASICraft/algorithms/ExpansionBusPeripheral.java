package cc.craftospc.ASICraft.algorithms;

import cc.craftospc.ASICraft.ASICraft;
import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        return other instanceof ExpansionBusPeripheral && blockEntity == ((ExpansionBusPeripheral) other).blockEntity;
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
    public final void input(IComputerAccess computer, IArguments args) throws LuaException {
        int slot = args.getInt(0);
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            blockEntity.algorithms[slot - 1].input(args, (res) -> {
                if (blockEntity.forward[slot - 1] && blockEntity.algorithms[slot] != null) {
                    try {
                        if (res instanceof ByteBuffer) res = new String(((ByteBuffer) res).array(), StandardCharsets.ISO_8859_1);
                        else if (res instanceof byte[]) res = new String((byte[]) res, StandardCharsets.ISO_8859_1);
                        this.input(computer, new ObjectArguments(slot + 1, res));
                    } catch (LuaException e) {
                        ASICraft.LOG.error("Could not process chained input", e);
                    }
                } else computer.queueEvent("asicraft.partial_result", computer.getAttachmentName(), slot, res);
            });
        else throw new LuaException("Slot does not have a card inserted");
    }

    @LuaFunction
    public final void finish(IComputerAccess computer, int slot) throws LuaException {
        if (slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null)
            blockEntity.algorithms[slot-1].finish((error, res) -> {
                try {
                    if (error == null) {
                        if (blockEntity.forward[slot - 1] && blockEntity.algorithms[slot] != null) {
                            try {
                                if (res instanceof ByteBuffer) res = new String(((ByteBuffer) res).array(), StandardCharsets.ISO_8859_1);
                                else if (res instanceof byte[]) res = new String((byte[]) res, StandardCharsets.ISO_8859_1);
                                blockEntity.algorithms[slot].input(new ObjectArguments(slot + 1, res), (ignored) -> {});
                                this.finish(computer, slot + 1);
                            } catch (LuaException e) {
                                computer.queueEvent("asicraft.result", computer.getAttachmentName(), slot + 1, false, e.getMessage());
                            }
                        } else computer.queueEvent("asicraft.result", computer.getAttachmentName(), slot, true, res);
                    } else {
                        ASICraft.LOG.error(error);
                        computer.queueEvent("asicraft.result", computer.getAttachmentName(), slot, false, error);
                    }
                } catch (Exception e) {
                    ASICraft.LOG.error("Could not process result for finish operation!", e);
                }
            });
        else throw new LuaException("Slot does not have a card inserted");
    }

    @LuaFunction
    public final MethodResult process(IComputerAccess computer, IArguments args) throws LuaException {
        computer.getAttachmentName();
        int slot = args.getInt(0);
        if (!(slot >= 1 && slot <= blockEntity.algorithms.length && blockEntity.algorithms[slot-1] != null))
            throw new LuaException("Slot does not have a card inserted");
        IAlgorithm algo = blockEntity.algorithms[slot-1];
        algo.input(args, (ignored) -> {});
        this.finish(computer, slot);
        int _destSlot = slot;
        while (blockEntity.forward[_destSlot-1]) _destSlot++;
        final int destSlot = _destSlot;
        String name = computer.getAttachmentName();
        return MethodResult.pullEvent("asicraft.result", new ILuaCallback() {
            @Override
            public MethodResult resume(Object[] args) throws LuaException {
                if (args[1].equals(name) && ((Double)args[2]).intValue() == destSlot) {
                    if ((Boolean)args[3]) return MethodResult.of(args[4]);
                    else throw new LuaException((String)args[4]);
                } else return MethodResult.pullEvent("asicraft.result", this);
            }
        });
    }
}
