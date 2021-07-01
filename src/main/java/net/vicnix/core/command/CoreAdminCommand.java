package net.vicnix.core.command;

import net.vicnix.core.VicnixCore;
import net.vicnix.core.listener.InventoryIconSelector;
import net.vicnix.core.provider.MysqlProvider;
import net.vicnix.core.utils.VicnixIcon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class CoreAdminCommand extends Command {

    public CoreAdminCommand(String name) {
        super(name, "", "", Collections.singletonList("ac"));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(VicnixCore.getInstance(), () -> this.executeCommand(sender, label, args));

        return false;
    }

    private void executeCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Use /" + label + " help");

            return;
        }

        MysqlProvider provider = MysqlProvider.getInstance();

        if (args[0].equalsIgnoreCase("seticon")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Use /" + label + " setemote <player> <emoteName>");

                return;
            }

            VicnixIcon icon = VicnixIcon.of(args[2]);

            if (icon == null) {
                sender.sendMessage(ChatColor.RED + "Icon not found");

                return;
            }

            provider.setPlayerIcon(args[1], icon);

            sender.sendMessage(ChatColor.GREEN + "Icon " + icon.getName() + " was added to " + args[1]);

            return;
        }

        if (args[0].equalsIgnoreCase("iconmenu")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Run this command in-game");

                return;
            }

            new InventoryIconSelector().open((Player) sender);
        }
    }
}