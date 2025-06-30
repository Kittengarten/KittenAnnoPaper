package com.iluyf.mc;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

public class Anno extends JavaPlugin implements Listener {

    public static final String WTA = "世界树纪元";
    private static final String KITTEN_DAY = "2017 年 04 月 25 日";
    private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日");
    private static final long KITTEN_EPOCH = LocalDate.parse(KITTEN_DAY, formater).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    private static final int SECONDS_PER_DAY = 85653;
    private static final int TIME_RATIO = 72; // Minecraft 72 倍时间流速
    public static final JoinConfiguration JOIN_CONF = JoinConfiguration.noSeparators();
    BukkitRunnable scheduler;
    static long day;
    static FileConfiguration annoConfig;

    // 获取天数戳
    public static long getDay() {
        Instant now = Instant.now();
        return (TIME_RATIO * (now.getEpochSecond() - KITTEN_EPOCH)
                + now.toEpochMilli() % 1000 * TIME_RATIO / 1000)
                / SECONDS_PER_DAY;
    }

    // 获取播报内容
    public static Component getAnnoBroadcast() {
        day = getDay();
        return Compute.output(day);
    }

    @Override
    public void onLoad() {
        getServer().sendMessage(Component.text(WTA + "已加载。", NamedTextColor.AQUA));
    }

    @Override
    public void onEnable() {
        this.scheduler = new BukkitRunnable() {
            @Override
            public void run() {
                if (getDay() > day) {
                    getServer().sendMessage(getAnnoBroadcast());
                    Reward reward = new Reward();
                    reward.giveReward(day);
                    reward.givePLISReward(day);
                }
            }
        };
        this.scheduler.runTaskTimer(this, 100L, 100L);
        PluginCommand command = this.getCommand("kittenanno");
        if (command != null) {
            command.setExecutor(new AnnoCommand());
        } else {
            getLogger().severe("无法使用命令 'kittenanno'：未在 plugin.yml 中注册！");
        }
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        annoConfig = getConfig();
        getServer().sendMessage(Component.text(WTA + "开始运行。", NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        if (scheduler != null && !scheduler.isCancelled()) {
            scheduler.cancel();
        }
        getServer().sendMessage(Component.text(WTA + "暂停运行。", NamedTextColor.RED));
    }

    class AnnoCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            String commandName = cmd.getName().toLowerCase();
            if (!commandName.equals("kittenanno") && !commandName.equals("anno")) {
                return false;
            }
            sender.sendMessage(sender.hasPermission("kittenanno.anno")
                    ? getAnnoBroadcast()
                    : Component.text("无权使用命令！"));
            return true;
        }
    }

    final class JoinListener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent event) throws ParseException {
            Player player = event.getPlayer();
            String welcome_messages = annoConfig.getString("welcome_messages");
            player.sendMessage(welcome_messages != null && !welcome_messages.trim().isEmpty()
                    ? welcome_messages : "欢迎来到幼喵园！");
            player.sendMessage(Component.text("今天是").append(getAnnoBroadcast()));
        }
    }
}
