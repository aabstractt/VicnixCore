package net.vicnix.core;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.vicnix.core.command.CoreAdminCommand;
import net.vicnix.core.listener.PlayerJoinListener;
import net.vicnix.core.provider.MysqlProvider;
import net.vicnix.core.utils.VicnixIcon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VicnixCore extends JavaPlugin {

    @Getter
    private static VicnixCore instance;
    @Getter
    private static LuckPerms luckPerms;

    public final static Map<UUID, VicnixIcon> iconList = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(false);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        try {
            MysqlProvider.getInstance().init();
        } catch (SQLException e) {
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.shutdown();

            return;
        }

        registerCommand(new CoreAdminCommand("coreadmin"));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public static void updateTabEntry(UUID uuid) {
        Player target = Bukkit.getPlayer(uuid);

        if (target == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTabEntry(target, player, 1);
        }
    }

    public static void updateTabEntry(Player target, Player updateTo, int priority) {
        String pName = target.getName();

        String name = priority + pName.charAt(0) + UUID.randomUUID().toString().substring(0, 10);

        PacketPlayOutScoreboardTeam pk = new PacketPlayOutScoreboardTeam();

        String playerPrefix = instance.getPlayerPrefix(target);
        String lastColor = ChatColor.getLastColors(playerPrefix);

        String suffix = getIconFormat(target.getUniqueId());

        setField(pk, "a", name);
        setField(pk, "b", lastColor + pName);
        setField(pk, "c", playerPrefix);

        if (suffix != null) {
            setField(pk, "d", " " + suffix);
        }

        setField(pk, "e", "ALWAYS");
        setField(pk, "g", Collections.singletonList(target.getName()));
        setField(pk, "h", 0);
        setField(pk, "i", 0);

        try {
            Object handle = updateTo.getClass().getMethod("getHandle").invoke(updateTo);

            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(playerConnection, pk);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    private static void setField(Object packet, String field, Object value) {
        try {
            Field fieldObject = packet.getClass().getDeclaredField(field);

            fieldObject.setAccessible(true);
            fieldObject.set(field, value);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public String getPlayerPrefix(Player player) {
        User user = VicnixCore.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);

        String prefix = user.getCachedData().getMetaData().getPrefix();

        if (prefix == null) {
            prefix = ChatColor.GRAY.toString();
        }

        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public static String getIconFormat(UUID uuid) {
        MysqlProvider provider = MysqlProvider.getInstance();

        VicnixIcon icon = null;

        if (iconList.containsKey(uuid)) {
            icon = iconList.get(uuid);
        } else {
            iconList.put(uuid, VicnixIcon.of(provider.getPlayerIconId(uuid)));
        }

        return icon != null ? icon.getFormat() : null;
    }

    public void registerCommand(Command command) {
        SimpleCommandMap simpleCommandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();

        simpleCommandMap.register(getDescription().getName(), command);
    }
}