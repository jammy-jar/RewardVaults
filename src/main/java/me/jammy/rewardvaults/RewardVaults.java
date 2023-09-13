package me.jammy.rewardvaults;

import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import me.dthbr.utils.DeathUtils;
import me.dthbr.utils.config.Cfgs;
import me.jammy.rewardvaults.command.RewardVaultCmd;
import me.jammy.rewardvaults.db.ConnectionHandler;
import me.jammy.rewardvaults.db.DataAccess;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RewardVaults extends JavaPlugin {

    @Getter
    private static RewardVaults instance = null;
    @Getter
    private Cfgs langCfg;
    @Getter
    private DataAccess db;

    public void setupDb() {
        try {
            ConnectionHandler.initDb();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong when initialising database. Exception:\n" + e);
            getServer().getPluginManager().disablePlugin(this);
        }
        this.db = new DataAccess();
    }

    private void setupInventoryAPI() {
        new InventoryAPI(this).init();
    }

    @Override
    public void onLoad() {
        instance = this;

        DeathUtils.init(this);

        saveDefaultConfig();
        registerConfigs();
    }

    @Override
    public void onEnable() {
        setupDb();
        setupInventoryAPI();

        this.getCommand("rewardvault").setExecutor(new RewardVaultCmd());
    }

    @Override
    public void onDisable() {
        ConnectionHandler.disconnect();
    }

    private void registerConfigs() {
        langCfg = Cfgs.of("lang.yml");
    }
}
