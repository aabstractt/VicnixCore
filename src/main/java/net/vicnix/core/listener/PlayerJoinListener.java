package net.vicnix.core.listener;

import net.vicnix.core.VicnixCore;
import net.vicnix.core.factory.EmoteFactory;
import net.vicnix.core.provider.MysqlProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(VicnixCore.getInstance(), () -> MysqlProvider.getInstance().createUser(player.getName(), player.getUniqueId()));

        String emotePrefix = getEmotePrefix(player.getUniqueId());

        if (emotePrefix == null) {
            return;
        }

        emotePrefix = ChatColor.translateAlternateColorCodes('&', emotePrefix);

        for (Player p : Bukkit.getOnlinePlayers()) {
            EmoteFactory.getInstance().sendNameTag(player, emotePrefix, emotePrefix, p, player.isOp() ? 0 : 1);
        }
    }

    private String getEmotePrefix(UUID uuid) {
        MysqlProvider provider = MysqlProvider.getInstance();

        int emoteId = provider.getPlayerEmoteId(uuid);

        if (emoteId == -1) {
            return null;
        }

        return provider.getEmoteFormat(emoteId);
    }
}