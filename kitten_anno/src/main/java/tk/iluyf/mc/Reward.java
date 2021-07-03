package tk.iluyf.mc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.*;

public class Reward {
    public ItemStack randomReward(long seed, String playerName, long annoDay) {
        String monthReward[] = new String[3];
        short month_Cycle;
        int randomNumber = new Random(seed - annoDay).nextInt(100);
        for (month_Cycle = 0; month_Cycle <= AnnoCompute.commonYearMonthCount; ++month_Cycle) {
            if (randomNumber < Short
                    .valueOf(KittenAnno.config.getString("reward.month" + String.valueOf(month_Cycle) + ".weight"))) {
                monthReward[0] = KittenAnno.config
                        .getString("reward.month" + String.valueOf(month_Cycle - 1) + ".name");
                monthReward[1] = KittenAnno.config.getString("reward.month" + String.valueOf(month_Cycle - 1) + ".id");
                monthReward[2] = KittenAnno.config
                        .getString("reward.month" + String.valueOf(month_Cycle - 1) + ".quantity");
                break;
            }
            if (month_Cycle == AnnoCompute.commonYearMonthCount) {
                monthReward[0] = KittenAnno.config.getString("reward.month" + String.valueOf(month_Cycle) + ".name");
                monthReward[1] = KittenAnno.config.getString("reward.month" + String.valueOf(month_Cycle) + ".id");
                monthReward[2] = KittenAnno.config
                        .getString("reward.month" + String.valueOf(month_Cycle) + ".quantity");
            }
        }
        Bukkit.broadcastMessage(BOLD + "给予" + RESET + BLUE + UNDERLINE + "[" + playerName + "]" + RESET + BOLD
                + "月中随机奖励：" + RESET + YELLOW + UNDERLINE + "[" + monthReward[0] + "]" + RESET + GREEN + monthReward[2]
                + RESET + "个");
        return rewardOutput(monthReward, seed, annoDay);
    }

    public ItemStack normalReward(long seed, String playerName, long annoDay) {
        String monthReward[] = new String[3];
        short normalNumber = (short) new AnnoCompute().annoToValue(annoDay)[1];
        for (short month_Cycle = 0; month_Cycle <= AnnoCompute.commonYearMonthCount; ++month_Cycle) {
            monthReward[0] = KittenAnno.config.getString("reward.month" + String.valueOf(normalNumber) + ".name");
            monthReward[1] = KittenAnno.config.getString("reward.month" + String.valueOf(normalNumber) + ".id");
            monthReward[2] = KittenAnno.config.getString("reward.month" + String.valueOf(normalNumber) + ".quantity");
        }
        Bukkit.broadcastMessage(BOLD + "给予" + RESET + BLUE + UNDERLINE + "[" + playerName + "]" + RESET + BOLD
                + "月初固定奖励：" + RESET + YELLOW + UNDERLINE + "[" + monthReward[0] + "]" + RESET + GREEN + monthReward[2]
                + RESET + "个");
        return rewardOutput(monthReward, seed, annoDay);
    }

    private ItemStack rewardOutput(String monthReward[], long seed, Long annoDay) {
        if (monthReward[0].equals("随机音乐唱片")) {
            ItemStack itemReward = music_disc(Short.valueOf(monthReward[2]), seed - annoDay);
            return itemReward;
        } else {
            ItemStack itemReward = new ItemStack(Material.getMaterial(monthReward[1]), Integer.valueOf(monthReward[2]));
            return mending(monthReward[1], itemReward);
        }
    }

    private ItemStack mending(String id, ItemStack itemReward) {
        if (id.equals("ENCHANTED_BOOK")) {
            Enchantment enchantment_ = new EnchantmentWrapper("mending");
            EnchantmentStorageMeta enchantmentStorageMeta_ = (EnchantmentStorageMeta) itemReward.getItemMeta();
            enchantmentStorageMeta_.addStoredEnchant(enchantment_, enchantment_.getMaxLevel(), false);
            itemReward.setItemMeta(enchantmentStorageMeta_);
        }
        return itemReward;
    }

    private ItemStack music_disc(short quantity, long seed) {
        int randomNumber = new Random(seed).nextInt(13);
        String id = KittenAnno.config.getString("reward.month11.ids.id" + String.valueOf(randomNumber));
        ItemStack itemReward = new ItemStack(Material.getMaterial(id), Integer.valueOf(quantity));
        return itemReward;
    }

    public void giveReward(long annoDay) {
        Collection<? extends Player> onlinePlayerList = Bukkit.getOnlinePlayers();
        for (Iterator<? extends Player> iterator_ = onlinePlayerList.iterator(); iterator_.hasNext();) {
            Player player_ = iterator_.next();
            PlayerInventory inventory_ = player_.getInventory();
            switch ((short) new AnnoCompute().annoToValue(annoDay)[2]) {
                case 1:
                    inventory_.addItem(normalReward(player_.getName().hashCode(), player_.getName(), annoDay));
                    break;
                case 11:
                    inventory_.addItem(randomReward(player_.getName().hashCode(), player_.getName(), annoDay));
                    break;
            }
        }
    }
}