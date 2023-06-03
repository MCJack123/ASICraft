package cc.craftospc.ASICraft.item;

import cc.craftospc.ASICraft.util.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MicrochipItem extends Item {
    public MicrochipItem() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    public static String getAlgorithm(ItemStack stack) {
        return stack.getOrCreateTag().getString("Algorithm");
    }

    public static void setAlgorithm(ItemStack stack, String algorithm) {
        stack.getOrCreateTag().putString("Algorithm", algorithm);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("algorithm.asicraft." + getAlgorithm(itemStack)));
    }

}
