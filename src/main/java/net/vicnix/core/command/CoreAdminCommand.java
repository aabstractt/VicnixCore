package net.vicnix.core.command;

import net.vicnix.core.VicnixCore;
import net.vicnix.core.provider.MysqlProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals("coreadmin")) {
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(VicnixCore.getInstance(), () -> this.executeCommand(sender, label, args));

        return false;
    }

    private void executeCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Use /" + label + " help");

            return;
        }

        MysqlProvider provider = MysqlProvider.getInstance();

        if (args[0].equals("addemote")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Use /" + label + " addemote <emoteName> <emote>");

                return;
            }

            if (provider.getEmote(args[1]) != null) {
                sender.sendMessage(ChatColor.RED + "Emote already exists");

                return;
            }

            provider.addEmote(args[1], args[2]);

            sender.sendMessage(ChatColor.GREEN + "Emote " + ChatColor.AQUA + args[1] + ChatColor.GREEN + " was added.");

            return;
        }

        if (args[0].equalsIgnoreCase("setemote")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Use /" + label + " setemote <player> <emoteName>");

                return;
            }

            int emoteId = provider.getEmoteId(args[2]);

            if (emoteId == -1) {
                sender.sendMessage(ChatColor.RED + "Emote not found");

                return;
            }

            provider.setPlayerEmote(args[1], emoteId);

            sender.sendMessage(ChatColor.GREEN + "Emote " + args[2] + " was added to " + args[1]);
        }
    }
}