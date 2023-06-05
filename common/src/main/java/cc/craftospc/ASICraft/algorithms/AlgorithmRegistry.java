package cc.craftospc.ASICraft.algorithms;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AlgorithmRegistry {
    public static final String[] ALGORITHMS = new String[] {
        // Cryptography
        "encode_aes",
        "decode_aes",
        "encode_rsa",
        "decode_rsa",
        "generate_rsa",
        "encode_chacha20",
        "decode_chacha20",
        "transform_pbkdf2",
        "transform_sha256",
        "transform_sha3",
        "transform_blake3",
        // Audio codecs
        "encode_mp3",
        "decode_mp3",
        "encode_ogg",
        "decode_ogg",
        "encode_flac",
        "decode_flac",
        "encode_wav",
        "decode_wav",
        "encode_m4a",
        "decode_m4a",
        "encode_dfpwm",
        "decode_dfpwm",
        // Audio transformers
        "transform_resample",
        "transform_mix_mono",
        // Image codecs
        "encode_png",
        "decode_png",
        "encode_jpg",
        "decode_jpg",
        "encode_webp",
        "decode_webp",
        "encode_gif",
        "decode_gif",
        // Image transformers
        "transform_quantize",
        "transform_pixelize",
        // Video codecs
        "encode_mp4",
        "decode_mp4",
        "encode_mov",
        "decode_mov",
        "encode_webm",
        "decode_webm",
        "encode_ogv",
        "decode_ogv",
        // Compression
        "encode_lzw",
        "decode_lzw",
        "encode_deflate",
        "decode_deflate",
        "encode_bzip2",
        "decode_bzip2",
        "encode_lzma",
        "decode_lzma",
        "encode_zstd",
        "decode_zstd",
    };

    public static IAlgorithm createAlgorithm(@Nonnull String type) {
        if (type.equals("encode_aes")) return new AESEncodeAlgorithm();
        else if (type.equals("decode_aes")) return new AESDecodeAlgorithm();
        else if (type.equals("encode_rsa")) return new RSAEncodeAlgorithm();
        else if (type.equals("decode_rsa")) return new RSADecodeAlgorithm();
        else if (type.equals("generate_rsa")) return new RSAKeyGeneratorAlgorithm();
        return null;
    }
}
