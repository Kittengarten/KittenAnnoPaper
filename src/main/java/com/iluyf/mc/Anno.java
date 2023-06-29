package com.iluyf.mc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.kyori.adventure.text.format.Style;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class Anno extends JavaPlugin implements Listener {
    private final static String kittenDay = "2017 年 04 月 25 日";
    private final static int secondsPerDay = 85653;

    public static long day;
    public static FileConfiguration annoConfig;

    // 获取天数戳
    public long getDay() throws ParseException {
        long unix = System.currentTimeMillis() / 1000L - 8 * 3600; // 排除时区影响（硬编码，仅适用于 UTC+8）
        DateFormat df = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
        long epoch = df.parse(kittenDay).getTime() / 1000L; // 初始时间戳
        long wtaUnix = 72 * (unix - epoch) + System.currentTimeMillis() % 1000 * 72 / 1000;
        return wtaUnix / secondsPerDay;
    }

    // 获取播报内容
    public String getAnnoBroadcast() {
        try {
            day = getDay();
            Compute annoCompute = new Compute();
            return annoCompute.output(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onLoad() {
        getLogger().info(AQUA + "世界树纪元已加载。" + Style.empty());
    }

    @Override
    public void onEnable() {
        this.getCommand("kittenanno").setExecutor(new AnnoCommand());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        annoConfig = getConfig();
        getLogger().info(GREEN + "世界树纪元开始运行。" + Style.empty());
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (getDay() > day) {
                        Bukkit.getServer().broadcast(Component.text(getAnnoBroadcast()));
                        new Reward().giveReward(day);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, 100L, 100L);
    }

    @Override
    public void onDisable() {
        getLogger().info(RED + "世界树纪元暂停运行。" + Style.empty());
    }

    public class AnnoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (cmd.getName().equalsIgnoreCase("kittenanno") || cmd.getName().equalsIgnoreCase("anno")) {
                if (sender.hasPermission("kittenanno.anno")) {
                    sender.sendMessage(getAnnoBroadcast());
                    return true;
                }
            }
            return false;
        }
    }

    public final class JoinListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) throws ParseException {
            event.getPlayer().sendMessage(annoConfig.getString("welcome_messages"));
            event.getPlayer().sendMessage("今天是" + getAnnoBroadcast());
        }
    }
}
