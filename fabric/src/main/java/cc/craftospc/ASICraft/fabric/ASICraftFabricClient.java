package cc.craftospc.ASICraft.fabric;

import cc.craftospc.ASICraft.util.Registry;
import net.fabricmc.api.ClientModInitializer;

public class ASICraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Registry.registerClient();
    }
}
