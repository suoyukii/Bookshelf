package net.darkhax.bookshelf.impl.registry;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.IBindRenderLayer;
import net.darkhax.bookshelf.api.item.tab.TabBuilder;
import net.darkhax.bookshelf.api.registry.CommandArgumentEntry;
import net.darkhax.bookshelf.api.registry.IContentLoader;
import net.darkhax.bookshelf.api.registry.IRegistryEntries;
import net.darkhax.bookshelf.api.registry.RegistryDataProvider;
import net.darkhax.bookshelf.impl.util.ForgeEventHelper;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ContentLoaderForge implements IContentLoader {

    // Content loading
    @Override
    public void loadContent(RegistryDataProvider content) {

        this.consumeRegistry(content.blocks, Registries.BLOCK);
        this.consumeRegistry(content.fluids, Registries.FLUID);
        this.consumeRegistry(content.items, Registries.ITEM);
        this.consumeRegistry(content.bannerPatterns, Registries.BANNER_PATTERN);
        this.consumeRegistry(content.mobEffects, Registries.MOB_EFFECT);
        this.consumeRegistry(content.sounds, Registries.SOUND_EVENT);
        this.consumeRegistry(content.potions, Registries.POTION);
        this.consumeRegistry(content.enchantments, Registries.ENCHANTMENT);
        this.consumeRegistry(content.entities, Registries.ENTITY_TYPE);
        this.consumeRegistry(content.blockEntities, Registries.BLOCK_ENTITY_TYPE);
        this.consumeRegistry(content.particleTypes, Registries.PARTICLE_TYPE);
        this.consumeRegistry(content.menus, Registries.MENU);
        this.consumeRegistry(content.recipeSerializers, Registries.RECIPE_SERIALIZER);
        this.consumeRegistry(content.paintings, Registries.PAINTING_VARIANT);
        this.consumeRegistry(content.attributes, Registries.ATTRIBUTE);
        this.consumeRegistry(content.stats, Registries.STAT_TYPE);
        this.consumeRegistry(content.villagerProfessions, Registries.VILLAGER_PROFESSION);
        this.consumeRegistry(content.recipeTypes, Registries.RECIPE_TYPE);
        this.consumeArgumentTypes(content.commandArguments);
        this.consumeWithForgeEvent(content.commands, RegisterCommandsEvent.class, (event, id, builder) -> builder.build(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));

        ForgeEventHelper.addContextListener(VillagerTradesEvent.class, content.trades.getVillagerTrades(), this::registerVillagerTrades);
        ForgeEventHelper.addContextListener(WandererTradesEvent.class, content.trades.getCommonWanderingTrades(), content.trades.getRareWanderingTrades(), this::registerWanderingTrades);

        this.consumeWithForgeEvent(content.dataListeners, AddReloadListenerEvent.class, (event, id, arg) -> event.addListener(arg));
        this.consumeWithModEvent(content.creativeTabs, CreativeModeTabEvent.Register.class, (event, id, arg) -> {
            event.registerCreativeModeTab(id, builder -> {
                arg.accept(new TabBuilder(builder.title(Component.translatable("itemGroup.%s.%s".formatted(id.getNamespace(), id.getPath())))));
            });
        });

        if (Services.PLATFORM.isPhysicalClient()) {

            this.loadClient(content);
        }
    }

    private void loadClient(RegistryDataProvider content) {

        this.consumeWithModEvent(content.resourceListeners, RegisterClientReloadListenersEvent.class, (event, id, arg) -> event.registerReloadListener(arg));

        this.consumeWithParallelEvent(content.blocks, FMLClientSetupEvent.class, (event, block) -> {
            if (block instanceof IBindRenderLayer bindLayer) {
                ItemBlockRenderTypes.setRenderLayer(block, bindLayer.getRenderLayerToBind());
            }
        });
    }

    private void registerVillagerTrades(VillagerTradesEvent event, Map<VillagerProfession, Multimap<Integer, VillagerTrades.ItemListing>> trades) {

        final Multimap<Integer, VillagerTrades.ItemListing> newTrades = trades.get(event.getType());

        if (newTrades != null) {

            final Int2ObjectMap<List<VillagerTrades.ItemListing>> tradeData = event.getTrades();

            for (Map.Entry<Integer, VillagerTrades.ItemListing> entry : newTrades.entries()) {

                tradeData.computeIfAbsent(entry.getKey(), ArrayList::new).add(entry.getValue());
            }
        }
    }

    private void registerWanderingTrades(WandererTradesEvent event, List<VillagerTrades.ItemListing> common, List<VillagerTrades.ItemListing> rare) {

        event.getGenericTrades().addAll(common);
        event.getRareTrades().addAll(rare);
    }

    private <ET extends Event, RT> void consumeWithForgeEvent(IRegistryEntries<RT> registry, Class<ET> eventType, EventConsumer<ET, RT> func) {

        final Consumer<ET> listener = event -> registry.build((id, value) -> func.apply(event, id, value));
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, listener);
    }

    private <ET extends Event & IModBusEvent, RT> void consumeWithModEvent(IRegistryEntries<RT> registry, Class<ET> eventType, EventConsumer<ET, RT> func) {

        final Consumer<ET> listener = event -> {

            registry.build((id, value) -> func.apply(event, id, value));
        };

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, eventType, listener);
    }

    private <T> void consumeRegistry(IRegistryEntries<T> registry, ResourceKey<? extends Registry<T>> registryKey) {

        this.consumeRegistry(registry, registryKey, v -> v);
    }

    private <T> void consumeRegistry(IRegistryEntries<T> registry, ResourceKey<? extends Registry<T>> registryKey, Function<T, ? extends T> wrapper) {

        final Consumer<RegisterEvent> listener = event -> {

            if (event.getRegistryKey().equals(registryKey)) {

                registry.build((id, value) -> event.register(registryKey, id, () -> wrapper.apply(value)));
            }
        };

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterEvent.class, listener);
    }

    private void consumeArgumentTypes(IRegistryEntries<CommandArgumentEntry<?, ?, ?>> registry) {

        if (registry.isEmpty()) {

            return;
        }

        final Consumer<RegisterEvent> listener = event -> {

            if (event.getRegistryKey().equals(Registries.COMMAND_ARGUMENT_TYPE)) {

                registry.build((id, value) -> {

                    event.register(Registries.COMMAND_ARGUMENT_TYPE, id, () -> {

                        final ArgumentTypeInfo serializer = value.getSerializer().get();
                        ArgumentTypeInfos.registerByClass((Class) value.getType(), serializer);
                        return serializer;
                    });
                });
            }
        };

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterEvent.class, listener);
    }

    private <ET extends ParallelDispatchEvent, RT> void consumeWithParallelEvent(IRegistryEntries<RT> registry, Class<ET> eventType, BiConsumer<ET, RT> func) {

        final Consumer<ET> listener = event -> {

            event.enqueueWork(() -> {
                for (RT entry : registry) {
                    func.accept(event, entry);
                }
            });
        };

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, eventType, listener);
    }

    interface EventConsumer<E extends Event, V> {

        void apply(E event, ResourceLocation id, V value);
    }
}
