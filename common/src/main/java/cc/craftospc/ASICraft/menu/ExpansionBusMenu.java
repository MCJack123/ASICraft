package cc.craftospc.ASICraft.menu;

import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import cc.craftospc.ASICraft.util.Registry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ExpansionBusMenu extends AbstractContainerMenu {
    private int tier;
    private ExpansionBusBlockEntity blockEntity;
    private Container inventory;

    public class ExpansionBusScreen extends AbstractContainerScreen<ExpansionBusMenu> {
        private static final ResourceLocation[] BACKGROUND = new ResourceLocation[] {
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t1.png"),
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t2.png"),
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t3.png")
        };

        public ExpansionBusScreen(Inventory inventory, Component component) {
            super(ExpansionBusMenu.this, inventory, component);
        }

        @Override
        public void render(PoseStack poseStack, int i, int j, float f) {
            this.renderBackground(poseStack);
            super.render(poseStack, i, j, f);
            this.renderTooltip(poseStack, i, j);
        }

        @Override
        protected void renderBg(PoseStack poseStack, float f, int i, int j) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, BACKGROUND[tier-1]);
            blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        }
    }

    private static class CardSlot extends Slot {
        public CardSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Registry.Items.ACCELERATOR_CARD.get());
        }
    }

    public ExpansionBusMenu(int tier, int i, Inventory inventory) {
        this(tier, i, inventory, new SimpleContainer(switch (tier) {case 1 -> 1; case 2 -> 3; case 3 -> 6; default -> 0;}));
    }

    public ExpansionBusMenu(int tier, int m, Inventory player, Container inventory) {
        super(Registry.Menus.EXPANSION_BUS[tier-1].get(), m);
        this.tier = tier;
        this.inventory = inventory;
        if (tier == 3) {
            addSlot(new CardSlot(inventory, 0, 35, 33));
            addSlot(new CardSlot(inventory, 1, 53, 33));
            addSlot(new CardSlot(inventory, 2, 71, 33));
            addSlot(new CardSlot(inventory, 3, 89, 33));
            addSlot(new CardSlot(inventory, 4, 107, 33));
            addSlot(new CardSlot(inventory, 5, 125, 33));
        } else {
            addSlot(new CardSlot(inventory, tier - 1, 80, 33));
            if (tier == 2) {
                addSlot(new CardSlot(inventory, 0, 62, 33));
                addSlot(new CardSlot(inventory, 2, 98, 33));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
