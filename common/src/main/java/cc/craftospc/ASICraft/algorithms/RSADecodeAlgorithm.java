package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

public class RSADecodeAlgorithm implements IAlgorithm {
    private RSAEncodeAlgorithm.PKCS1EncodedKey key = null;
    private Cipher cipher = null;
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    @Override
    public String getType() {
        return "decode_rsa";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[][] {
            new String[] {"key", "string"},
            new String[] {"isPublic", "boolean"},
        };
    }

    @Override
    public Object getProperty(String name) {
        if (Objects.equals(name, "key")) {
            if (key == null) return null;
            return ByteBuffer.wrap(key.getEncoded());
        } else if (Objects.equals(name, "isPublic")) {
            return key != null && key instanceof RSAPublicKey;
        } else return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        if (cipher != null) throw new LuaException("Cannot change properties while input is in progress");
        if (Objects.equals(name, "key")) {
            ByteBuffer str = value.getBytes(2);
            byte[] data = new byte[str.capacity()];
            str.get(data);
            key = new RSAEncodeAlgorithm.PKCS1EncodedKey(data);
        } else if (Objects.equals(name, "isPublic")) {
            if (key == null) return;
            boolean isPublic = value.getBoolean(2);
            if (isPublic && !(key instanceof RSAPublicKey)) key = new RSAEncodeAlgorithm.PKCS1EncodedPublicKey(key);
            else if (!isPublic && (key instanceof RSAPublicKey)) key = new RSAEncodeAlgorithm.PKCS1EncodedKey((RSAEncodeAlgorithm.PKCS1EncodedPublicKey) key);
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(1);
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        if (cipher == null) {
            if (key == null) throw new LuaException("Could not create decryptor: Key has not been set");
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, key);
            } catch (Exception e) {
                cipher = null;
                throw new LuaException("Could not create decryptor: " + e.getMessage());
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
            callback.finish("Could not finish decryption: " + e.getMessage(), null);
        } finally {
            cipher = null;
            output.reset();
        }
    }
}
