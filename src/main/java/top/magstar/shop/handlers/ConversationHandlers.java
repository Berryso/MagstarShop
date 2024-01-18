package top.magstar.shop.handlers;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.magstar.shop.Shop;
import top.magstar.shop.datamanagers.runtimes.*;
import top.magstar.shop.datamanagers.statics.AbstractDataManager;
import top.magstar.shop.datamanagers.statics.IDataManager;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.objects.ChestShopFactory;
import top.magstar.shop.utils.GeneralUtils;
import top.magstar.shop.utils.fileutils.ConfigUtils;
import top.magstar.shop.utils.fileutils.Message;

import java.util.Objects;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class ConversationHandlers {
    final static class FirstHandler extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return CurrencyHandlers.currencyForName(s) != null || s.equalsIgnoreCase("q");
        }
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (!s.equalsIgnoreCase("q")) {
                ChestShop cs = RuntimeManager.getChestShop((Player)conversationContext.getForWhom());
                assert cs != null;
                cs.setCurrency(CurrencyHandlers.currencyForName(s));
                RuntimeManager.changeShop((Player) conversationContext.getForWhom(), cs);
                return new SecondHandler();
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
                RuntimeManager.removePlayer((Player) conversationContext.getForWhom());
                return Prompt.END_OF_CONVERSATION;
            }
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return GeneralUtils.getTranslate(ConfigUtils.getConfig().getString("prefix") + Message.getConfig().getStringList("flow-path").get(1));
        }
    }
    final static class SecondHandler extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return (Pattern.compile("^[0-9]*").matcher(s).matches() && Integer.parseInt(s) > 0) || s.equalsIgnoreCase("q");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (!s.equalsIgnoreCase("q")) {
                ChestShop cs = RuntimeManager.getChestShop((Player) conversationContext.getForWhom());
                assert cs != null;
                cs.setPrice(Integer.parseUnsignedInt(s));
                return new ThirdHandler();
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
                RuntimeManager.removePlayer((Player) conversationContext.getForWhom());
                return Prompt.END_OF_CONVERSATION;
            }
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return GeneralUtils.getTranslate(ConfigUtils.getConfig().getString("prefix") + Message.getConfig().getStringList("flow-path").get(2));
        }
    }
    final static class ThirdHandler extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.create.getTranslate());
                RuntimeDataManager.loadDataSilently(RuntimeManager.getChestShop((Player) conversationContext.getForWhom()));
                IDataManager idm = AbstractDataManager.getInstance();
                idm.saveData();
                idm.loadData();
                Shop.getInstance().getLogger().info("检测到玩家创建商店，已自动存储数据。");
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
            }
            RuntimeManager.removePlayer((Player) conversationContext.getForWhom());
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return (ConfigUtils.getConfig().getString("prefix") + Message.getConfig().getStringList("flow-path").get(3))
                    .replace("{price}", String.valueOf(Objects.requireNonNull(RuntimeManager.getChestShop((Player) conversationContext.getForWhom())).getPrice()))
                    .replace("{currency}", Objects.requireNonNull(RuntimeManager.getChestShop((Player) conversationContext.getForWhom())).getCurrency().getName())
                    .replace("{item}", Objects.requireNonNull(RuntimeManager.getChestShop((Player) conversationContext.getForWhom())).getItem().getItemMeta().hasDisplayName() ? Objects.requireNonNull(Objects.requireNonNull(RuntimeManager.getChestShop((Player) conversationContext.getForWhom())).getItem().getItemMeta().getDisplayName()) : Objects.requireNonNull(RuntimeManager.getChestShop((Player) conversationContext.getForWhom())).getItem().getType().toString())
                    .replace("&", "§");
        }
    }
    final static class PriceChange extends ValidatingPrompt {

        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return (Pattern.compile("^[0-9]*").matcher(s).matches() && Integer.parseInt(s) > 0) || s.equalsIgnoreCase("q");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (!s.equalsIgnoreCase("q")) {
                ChestShop cs = EditShopManager.getShop((Player) conversationContext.getForWhom());
                assert cs != null;
                cs.setPrice(Integer.parseInt(s));
                EditShopManager.saveData((Player) conversationContext.getForWhom());
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.success.getTranslate());
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return (ConfigUtils.getConfig().getString("prefix") + Message.price.getTranslate());
        }
    }
    final static class TakeOutStore extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return (Pattern.compile("^[0-9]*").matcher(s).matches() && Integer.parseInt(s) > 0) || s.equalsIgnoreCase("q") || !s.equals("0");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (!s.equalsIgnoreCase("q")) {
                ChestShop cs = EditShopManager.getShop((Player) conversationContext.getForWhom());
                assert cs != null;
                int store = cs.getStore();
                if (store < Integer.parseInt(s)) {
                    conversationContext.getForWhom().sendRawMessage(Message.notEnough.getTranslate());
                } else {
                    PlayerInventory inv = ((Player) conversationContext.getForWhom()).getInventory();
                    int count = 0;
                    for (int i = 0; i < inv.getSize(); i++) {
                        if (inv.getItem(i) == null) {
                            count += cs.getItem().getMaxStackSize();
                        }
                    }
                    if (count < Integer.parseInt(s)) {
                        conversationContext.getForWhom().sendRawMessage(Message.notEmpty.getTranslate());
                    } else {
                        cs.setStore(store - Integer.parseInt(s));
                        int num = Integer.parseInt(s);
                        for (int i = 0; i < inv.getSize(); i++) {
                            if (inv.getItem(i) == null) {
                                ItemStack item = cs.getItem();
                                item.setItemMeta(cs.getMeta());
                                if (num <= item.getMaxStackSize()) {
                                    item.setAmount(num);
                                    inv.setItem(i, item);
                                    break;
                                } else {
                                    item.setAmount(item.getMaxStackSize());
                                    inv.setItem(i, item);
                                    num -= item.getMaxStackSize();
                                }
                            }
                        }
                        EditShopManager.saveData((Player) conversationContext.getForWhom());
                        conversationContext.getForWhom().sendRawMessage(Message.getItem.getTranslate().replace("{amount}", s).replace("{item}", cs.getMeta().hasDisplayName() ? cs.getMeta().getDisplayName() : cs.getItem().getType().toString()));
                    }
                }
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
            }
            EditShopManager.removeShop((Player) conversationContext.getForWhom());
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return ConfigUtils.getConfig().getString("prefix") + Message.takeOut.getTranslate().replace("{store}", String.valueOf(EditShopManager.getShop((Player) conversationContext.getForWhom()).getStore()));
        }
    }
    final static class RemoveShop extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("n");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (s.equalsIgnoreCase("y")) {
                Player p = (Player) conversationContext.getForWhom();
                ChestShop cs = EditShopManager.getShop(p);
                AbstractDataManager.getInstance().deleteData(cs);
                RuntimeDataManager.removeShopByLocation(cs);
                ItemStack i = cs.getItem();
                i.setItemMeta(cs.getMeta());
                i.setAmount(cs.getStore());
                cs.removeItem();
                cs.setHasRemoved(true);
                if (cs.getStore() > 0) {
                    RuntimeDataManager.addPlayerItem(p.getUniqueId().toString(), i);
                }
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.removeAccepted.getTranslate());
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.removeCancelled.getTranslate());
            }
            EditShopManager.removeShop((Player) conversationContext.getForWhom());
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return ConfigUtils.getConfig().getString("prefix") + Message.removeConfirm.getTranslate();
        }
    }
    final static class SellConversation extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return (Pattern.compile("^[0-9]*").matcher(s).matches() && Integer.parseInt(s) > 0) || s.equalsIgnoreCase("q");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (!s.equalsIgnoreCase("q")) {
                if (Integer.parseInt(s) > (TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).isAdmin() ? TradeShopManager.getInventoryItemCount((Player) conversationContext.getForWhom()) : Math.min(TradeShopManager.getInventoryItemCount((Player) conversationContext.getForWhom()), TradeShopManager.getShopVolume(TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()))))) {
                    conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.noVolume.getTranslate());
                } else {
                    conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.sellConfirm.getTranslate()
                            .replace("{count}", s)
                            .replace("{item}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().hasDisplayName() ? TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getItem().getType().toString() : TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().getDisplayName())
                            .replace("{price}", String.valueOf(TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getPrice() * Integer.parseInt(s)))
                            .replace("{currency}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getCurrency().getName())
                    );
                    int sum = Integer.parseInt(s);
                    ChestShop cs = TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom());
                    if (!TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).isAdmin()) {
                        cs.setStore(cs.getStore() + sum);
                        TradeShopManager.saveShop(cs);
                        CurrencyHandlers.takeCurrency(cs.getOwner(), cs.getCurrency(), cs.getPrice() * sum);
                    }
                    CurrencyHandlers.addCurrency(((Player) conversationContext.getForWhom()).getUniqueId(), cs.getCurrency(), (int) Math.round(cs.getPrice() * sum * (1 - cs.getTax())));
                    ItemStack i = new ItemStack(cs.getItem());
                    i.setItemMeta(cs.getMeta());
                    for (ItemStack item : ((Player) conversationContext.getForWhom()).getInventory().getContents()) {
                        if (item != null) {
                            if (GeneralUtils.isItemStackSame(item, i)) {
                                if (sum < item.getAmount()) {
                                    item.setAmount(item.getAmount() - sum);
                                    break;
                                } else {
                                    sum -= item.getAmount();
                                    item.setAmount(0);
                                }
                            }
                        }
                    }
                }
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
            }
            TradeShopManager.removePlayer((Player) conversationContext.getForWhom());
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return ConfigUtils.getConfig().getString("prefix") + Message.sellInput.getTranslate()
                    .replace("{count}", String.valueOf(TradeShopManager.getInventoryItemCount((Player) conversationContext.getForWhom())))
                    .replace("{item}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().hasDisplayName() ? TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getItem().getType().toString() : TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().getDisplayName())
                    .replace("{max}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).isAdmin() ? String.valueOf(TradeShopManager.getInventoryItemCount((Player) conversationContext.getForWhom())) : String.valueOf(Math.max(TradeShopManager.getShopVolume(TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom())), TradeShopManager.getInventoryItemCount((Player) conversationContext.getForWhom()))));
        }
    }
    final static class PurchaseConversation extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(@NotNull ConversationContext conversationContext, @NotNull String s) {
            return (Pattern.compile("^[0-9]*").matcher(s).matches() && Integer.parseInt(s) > 0) || s.equalsIgnoreCase("q");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            Player p = (Player) conversationContext.getForWhom();
            if (!s.equalsIgnoreCase("q")) {
                ChestShop cs = TradeShopManager.getAccessingShop(p);
                int sum = Integer.parseInt(s);
                int price = cs.getPrice();
                int money = CurrencyHandlers.getCurrency(p.getUniqueId(), cs.getCurrency());
                if ((money >= sum * price) && (cs.isAdmin() || (sum <= cs.getStore()))) {
                    conversationContext.getForWhom().sendRawMessage(
                            ConfigUtils.getConfig().getString("prefix") + Message.purchaseConfirm.getTranslate()
                                    .replace("{price}", String.valueOf(sum * price))
                                    .replace("{currency}", cs.getCurrency().getName())
                                    .replace("{count}", s)
                                    .replace("{item}", cs.getMeta().hasDisplayName() ? cs.getMeta().getDisplayName() : cs.getItem().getType().toString())
                    );
                    p.closeInventory();
                    if (!cs.isAdmin()) {
                        cs.setStore(cs.getStore() - sum);
                        CurrencyHandlers.addCurrency(cs.getOwner(), cs.getCurrency(), (int) Math.round(price * sum * (1 - cs.getTax())));
                    }
                    CurrencyHandlers.takeCurrency(p.getUniqueId(), cs.getCurrency(), price * sum);
                    ItemStack i = new ItemStack(cs.getItem());
                    i.setItemMeta(cs.getMeta());
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item != null && GeneralUtils.isItemStackSame(item, i)) {
                            if (item.getAmount() < item.getMaxStackSize()) {
                                int max = item.getMaxStackSize();
                                int delta = max - item.getAmount();
                                if (sum > delta) {
                                    item.setAmount(max);
                                    sum -= delta;
                                }
                                else {
                                    item.setAmount(item.getAmount() + sum);
                                    sum = 0;
                                }
                            }
                        }
                    }
                    PlayerInventory inv = p.getInventory();
                    if (sum != 0) {
                        for (int index = 0; index <= 35; index++) {
                            if (inv.getItem(index) == null) {
                                ItemStack item = new ItemStack(i);
                                int max = item.getMaxStackSize();
                                if (sum > max) {
                                    item.setAmount(max);
                                    sum -= max;
                                    inv.setItem(index, item);
                                    p.updateInventory();
                                } else if (sum > 0) {
                                    item.setAmount(sum);
                                    sum = 0;
                                    inv.setItem(index, item);
                                    p.updateInventory();
                                    break;
                                }
                            }
                        }
                    }
                    if (sum != 0) {
                        ItemStack item = new ItemStack(i);
                        item.setAmount(sum);
                        RuntimeDataManager.addPlayerItem(p.getUniqueId().toString(), item);
                        conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.purchaseOverFlow.getTranslate());
                    }
                } else {
                    conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.noMoney.getTranslate());
                }
            } else {
                conversationContext.getForWhom().sendRawMessage(ConfigUtils.getConfig().getString("prefix") + Message.quit.getTranslate());
            }
            TradeShopManager.removePlayer(p);
            return null;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return ConfigUtils.getConfig().getString("prefix") + Message.purchaseInput.getTranslate()
                    .replace("{count}", String.valueOf(CurrencyHandlers.getCurrency(((Player) conversationContext.getForWhom()).getUniqueId(), TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getCurrency())))
                    .replace("{currency}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getCurrency().getName())
                    .replace("{all}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).isAdmin() ? String.valueOf(CurrencyHandlers.getCurrency(((Player) conversationContext.getForWhom()).getUniqueId(), TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getCurrency()) / TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getPrice()) : String.valueOf(Math.min(CurrencyHandlers.getCurrency(((Player) conversationContext.getForWhom()).getUniqueId(), TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getCurrency()) / TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getPrice(), TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getStore())))
                    .replace("{item}", TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().hasDisplayName() ? TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getMeta().getDisplayName() : TradeShopManager.getAccessingShop((Player) conversationContext.getForWhom()).getItem().getType().toString());
        }
    }
}
