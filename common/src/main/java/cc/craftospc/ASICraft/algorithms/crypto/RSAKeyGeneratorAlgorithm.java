package cc.craftospc.ASICraft.algorithms.crypto;

import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.algorithms.IAlgorithm;
import cc.craftospc.ASICraft.algorithms.IAlgorithmFinishCallback;
import cc.craftospc.ASICraft.algorithms.IAlgorithmPartialResultCallback;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class RSAKeyGeneratorAlgorithm implements IAlgorithm {
    private int keySize = 2048;

    @Override
    public String getType() {
        return "generate_rsa";
    }

    @Override
    public String[][] getAvailableProperties() {
        return new String[][] {
            new String[] {"size", "number"}
        };
    }

    @Override
    public Object getProperty(String name) {
        if (name.equals("size")) return keySize;
        return null;
    }

    @Override
    public void setProperty(String name, IArguments value) throws LuaException {
        if (name.equals("size")) {
            int n = value.getInt(2);
            if (n != 1024 && n != 2048 && n != 3072 && n != 4096) throw new LuaException("bad argument #3 (key size must be 1024, 2048, 3072, or 4096)");
            keySize = n;
        } else throw new LuaException("bad argument #2 (invalid property)");
    }

    @Override
    public void input(IArguments args, IAlgorithmPartialResultCallback callback) throws LuaException {

    }

    @Override
    public void finish(IAlgorithmFinishCallback callback) throws LuaException {
        AlgorithmRegistry.queueWork(() -> {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(keySize);
                KeyPair pair = generator.generateKeyPair();
                RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
                RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
                Map<String, String> retval = new HashMap<>();
                retval.put("private", encodeKey(privateKey.getModulus(), privateKey.getPrivateExponent()));
                retval.put("public", encodeKey(publicKey.getModulus(), publicKey.getPublicExponent()));
                callback.finish(null, retval);
            } catch (NoSuchAlgorithmException ignored) {}
        });
    }

    private int tagLength(int n) {
        if (n < 128) return 2;
        else if (n < 256) return 3;
        else return 4;
    }

    private void writeSize(ByteBuffer buf, int n) {
        if (n < 128) buf.put((byte)n);
        else if (n < 256) {buf.put((byte)0x81); buf.put((byte)n);}
        else {buf.put((byte)0x82); buf.put((byte)(n >> 8)); buf.put((byte)(n & 0xFF));}
    }

    private String encodeKey(BigInteger n, BigInteger e) {
        byte[] nbytes = n.toByteArray(), ebytes = e.toByteArray();
        int nsize = nbytes.length, esize = ebytes.length;
        if ((nbytes[0] & 0x80) != 0) nsize++;
        if ((ebytes[0] & 0x80) != 0) esize++;
        int datasize = nsize + esize + tagLength(nsize) + tagLength(esize);
        ByteBuffer buf = ByteBuffer.allocate(datasize + tagLength(datasize));
        buf.put((byte)0x30);
        writeSize(buf, datasize);
        buf.put((byte)2);
        writeSize(buf, nsize);
        if ((nbytes[0] & 0x80) != 0) buf.put((byte)0);
        buf.put(nbytes);
        buf.put((byte)2);
        writeSize(buf, esize);
        if ((ebytes[0] & 0x80) != 0) buf.put((byte)0);
        buf.put(ebytes);
        return new String(buf.array(), StandardCharsets.ISO_8859_1);
    }
}
