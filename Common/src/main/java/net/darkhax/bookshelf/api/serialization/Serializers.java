package net.darkhax.bookshelf.api.serialization;

import net.darkhax.bookshelf.api.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.UUID;

public final class Serializers {

    // JAVA TYPES
    public static final ISerializer<Boolean> BOOLEAN = SerializerBoolean.SERIALIZER;
    public static final ISerializer<Byte> BYTE = SerializerByte.SERIALIZER;
    public static final ISerializer<Short> SHORT = SerializerShort.SERIALIZER;
    public static final ISerializer<Integer> INT = SerializerInteger.SERIALIZER;
    public static final ISerializer<Long> LONG = SerializerLong.SERIALIZER;
    public static final ISerializer<Float> FLOAT = SerializerFloat.SERIALIZER;
    public static final ISerializer<Double> DOUBLE = SerializerDouble.SERIALIZER;
    public static final ISerializer<String> STRING = SerializerString.SERIALIZER;
    public static final ISerializer<UUID> UUID = SerializerUUID.SERIALIZER;

    // MINECRAFT TYPES
    public static final ISerializer<ResourceLocation> RESOURCE_LOCATION = SerializerResourceLocation.SERIALIZER;
    public static final ISerializer<ItemStack> ITEM_STACK = SerializerItemStack.SERIALIZER;
    public static final ISerializer<CompoundTag> COMPOUND_TAG = SerializerCompoundTag.SERIALIZER;
    public static final ISerializer<Component> TEXT = SerializerText.SERIALIZER;
    public static final ISerializer<BlockPos> BLOCK_POS = SerializerBlockPos.SERIALIZER;
    public static final ISerializer<Ingredient> INGREDIENT = SerializerIngredient.SERIALIZER;
    public static final ISerializer<BlockState> BLOCK_STATE = SerializerBlockState.SERIALIZER;
    public static final ISerializer<AttributeModifier> ATTRIBUTE_MODIFIER = SerializerAttributeModifier.SERIALIZER;
    public static final ISerializer<MobEffectInstance> EFFECT_INSTANCE = new SerializerEffectInstance();

    // ENUMS
    public static final ISerializer<Rarity> ITEM_RARITY = new SerializerEnum<>(Rarity.class);
    public static final ISerializer<Enchantment.Rarity> ENCHANTMENT_RARITY = new SerializerEnum<>(Enchantment.Rarity.class);
    public static final ISerializer<AttributeModifier.Operation> ATTRIBUTE_OPERATION = new SerializerEnum<>(AttributeModifier.Operation.class);

    // REGISTRY TYPES
    public static final ISerializer<Block> BLOCK = new SerializerRegistryEntry<>(Services.REGISTRIES.blocks());
    public static final ISerializer<Item> ITEM = new SerializerRegistryEntry<>(Services.REGISTRIES.items());
    public static final ISerializer<Enchantment> ENCHANTMENT = new SerializerRegistryEntry<>(Services.REGISTRIES.enchantments());
    public static final ISerializer<Motive> MOTIVE = new SerializerRegistryEntry<>(Services.REGISTRIES.paintings());
    public static final ISerializer<MobEffect> MOB_EFFECT = new SerializerRegistryEntry<>(Services.REGISTRIES.mobEffects());
    public static final ISerializer<Potion> POTION = new SerializerRegistryEntry<>(Services.REGISTRIES.potions());
    public static final ISerializer<Attribute> ATTRIBUTE = new SerializerRegistryEntry<>(Services.REGISTRIES.attributes());
    public static final ISerializer<VillagerProfession> VILLAGER_PROFESSION = new SerializerRegistryEntry<>(Services.REGISTRIES.villagerProfessions());
    public static final ISerializer<VillagerType> VILLAGER_TYPE = new SerializerRegistryEntry<>(Services.REGISTRIES.villagerTypes());
    public static final ISerializer<SoundEvent> SOUND_EVENT = new SerializerRegistryEntry<>(Services.REGISTRIES.sounds());
    public static final ISerializer<MenuType<?>> MENU = new SerializerRegistryEntry<>(Services.REGISTRIES.menuTypes());
    public static final ISerializer<ParticleType<?>> PARTICLE = new SerializerRegistryEntry<>(Services.REGISTRIES.particles());
    public static final ISerializer<EntityType<?>> ENTITY = new SerializerRegistryEntry<>(Services.REGISTRIES.entities());
    public static final ISerializer<BlockEntityType<?>> BLOCK_ENTITY = new SerializerRegistryEntry<>(Services.REGISTRIES.blockEntities());
    public static final ISerializer<GameEvent> GAME_EVENT = new SerializerRegistryEntry<>(Services.REGISTRIES.gameEvents());
}