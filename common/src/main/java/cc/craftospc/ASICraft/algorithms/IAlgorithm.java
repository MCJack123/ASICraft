package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

public interface IAlgorithm {
    String getType();
    String[][] getAvailableProperties();
    Object getProperty(String name);
    void setProperty(String name, IArguments value) throws LuaException;
    void input(IArguments args, IAlgorithmPartialResultCallback callback) throws LuaException;
    void finish(IAlgorithmFinishCallback callback) throws LuaException;
}
