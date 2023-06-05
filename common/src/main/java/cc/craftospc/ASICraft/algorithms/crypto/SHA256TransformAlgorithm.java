package cc.craftospc.ASICraft.algorithms.crypto;

import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.algorithms.IAlgorithm;
import cc.craftospc.ASICraft.algorithms.IAlgorithmFinishCallback;
import cc.craftospc.ASICraft.algorithms.IAlgorithmPartialResultCallback;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256TransformAlgorithm implements IAlgorithm {
    MessageDigest hasher = null;

    @Override
    public String getType() {
        return "transform_sha256";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[0][];
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args, IAlgorithmPartialResultCallback callback) throws LuaException {
        if (hasher == null) {
            try {hasher = MessageDigest.getInstance("SHA-256");}
            catch (NoSuchAlgorithmException ignored) {}
        }
        ByteBuffer data = args.getBytes(1);
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        hasher.update(bytes);
    }

    @Override
    public void finish(IAlgorithmFinishCallback callback) throws LuaException {
        if (hasher == null) callback.finish(null, null);
        callback.finish(null, hasher.digest());
    }
}
