package creeperpookie.blockdisplayeditor.commands;

import creeperpookie.blockdisplayeditor.BlockDisplayEditor;
import creeperpookie.blockdisplayeditor.items.CustomItem;
import creeperpookie.blockdisplayeditor.items.ItemType;
import creeperpookie.blockdisplayeditor.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockDisplayCommand implements CommandExecutor, TabCompleter
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		if (args.length == 0) printHelp(sender, command, label, args);
		else switch (args[0])
		{
			case "item" ->
			{
				if (args.length != 1 && args.length != 2) printHelp(sender, command, label, args);
				else if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.item")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (args.length == 2 && Bukkit.getOnlinePlayers().stream().noneMatch(player -> player.getName().equalsIgnoreCase(args[1]))) Utility.sendError(sender, "Player " + args[1] + " is not currently online!");
				else
				{
					Player target = null;
					if (args.length == 1)
					{
						if (!(sender instanceof Player)) Utility.sendError(sender, "When running as a non-player, specifying a player target is required!");
						else target = (Player) sender;
					}
					else target = Bukkit.getPlayer(args[1]);
					int slot = -1;
					boolean successfullyGaveItem = false;
					if (CustomItem.hasCustomItem(target.getInventory(), ItemType.SELECTION_STICK))
					{
						Utility.broadcast("Player has a selection stick, scanning inventory!");
						while (slot < target.getInventory().getSize())
						{
							slot++;
							ItemStack currentItem = target.getInventory().getItem(slot);
							if (CustomItem.isCustomItem(currentItem, ItemType.SELECTION_STICK) && currentItem.getAmount() < currentItem.getMaxStackSize())
							{
								Utility.broadcast("Found potential slot for selection stick at slot " + slot);
								currentItem.setAmount(currentItem.getAmount() + 1);
								successfullyGaveItem = true;
								break;
							}
						}
					}
					if ((slot == -1 || slot == target.getInventory().getSize()) && target.getInventory().firstEmpty() != -1)
					{
						Utility.broadcast("Player does not have any valid selection stick slots, setting first empty slot");
						target.getInventory().setItem(target.getInventory().firstEmpty(), CustomItem.getItem(ItemType.SELECTION_STICK).getItemStack());
						successfullyGaveItem = true;
					}
					else if (!successfullyGaveItem)
					{
						Utility.sendError(sender, (sender == target ? "You don't" : target.getName() + "doesn't") + " have enough space in " + (sender == target ? "your" : "their") + " inventory!");
						return true;
					}
					sender.sendMessage(Component.text("Successfully gave").appendSpace().append(Component.text(sender == target ? "you" : target.getName())).appendSpace().append(Component.text("a")).appendSpace().append(CustomItem.getItem(ItemType.SELECTION_STICK).getItemStack().effectiveName().decoration(TextDecoration.ITALIC, false)));
				}
			}
			case "reload" ->
			{
				if (args.length > 1) printHelp(sender, command, label, args);
				else if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.reload")) Utility.sendError(sender, "You don't have permission to do that!");
				else
				{
					BlockDisplayEditor.reloadConfigs();
					Utility.sendFeedback(sender, "Successfully reloaded all Block Display Editor configs");
				}
			}
		}
		return true;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		return List.of();
	}

	public void printHelp(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
	{
		// TODO verify permissions to suggest commands
		switch (args.length)
		{
			case 0 -> Utility.sendFeedback(sender, "Usage: /" + label + " [item | reload]");
			case 2 ->
			{
				if (args[0].equalsIgnoreCase("item")) Utility.sendFeedback(sender, "Usage: /" + label + " item [<player>]");
				else if (args[0].equalsIgnoreCase("reload")) Utility.sendFeedback(sender, "Usage: /" + label + " reload");
			}
		}
	}
}
