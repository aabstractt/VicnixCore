package net.vicnix.core;

import lombok.Getter;
import net.luckperms.api.LuckPerms;
import net.vicnix.core.command.CoreAdminCommand;
import net.vicnix.core.factory.EmoteFactory;
import net.vicnix.core.listener.PlayerJoinListener;
import net.vicnix.core.provider.MysqlProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class VicnixCore extends JavaPlugin {

    @Getter
    private static VicnixCore instance;
    @Getter
    private static LuckPerms luckPerms;

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

            EmoteFactory.getInstance().init();
        } catch (SQLException e) {
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.shutdown();

            return;
        }

        Bukkit.getPluginCommand("coreadmin").setExecutor(new CoreAdminCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }
}