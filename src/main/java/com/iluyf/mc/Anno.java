package com.iluyf.mc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.bukkit.ChatColor.*;

public class Anno extends JavaPlugin implements Listener {
    public static long day;

    // 获取天数戳
    public long getDay() throws ParseException {
        int secondsPerDay = 85653;
        String kittenDay = "2017年04月25日";
        long unix = System.currentTimeMillis() / 1000L - 8 * 3600; // 排除时区影响（硬编码，仅适用于 UTC+8）
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        long epoch = df.parse(kittenDay).getTime() / 1000L; // 初始时间戳
        long wtaUnix = 72 * (unix - epoch) + System.currentTimeMillis() % 1000 * 72 / 1000;
        return wtaUnix / secondsPerDay;
    }

    public String getAnnoBroadcast() throws ParseException {
        day = getDay();
        Compute annoCompute_ = new Compute();
        return annoCompute_.output(day + 1);
    }

    @Override
    public void onLoad() {
        getLogger().info(AQUA + "世界树纪元已加载。");
    }

    @Override
    public void onEnable() {
        this.getCommand("kittenanno").setExecutor(new AnnoCommand());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getLogger().info(GREEN + "世界树纪元开始运行。");
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (getDay() > day) {
                        try {
                            Bukkit.getServer().broadcast(Component.text(getAnnoBroadcast()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        new Reward().giveReward(day + 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, 100L, 100L);
    }

    @Override
    public void onDisable() {
        getLogger().info(RED + "世界树纪元暂停运行。");
    }

    public class AnnoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (cmd.getName().equalsIgnoreCase("kittenanno") || cmd.getName().equalsIgnoreCase("anno")) {
                if (sender.hasPermission("kittenanno.anno")) {
                    try {
                        sender.sendMessage(getAnnoBroadcast());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public final class JoinListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) throws ParseException {
            event.getPlayer().sendMessage(getConfig().getString("welcome_messages"));
            event.getPlayer().sendMessage("今天是" + getAnnoBroadcast());
        }
    }
}
