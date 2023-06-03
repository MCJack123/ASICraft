package cc.craftospc.ASICraft.forge;

import cc.craftospc.ASICraft.ASICraftExpectPlatform;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ASICraftExpectPlatformImpl {
    /**
     * This is our actual method to {@link ASICraftExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static double getReachDistance(Player player) {
        return player.getBlockReach();
    }
}
