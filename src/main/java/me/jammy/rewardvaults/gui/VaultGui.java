package me.jammy.rewardvaults.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.dthbr.utils.config.Msgs;
import me.dthbr.utils.inventory.ItemBuilder;
import me.jammy.rewardvaults.RewardVaults;
import me.jammy.rewardvaults.util.InvIcon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class VaultGui extends Gui {
    private final PaginationManager paginationManager = new PaginationManager(this);

    private final RewardVaults plugin = RewardVaults.getInstance();

    public VaultGui(@NotNull final Player player, final int pageNum) {
        super(player, "vault",
                player.displayName().append(Msgs.of("'s Vault <gray>- <yellow>Page " + pageNum).asComp()),
                4);
        paginationManager.registerPageSlotsBetween(0, 26);
    }

    public VaultGui(@NotNull final Player player) {
        this(player, 1);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        ItemStack[] items = plugin.getDb().fetchInventory(player);
        for (ItemStack item : items) {
            paginationManager.addItem(new Icon(item).onClick(e -> e.setCancelled(false)));
        }

        if (paginationManager.getLastPage() > 0) {
            ItemStack nextItem = InvIcon.NEXT.asBuilder()
                    .name("<gray>Next Page")
                    .build();
            Icon saveIcon = new Icon(nextItem).onClick(e -> paginationManager.goNextPage().update());
            addItem(32, saveIcon);

            ItemStack pageItem = ItemBuilder
                    .start(Material.PAPER)
                    .name("<white>Page: <yellow>" + paginationManager.getCurrentPage())
                    .build();
            addItem(31, pageItem);

            ItemStack prevItem = InvIcon.PREV.asBuilder()
                    .name("<gray>Previous Page")
                    .build();
            saveIcon = new Icon(prevItem).onClick(e -> paginationManager.goPreviousPage().update());
            addItem(30, saveIcon);
        }
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        Msgs.of(event.getAction().name()).send(player);
        return false;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        ItemStack[] items = paginationManager.getItems()
                .stream().map(Icon::getItem).toArray(ItemStack[]::new);
        plugin.getDb().updateInventory(player, items);
    }
}
