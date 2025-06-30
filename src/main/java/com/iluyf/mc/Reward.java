package com.iluyf.mc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

class Reward {

    class MonthReward {

        String name, id, quantity;
    }

    ItemStack randomReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        int randomNumber = new Random(seed - annoDay).nextInt(100);
        for (short monthCycle = 0; monthCycle <= Compute.COMMON_YEAR_MONTH_N; ++monthCycle) {
            if (randomNumber < Short.parseShort(Anno.annoConfig.getString("reward.month" + String.valueOf(monthCycle) + ".weight"))) {
                String last = String.valueOf(monthCycle - 1);
                monthReward.name = Anno.annoConfig.getString("reward.month" + last + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + last + ".id");
                monthReward.quantity = Anno.annoConfig.getString("reward.month" + last + ".quantity");
                break;
            }
            if (monthCycle == Compute.COMMON_YEAR_MONTH_N) {
                String self = String.valueOf(monthCycle);
                monthReward.name = Anno.annoConfig.getString("reward.month" + self + ".name");
                monthReward.id = Anno.annoConfig.getString("reward.month" + self + ".id");
                monthReward.quantity = Anno.annoConfig
                        .getString("reward.month" + self + ".quantity");
            }
        }
        broadcast("月中随机奖励", playerName, monthReward);
        return rewardOutput(monthReward, seed, annoDay);
    }

    ItemStack normalReward(long seed, String playerName, long annoDay) {
        MonthReward monthReward = new MonthReward();
        long normalNumber = Compute.annoToValue(annoDay)[1];
        String normal = String.valueOf(normalNumber);
        for (short monthCycle = 0; monthCycle <= Compute.COMMON_YEAR_MONTH_N; ++monthCycle) {
            monthReward.name = Anno.annoConfig.getString("reward.month" + normal + ".name");
            monthReward.id = Anno.annoConfig.getString("reward.month" + normal + ".id");
            monthReward.quantity = Anno.annoConfig.getString("reward.month" + normal + ".quantity");
        }
        broadcast("月初固定奖励", playerName, monthReward);
        return rewardOutput(monthReward, seed, annoDay);
    }

    ItemStack rewardOutput(MonthReward monthReward, long seed, Long annoDay) {
        if (monthReward.name.equals("随机音乐唱片")) {
            ItemStack itemReward = musicDisc(Short.parseShort(monthReward.quantity), seed - annoDay);
            return itemReward;
        }
        ItemStack itemReward = new ItemStack(Material.getMaterial(monthReward.id.toUpperCase()),
                Integer.parseInt(monthReward.quantity));
        return luckOrEnchantedBook(monthReward, itemReward);
    }

    ItemStack luckOrEnchantedBook(MonthReward monthReward, ItemStack itemReward) {
        if (monthReward.id.equalsIgnoreCase("potion")) {
            PotionMeta potionMeta = (PotionMeta) itemReward.getItemMeta();
            potionMeta.setBasePotionType(PotionType.LUCK);
            itemReward.setItemMeta(potionMeta);
            return itemReward;
        }
        Enchantment enchantment;
        if (monthReward.id.equalsIgnoreCase("ENCHANTED_BOOK")) {
            switch (monthReward.name) {
                case "附魔书（迅捷潜行）" ->
                    enchantment = Enchantment.SWIFT_SNEAK;
                case "附魔书（经验修补）" ->
                    enchantment = Enchantment.MENDING;
                default -> {
                    return itemReward;
                }
            }
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemReward.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, enchantment.getMaxLevel(), false);
            itemReward.setItemMeta(enchantmentStorageMeta);
        }
        return itemReward;
    }

    ItemStack musicDisc(short quantity, long seed) {
        int randomNumber = new Random(seed).nextInt(15);
        String id = Anno.annoConfig.getString("reward.month11.ids.id" + String.valueOf(randomNumber + 1));
        ItemStack itemReward = new ItemStack(Material.getMaterial((id != null ? id : "").toUpperCase()), Integer.valueOf(quantity));
        return itemReward;
    }

    void giveReward(long annoDay) {
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayerList) {
            PlayerInventory inventory = player.getInventory();
            switch ((short) Compute.annoToValue(annoDay)[2]) {
                case 1 ->
                    inventory.addItem(normalReward(player.getName().hashCode(), player.getName(), annoDay));
                case 11 ->
                    inventory.addItem(randomReward(player.getName().hashCode(), player.getName(), annoDay));
            }
        }
    }

    void broadcast(String s, String p, MonthReward r) {
        Bukkit.getServer().sendMessage(Component.join(
                Anno.JOIN_CONF,
                Component.text("给予", null, TextDecoration.BOLD),
                Component.text("[" + p + "]", NamedTextColor.BLUE, TextDecoration.UNDERLINED),
                Component.text(s, null, TextDecoration.BOLD),
                Component.text("[" + r.name + "]", NamedTextColor.YELLOW, TextDecoration.UNDERLINED),
                Component.text(r.quantity, NamedTextColor.GREEN),
                Component.text("个")));
    }

    void givePLISReward(long annoDay) {
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayerList) {
            // 创建新的 StringJoiner 作为命令开头
            interface NewStringJoiner {

                StringJoiner create();
            }
            NewStringJoiner newStringJoiner = () -> {
                return new StringJoiner(" ").add("plis");
            };
            ArrayList<StringJoiner> commandGroup = new ArrayList<>();
            switch ((short) Compute.annoToValue(annoDay)[2]) {
                case 1 -> {
                    // 月初奖励
                    commandGroup.add(newStringJoiner.create().add("giveEnchantedCokes").add("10"));
                    commandGroup.add(newStringJoiner.create().add("giveSecretMedicines").add("1"));
                    commandGroup.add(newStringJoiner.create().add("giveProtectionAmulet").add("10"));
                }
                case 11 -> {
                    // 月中奖励
                    commandGroup.add(newStringJoiner.create().add("giveEnchantedCokes").add("10"));
                    commandGroup.add(newStringJoiner.create().add("giveSecretMedicines").add("1"));
                    commandGroup.add(newStringJoiner.create().add("giveProtectionCard").add("1"));
                }
                default -> // 每日奖励
                    commandGroup.add(newStringJoiner.create().add("giveEnchantedCokes").add("1"));
            }
            for (StringJoiner sj : commandGroup) {
                try {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), sj.add(player.getName()).toString());
                } catch (CommandException e) {
                }
            }
        }
    }
}
