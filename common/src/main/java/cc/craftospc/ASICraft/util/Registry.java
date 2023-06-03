package cc.craftospc.ASICraft.util;

import cc.craftospc.ASICraft.ASICraft;
import cc.craftospc.ASICraft.algorithms.AlgorithmRegistry;
import cc.craftospc.ASICraft.block.CardBuilderBlock;
import cc.craftospc.ASICraft.block.ExpansionBusBlock;
import cc.craftospc.ASICraft.block.MicrochipDesignerBlock;
import cc.craftospc.ASICraft.blockentity.CardBuilderBlockEntity;
import cc.craftospc.ASICraft.blockentity.ExpansionBusBlockEntity;
import cc.craftospc.ASICraft.blockentity.MicrochipDesignerBlockEntity;
import cc.craftospc.ASICraft.item.AcceleratorCardItem;
import cc.craftospc.ASICraft.item.MicrochipItem;
import cc.craftospc.ASICraft.item.MicrochipRecipeItem;
import cc.craftospc.ASICraft.menu.CardBuilderMenu;
import cc.craftospc.ASICraft.menu.ExpansionBusMenu;
import cc.craftospc.ASICraft.menu.MicrochipDesignerMenu;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public final class Registry {
    public static final CreativeTabRegistry.TabSupplier CREATIVE_TAB = CreativeTabRegistry.create(new ResourceLocation(ASICraft.MOD_ID, "tab"), Registry::registerCreativeTab);

    public static final class Blocks {
        static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ASICraft.MOD_ID, Registries.BLOCK);

        public static final RegistrySupplier<Block> MICROCHIP_DESIGNER = REGISTRY.register("microchip_designer", MicrochipDesignerBlock::new);
        public static final RegistrySupplier<Block> CARD_BUILDER = REGISTRY.register("card_builder", CardBuilderBlock::new);
        public static final RegistrySupplier<Block> EXPANSION_BUS_T1 = REGISTRY.register("expansion_bus_tier1", () -> new ExpansionBusBlock(1));
        public static final RegistrySupplier<Block> EXPANSION_BUS_T2 = REGISTRY.register("expansion_bus_tier2", () -> new ExpansionBusBlock(2));
        public static final RegistrySupplier<Block> EXPANSION_BUS_T3 = REGISTRY.register("expansion_bus_tier3", () -> new ExpansionBusBlock(3));
    }

    public static final class BlockEntities {
        static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ASICraft.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

        public static final RegistrySupplier<BlockEntityType<MicrochipDesignerBlockEntity>> MICROCHIP_DESIGNER = REGISTRY.register("microchip_designer", () ->
                new BlockEntityType<>(MicrochipDesignerBlockEntity::new, new HashSet<>(Collections.singletonList(Blocks.MICROCHIP_DESIGNER.get())), null));
        public static final RegistrySupplier<BlockEntityType<CardBuilderBlockEntity>> CARD_BUILDER = REGISTRY.register("card_builder", () ->
            new BlockEntityType<>(CardBuilderBlockEntity::new, new HashSet<>(Collections.singletonList(Blocks.CARD_BUILDER.get())), null));
        public static final RegistrySupplier<BlockEntityType<ExpansionBusBlockEntity>> EXPANSION_BUS = REGISTRY.register("expansion_bus", () ->
            new BlockEntityType<>((pos, state) -> new ExpansionBusBlockEntity(((ExpansionBusBlock) state.getBlock()).tier, pos, state),
                new HashSet<Block>(Arrays.asList(Blocks.EXPANSION_BUS_T1.get(), Blocks.EXPANSION_BUS_T2.get(), Blocks.EXPANSION_BUS_T3.get())), null));
    }

    public static final class Items {
        static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ASICraft.MOD_ID, Registries.ITEM);

        public static final RegistrySupplier<Item> MICROCHIP = REGISTRY.register("microchip", MicrochipItem::new);
        public static final RegistrySupplier<Item> MICROCHIP_DESIGNER = REGISTRY.register("microchip_designer", () ->
                new BlockItem(Blocks.MICROCHIP_DESIGNER.get(), new Item.Properties()));
        public static final RegistrySupplier<Item> MICROCHIP_RECIPE = REGISTRY.register("microchip_recipe", MicrochipRecipeItem::new);
        public static final RegistrySupplier<Item> ACCELERATOR_CARD = REGISTRY.register("accelerator_card", AcceleratorCardItem::new);
        public static final RegistrySupplier<Item> CIRCUIT_BOARD = REGISTRY.register("circuit_board", () -> new Item(new Item.Properties()));
        public static final RegistrySupplier<Item> CARD_BUILDER = REGISTRY.register("card_builder", () ->
            new BlockItem(Blocks.CARD_BUILDER.get(), new Item.Properties()));
        public static final RegistrySupplier<Item> EXPANSION_BUS_T1 = REGISTRY.register("expansion_bus_tier1", () ->
            new BlockItem(Blocks.EXPANSION_BUS_T1.get(), new Item.Properties()));
        public static final RegistrySupplier<Item> EXPANSION_BUS_T2 = REGISTRY.register("expansion_bus_tier2", () ->
            new BlockItem(Blocks.EXPANSION_BUS_T2.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));
        public static final RegistrySupplier<Item> EXPANSION_BUS_T3 = REGISTRY.register("expansion_bus_tier3", () ->
            new BlockItem(Blocks.EXPANSION_BUS_T3.get(), new Item.Properties().rarity(Rarity.RARE)));
    }

    public static final class Menus {
        static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ASICraft.MOD_ID, Registries.MENU);

        public static final RegistrySupplier<MenuType<MicrochipDesignerMenu>> MICROCHIP_DESIGNER = REGISTRY.register("microchip_designer", () ->
                new MenuType<>(MicrochipDesignerMenu::new, FeatureFlags.VANILLA_SET));
        public static final RegistrySupplier<MenuType<CardBuilderMenu>> CARD_BUILDER = REGISTRY.register("card_builder", () ->
            new MenuType<>(CardBuilderMenu::new, FeatureFlags.VANILLA_SET));
        public static final RegistrySupplier<MenuType<ExpansionBusMenu>> EXPANSION_BUS_T1 = REGISTRY.register("expansion_bus_tier1", () ->
            new MenuType<>((i, inv) -> new ExpansionBusMenu(1, i, inv), FeatureFlags.VANILLA_SET));
        public static final RegistrySupplier<MenuType<ExpansionBusMenu>> EXPANSION_BUS_T2 = REGISTRY.register("expansion_bus_tier2", () ->
            new MenuType<>((i, inv) -> new ExpansionBusMenu(2, i, inv), FeatureFlags.VANILLA_SET));
        public static final RegistrySupplier<MenuType<ExpansionBusMenu>> EXPANSION_BUS_T3 = REGISTRY.register("expansion_bus_tier3", () ->
            new MenuType<>((i, inv) -> new ExpansionBusMenu(3, i, inv), FeatureFlags.VANILLA_SET));
        public static final RegistrySupplier<MenuType<ExpansionBusMenu>>[] EXPANSION_BUS = new RegistrySupplier[] {EXPANSION_BUS_T1, EXPANSION_BUS_T2, EXPANSION_BUS_T3};
    }

    public static void register() {
        Blocks.REGISTRY.register();
        BlockEntities.REGISTRY.register();
        Items.REGISTRY.register();
        Menus.REGISTRY.register();
    }

    public static void registerClient() {
        MenuScreens.register(Registry.Menus.MICROCHIP_DESIGNER.get(), MicrochipDesignerMenu.MicrochipDesignerScreen::new);
        MenuScreens.register(Menus.CARD_BUILDER.get(), CardBuilderMenu.CardBuilderScreen::new);
        MenuScreens.register(Menus.EXPANSION_BUS_T1.get(), (ExpansionBusMenu menu, Inventory inventory, Component component) -> menu.new ExpansionBusScreen(inventory, component));
        MenuScreens.register(Menus.EXPANSION_BUS_T2.get(), (ExpansionBusMenu menu, Inventory inventory, Component component) -> menu.new ExpansionBusScreen(inventory, component));
        MenuScreens.register(Menus.EXPANSION_BUS_T3.get(), (ExpansionBusMenu menu, Inventory inventory, Component component) -> menu.new ExpansionBusScreen(inventory, component));
    }

    public static CreativeModeTab.Builder registerCreativeTab(CreativeModeTab.Builder builder) {
        return builder
            .icon(() -> new ItemStack(Items.MICROCHIP.get()))
            .title(Component.translatable("itemGroup.asicraft"))
            .displayItems((context, out) -> {
                out.accept(Items.MICROCHIP_DESIGNER.get());
                out.accept(Items.CARD_BUILDER.get());
                out.accept(Items.EXPANSION_BUS_T1.get());
                out.accept(Items.EXPANSION_BUS_T2.get());
                out.accept(Items.EXPANSION_BUS_T3.get());
                out.accept(Items.CIRCUIT_BOARD.get());
                for (String algo : AlgorithmRegistry.ALGORITHMS) {
                    ItemStack stack = new ItemStack(Items.MICROCHIP_RECIPE.get());
                    MicrochipRecipeItem.setAlgorithm(stack, algo);
                    out.accept(stack);
                }
                for (String algo : AlgorithmRegistry.ALGORITHMS) {
                    ItemStack stack = new ItemStack(Items.MICROCHIP.get());
                    MicrochipItem.setAlgorithm(stack, algo);
                    out.accept(stack);
                }
                for (String algo : AlgorithmRegistry.ALGORITHMS) {
                    ItemStack stack = new ItemStack(Items.ACCELERATOR_CARD.get());
                    AcceleratorCardItem.setAlgorithm(stack, algo);
                    out.accept(stack);
                }
            });
    }
}
