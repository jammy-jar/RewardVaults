package me.jammy.rewardvaults.command;

import me.dthbr.utils.config.Msgs;
import me.jammy.rewardvaults.gui.VaultGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RewardVaultCmd implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, final @NotNull String[] strings) {
        if (!(sender instanceof Player player)) {
            Msgs.of("You must be a player to do this!").send(sender);
            return true;
        }

        new VaultGui(player).open();
        return true;
    }
}
