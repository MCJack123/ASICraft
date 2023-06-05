package cc.craftospc.ASICraft.menu;

import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import cc.craftospc.ASICraft.util.Registry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ExpansionBusMenu extends AbstractContainerMenu {
    private int tier;
    private Container inventory;
    private ContainerData data;

    public class ExpansionBusScreen extends AbstractContainerScreen<ExpansionBusMenu> {
        private static final ResourceLocation[] BACKGROUND = new ResourceLocation[] {
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t1.png"),
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t2.png"),
            new ResourceLocation("asicraft", "textures/gui/expansion_bus_t3.png")
        };
        private int hover = -1;

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
            switch (tier) {
                case 2: {
                    blit(poseStack, leftPos + 75, topPos + 56, 240 + data.get(0), hover == 0 ? 248 : 240, 8, 8);
                    blit(poseStack, leftPos + 93, topPos + 18, 240 + data.get(1), hover == 1 ? 248 : 240, 8, 8);
                    break;
                }
                case 3: {
                    blit(poseStack, leftPos + 48, topPos + 56, 240 + data.get(0), hover == 0 ? 248 : 240, 8, 8);
                    blit(poseStack, leftPos + 66, topPos + 18, 240 + data.get(1), hover == 1 ? 248 : 240, 8, 8);
                    blit(poseStack, leftPos + 84, topPos + 56, 240 + data.get(2), hover == 2 ? 248 : 240, 8, 8);
                    blit(poseStack, leftPos + 102, topPos + 18, 240 + data.get(3), hover == 3 ? 248 : 240, 8, 8);
                    blit(poseStack, leftPos + 120, topPos + 56, 240 + data.get(4), hover == 4 ? 248 : 240, 8, 8);
                    break;
                }
            }
        }

        @Override
        protected void renderTooltip(PoseStack poseStack, int i, int j) {
            if (hover != -1) renderTooltip(poseStack, minecraft.font.split(Component.translatable(data.get(hover) != 0 ? "gui.asicraft.expansion_bus.forward.enabled.tooltip" : "gui.asicraft.expansion_bus.forward.disabled.tooltip").withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))), 150), i, j);
            else super.renderTooltip(poseStack, i, j);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            int x = (int)d - leftPos, y = (int)e - topPos;
            switch (tier) {
                case 2: {
                    if (x >= 75 && x < 83 && y >= 56 && y < 64) {sendButtonClick(0); return true;}
                    else if (x >= 93 && x < 101 && y >= 18 && y < 26) {sendButtonClick(1); return true;}
                    break;
                }
                case 3: {
                    if (x >= 48 && x < 56 && y >= 56 && y < 64) {sendButtonClick(0); return true;}
                    else if (x >= 66 && x < 74 && y >= 18 && y < 26) {sendButtonClick(1); return true;}
                    else if (x >= 84 && x < 92 && y >= 56 && y < 64) {sendButtonClick(2); return true;}
                    else if (x >= 102 && x < 110 && y >= 18 && y < 26) {sendButtonClick(3); return true;}
                    else if (x >= 120 && x < 128 && y >= 56 && y < 64) {sendButtonClick(4); return true;}
                    break;
                }
            }
            return super.mouseClicked(d, e, i);
        }

        @Override
        public void mouseMoved(double d, double e) {
            super.mouseMoved(d, e);
            hover = -1;
            int x = (int)d - leftPos, y = (int)e - topPos;
            switch (tier) {
                case 2: {
                    if (x >= 75 && x < 83 && y >= 56 && y < 64) hover = 0;
                    else if (x >= 93 && x < 101 && y >= 18 && y < 26) hover = 1;
                    break;
                }
                case 3: {
                    if (x >= 48 && x < 56 && y >= 56 && y < 64) hover = 0;
                    else if (x >= 66 && x < 74 && y >= 18 && y < 26) hover = 1;
                    else if (x >= 84 && x < 92 && y >= 56 && y < 64) hover = 2;
                    else if (x >= 102 && x < 110 && y >= 18 && y < 26) hover = 3;
                    else if (x >= 120 && x < 128 && y >= 56 && y < 64) hover = 4;
                    break;
                }
            }
        }

        private void sendButtonClick(int i) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, i);
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
        // Java is useless. Why can't I define a variable before calling the constructor? It's just a single constant!
        this(tier, i, inventory, new SimpleContainer(switch (tier) {case 1 -> 1; case 2 -> 3; case 3 -> 6; default -> 0;}), new SimpleContainerData(switch (tier) {case 1 -> 1; case 2 -> 3; case 3 -> 6; default -> 0;}));
    }

    public ExpansionBusMenu(int tier, int m, Inventory player, Container inventory, ContainerData data) {
        super(Registry.Menus.EXPANSION_BUS[tier-1].get(), m);
        this.tier = tier;
        this.inventory = inventory;
        this.data = data;
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
                addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(player, i, 8 + i * 18, 142));
        }
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int i) {
        if (i <= data.getCount()) {
            toggle(i);
            return true;
        }
        return false;
    }

    private void toggle(int i) {
        setData(i, data.get(i) == 0 ? 8 : 0);
        broadcastChanges();
    }
}
