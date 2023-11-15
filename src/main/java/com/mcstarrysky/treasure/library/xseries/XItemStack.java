/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.mcstarrysky.treasure.library.xseries;

import com.google.common.base.Enums;
import com.google.common.base.Strings;
import kotlin.Unit;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import taboolib.library.configuration.ConfigurationSection;
import taboolib.library.xseries.XEnchantment;
import taboolib.library.xseries.XMaterial;
import taboolib.library.xseries.XPotion;
import taboolib.library.xseries.XSkull;
import taboolib.module.chat.UtilKt;
import taboolib.module.configuration.Configuration;
import taboolib.module.configuration.Type;
import taboolib.module.nms.NMSItemRawKt;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static taboolib.library.xseries.XMaterial.supports;

/**
 * <b>XItemStack</b> - YAML Item Serializer<br>
 * Using ConfigurationSection Example:
 * <pre>
 *     ConfigurationSection section = plugin.getConfig().getConfigurationSection("staffs.dragon-staff");
 *     ItemStack item = XItemStack.deserialize(section);
 * </pre>
 * ItemStack: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/ItemStack.html
 *
 * @author Crypto Morin
 * @version 6.0.0
 * @see ItemStack
 */
@SuppressWarnings("ALL")
public final class XItemStack {
    public static final ItemFlag[] ITEM_FLAGS = ItemFlag.values();

    /**
     * Because item metas cannot be applied to AIR, apparently.
     */
    private static final XMaterial DEFAULT_MATERIAL = XMaterial.NETHER_PORTAL;

    private XItemStack() {
    }

    /**
     * Deserialize an ItemStack from the config.
     *
     * @param config the config section to deserialize the ItemStack object from.
     * @return a deserialized ItemStack.
     * @since 1.0.0
     */
    @NotNull
    public static ItemStack deserialize(@NotNull ConfigurationSection config) {
        return edit(new ItemStack(DEFAULT_MATERIAL.parseMaterial()), config, Function.identity(), null);
    }

    /**
     * Deserialize an ItemStack from a {@code Map}.
     *
     * @param serializedItem the map holding the item configurations to deserialize
     *                       the ItemStack object from.
     * @return a deserialized ItemStack.
     */
    @NotNull
    public static ItemStack deserialize(@NotNull Map<String, Object> serializedItem) {
        Objects.requireNonNull(serializedItem, "serializedItem cannot be null.");
        return deserialize(mapToConfigSection(serializedItem));
    }

    @NotNull
    public static ItemStack deserialize(@NotNull ConfigurationSection config,
                                        @NotNull Function<String, String> translator) {
        return deserialize(config, translator, null);
    }

    /**
     * Deserialize an ItemStack from the config.
     *
     * @param config the config section to deserialize the ItemStack object from.
     * @return an edited ItemStack.
     * @since 7.2.0
     */
    @NotNull
    public static ItemStack deserialize(@NotNull ConfigurationSection config,
                                        @NotNull Function<String, String> translator,
                                        @Nullable Consumer<Exception> restart) {
        return edit(new ItemStack(DEFAULT_MATERIAL.parseMaterial()), config, translator, restart);
    }


    /**
     * Deserialize an ItemStack from a {@code Map}.
     *
     * @param serializedItem the map holding the item configurations to deserialize
     *                       the ItemStack object from.
     * @param translator     the translator to use for translating the item's name.
     * @return a deserialized ItemStack.
     */
    @NotNull
    public static ItemStack deserialize(@NotNull Map<String, Object> serializedItem, @NotNull Function<String, String> translator) {
        Objects.requireNonNull(serializedItem, "serializedItem cannot be null.");
        Objects.requireNonNull(translator, "translator cannot be null.");
        return deserialize(mapToConfigSection(serializedItem), translator);
    }

    private static int toInt(String str, @SuppressWarnings("SameParameterValue") int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    private static List<String> split(@NotNull String str, @SuppressWarnings("SameParameterValue") char separatorChar) {
        List<String> list = new ArrayList<>(5);
        boolean match = false, lastMatch = false;
        int len = str.length();
        int start = 0;

        for (int i = 0; i < len; i++) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }

                // This is important, it should not be i++
                start = i + 1;
                continue;
            }

            lastMatch = false;
            match = true;
        }

        if (match || lastMatch) {
            list.add(str.substring(start, len));
        }
        return list;
    }

    private static List<String> splitNewLine(String str) {
        int len = str.length();
        List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false, lastMatch = false;

        while (i < len) {
            if (str.charAt(i) == '\n') {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }

        if (match || lastMatch) {
            list.add(str.substring(start, i));
        }

        return list;
    }

    /**
     * Deserialize an ItemStack from the config.
     *
     * @param config the config section to deserialize the ItemStack object from.
     * @return an edited ItemStack.
     * @since 1.0.0
     */
    @SuppressWarnings("deprecation")
    @NotNull
    public static ItemStack edit(@NotNull ItemStack item,
                                 @NotNull final ConfigurationSection config,
                                 @NotNull final Function<String, String> translator,
                                 @Nullable final Consumer<Exception> restart) {
        Objects.requireNonNull(item, "Cannot operate on null ItemStack, considering using an AIR ItemStack instead");
        Objects.requireNonNull(config, "Cannot deserialize item to a null configuration section.");
        Objects.requireNonNull(translator, "Translator function cannot be null");

        // MCStarrySky - Remove material setter in order to keep ItemsAdder's meta

        // Amount
        int amount = config.getInt("amount");
        if (amount > 1) item.setAmount(amount);

        ItemMeta meta;
        { // For Java's stupid closure capture system.
            ItemMeta tempMeta = item.getItemMeta();
            if (tempMeta == null) {
                // When AIR is null. Useful for when you just want to use the meta to save data and
                // set the type later. A simple CraftMetaItem.
                meta = Bukkit.getItemFactory().getItemMeta(XMaterial.STONE.parseMaterial());
            } else {
                meta = tempMeta;
            }
        }


        // Durability - Damage
        if (supports(13)) {
            if (meta instanceof Damageable) {
                int damage = config.getInt("damage");
                if (damage > 0) ((Damageable) meta).setDamage(damage);
            }
        } else {
            int damage = config.getInt("damage");
            if (damage > 0) item.setDurability((short) damage);
        }

        // Special Items
        if (meta instanceof SkullMeta) {
            String skull = config.getString("skull");
            if (skull != null) XSkull.applySkin(meta, skull);
        } else if (meta instanceof BannerMeta) {
            BannerMeta banner = (BannerMeta) meta;
            ConfigurationSection patterns = config.getConfigurationSection("patterns");

            //System.out.println("patters v2: "  + patterns);
            if (patterns != null) {
                for (String pattern : patterns.getKeys(false)) {
                    PatternType type = PatternType.getByIdentifier(pattern);
                    if (type == null)
                        type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                    DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                    banner.addPattern(new Pattern(color, type));
                }
            }
        } else if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leather = (LeatherArmorMeta) meta;
            String colorStr = config.getString("color");
            if (colorStr != null) {
                leather.setColor(parseColor(colorStr));
            }
        } else if (meta instanceof PotionMeta) {
            if (supports(9)) {
                PotionMeta potion = (PotionMeta) meta;

                for (String effects : config.getStringList("effects")) {
                    XPotion.Effect effect = XPotion.parseEffect(effects);
                    if (effect.hasChance()) potion.addCustomEffect(effect.getEffect(), true);
                }

                String baseEffect = config.getString("base-effect");
                if (!Strings.isNullOrEmpty(baseEffect)) {
                    List<String> split = split(baseEffect, ',');
                    PotionType type = Enums.getIfPresent(PotionType.class, split.get(0).trim().toUpperCase(Locale.ENGLISH)).or(PotionType.UNCRAFTABLE);
                    boolean extended = split.size() != 1 && Boolean.parseBoolean(split.get(1).trim());
                    boolean upgraded = split.size() > 2 && Boolean.parseBoolean(split.get(2).trim());
                    PotionData potionData = new PotionData(type, extended, upgraded);
                    potion.setBasePotionData(potionData);
                }

                if (config.contains("color")) {
                    potion.setColor(Color.fromRGB(config.getInt("color")));
                }
            } else {

                if (config.contains("level")) {
                    int level = config.getInt("level");
                    String baseEffect = config.getString("base-effect");
                    if (!Strings.isNullOrEmpty(baseEffect)) {
                        List<String> split = split(baseEffect, ',');
                        PotionType type = Enums.getIfPresent(PotionType.class, split.get(0).trim().toUpperCase(Locale.ENGLISH)).or(PotionType.SLOWNESS);
                        boolean extended = split.size() != 1 && Boolean.parseBoolean(split.get(1).trim());
                        boolean splash = split.size() > 2 && Boolean.parseBoolean(split.get(2).trim());

                        item = (new Potion(type, level, splash, extended)).toItemStack(1);
                    }
                }
            }
        } else if (meta instanceof BlockStateMeta) {
            BlockStateMeta bsm = (BlockStateMeta) meta;
            BlockState state = bsm.getBlockState();

            if (state instanceof CreatureSpawner) {
                CreatureSpawner spawner = (CreatureSpawner) state;
                spawner.setSpawnedType(Enums.getIfPresent(EntityType.class, config.getString("spawner").toUpperCase(Locale.ENGLISH)).orNull());
                spawner.update(true);
                bsm.setBlockState(spawner);
            } else if (supports(11) && state instanceof ShulkerBox) {
                ConfigurationSection shulkerSection = config.getConfigurationSection("contents");
                if (shulkerSection != null) {
                    ShulkerBox box = (ShulkerBox) state;
                    for (String key : shulkerSection.getKeys(false)) {
                        ItemStack boxItem = deserialize(shulkerSection.getConfigurationSection(key));
                        int slot = toInt(key, 0);
                        box.getInventory().setItem(slot, boxItem);
                    }
                    box.update(true);
                    bsm.setBlockState(box);
                }
            } else if (state instanceof Banner) {
                Banner banner = (Banner) state;
                ConfigurationSection patterns = config.getConfigurationSection("patterns");
                if (!supports(14)) {
                    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/diff/src/main/java/org/bukkit/craftbukkit/block/CraftBanner.java?until=b3dc236663a55450c69356e660c0c84f0abbb3aa
                    banner.setBaseColor(DyeColor.WHITE);
                }

                //System.out.println("patterns are "  + patterns);
                if (patterns != null) {
                    for (String pattern : patterns.getKeys(false)) {
                        PatternType type = PatternType.getByIdentifier(pattern);
                        if (type == null)
                            type = Enums.getIfPresent(PatternType.class, pattern.toUpperCase(Locale.ENGLISH)).or(PatternType.BASE);
                        DyeColor color = Enums.getIfPresent(DyeColor.class, patterns.getString(pattern).toUpperCase(Locale.ENGLISH)).or(DyeColor.WHITE);

                        banner.addPattern(new Pattern(color, type));
                    }

                    banner.update(true);
                    bsm.setBlockState(banner);
                }
            }
        } else if (meta instanceof FireworkMeta) {
            FireworkMeta firework = (FireworkMeta) meta;
            firework.setPower(config.getInt("power"));

            ConfigurationSection fireworkSection = config.getConfigurationSection("firework");
            if (fireworkSection != null) {
                FireworkEffect.Builder builder = FireworkEffect.builder();
                for (String fws : fireworkSection.getKeys(false)) {
                    ConfigurationSection fw = config.getConfigurationSection("firework." + fws);

                    builder.flicker(fw.getBoolean("flicker"));
                    builder.trail(fw.getBoolean("trail"));
                    builder.with(Enums.getIfPresent(FireworkEffect.Type.class, fw.getString("type")
                                    .toUpperCase(Locale.ENGLISH))
                            .or(FireworkEffect.Type.STAR));

                    ConfigurationSection colorsSection = fw.getConfigurationSection("colors");
                    List<String> fwColors = colorsSection.getStringList("base");
                    List<Color> colors = new ArrayList<>(fwColors.size());
                    for (String colorStr : fwColors) colors.add(parseColor(colorStr));
                    builder.withColor(colors);

                    fwColors = colorsSection.getStringList("fade");
                    colors = new ArrayList<>(fwColors.size());
                    for (String colorStr : fwColors) colors.add(parseColor(colorStr));
                    builder.withFade(colors);

                    firework.addEffect(builder.build());
                }
            }
        } else if (meta instanceof BookMeta) {
            BookMeta book = (BookMeta) meta;
            ConfigurationSection bookInfo = config.getConfigurationSection("book");

            if (bookInfo != null) {
                book.setTitle(bookInfo.getString("title"));
                book.setAuthor(bookInfo.getString("author"));
                book.setPages(bookInfo.getStringList("pages"));

                if (supports(9)) {
                    String generationValue = bookInfo.getString("generation");
                    if (generationValue != null) {
                        BookMeta.Generation generation = Enums.getIfPresent(BookMeta.Generation.class, generationValue).orNull();
                        book.setGeneration(generation);
                    }
                }
            }
        } else if (meta instanceof MapMeta) {
            MapMeta map = (MapMeta) meta;
            ConfigurationSection mapSection = config.getConfigurationSection("map");

            if (mapSection != null) {
                map.setScaling(mapSection.getBoolean("scaling"));
                if (supports(11)) {
                    if (mapSection.contains("location")) map.setLocationName(mapSection.getString("location"));
                    if (mapSection.contains("color")) {
                        Color color = parseColor(mapSection.getString("color"));
                        map.setColor(color);
                    }
                }

                if (supports(14)) {
                    ConfigurationSection view = mapSection.getConfigurationSection("view");
                    if (view != null) {
                        World world = Bukkit.getWorld(view.getString("world"));
                        if (world != null) {
                            MapView mapView = Bukkit.createMap(world);
                            mapView.setWorld(world);
                            mapView.setScale(Enums.getIfPresent(MapView.Scale.class, view.getString("scale")).or(MapView.Scale.NORMAL));
                            mapView.setLocked(view.getBoolean("locked"));
                            mapView.setTrackingPosition(view.getBoolean("tracking-position"));
                            mapView.setUnlimitedTracking(view.getBoolean("unlimited-tracking"));

                            ConfigurationSection centerSection = view.getConfigurationSection("center");
                            mapView.setCenterX(centerSection.getInt("x"));
                            mapView.setCenterZ(centerSection.getInt("z"));

                            map.setMapView(mapView);
                        }
                    }
                }
            }
        } else if (supports(17)) {
            if (meta instanceof AxolotlBucketMeta) {
                AxolotlBucketMeta bucket = (AxolotlBucketMeta) meta;
                String variantStr = config.getString("color");
                if (variantStr != null) {
                    Axolotl.Variant variant = Enums.getIfPresent(Axolotl.Variant.class, variantStr.toUpperCase(Locale.ENGLISH)).or(Axolotl.Variant.BLUE);
                    bucket.setVariant(variant);
                }
            }
        } else if (supports(16)) {
            if (meta instanceof CompassMeta) {
                CompassMeta compass = (CompassMeta) meta;
                compass.setLodestoneTracked(config.getBoolean("tracked"));

                ConfigurationSection lodestone = config.getConfigurationSection("lodestone");
                if (lodestone != null) {
                    World world = Bukkit.getWorld(lodestone.getString("world"));
                    double x = lodestone.getDouble("x");
                    double y = lodestone.getDouble("y");
                    double z = lodestone.getDouble("z");
                    compass.setLodestone(new Location(world, x, y, z));
                }
            }
        } else if (supports(15)) {
            if (meta instanceof SuspiciousStewMeta) {
                SuspiciousStewMeta stew = (SuspiciousStewMeta) meta;
                for (String effects : config.getStringList("effects")) {
                    XPotion.Effect effect = XPotion.parseEffect(effects);
                    if (effect.hasChance()) stew.addCustomEffect(effect.getEffect(), true);
                }
            }
        } else if (supports(14)) {
            if (meta instanceof CrossbowMeta) {
                CrossbowMeta crossbow = (CrossbowMeta) meta;
                for (String projectiles : config.getConfigurationSection("projectiles").getKeys(false)) {
                    ItemStack projectile = deserialize(config.getConfigurationSection("projectiles." + projectiles));
                    crossbow.addChargedProjectile(projectile);
                }
            } else if (meta instanceof TropicalFishBucketMeta) {
                TropicalFishBucketMeta tropical = (TropicalFishBucketMeta) meta;
                DyeColor color = Enums.getIfPresent(DyeColor.class, config.getString("color")).or(DyeColor.WHITE);
                DyeColor patternColor = Enums.getIfPresent(DyeColor.class, config.getString("pattern-color")).or(DyeColor.WHITE);
                TropicalFish.Pattern pattern = Enums.getIfPresent(TropicalFish.Pattern.class, config.getString("pattern")).or(TropicalFish.Pattern.BETTY);

                tropical.setBodyColor(color);
                tropical.setPatternColor(patternColor);
                tropical.setPattern(pattern);
            }
            // Apparently Suspicious Stew was never added in 1.14
        } else if (!supports(13)) {
            // Spawn Eggs
            if (supports(11)) {
                if (meta instanceof SpawnEggMeta) {
                    String creatureName = config.getString("creature");
                    if (!Strings.isNullOrEmpty(creatureName)) {
                        SpawnEggMeta spawnEgg = (SpawnEggMeta) meta;
                        com.google.common.base.Optional<EntityType> creature = Enums.getIfPresent(EntityType.class, creatureName.toUpperCase(Locale.ENGLISH));
                        if (creature.isPresent()) spawnEgg.setSpawnedType(creature.get());
                    }
                }
            } else {
                MaterialData data = item.getData();
                if (data instanceof SpawnEgg) {
                    String creatureName = config.getString("creature");
                    if (!Strings.isNullOrEmpty(creatureName)) {
                        SpawnEgg spawnEgg = (SpawnEgg) data;
                        com.google.common.base.Optional<EntityType> creature = Enums.getIfPresent(EntityType.class, creatureName.toUpperCase(Locale.ENGLISH));
                        if (creature.isPresent()) spawnEgg.setSpawnedType(creature.get());
                        item.setData(data);
                    }
                }
            }
        }

        // Display Name
        String name = config.getString("name");
        if (!Strings.isNullOrEmpty(name)) {
            String translated = translator.apply(name);

            // MCStarrySky start - Apply TabooLib Components
            // meta.setDisplayName(translated);
            NMSItemRawKt.setDisplayName(meta, UtilKt.component(translated).buildColored(t -> Unit.INSTANCE));
            // MCStarrySky end

        } else if (name != null && name.isEmpty())
            meta.setDisplayName(" "); // For GUI easy access configuration purposes

        // Unbreakable
        if (supports(11)) meta.setUnbreakable(config.getBoolean("unbreakable"));

        // Custom Model Data
        if (supports(14)) {
            int modelData = config.getInt("custom-model-data");
            if (modelData != 0) meta.setCustomModelData(modelData);
        }

        // Lore
        if (config.contains("lore")) {
            List<String> translatedLore;
            List<String> lores = config.getStringList("lore");
            if (!lores.isEmpty()) {
                translatedLore = new ArrayList<>(lores.size());

                for (String lore : lores) {
                    if (lore.isEmpty()) {
                        translatedLore.add(" ");
                        continue;
                    }

                    for (String singleLore : splitNewLine(lore)) {
                        if (singleLore.isEmpty()) {
                            translatedLore.add(" ");
                            continue;
                        }
                        translatedLore.add(translator.apply(singleLore));
                    }
                }
            } else {
                String lore = config.getString("lore");
                translatedLore = new ArrayList<>(10);

                if (!Strings.isNullOrEmpty(lore)) {
                    for (String singleLore : splitNewLine(lore)) {
                        if (singleLore.isEmpty()) {
                            translatedLore.add(" ");
                            continue;
                        }
                        translatedLore.add(translator.apply(singleLore));
                    }
                }
            }

            // MCStarrySky start - Apply TabooLib Components
            // meta.setLore(translatedLore);
            NMSItemRawKt.setLore(meta, translatedLore.stream()
                    .map(line -> UtilKt.component(line).buildColored(t -> Unit.INSTANCE))
                    .collect(Collectors.toList()));
            // MCStarrySky end
        }

        // Enchantments
        ConfigurationSection enchants = config.getConfigurationSection("enchants");
        if (enchants != null) {
            for (String ench : enchants.getKeys(false)) {
                Optional<XEnchantment> enchant = XEnchantment.matchXEnchantment(ench);
                enchant.ifPresent(xEnchantment -> meta.addEnchant(xEnchantment.getEnchant(), enchants.getInt(ench), true));
            }
        } else if (config.getBoolean("glow")) {
            meta.addEnchant(XEnchantment.DURABILITY.getEnchant(), 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // HIDE_UNBREAKABLE is not for UNBREAKING enchant.
        }

        // Enchanted Books
        ConfigurationSection enchantment = config.getConfigurationSection("stored-enchants");
        if (enchantment != null) {
            for (String ench : enchantment.getKeys(false)) {
                Optional<XEnchantment> enchant = XEnchantment.matchXEnchantment(ench);
                EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
                enchant.ifPresent(xEnchantment -> book.addStoredEnchant(xEnchantment.getEnchant(), enchantment.getInt(ench), true));
            }
        }

        // Flags
        List<String> flags = config.getStringList("flags");
        if (!flags.isEmpty()) {
            for (String flag : flags) {
                flag = flag.toUpperCase(Locale.ENGLISH);
                if (flag.equals("ALL")) {
                    meta.addItemFlags(ITEM_FLAGS);
                    break;
                }

                ItemFlag itemFlag = Enums.getIfPresent(ItemFlag.class, flag).orNull();
                if (itemFlag != null) meta.addItemFlags(itemFlag);
            }
        } else {
            String allFlags = config.getString("flags");
            if (!Strings.isNullOrEmpty(allFlags) && allFlags.equalsIgnoreCase("ALL"))
                meta.addItemFlags(ITEM_FLAGS);
        }

        // Atrributes - https://minecraft.gamepedia.com/Attribute
        if (supports(13)) {
            ConfigurationSection attributes = config.getConfigurationSection("attributes");
            if (attributes != null) {
                for (String attribute : attributes.getKeys(false)) {
                    Attribute attributeInst = Enums.getIfPresent(Attribute.class, attribute.toUpperCase(Locale.ENGLISH)).orNull();
                    if (attributeInst == null) continue;
                    ConfigurationSection section = attributes.getConfigurationSection(attribute);
                    if (section == null) continue;

                    String attribId = section.getString("id");
                    UUID id = attribId != null ? UUID.fromString(attribId) : UUID.randomUUID();
                    EquipmentSlot slot = section.getString("slot") != null ? Enums.getIfPresent(EquipmentSlot.class, section.getString("slot")).or(EquipmentSlot.HAND) : null;

                    AttributeModifier modifier = new AttributeModifier(
                            id,
                            section.getString("name"),
                            section.getInt("amount"),
                            Enums.getIfPresent(AttributeModifier.Operation.class, section.getString("operation"))
                                    .or(AttributeModifier.Operation.ADD_NUMBER),
                            slot);

                    meta.addAttributeModifier(attributeInst, modifier);
                }
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Converts a {@code Map<?, ?>} into a {@code ConfigurationSection}.
     *
     * @param map the map to convert.
     * @return a {@code ConfigurationSection} containing the map values.
     */
    @NotNull
    public static ConfigurationSection mapToConfigSection(@NotNull Map<?, ?> map) {
        ConfigurationSection config = Configuration.Companion.empty(Type.YAML, false);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value == null) continue;
            if (value instanceof Map<?, ?>) {
                value = mapToConfigSection((Map<?, ?>) value);
            }

            config.set(key, value);
        }

        return config;
    }

    /**
     * Converts a {@code ConfigurationSection} into a {@code Map<String, Object>}.
     *
     * @param config the configuration section to convert.
     * @return a {@code Map<String, Object>} containing the configuration section values.
     */
    @NotNull
    private static Map<String, Object> configSectionToMap(@NotNull ConfigurationSection config) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (String key : config.getKeys(false)) {
            Object value = config.get(key);

            if (value == null) continue;
            if (value instanceof ConfigurationSection) {
                value = configSectionToMap((ConfigurationSection) value);
            }

            map.put(key, value);
        }

        return map;
    }

    /**
     * Parses RGB color codes from a string.
     * This only works for 1.13 and above.
     *
     * @param str the RGB string.
     * @return a color based on the RGB.
     * @since 1.1.0
     */
    @NotNull
    public static Color parseColor(@Nullable String str) {
        if (Strings.isNullOrEmpty(str)) return Color.BLACK;
        List<String> rgb = split(str.replace(" ", ""), ',');
        if (rgb.size() < 3) return Color.WHITE;
        return Color.fromRGB(toInt(rgb.get(0), 0), toInt(rgb.get(1), 0), toInt(rgb.get(2), 0));
    }
}