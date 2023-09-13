package me.jammy.rewardvaults.db;

import me.dthbr.utils.utils.InventorySave;
import me.jammy.rewardvaults.RewardVaults;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DataAccess {
    private static final RewardVaults plugin = RewardVaults.getInstance();
    private final Logger log = plugin.getLogger();
    private final Connection conn = ConnectionHandler.getConn();
    private final String storageMethod;

    public DataAccess() {
        storageMethod = ConnectionHandler.getStorageMethod();
    }

    public void updateInventory(Player player, ItemStack[] items) {
        String base64 = InventorySave.toBase64(items);

        try (PreparedStatement stmt = conn.prepareStatement(
                (storageMethod.equalsIgnoreCase("sqlite") ?  "INSERT OR " : "") +
                        """
                               REPLACE INTO table(user_data)
                                VALUES(?, ?);
                                """

        )) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, base64);
        } catch (SQLException e) {
            log.warning("Something went wrong when creating/updating the reward vault! Error: " + e);
        }
    }

    public ItemStack[] fetchInventory(Player player) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT items FROM user_data WHERE uuid = ?"
        )) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = stmt.executeQuery();

            return InventorySave.fromBase64(resultSet.getString(1));
        } catch (SQLException e) {
            log.warning("This player does not have a reward vault! Error: " + e);
            return new ItemStack[1];
        }
    }
}
