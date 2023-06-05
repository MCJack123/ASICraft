package cc.craftospc.ASICraft.algorithms.crypto;

import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.algorithms.IAlgorithm;
import cc.craftospc.ASICraft.algorithms.IAlgorithmFinishCallback;
import cc.craftospc.ASICraft.algorithms.IAlgorithmPartialResultCallback;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ChaCha20EncodeAlgorithm implements IAlgorithm {
    private boolean isProcessing = false;
    private boolean partial = false;
    private byte[] key = new byte[32];
    private byte[] nonce = new byte[12];
    private Cipher cipher = null;
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    @Override
    public String getType() {
        return "encode_chacha20";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[][] {
            new String[] {"partial", "boolean"},
            new String[] {"key", "string"},
            new String[] {"nonce", "string"}
        };
    }

    @Override
    public Object getProperty(String name) {
        if (Objects.equals(name, "partial")) {
            return partial;
        } else if (Objects.equals(name, "key")) {
            return ByteBuffer.wrap(key);
        } else if (Objects.equals(name, "nonce")) {
            return ByteBuffer.wrap(nonce);
        } else return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        if (cipher != null) throw new LuaException("Cannot change properties while input is in progress");
        if (Objects.equals(name, "partial")) partial = value.getBoolean(2);
        else if (Objects.equals(name, "key")) {
            ByteBuffer str = value.getBytes(2);
            if (str.capacity() != 32) throw new LuaException("bad argument #3 (key must be 32 bytes long)");
            str.get(key);
        } else if (Objects.equals(name, "nonce")) {
            ByteBuffer str = value.getBytes(2);
            if (str.capacity() != 12) throw new LuaException("bad argument #3 (nonce must be 12 bytes long)");
            str.get(nonce);
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args, IAlgorithmPartialResultCallback callback) throws LuaException {
        if (isProcessing) throw new LuaException("Card is currently processing data");
        ByteBuffer data = args.getBytes(1);
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        if (cipher == null) {
            try {
                SecretKey skey = new SecretKeySpec(key, "ChaCha20");
                cipher = Cipher.getInstance("ChaCha20");
                cipher.init(Cipher.ENCRYPT_MODE, skey, new ChaCha20ParameterSpec(nonce, 1));
            } catch (Exception e) {
                cipher = null;
                throw new LuaException("Could not create encryptor: " + e.getMessage());
            }
        }
        AlgorithmRegistry.queueWork(() -> {
            byte[] res = cipher.update(bytes);
            if (partial) callback.partialResult(res);
            else output.writeBytes(res);
        });
    }

    @Override
    public void finish(IAlgorithmFinishCallback callback) throws LuaException {
        if (isProcessing) throw new LuaException("Card is currently processing data");
        AlgorithmRegistry.queueWork(() -> {
            isProcessing = true;
            try {
                output.writeBytes(cipher.doFinal());
                callback.finish(null, ByteBuffer.wrap(output.toByteArray()));
            } catch (Exception e) {
                callback.finish("Could not finish encryption: " + e.getMessage(), null);
            } finally {
                cipher = null;
                isProcessing = false;
                output.reset();
            }
        });
    }
}
