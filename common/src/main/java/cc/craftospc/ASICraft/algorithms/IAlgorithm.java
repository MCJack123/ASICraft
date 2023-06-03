package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public interface IAlgorithm {
    String getType();
    String[][] getAvailableProperties();
    Object getProperty(String name);
    void setProperty(String name, IArguments value) throws LuaException;
    void input(byte[] data) throws LuaException;
    void finish(IComputerAccess computer, int slot) throws LuaException;
}
