package top.magstar.shop.handlers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.runtimes.*;
import top.magstar.shop.datamanagers.statics.AbstractDataManager;
import top.magstar.shop.objects.ButtonType;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.PlayerOpenShopEvent;
import top.magstar.shop.objects.ShopType;
import top.magstar.shop.utils.GeneralUtils;
import top.magstar.shop.utils.GuiUtils;
import top.magstar.shop.utils.fileutils.*;

import java.util.*;

@SuppressWarnings("deprecation")
public class EventHandlers implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        String prefix = ConfigUtils.getConfig().getString("prefix");
        if (e.hasBlock()) {
            if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.CHEST && ((org.bukkit.block.data.type.Chest) e.getClickedBlock().getBlockData()).getType() == org.bukkit.block.data.type.Chest.Type.SINGLE) {
                if (!RuntimeDataManager.isChestShop(e.getClickedBlock().getLocation())) {
                    if (p.getInventory().getItemInMainHand().getType() != Material.AIR && e.getAction() == Action.LEFT_CLICK_BLOCK && p.getGameMode() == GameMode.SURVIVAL) {
                        if (EditShopManager.getShop(p) != null) {
                            p.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onEdit.getTranslate());
                            e.setCancelled(true);
                        } else if (TradeShopManager.getAccessingShop(p) != null) {
                            p.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onTrade.getTranslate());
                            e.setCancelled(true);
                        } else {
                            if (((Chest)e.getClickedBlock().getState()).getBlockInventory().isEmpty()) {
                                if (((Chest)e.getClickedBlock().getState()).getBlockInventory().getSize() == 27) {
                                    if (p.hasPermission("magstarshop.createshop") && !RuntimeManager.isMapContains(p)) {
                                        List<String> flowPath = Message.getConfig().getStringList("flow-path");
                                        p.sendMessage(GeneralUtils.getTranslate(prefix + flowPath.get(0)));
                                        Conversation conversation = new ConversationFactory(Shop.getInstance())
                                                .withFirstPrompt(new ConversationHandlers.FirstHandler())
                                                .withTimeout(60)
                                                .buildConversation(p);
                                        RuntimeManager.addPlayer(p, e.getClickedBlock().getLocation());
                                        conversation.begin();
                                    } else {
                                        if (!p.hasPermission("magstarshop.createshop")) {
                                            p.sendMessage(prefix + Message.noPerm.getTranslate());
                                        } else {
                                            p.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onCreate.getTranslate());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.getPlayer().isSneaking()) {
                        if (p.getUniqueId().toString().equals(Objects.requireNonNull(RuntimeDataManager.getShop(e.getClickedBlock().getLocation())).getOwner().toString())) {
                            if (EditShopManager.getShop(e.getPlayer()) != null) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onEdit.getTranslate());
                            } else if (TradeShopManager.getAccessingShop(e.getPlayer()) != null) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onTrade.getTranslate());
                            } else if (TradeShopManager.isShopAccessing(RuntimeDataManager.getShop(e.getClickedBlock().getLocation()))) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.otherAccessing.getTranslate());
                            } else {
                                PlayerOpenShopEvent event = new PlayerOpenShopEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace(), e.getHand(), Objects.requireNonNull(RuntimeDataManager.getShop(e.getClickedBlock().getLocation())));
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    GuiUtils.createShopInfoGui(Objects.requireNonNull(RuntimeDataManager.getShop(e.getClickedBlock().getLocation())), p);
                                    EditShopManager.addShop(RuntimeDataManager.getShop(e.getClickedBlock().getLocation()));
                                }
                            }
                        } else {
                            if (TradeShopManager.getAccessingShop(e.getPlayer()) != null) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onTrade.getTranslate());
                            } else if (EditShopManager.getShop(e.getPlayer()) != null) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onEdit.getTranslate());
                            } else if (TradeShopManager.isShopAccessing(RuntimeDataManager.getShop(e.getClickedBlock().getLocation()))) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.otherAccessing.getTranslate());
                            } else if (EditShopManager.containShop(RuntimeDataManager.getShop(e.getClickedBlock().getLocation()))) {
                                e.getPlayer().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.ownerEditing.getTranslate());
                            } else {
                                PlayerOpenShopEvent event = new PlayerOpenShopEvent(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e.getBlockFace(), e.getHand(), Objects.requireNonNull(RuntimeDataManager.getShop(e.getClickedBlock().getLocation())));
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    ChestShop cs = RuntimeDataManager.getShop(e.getClickedBlock().getLocation());
                                    GuiUtils.createTradeGui(cs, p);
                                    TradeShopManager.addPlayer(p, cs);
                                }
                            }
                        }
                        e.setCancelled(true);
                    }
                    else if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().isSneaking()) {
                        if (!p.getUniqueId().toString().equals(Objects.requireNonNull(RuntimeDataManager.getShop(e.getClickedBlock().getLocation())).getOwner().toString())) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onHopperPickUpItem(InventoryPickupItemEvent e) {
        if (RuntimeDataManager.isItemFromChestShop(e.getItem())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onHopperMoveItem(InventoryMoveItemEvent e) {
        if (RuntimeDataManager.isChestShop(e.getSource().getLocation()) || RuntimeDataManager.isChestShop(e.getDestination().getLocation())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onClickGui(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        List<Integer> intList = new ArrayList<>();
        if (e.getWhoClicked().getOpenInventory().getTitle().equals(ShopInfo.getConfig().getString("Title").replace("{player}", e.getWhoClicked().getName()))) {
            List<Integer> sideLocations = GuiUtils.getShopSideLocations();
            Map<ButtonType, Integer> otherLocations = GuiUtils.getOtherLocations();
            if (sideLocations.contains(e.getRawSlot())) {
                e.setCancelled(true);
            }
            if (otherLocations.containsValue(e.getRawSlot())) {
                ButtonType type = GeneralUtils.getType(otherLocations, e.getRawSlot());
                switch (Objects.requireNonNull(type)) {
                    case price -> {
                        e.getWhoClicked().closeInventory();
                        Conversation conversation = new ConversationFactory(Shop.getInstance())
                                .withFirstPrompt(new ConversationHandlers.PriceChange())
                                .withTimeout(60)
                                .buildConversation(p);
                        conversation.begin();
                        e.setCancelled(true);
                    }
                    case item -> {
                        e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        e.setCancelled(true);
                        intList = GuiUtils.createRestockingGui(EditShopManager.getShop((Player) e.getWhoClicked()), (Player) e.getWhoClicked());
                    }
                    case player -> {
                        e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        e.setCancelled(true);
                        ChestShop cs = EditShopManager.getShop(p);
                        assert cs != null;
                        cs.setType(cs.getType().getOppositeType());
                        GuiUtils.createShopInfoGui(EditShopManager.getShop(p), p);
                    }
                    case remove -> {
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        Conversation conv = new ConversationFactory(Shop.getInstance())
                                .withFirstPrompt(new ConversationHandlers.RemoveShop())
                                .withTimeout(60)
                                .buildConversation(p);
                        conv.begin();
                    }
                    default -> {
                        e.setCancelled(true);
                    }
                }
            }
        }
        if (e.getWhoClicked().getOpenInventory().getTitle().equals(GeneralUtils.getTranslate(Objects.requireNonNull(Restocking.getConfig().getString("Title"))).replace("{player}", p.getName()))) {
            if (!intList.contains(e.getRawSlot()) && e.getRawSlot() <= 53) {
                boolean contain = false;
                if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                    if (e.getCurrentItem().getItemMeta().hasLore()) {
                        for (String s : e.getCurrentItem().getLore()) {
                            if (s.contains(GeneralUtils.getTranslate(Restocking.getConfig().getString("TakeStoreLore")))) {
                                contain = true;
                                break;
                            }
                        }
                    }
                }
                if (contain) {
                    p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    Conversation conv = new ConversationFactory(Shop.getInstance())
                            .withFirstPrompt(new ConversationHandlers.TakeOutStore())
                            .withTimeout(60)
                            .buildConversation(p);
                    conv.begin();
                }
                e.setCancelled(true);
            } else {
                if (e.getRawSlot() >= 54) {
                    ItemStack item = e.getCurrentItem();
                    if (item != null && item.getType() != Material.AIR) {
                        ChestShop cs = EditShopManager.getShop((Player) e.getWhoClicked());
                        ItemStack i = cs.getItem();
                        i.setItemMeta(cs.getMeta());
                        if (!GeneralUtils.isItemStackSame(i, item)) {
                            e.setCancelled(true);
                        }
                    }
                } else {
                    ItemStack item = e.getCursor();
                    if (item != null && item.getType() != Material.AIR) {
                        ChestShop cs = EditShopManager.getShop((Player) e.getWhoClicked());
                        ItemStack i = cs.getItem();
                        i.setItemMeta(cs.getMeta());
                        if (!GeneralUtils.isItemStackSame(i, item)) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
        if (RuntimeViewerManager.isContain(p)) {
            if (e.getWhoClicked().getOpenInventory().getTitle().equals(Viewer.getConfig().getString("Title").replace("{page}", String.valueOf(RuntimeViewerManager.getPage((Player) e.getWhoClicked()))))) {
                int prev = GuiUtils.getPrevPageLocation();
                int next = GuiUtils.getNextPageLocation();
                List<Integer> available = Viewer.getConfig().getIntegerList("Buttons.<available_locations>");
                List<Integer> side = GuiUtils.getViewerSideLocations();
                if (e.getRawSlot() == prev && e.getCurrentItem() != null) {
                    if (RuntimeViewerManager.getPage(p) != 1) {
                        p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        GuiUtils.createViewerGui(p, RuntimeViewerManager.getPage(p) - 1, RuntimeDataManager.calculateViewerAllPage(p));
                        RuntimeViewerManager.minusPage(p);
                    }
                    e.setCancelled(true);
                }
                if (e.getRawSlot() == next && e.getCurrentItem() != null) {
                    if (RuntimeDataManager.calculateViewerAllPage(p) != 1) {
                        p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        GuiUtils.createViewerGui(p, RuntimeViewerManager.getPage(p) + 1, RuntimeDataManager.calculateViewerAllPage(p));
                        RuntimeViewerManager.addPage(p);
                    } else {
                        p.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        GuiUtils.createViewerGui(p, 1, 1);
                    }
                    e.setCancelled(true);
                }
                if (available.contains(e.getRawSlot())) {
                    Map<Integer, ItemStack> playerMap = RuntimeDataManager.getItems(p.getUniqueId().toString());
                    if (e.getCursor() == null && e.getCurrentItem() != null) {
                        if (e.getCurrentItem().getType() != Material.AIR) {
                            ItemStack item = e.getCurrentItem();
                            int id = 0;
                            for (int i : playerMap.keySet()) {
                                if (GeneralUtils.isItemStackSame(item, playerMap.get(i))) {
                                    id = i;
                                }
                            }
                            if (playerMap.get(id).getAmount() - item.getAmount() == 0) {
                                RuntimeDataManager.deleteItem(p.getUniqueId().toString(), id);
                                AbstractDataManager.getInstance().saveData();
                                AbstractDataManager.getInstance().deleteItems(p, id);
                            } else {
                                RuntimeDataManager.setItemAmount(p.getUniqueId().toString(), id, playerMap.get(id).getAmount() - item.getAmount());
                            }
                        }
                    } else {
                        if (e.getAction().equals(InventoryAction.PICKUP_ALL) || e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            if (e.getCursor() != null && e.getCurrentItem() != null) {
                                if (e.getCursor().getType() == Material.AIR && e.getCurrentItem().getType() != Material.AIR) {
                                    ItemStack item = e.getCurrentItem();
                                    int id = 0;
                                    for (int i : playerMap.keySet()) {
                                        if (GeneralUtils.isItemStackSame(item, playerMap.get(i))) {
                                            id = i;
                                        }
                                    }
                                    if (playerMap.get(id).getAmount() - item.getAmount() == 0) {
                                        RuntimeDataManager.deleteItem(p.getUniqueId().toString(), id);
                                        AbstractDataManager.getInstance().saveData();
                                        AbstractDataManager.getInstance().deleteItems(p, id);
                                    } else {
                                        RuntimeDataManager.setItemAmount(p.getUniqueId().toString(), id, playerMap.get(id).getAmount() - item.getAmount());
                                    }
                                }
                            } else {
                                e.setCancelled(true);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
                if (e.getRawSlot() >= 54) {
                    if (e.getAction() != InventoryAction.PLACE_ALL || e.getAction() != InventoryAction.PLACE_ONE || e.getAction() != InventoryAction.PLACE_SOME || e.getAction() != InventoryAction.SWAP_WITH_CURSOR) {
                        e.setCancelled(true);
                    }
                }
                if (side.contains(e.getRawSlot())) {
                    e.setCancelled(true);
                }
            }
        }
        if (TradeShopManager.isPlayerTrading((Player) e.getWhoClicked())) {
            if (e.getWhoClicked().getOpenInventory().getTitle().equals(Objects.requireNonNull(Trade.getConfig().getString("Title")).replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(TradeShopManager.getAccessingShop(p).getOwner()).getName())))) {
                if (e.getRawSlot() == GuiUtils.getTradeButtonLocation()) {
                    e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                    ChestShop cs = TradeShopManager.getAccessingShop(p);
                    if (cs.getType() == ShopType.purchase) {
                        Conversation conv = new ConversationFactory(Shop.getInstance())
                                .withFirstPrompt(new ConversationHandlers.SellConversation())
                                .withTimeout(60)
                                .buildConversation((Player) e.getWhoClicked());
                        conv.begin();
                    } else if (cs.getType() == ShopType.sale) {
                        Conversation conv = new ConversationFactory(Shop.getInstance())
                                .withFirstPrompt(new ConversationHandlers.PurchaseConversation())
                                .withTimeout(60)
                                .buildConversation((Player) e.getWhoClicked());
                        conv.begin();
                    }
                }
                e.setCancelled(true);
            }
        }
        if (ShopListManager.isContain((Player) e.getWhoClicked())) {
            if (e.getWhoClicked().getOpenInventory().getTitle().equals(ShopList.getConfig().getString("Title").replace("{page}", String.valueOf(ShopListManager.getPage((Player) e.getWhoClicked()))))) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getPlayer().getOpenInventory().getTitle().equals(ShopInfo.getConfig().getString("Title").replace("{player}", e.getPlayer().getName())) && e.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
            EditShopManager.removeShop(p);
        }
        if (e.getPlayer().getOpenInventory().getTitle().equals(Restocking.getConfig().getString("Title").replace("{player}", e.getPlayer().getName()))) {
            List<Integer> list = GuiUtils.getAvailableLocations();
            ChestShop cs = EditShopManager.getShop(p);
            int sum = 0;
            for (int loc : list) {
                ItemStack i = e.getInventory().getItem(loc);
                if (i != null && i.getType() != Material.AIR) {
                    sum += i.getAmount();
                }
            }
            cs.setStore(sum + cs.getStore());
            if (e.getReason() == InventoryCloseEvent.Reason.PLAYER) {
                EditShopManager.removeShop(p);
            }
        }
        if (TradeShopManager.isPlayerTrading(p)) {
            if (e.getPlayer().getOpenInventory().getTitle().equals(Trade.getConfig().getString("Title").replace("{owner}", Objects.requireNonNull(Bukkit.getOfflinePlayer(TradeShopManager.getAccessingShop(p).getOwner()).getName()))) && e.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
                TradeShopManager.removePlayer(p);
            }
        }
        if (RuntimeViewerManager.isContain((Player) e.getPlayer())) {
            if (e.getPlayer().getOpenInventory().getTitle().equals(Viewer.getConfig().getString("Title").replace("{page}", String.valueOf(RuntimeViewerManager.getPage((Player) e.getPlayer())))) && e.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
                RuntimeViewerManager.removePlayer((Player) e.getPlayer());
            }
        }
        if (ShopListManager.isContain((Player) e.getPlayer())) {
            if (e.getPlayer().getOpenInventory().getTitle().equals(ShopList.getConfig().getString("Title").replace("{page}", String.valueOf(ShopListManager.getPage((Player) e.getPlayer())))) && e.getReason() != InventoryCloseEvent.Reason.PLUGIN) {
                ShopListManager.removePlayer((Player) e.getPlayer());
            }
        }
    }
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (e.getBlock().getType() == Material.CHEST) {
            if (RuntimeDataManager.isChestShop(e.getBlock().getLocation())) {
                ChestShop cs = RuntimeDataManager.getShop(e.getBlock().getLocation());
                if (p.getUniqueId().toString().equals(cs.getOwner().toString())) {
                    if (EditShopManager.containShop(cs)) {
                        p.sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onEdit.getTranslate());
                        e.setCancelled(true);
                    } else if (TradeShopManager.containShop(cs)) {
                        p.sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.onTrade.getTranslate());
                        e.setCancelled(true);
                    } else {
                        AbstractDataManager.getInstance().deleteData(cs);
                        RuntimeDataManager.removeShop(cs);
                        ItemStack i = new ItemStack(cs.getItem());
                        i.setItemMeta(cs.getMeta());
                        i.setAmount(cs.getStore());
                        if (cs.getStore() > 0) {
                            RuntimeDataManager.addPlayerItem(p.getUniqueId().toString(), i);
                        }
                        cs.removeItem();
                        cs.setHasRemoved(true);
                        p.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.removeAccepted.getTranslate());
                    }
                } else {
                    p.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.noPerm.getTranslate());
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.CHEST) {
            List<Location> list = GeneralUtils.isChestNear(e.getBlock().getLocation());
            if (!list.isEmpty()) {
                for (Location loc : list) {
                    if (RuntimeDataManager.isChestShop(loc)) {
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        List<Block> blocks = e.blockList();
        for (Block b : blocks) {
            Location loc = b.getLocation();
            if (RuntimeDataManager.isChestShop(loc)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        List<Block> blocks = e.blockList();
        for (Block b : blocks) {
            Location loc = b.getLocation();
            if (RuntimeDataManager.isChestShop(loc)) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        Bukkit.getScheduler().runTaskLater(Shop.getInstance(), () -> {
            RuntimeDataManager.removeItemEntityInChunk(chunk);
        }, 5);
    }
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        Chunk chunk = e.getChunk();
        Bukkit.getScheduler().runTaskLater(Shop.getInstance(), () -> {
            RuntimeDataManager.loadItemEntityInChunk(chunk);
        }, 5);
    }
}
