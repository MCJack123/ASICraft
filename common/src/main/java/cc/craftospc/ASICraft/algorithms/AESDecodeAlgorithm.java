package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class AESDecodeAlgorithm implements IAlgorithm {
    private byte[] key = new byte[32];
    private byte[] iv = new byte[16];
    private Cipher cipher = null;
    private String cipherMode = "CBC";
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    @Override
    public String getType() {
        return "decode_aes";
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
            return ByteBuffer.wrap(key);
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
            byte[] str = value.getBytes(2).array();
            if (str.length != 32 && str.length != 24 && str.length != 16) throw new LuaException("bad argument #3 (key must be 16, 24, or 32 bytes long)");
            key = str;
        } else if (Objects.equals(name, "iv")) {
            byte[] str = value.getBytes(2).array();
            if (str.length != 16) throw new LuaException("bad argument #3 (IV must be 16 bytes long)");
            iv = str;
        } else if (Objects.equals(name, "cipherMode")) {
            String str = value.getString(2).toUpperCase();
            if (!Objects.equals(str, "ECB") && !Objects.equals(str, "CBC") && !Objects.equals(str, "CFB") &&
                !Objects.equals(str, "OFB") && !Objects.equals(str, "CTR") && !Objects.equals(str, "GCM"))
                throw new LuaException("bad argument #3 (invalid cipher mode)");
            cipherMode = str;
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(byte[] data) throws LuaException {
        if (cipher == null) {
            try {
                SecretKey skey = new SecretKeySpec(key, "AES");
                cipher = Cipher.getInstance("AES/" + cipherMode + "/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
            } catch (Exception e) {
                cipher = null;
                throw new LuaException("Could not create decryptor: " + e.getMessage());
            }
        }
        output.writeBytes(cipher.update(data));
    }

    @Override
    public void finish(IComputerAccess computer, int slot) {
        try {
            output.writeBytes(cipher.doFinal());
            computer.queueEvent("asicraft.result", computer.getAttachmentName(), slot, true, ByteBuffer.wrap(output.toByteArray()));
        } catch (Exception e) {
            computer.queueEvent("asicraft.result", computer.getAttachmentName(), slot, false, "Could not finish decryption: " + e.getMessage());
        } finally {
            cipher = null;
            output.reset();
        }
    }
}
