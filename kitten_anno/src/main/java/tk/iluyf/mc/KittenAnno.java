package tk.iluyf.mc;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.*;

public final class KittenAnno extends JavaPlugin {
    long annoTick;
    long annoTickNew;
    long annoDay;
    long annoDayNew;
    String annoString;

    private void annoBroadcast(String annoString) {
        Bukkit.broadcastMessage(annoString);
    }

    @Override
    public void onLoad() {
        getLogger().info(AQUA + "世界树纪元已加载。");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("kittenanno").setExecutor(new AnnoCommand());
        getLogger().info(GREEN + "世界树纪元开始运行。");

        new BukkitRunnable() {
            @Override
            public void run() {
                annoTickNew = Bukkit.getWorlds().get(0).getGameTime();
                annoDay = 1 + annoTick / 24000;
                annoDayNew = 1 + annoTickNew / 24000;
                if (annoDayNew > annoDay) {
                    AnnoCompute annoCompute_ = new AnnoCompute();
                    String annoString = annoCompute_.main(annoDayNew);
                    annoBroadcast(annoString);
                }
                annoTick = annoTickNew;
            }
        }.runTaskTimer(this, 100L, 1000L);

    }

    @Override
    public void onDisable() {
        getLogger().info(RED + "世界树纪元暂停运行。");
    }

    public class AnnoCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (cmd.getName().equalsIgnoreCase("anno") || cmd.getName().equalsIgnoreCase("kittenanno")) {
                annoTickNew = Bukkit.getWorlds().get(0).getGameTime();
                annoDay = 1 + annoTick / 24000;
                annoDayNew = 1 + annoTickNew / 24000;
                annoTick = annoTickNew;
                AnnoCompute annoCompute_ = new AnnoCompute();
                String annoString = annoCompute_.main(annoDayNew);
                sender.sendMessage(annoString);
                return true;
            }
            return false;
        }
    }

}
