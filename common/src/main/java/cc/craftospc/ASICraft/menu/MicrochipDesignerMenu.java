package cc.craftospc.ASICraft.menu;

import cc.craftospc.ASICraft.item.MicrochipItem;
import cc.craftospc.ASICraft.item.MicrochipRecipeItem;
import cc.craftospc.ASICraft.util.Registry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class MicrochipDesignerMenu extends ItemCombinerMenu {
    public static class MicrochipDesignerScreen extends ItemCombinerScreen<ItemCombinerMenu> {
        private static final ResourceLocation BACKGROUND = new ResourceLocation("asicraft", "textures/gui/microchip_designer.png");

        public MicrochipDesignerScreen(ItemCombinerMenu menu, Inventory inventory, Component component) {
            super(menu, inventory, component, BACKGROUND);
        }

        @Override
        protected void renderErrorIcon(PoseStack poseStack, int i, int j) {

        }
    }

    public MicrochipDesignerMenu(int i, Inventory inventory) {
        super(Registry.Menus.MICROCHIP_DESIGNER.get(), i, inventory, ContainerLevelAccess.NULL);
    }

    @Override
    protected boolean mayPickup(Player player, boolean bl) {
        return slots.get(0).hasItem() && slots.get(1).hasItem() && slots.get(2).hasItem() && slots.get(3).hasItem() && slots.get(4).hasItem();
    }

    @Override
    protected void onTake(Player player, ItemStack itemStack) {
        slots.get(0).remove(1);
        slots.get(1).remove(1);
        slots.get(2).remove(1);
        slots.get(3).remove(1);
    }

    @Override
    protected boolean isValidBlock(BlockState blockState) {
        return true;
    }

    @Override
    public void createResult() {
        if (!(slots.get(0).hasItem() && slots.get(1).hasItem() && slots.get(2).hasItem() && slots.get(3).hasItem() && slots.get(4).hasItem()))
            slots.get(5).set(ItemStack.EMPTY);
        else {
            ItemStack stack = new ItemStack(Registry.Items.MICROCHIP.get());
            MicrochipItem.setAlgorithm(stack, MicrochipRecipeItem.getAlgorithm(slots.get(4).getItem()));
            slots.get(5).set(stack);
        }
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 25, 24, item -> item.is(Items.IRON_INGOT))
                .withSlot(1, 43, 24, item -> item.is(Items.GOLD_INGOT))
                .withSlot(2, 25, 42, item -> item.is(Items.REDSTONE))
                .withSlot(3, 43, 42, item -> item.is(Items.STONE))
                .withSlot(4, 80, 33, item -> item.is(Registry.Items.MICROCHIP_RECIPE.get()))
                .withResultSlot(5, 125, 33)
                .build();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        player.getInventory().add(slots.get(0).getItem());
        player.getInventory().add(slots.get(1).getItem());
        player.getInventory().add(slots.get(2).getItem());
        player.getInventory().add(slots.get(3).getItem());
        player.getInventory().add(slots.get(4).getItem());
    }
}
