package cc.craftospc.ASICraft.algorithms;

import java.util.Objects;

public class AlgorithmRegistry {
    public static final String[] ALGORITHMS = new String[] {
        "encode_aes",
        "decode_aes"
    };

    public static IAlgorithm createAlgorithm(String type) {
        if (Objects.equals(type, "encode_aes")) return new AESEncodeAlgorithm();
        else if (Objects.equals(type, "decode_aes")) return new AESDecodeAlgorithm();
        return null;
    }
}
