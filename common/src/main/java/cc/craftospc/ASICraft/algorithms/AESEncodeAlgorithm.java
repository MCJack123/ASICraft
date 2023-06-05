package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class AESEncodeAlgorithm implements IAlgorithm {
    private byte[] key = new byte[32];
    private int keyLen = 16;
    private byte[] iv = new byte[16];
    private Cipher cipher = null;
    private String cipherMode = "CBC";
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    @Override
    public String getType() {
        return "encode_aes";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[][] {
            new String[] {"cipherMode", "string"},
            new String[] {"key", "string"},
            new String[] {"iv", "string"}
        };
    }

    @Override
    public Object getProperty(String name) {
        if (Objects.equals(name, "key")) {
            return ByteBuffer.wrap(key, 0, keyLen);
        } else if (Objects.equals(name, "iv")) {
            return ByteBuffer.wrap(iv);
        } else if (Objects.equals(name, "cipherMode")) {
            return cipherMode;
        } else return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        if (cipher != null) throw new LuaException("Cannot change properties while input is in progress");
        if (Objects.equals(name, "key")) {
            ByteBuffer str = value.getBytes(2);
            if (str.capacity() != 32 && str.capacity() != 24 && str.capacity() != 16) throw new LuaException("bad argument #3 (key must be 16, 24, or 32 bytes long)");
            keyLen = str.capacity();
            str.get(key);
        } else if (Objects.equals(name, "iv")) {
            ByteBuffer str = value.getBytes(2);
            if (str.capacity() != 16) throw new LuaException("bad argument #3 (IV must be 16 bytes long)");
            str.get(iv);
        } else if (Objects.equals(name, "cipherMode")) {
            String str = value.getString(2).toUpperCase();
            if (!Objects.equals(str, "ECB") && !Objects.equals(str, "CBC") && !Objects.equals(str, "CFB") &&
                !Objects.equals(str, "OFB") && !Objects.equals(str, "CTR") && !Objects.equals(str, "GCM"))
                throw new LuaException("bad argument #3 (invalid cipher mode)");
            cipherMode = str;
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(1);
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        if (cipher == null) {
            try {
                SecretKey skey = new SecretKeySpec(key, 0, keyLen, "AES");
                cipher = Cipher.getInstance("AES/" + cipherMode + "/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
            } catch (Exception e) {
                cipher = null;
                throw new LuaException("Could not create encryptor: " + e.getMessage());
            }
        }
        output.writeBytes(cipher.update(bytes));
    }

    @Override
    public void finish(IAlgorithmFinishCallback callback) {
        try {
            output.writeBytes(cipher.doFinal());
            callback.finish(null, ByteBuffer.wrap(output.toByteArray()));
        } catch (Exception e) {
            callback.finish("Could not finish encryption: " + e.getMessage(), null);
        } finally {
            cipher = null;
            output.reset();
        }
    }
}
