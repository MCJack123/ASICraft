package cc.craftospc.ASICraft.fabric;

import cc.craftospc.ASICraft.ASICraft;
import cc.craftospc.ASICraft.util.Registry;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.fabricmc.api.ModInitializer;

public class ASICraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ASICraft.init();
        PeripheralLookup.get().registerForBlockEntity((entity, direction) -> entity.peripheral, Registry.BlockEntities.EXPANSION_BUS.get());
    }
}
