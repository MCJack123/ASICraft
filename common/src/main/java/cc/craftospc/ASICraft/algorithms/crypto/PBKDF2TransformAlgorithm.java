package cc.craftospc.ASICraft.algorithms.crypto;

import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.algorithms.IAlgorithm;
import cc.craftospc.ASICraft.algorithms.IAlgorithmFinishCallback;
import cc.craftospc.ASICraft.algorithms.IAlgorithmPartialResultCallback;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class PBKDF2TransformAlgorithm implements IAlgorithm {
    private boolean isProcessing = false;
    private int rounds = 200000;
    private int bits = 128;
    private byte[] salt = null;
    private String partialInput = "";

    @Override
    public String getType() {
        return "transform_pbkdf2";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[][] {
            new String[] {"rounds", "number"},
            new String[] {"bits", "number"},
            new String[] {"salt", "string"},
        };
    }

    @Override
    public Object getProperty(String name) {
        if (name.equals("rounds")) return rounds;
        else if (name.equals("bits")) return bits;
        else if (name.equals("salt")) return salt;
        else return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        if (name.equals("rounds")) rounds = value.getInt(2);
        else if (name.equals("bits")) {
            bits = value.getInt(2);
            salt = null;
        } else if (name.equals("salt")) {
            ByteBuffer str = value.optBytes(2).orElse(null);
            if (str == null || str.capacity() == 0) {
                salt = null;
                return;
            }
            if (str.capacity() != bits / 8) throw new LuaException("bad argument #3 (salt must be the same size as bits)");
            if (salt == null) salt = new byte[bits / 8];
            str.get(salt);
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args, IAlgorithmPartialResultCallback callback) throws LuaException {
        if (isProcessing) throw new LuaException("Card is currently processing data");
        partialInput += args.getString(1);
    }

    @Override
    public void finish(IAlgorithmFinishCallback callback) throws LuaException {
        if (isProcessing) throw new LuaException("Card is currently processing data");
        AlgorithmRegistry.queueWork(() -> {
            isProcessing = true;
            byte[] isalt = salt;
            if (isalt == null) {
                isalt = new byte[bits / 8];
                SecureRandom random = new SecureRandom();
                random.nextBytes(isalt);
            }
            try {
                KeySpec spec = new PBEKeySpec(partialInput.toCharArray(), isalt, rounds, bits);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                byte[] key = factory.generateSecret(spec).getEncoded();
                callback.finish(null, key);
            } catch (Exception e) {
                callback.finish(e.getMessage(), null);
            } finally {
                isProcessing = false;
            }
        });
    }
}
