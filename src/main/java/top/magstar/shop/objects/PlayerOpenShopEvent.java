package top.magstar.shop.objects;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class PlayerOpenShopEvent extends PlayerInteractEvent {
    private final ChestShop cs;
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;
    public PlayerOpenShopEvent(@NotNull Player who, @NotNull Action action, @Nullable ItemStack item, @NotNull Block clickedBlock, @NotNull BlockFace clickedFace, @Nullable EquipmentSlot hand, @NotNull ChestShop shop) {
        super(who, action, item, clickedBlock, clickedFace, hand);
        this.cs = shop;
    }
    public ChestShop getChestShop() {
        return cs;
    }
    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return handlers;
    }
    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    @Nonnull
    public Block getClickedBlock() {
        return this.blockClicked;
    }
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
