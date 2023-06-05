package cc.craftospc.ASICraft.algorithms;

import cc.craftospc.ASICraft.ASICraft;
//import cc.craftospc.ASICraft.algorithms.audio.*;
//import cc.craftospc.ASICraft.algorithms.compression.*;
import cc.craftospc.ASICraft.algorithms.crypto.*;
//import cc.craftospc.ASICraft.algorithms.image.*;
//import cc.craftospc.ASICraft.algorithms.video.*;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        else if (type.equals("encode_chacha20")) return new ChaCha20EncodeAlgorithm();
        else if (type.equals("decode_chacha20")) return new ChaCha20DecodeAlgorithm();
        else if (type.equals("transform_pbkdf2")) return new PBKDF2TransformAlgorithm();
        else if (type.equals("transform_sha256")) return new SHA256TransformAlgorithm();
        else if (type.equals("transform_sha3")) return new SHA3TransformAlgorithm();
        return null;
    }

    public static Thread workerThread = null;
    private static final Queue<Runnable> workQueue = new ConcurrentLinkedQueue<>();
    private static final Runnable workLoop = () -> {
        Thread thread = workerThread;
        while (true) {
            while (workQueue.isEmpty()) {
                synchronized (thread) {
                    try {
                        thread.wait();
                    } catch (InterruptedException e) {
                        if (workerThread == thread) workerThread = null;
                        return;
                    }
                }
            }
            Runnable work = workQueue.remove();
            try {work.run();}
            catch (Exception e) {ASICraft.LOG.error("Exception thrown in worker thread!", e);}
        }
    };

    public static void queueWork(Runnable work) {
        workQueue.add(work);
        if (workerThread == null) {
            workerThread = new Thread(workLoop);
            workerThread.setName("ASICraft Worker Thread");
            workerThread.start();
        } else {
            synchronized (workerThread) {
                workerThread.notifyAll();
            }
        }
    }

    public static void shutdown() {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
    }
}
