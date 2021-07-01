package net.vicnix.core.factory;

import lombok.Getter;
import net.luckperms.api.model.user.User;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.vicnix.core.VicnixCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class EmoteFactory {

    @Getter
    private final static EmoteFactory instance = new EmoteFactory();

    public void init() {

    }

    public String getPlayerPrefix(Player player) {
        User user = VicnixCore.getLuckPerms().getPlayerAdapter(Player.class).getUser(player);

        String prefix = user.getCachedData().getMetaData().getPrefix();

        if (prefix == null) {
            prefix = ChatColor.GRAY.toString();
        }

        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public void sendNameTag(Player player, String prefix, String suffix, Player to, int priority) {
        String pName = player.getName();

        String name = priority + pName.charAt(0) + UUID.randomUUID().toString().substring(0, 10);

        PacketPlayOutScoreboardTeam pk = new PacketPlayOutScoreboardTeam();

        Class<? extends PacketPlayOutScoreboardTeam> packetClass = pk.getClass();

        String playerPrefix = getPlayerPrefix(player);
        String lastColor = ChatColor.getLastColors(playerPrefix);

        setField(pk, Objects.requireNonNull(getField(packetClass, "a")), name);
        setField(pk, Objects.requireNonNull(getField(packetClass, "b")), lastColor + pName);
        setField(pk, Objects.requireNonNull(getField(packetClass, "c")), playerPrefix);
        setField(pk, Objects.requireNonNull(getField(packetClass, "d")), " " + suffix);
        setField(pk, Objects.requireNonNull(getField(packetClass, "e")), "ALWAYS");
        setField(pk, Objects.requireNonNull(getField(packetClass, "g")), new ArrayList<String>() {{add(player.getName());}});
        setField(pk, Objects.requireNonNull(getField(packetClass, "h")), 0);
        setField(pk, Objects.requireNonNull(getField(packetClass, "i")), 0);

        try {
            Object handle = to.getClass().getMethod("getHandle").invoke(to);

            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(playerConnection, pk);
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    private static void setField(Object packet, Field field, Object value) {
        field.setAccessible(true);

        try {
            System.out.println(value);

            field.set(packet, value);
        } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
        }

        field.setAccessible(!field.isAccessible());
    }

    private static Field getField(Class<?> packetClass, String fieldname) {
        try {
            return packetClass.getDeclaredField(fieldname);
        } catch (SecurityException | NoSuchFieldException var3) {
            var3.printStackTrace();
        }

        return null;
    }
}