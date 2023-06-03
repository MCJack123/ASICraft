package cc.craftospc.ASICraft.fabric;

import cc.craftospc.ASICraft.ASICraftExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;

import java.nio.file.Path;

public class ASICraftExpectPlatformImpl {
    /**
     * This is our actual method to {@link ASICraftExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
