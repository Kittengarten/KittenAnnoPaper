package tk.iluyf.mc;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.ChatColor.*;

public final class KittenAnno extends JavaPlugin implements Listener {

    private void annoBroadcast(String annoString) {
        Bukkit.broadcastMessage(annoString);
    }

    private String getAnnoBroadcast() {
        long annoTick = Bukkit.getWorlds().get(0).getGameTime();
        long annoDay = 1 + annoTick / 24000;
        AnnoCompute annoCompute_ = new AnnoCompute();
        return annoCompute_.main(annoDay);
    }

    @Override
    public void onLoad() {
        getLogger().info(AQUA + "世界树纪元已加载。");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("kittenanno").setExecutor(new AnnoCommand());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getLogger().info(GREEN + "世界树纪元开始运行。");
        new BukkitRunnable() {
            @Override
            public void run() {
                annoBroadcast(getAnnoBroadcast());
            }
        }.runTaskTimer(this, 100L, 24000L);

    }

    @Override
    public void onDisable() {
        getLogger().info(RED + "世界树纪元暂停运行。");
    }

    public class AnnoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (sender.hasPermission("kittenanno.anno")) {
                if (cmd.getName().equalsIgnoreCase("anno") || cmd.getName().equalsIgnoreCase("kittenanno")) {
                    sender.sendMessage(getAnnoBroadcast());
                    return true;
                }
            }
            return false;
        }
    }

    public final class JoinListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            event.getPlayer().sendMessage(getConfig().getString("welcome_messages"));
            event.getPlayer().sendMessage("今天是" + getAnnoBroadcast());
        }
    }
}
