package creeperpookie.blockdisplayeditor.commands;

import creeperpookie.blockdisplayeditor.BlockDisplayEditor;
import creeperpookie.blockdisplayeditor.handlers.BlockDisplayHandler;
import creeperpookie.blockdisplayeditor.items.CustomItem;
import creeperpookie.blockdisplayeditor.items.ItemType;
import creeperpookie.blockdisplayeditor.util.DefaultTextColor;
import creeperpookie.blockdisplayeditor.util.TransformationType;
import creeperpookie.blockdisplayeditor.util.Utility;
import creeperpookie.blockdisplayeditor.util.exceptions.InvalidAreaException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
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
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.item")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (args.length != 1 && args.length != 2) printHelp(sender, command, label, args);
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
						while (slot < target.getInventory().getSize())
						{
							slot++;
							ItemStack currentItem = target.getInventory().getItem(slot);
							if (CustomItem.isCustomItem(currentItem, ItemType.SELECTION_STICK) && currentItem.getAmount() < currentItem.getMaxStackSize())
							{
								currentItem.setAmount(currentItem.getAmount() + 1);
								successfullyGaveItem = true;
								break;
							}
						}
					}
					if ((slot == -1 || slot == target.getInventory().getSize()) && target.getInventory().firstEmpty() != -1) target.getInventory().setItem(target.getInventory().firstEmpty(), CustomItem.getItem(ItemType.SELECTION_STICK).getItemStack());
					else if (!successfullyGaveItem)
					{
						Utility.sendError(sender, (sender == target ? "You don't" : target.getName() + "doesn't") + " have enough space in " + (sender == target ? "your" : "their") + " inventory!");
						return true;
					}
					sender.sendMessage(Component.text("Successfully gave").appendSpace().append(Component.text(sender == target ? "you" : target.getName())).appendSpace().append(Component.text("a")).appendSpace().append(CustomItem.getItem(ItemType.SELECTION_STICK).getItemStack().effectiveName().decoration(TextDecoration.ITALIC, false)));
				}
			}
			case "blockstate" ->
			{
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.blockstate")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (args.length < 2) printHelp(sender, command, label, args);
				else if (!(sender instanceof Player playerSender)) Utility.sendError(sender, "This command can only be run by players!");
				else
				{
					StringBuilder blockState = new StringBuilder();
					for (int i = 1; i < args.length; i++)
					{
						blockState.append(args[i]);
						if (i != args.length - 1) blockState.append(" ");
					}
					BlockData blockData;
					try
					{
						blockData = Bukkit.createBlockData(blockState.toString());
					}
					catch (IllegalArgumentException e)
					{
						Utility.sendError(sender, "The given blockstate is not a valid block state!");
						return true;
					}
					BlockDisplayHandler.initializePlayer(playerSender);
					BlockDisplayHandler.getPlayerData(playerSender).setBlockData(blockData);
					sender.sendActionBar(Component.text("Successfully updated blockstate", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
				}
			}
			case "area" ->
			{
				// TODO allow freeform selection
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.area")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (!(sender instanceof Player playerSender)) Utility.sendError(sender, "This command can only be ran by players!");
				else
				{
					BlockDisplayHandler.addAreaSelectingPlayer(playerSender);
					if (!CustomItem.hasCustomItem(playerSender.getInventory(), ItemType.SELECTION_STICK)) playerSender.sendMessage(Component.text("Make sure to give yourself a selection stick to select the area!", DefaultTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
					playerSender.sendActionBar(Component.text("Enabled area selection mode (select an area by left and right clicking with the Selection Stick)"));
				}
			}
			case "select" ->
			{
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.select")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (!(sender instanceof Player)) Utility.sendError(sender, "This command can only be ran by players!");
				else if (BlockDisplayHandler.isSelectingPlayer((Player) sender)) Utility.sendError(sender, "You are already in block display select mode!");
				else
				{
					Player playerSender = (Player) sender;
					BlockDisplayHandler.addSelectingPlayer(playerSender);
					playerSender.sendActionBar(Component.text("Click an existing block display to select it (turn on entity hitboxes with F3 + B to see nearby invisible block displays!)", DefaultTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
				}
			}
			case "transform" ->
			{
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.transform")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (!(sender instanceof Player)) Utility.sendError(sender, "This command can only be ran by players!");
				else if (args.length < 3) printHelp(sender, command, label, args);
				else if (Arrays.stream(TransformationType.values()).map(TransformationType::getLowerCaseName).noneMatch(validArgument -> validArgument.equalsIgnoreCase(args[1]))) printHelp(sender, command, label, args);
				else
				{
					Player playerSender = (Player) sender;
					BlockDisplayHandler.initializePlayer(playerSender);
					TransformationType type;
					try
					{
						type = TransformationType.valueOf(args[1].toUpperCase());
					}
					catch (IllegalArgumentException e)
					{
						Utility.sendError(sender, "The given transformation type " + args[1] + " is not a valid transformation type!");
						printHelp(sender, command, label, args);
						return true;
					}
					BlockDisplayHandler.setPlayerTransformationType(playerSender, type);
					switch (type)
					{
						case LEFT_ROTATION, RIGHT_ROTATION ->
						{
							double degreeAngle;
							try
							{
								degreeAngle = Double.parseDouble(args[2]);
							}
							catch (NumberFormatException e)
							{
								Utility.sendError(sender, "The given degree angle must be a number!");
								return true;
							}
							BlockDisplayHandler.getPlayerData(playerSender).setLeftRotation(new Quaternionf(0, 0, 0, Math.cos(Math.toRadians(degreeAngle / 2))));
							playerSender.sendActionBar(Component.text("Face the direction you want to rotate the block display around for its axis of rotation, then click the Selection Stick", DefaultTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
						}
						case SCALE ->
						{
							if (args.length != 3 && args.length != 5) printHelp(sender, command, label, args);
							float linkedScale = 0;
							float xScale = 0, yScale = 0, zScale = 0;
							try
							{
								if (args.length == 5)
								{
									xScale = Float.parseFloat(args[2]);
									yScale = Float.parseFloat(args[3]);
									zScale = Float.parseFloat(args[4]);
								}
								else linkedScale = Float.parseFloat(args[2]);
							}
							catch (NumberFormatException e)
							{
								if (args.length == 3) Utility.sendError(sender, "The given scale factor must be a number!");
								else Utility.sendError(sender, "All axis' scale factors must be a number!");
								printHelp(sender, command, label, args);
							}
							if (linkedScale <= 0 && (xScale == 0 && yScale == 0 && zScale == 0)) Utility.sendError(sender, "The given scale factor must greater than zero!");
							else if (linkedScale == 0 && (xScale <= 0 || yScale <= 0 || zScale <= 0)) Utility.sendError(sender, "All axis' scale factors must greater than zero!");
							else BlockDisplayHandler.getPlayerData(playerSender).setScale(linkedScale == 0 ? new Vector3f(xScale, yScale, zScale) : new Vector3f(linkedScale));
							playerSender.sendActionBar(Component.text("Successfully updated block display scale factor").append(Component.text(linkedScale == 0 ? "s" : "")).color(DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
						}
						case TRANSLATION ->
						{
							if (args.length != 5) printHelp(sender, command, label, args);
							float xOffset, yOffset, zOffset;
							try
							{
								xOffset = Float.parseFloat(args[2]);
								yOffset = Float.parseFloat(args[3]);
								zOffset = Float.parseFloat(args[4]);
							}
							catch (NumberFormatException e)
							{
								Utility.sendError(sender, "The given axis' translation offsets must be a number!");
								return true;
							}
							if (xOffset == 0 && yOffset == 0 && zOffset == 0) Utility.sendError(sender, "All of the translation offsets must not be zero!");
							else
							{
								BlockDisplayHandler.getPlayerData(playerSender).setTranslation(new Vector3f(xOffset, yOffset, zOffset));
								playerSender.sendActionBar(Component.text("Successfully updated block display translation offset", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
							}
						}
					}
				}
			}
			case "save" ->
			{
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.save")) Utility.sendError(sender, "You do not have permission to perform this command!");
				else if (!(sender instanceof Player)) Utility.sendError(sender, "You must be a player to perform this command!");
				else if (!BlockDisplayHandler.initializePlayer((Player) sender) || BlockDisplayHandler.getPlayerData((Player) sender).getSelectedBlockDisplay() == null)
				{
					Utility.sendError(sender, "Saving a block display requires a block display selected!!");
					sender.sendMessage(Component.text("To create a new block display, run", DefaultTextColor.GOLD).appendSpace().append(Component.text("/").append(Component.text(label)).appendSpace().append(Component.text("create")).color(DefaultTextColor.GREEN)).append(Component.text("!").color(DefaultTextColor.GOLD)).decoration(TextDecoration.ITALIC, false));
				}
				else
				{
					Player playerSender = (Player) sender;
					try
					{
						BlockDisplayHandler.getPlayerData(playerSender).updateBlockDisplay(false);
					}
					catch (InvalidAreaException e)
					{
						Utility.sendError(sender, "You need to select an area first!");
						return true;
					}
					playerSender.sendActionBar(Component.text("Successfully updated block display!", DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
				}
			}
			case "create" ->
			{
				if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.create")) Utility.sendError(sender, "You don't have permission to do that!");
				else if (!(sender instanceof Player playerSender)) Utility.sendError(sender, "This command can only be ran by players!");
				else
				{
					BlockDisplayHandler.initializePlayer(playerSender);
					try
					{
						BlockDisplayHandler.getPlayerData(playerSender).updateBlockDisplay(true);
					}
					catch (InvalidAreaException e)
					{
						Utility.sendError(sender, "You need to select an area first!");
						return true;
					}
					playerSender.sendActionBar(Component.text("Successfully created block display at").appendSpace().append(Component.text(Utility.locationAsString(BlockDisplayHandler.getPlayerData(playerSender).getSelectedBlockDisplay().getLocation(), true, false))));
				}
			}
			case "dev_block" ->
			{
				// TODO no permission check, remove or restrict for production!
				if (!(sender instanceof Player playerSender)) Utility.sendError(sender, "This command can only be ran by players!");
				else
				{
					Location block = playerSender.getLocation().toBlockLocation();
					block.setYaw(0);
					block.setPitch(0);
					int count = 0;
					float scale = 16;
					for (int xOffset = 0; xOffset < scale; xOffset++)
					{
						for (int yOffset = 0; yOffset < scale; yOffset++)
						{
							for (int zOffset = 0; zOffset < scale; zOffset++)
							{
								if (((xOffset > 0 && xOffset < scale - 1) && (zOffset > 0 && zOffset < scale - 1) && (yOffset != 0 && yOffset != scale - 1))) continue;
								int finalCount = count;
								int finalXOffset = xOffset;
								int finalYOffset = yOffset;
								int finalZOffset = zOffset;
								Bukkit.getScheduler().runTaskLater(BlockDisplayEditor.getInstance(), () ->
								{
									BlockDisplay blockDisplay = block.getWorld().spawn(block.toBlockLocation().clone().add(finalXOffset / scale, finalYOffset / scale, finalZOffset / scale), BlockDisplay.class);
									blockDisplay.setBlock(finalCount % 2 == 0 ? Material.GREEN_WOOL.createBlockData() : Material.GREEN_CONCRETE.createBlockData());
									blockDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1/scale), new AxisAngle4f()));
								}, (long) (count / scale));
								count++;
							}
						}
					}
				}
			}
			case "reload" ->
			{
				//if (args.length > 1) printHelp(sender, command, label, args);
				/* else */if (!sender.isOp() && !sender.hasPermission("blockdisplayeditor.reload")) Utility.sendError(sender, "You don't have permission to do that!");
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
