package cc.craftospc.ASICraft.menu;

import cc.craftospc.ASICraft.item.AcceleratorCardItem;
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

public class CardBuilderMenu extends ItemCombinerMenu {
    public static class CardBuilderScreen extends ItemCombinerScreen<ItemCombinerMenu> {
        private static final ResourceLocation BACKGROUND = new ResourceLocation("asicraft", "textures/gui/card_builder.png");

        public CardBuilderScreen(ItemCombinerMenu menu, Inventory inventory, Component component) {
            super(menu, inventory, component, BACKGROUND);
        }

        @Override
        protected void renderErrorIcon(PoseStack poseStack, int i, int j) {

        }
    }

    public CardBuilderMenu(int i, Inventory inventory) {
        super(Registry.Menus.CARD_BUILDER.get(), i, inventory, ContainerLevelAccess.NULL);
    }

    @Override
    protected boolean mayPickup(Player player, boolean bl) {
        return slots.get(0).hasItem() && slots.get(1).hasItem();
    }

    @Override
    protected void onTake(Player player, ItemStack itemStack) {
        slots.get(0).remove(1);
        slots.get(1).remove(1);
    }

    @Override
    protected boolean isValidBlock(BlockState blockState) {
        return true;
    }

    @Override
    public void createResult() {
        if (!(slots.get(0).hasItem() && slots.get(1).hasItem()))
            slots.get(2).set(ItemStack.EMPTY);
        else {
            ItemStack stack = new ItemStack(Registry.Items.ACCELERATOR_CARD.get());
            AcceleratorCardItem.setAlgorithm(stack, MicrochipItem.getAlgorithm(slots.get(0).getItem()));
            slots.get(2).set(stack);
        }
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 53, 24, item -> item.is(Registry.Items.MICROCHIP.get()))
                .withSlot(1, 53, 42, item -> item.is(Registry.Items.CIRCUIT_BOARD.get()))
                .withResultSlot(2, 107, 33)
                .build();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        player.getInventory().add(slots.get(0).getItem());
        player.getInventory().add(slots.get(1).getItem());
    }
}
