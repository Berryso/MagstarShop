package top.magstar.shop.handlers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.magstar.shop.datamanagers.runtimes.RuntimeDataManager;
import top.magstar.shop.datamanagers.runtimes.RuntimeViewerManager;
import top.magstar.shop.datamanagers.runtimes.ShopListManager;
import top.magstar.shop.objects.ChestShop;
import top.magstar.shop.utils.GuiUtils;
import top.magstar.shop.utils.fileutils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
        } else {
            if (args[0].equalsIgnoreCase("view")) {
                if (sender instanceof Player) {
                    if (args.length != 1) {
                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
                    } else {
                        int allPage = RuntimeDataManager.calculateViewerAllPage((Player) sender);
                        GuiUtils.createViewerGui((Player) sender, 1, allPage);
                        RuntimeViewerManager.addPlayer((Player) sender);
                    }
                } else {
                    sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onlyPlayer.getTranslate());
                }
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("magstarshop.reload")) {
                    if (args.length != 2) {
                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
                    } else {
                        switch (args[1]) {
                            case "all" -> {
                                ConfigUtils.reload();
                                Message.reload();
                                Restocking.reload();
                                ShopInfo.reload();
                                Trade.reload();
                                Viewer.reload();
                                sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.reloadSuccess.getTranslate()
                                        .replace("{config}", "所有"));
                            }
                            case "message" -> {
                                Message.reload();
                                sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.reloadSuccess.getTranslate()
                                        .replace("{config}", "message"));
                            }
                            case "config" -> {
                                ConfigUtils.reload();
                                sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.reloadSuccess.getTranslate()
                                        .replace("{config}", "config"));
                            }
                            case "gui" -> {
                                Restocking.reload();
                                ShopInfo.reload();
                                Trade.reload();
                                Viewer.reload();
                                ShopList.reload();
                                sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.reloadSuccess.getTranslate()
                                        .replace("{config}", "GUI相关"));
                            }
                            default -> sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
                        }
                    }
                } else {
                    sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.noPerm.getTranslate());
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player) {
                    if (args.length != 1) {
                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
                    } else {
                        int allPage = RuntimeDataManager.calculateShopListAllPage((Player) sender);
                        GuiUtils.createShopListGui((Player) sender, 1, allPage);
                        ShopListManager.addPlayer((Player) sender);
                    }
                } else {
                    sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onlyPlayer.getTranslate());
                }
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("magstarshop.admin")) {
                        if (args.length == 1) {
                            Block block = ((Player) sender).getTargetBlockExact(5);
                            if (block != null) {
                                Location loc = block.getLocation();
                                if (RuntimeDataManager.isChestShop(loc)) {
                                    ChestShop cs = RuntimeDataManager.getShop(loc);
                                    assert cs != null;
                                    if (cs.isAdmin()) {
                                        cs.setAdmin(false);
                                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.setPlayer.getTranslate());
                                    } else {
                                        cs.setAdmin(true);
                                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.setAdmin.getTranslate());
                                    }
                                } else {
                                    sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.notShop.getTranslate());
                                }
                            } else {
                                sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.notShop.getTranslate());
                            }
                        } else {
                            sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.wrongSyntax.getTranslate());
                        }
                    } else {
                        sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.noPerm.getTranslate());
                    }
                } else {
                    sender.sendMessage(ConfigUtils.getConfig().getString("prefix") + Message.onlyPlayer.getTranslate());
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return switch (args[0]) {
                    case "v", "vi", "vie" -> Collections.singletonList("view");
                    case "r", "re", "rel", "relo", "reloa" -> Collections.singletonList("reload");
                    case "l", "li", "lis" -> Collections.singletonList("list");
                    case "a", "ad", "adm", "admi" -> Collections.singletonList("admin");
                    default -> Arrays.asList("view", "reload", "list", "admin");
                };
            }
            case 2 -> {
                if (args[0].equals("reload")) {
                    return switch (args[1]) {
                        case "a", "al" -> Collections.singletonList("all");
                        case "c", "co", "con", "conf", "confi" -> Collections.singletonList("config");
                        case "m", "me", "mes", "mess", "messa", "messag" -> Collections.singletonList("message");
                        case "g", "gu" -> Collections.singletonList("gui");
                        default -> Arrays.asList("all", "config", "message", "gui");
                    };
                }
                return null;
            }
            default -> {
                return null;
            }
        }
    }
}
