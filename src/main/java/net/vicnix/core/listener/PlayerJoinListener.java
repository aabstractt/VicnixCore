package net.vicnix.core.listener;

import net.vicnix.core.VicnixCore;
import net.vicnix.core.provider.MysqlProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(VicnixCore.getInstance(), () -> {
            MysqlProvider.getInstance().createUser(player.getName(), player.getUniqueId());

            for (Player p : Bukkit.getOnlinePlayers()) {
                VicnixCore.updateTabEntry(player, p, 0);

                VicnixCore.updateTabEntry(p, player, 0);
            }
        });
    }
}