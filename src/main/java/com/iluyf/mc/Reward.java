package com.iluyf.mc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionType;
import net.kyori.adventure.text.Component;
import org.bukkit.potion.PotionData;
import net.kyori.adventure.text.format.Style;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public class Reward {
    class MonthReward {
        String name, id, quantity;
    }

    public ItemStack randomReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        short monthCycle;
        int randomNumber = new Random(seed - annoDay).nextInt(100);
        for (monthCycle = 0; monthCycle <= Compute.commonYearMonthCount; ++monthCycle) {
            if (randomNumber < Short
                    .valueOf(Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".weight"))) {
                monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle - 1) + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle - 1) + ".id");
                monthReward.quantity = Anno.annoConfig
                        .getString("reward.month" + String.valueOf(monthCycle - 1) + ".quantity");
                break;
            }
            if (monthCycle == Compute.commonYearMonthCount) {
                monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".id");
                monthReward.quantity = Anno.annoConfig
                        .getString("reward.month" + String.valueOf(monthCycle) + ".quantity");
            }
        }
        Bukkit.getServer()
                .broadcast(Component.text(BOLD + "给予" + Style.empty() + BLUE + UNDERLINED + "[" + playerName + "]" + Style.empty() + BOLD
                        + "月中随机奖励：" + Style.empty() + YELLOW + UNDERLINED + "[" + monthReward.name + "]" + Style.empty() + GREEN
                        + monthReward.quantity + Style.empty() + "个"));
        return rewardOutput(monthReward, seed, annoDay);
    }

    public ItemStack normalReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        short normalNumber = (short) new Compute().annoToValue(annoDay)[1];
        for (short monthCycle = 0; monthCycle <= Compute.commonYearMonthCount; ++monthCycle) {
            monthReward.name = Anno.annoConfig.getString("reward.month" + String.valueOf(normalNumber) + ".name");
            monthReward.id = Anno.annoConfig.getString("reward.month" + String.valueOf(normalNumber) + ".id");
            monthReward.quantity = Anno.annoConfig.getString("reward.month" + String.valueOf(normalNumber) + ".quantity");
        }
        Bukkit.getServer()
                .broadcast(Component.text((BOLD + "给予" + Style.empty() + BLUE + UNDERLINED + "[" + playerName + "]" + Style.empty()
                        + BOLD + "月初固定奖励：" + Style.empty() + YELLOW + UNDERLINED + "[" + monthReward.name + "]" + Style.empty() + GREEN
                        + monthReward.quantity + Style.empty() + "个")));
        return rewardOutput(monthReward, seed, annoDay);
    }

    private ItemStack rewardOutput(MonthReward monthReward, long seed, Long annoDay) {
        if (monthReward.name.equals("随机音乐唱片")) {
            ItemStack itemReward = musicDisc(Short.valueOf(monthReward.quantity), seed - annoDay);
            return itemReward;
        } else {
            ItemStack itemReward = new ItemStack(Material.getMaterial(monthReward.id.toUpperCase()),
                    Integer.valueOf(monthReward.quantity));
            return mendingOrLuck(monthReward, itemReward);
        }
    }

    private ItemStack mendingOrLuck(MonthReward monthReward, ItemStack itemReward) {
        Enchantment enchantment;
        if (monthReward.id.equalsIgnoreCase("ENCHANTED_BOOK")) {
            if (monthReward.name.equals("附魔书（迅捷潜行）")) {
                enchantment = new EnchantmentWrapper("swift_sneak");
            } else {
                enchantment = new EnchantmentWrapper("mending");
            }
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemReward.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, enchantment.getMaxLevel(), false);
            itemReward.setItemMeta(enchantmentStorageMeta);
        } else if (monthReward.id.equalsIgnoreCase("potion")) {
            PotionData potionData = new PotionData(PotionType.LUCK);
            PotionMeta potionMeta = (PotionMeta) itemReward.getItemMeta();
            potionMeta.setBasePotionData(potionData);
            itemReward.setItemMeta(potionMeta);
        }
        return itemReward;
    }

    private ItemStack musicDisc(short quantity, long seed) {
        int randomNumber = new Random(seed).nextInt(15);
        String id = Anno.annoConfig.getString("reward.month11.ids.id" + String.valueOf(randomNumber + 1));
        ItemStack itemReward = new ItemStack(Material.getMaterial(id.toUpperCase()), Integer.valueOf(quantity));
        return itemReward;
    }

    public void giveReward(long annoDay) {
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for (Iterator<? extends Player> iterator = onlinePlayerList.iterator(); iterator.hasNext();) {
            Player player = iterator.next();
            PlayerInventory inventory = player.getInventory();
            switch ((short) new Compute().annoToValue(annoDay)[2]) {
                case 1:
                    inventory.addItem(normalReward(player.getName().hashCode(), player.getName(), annoDay));
                    break;
                case 11:
                    inventory.addItem(randomReward(player.getName().hashCode(), player.getName(), annoDay));
                    break;
            }
        }
    }
}