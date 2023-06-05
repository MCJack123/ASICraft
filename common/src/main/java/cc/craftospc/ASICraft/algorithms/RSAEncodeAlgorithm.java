package cc.craftospc.ASICraft.algorithms;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

public class RSAEncodeAlgorithm implements IAlgorithm {
    private PKCS1EncodedKey key = null;
    private Cipher cipher = null;
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    @Override
    public String getType() {
        return "encode_rsa";
    }

    public static class PKCS1EncodedKey implements RSAPrivateKey {
        protected BigInteger modulus;
        protected BigInteger exponent;
        protected byte[] keyData;

        protected PKCS1EncodedKey() {}

        public PKCS1EncodedKey(byte[] data) {
            keyData = data;
            if (data[0] != 0x30) throw new InvalidParameterException("Key is not a SEQUENCE");
            int pos = 2;
            if ((data[1] & 0x80) != 0) pos += (data[1] & 0x7F);
            if (data[pos++] != 2) throw new InvalidParameterException("First entry in key is not an INTEGER");
            int size;
            if ((data[pos] & 0x80) != 0) {
                int len = data[pos++] & 0x7F;
                size = new BigInteger(data, pos, len).intValue();
                pos += len;
            } else size = data[pos++] & 0xFF;
            modulus = new BigInteger(data, pos, size);
            pos += size;
            if (data[pos++] != 2) throw new InvalidParameterException("Second entry in key is not an INTEGER");
            if ((data[pos] & 0x80) != 0) {
                int len = data[pos++] & 0x7F;
                size = new BigInteger(data, pos, len).intValue();
                pos += len;
            } else size = data[pos++] & 0xFF;
            exponent = new BigInteger(data, pos, size);
        }

        public PKCS1EncodedKey(PKCS1EncodedPublicKey key) {
            exponent = key.exponent;
            modulus = key.modulus;
            keyData = key.keyData;
        }

        @Override
        public String getAlgorithm() {
            return "RSA";
        }

        @Override
        public String getFormat() {
            return "PKCS#1";
        }

        @Override
        public byte[] getEncoded() {
            return keyData;
        }

        @Override
        public BigInteger getModulus() {
            return modulus;
        }

        @Override
        public BigInteger getPrivateExponent() {
            return exponent;
        }
    }

    public static class PKCS1EncodedPublicKey extends PKCS1EncodedKey implements RSAPublicKey {
        public PKCS1EncodedPublicKey(byte[] data) {
            super(data);
        }

        public PKCS1EncodedPublicKey(PKCS1EncodedKey key) {
            exponent = key.exponent;
            modulus = key.modulus;
            keyData = key.keyData;
        }

        @Override
        public BigInteger getPublicExponent() {
            return exponent;
        }
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
            key = new PKCS1EncodedKey(data);
        } else if (Objects.equals(name, "isPublic")) {
            if (key == null) return;
            boolean isPublic = value.getBoolean(2);
            if (isPublic && !(key instanceof RSAPublicKey)) key = new PKCS1EncodedPublicKey(key);
            else if (!isPublic && (key instanceof RSAPublicKey)) key = new PKCS1EncodedKey((PKCS1EncodedPublicKey) key);
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args) throws LuaException {
        ByteBuffer data = args.getBytes(1);
        byte[] bytes = new byte[data.capacity()];
        data.get(bytes);
        if (cipher == null) {
            if (key == null) throw new LuaException("Could not create encryptor: Key has not been set");
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, key);
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
