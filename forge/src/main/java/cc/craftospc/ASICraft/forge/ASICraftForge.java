package cc.craftospc.ASICraft.forge;

import cc.craftospc.ASICraft.ASICraft;
import cc.craftospc.ASICraft.algorithms.ExpansionBusPeripheral;
import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import cc.craftospc.ASICraft.util.Registry;
import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ASICraft.MOD_ID)
public class ASICraftForge implements IPeripheralProvider {
    public ASICraftForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ASICraft.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ASICraft.init();
        ForgeComputerCraftAPI.registerPeripheralProvider(this);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(Registry::registerClient);
    }

    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
        if (world.getBlockEntity(pos) instanceof ExpansionBusBlockEntity)
            return LazyOptional.of(() -> new ExpansionBusPeripheral((ExpansionBusBlockEntity) world.getBlockEntity(pos)));
        return LazyOptional.empty();
    }
}
