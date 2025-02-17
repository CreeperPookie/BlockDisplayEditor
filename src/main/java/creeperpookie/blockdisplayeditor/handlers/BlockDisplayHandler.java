package creeperpookie.blockdisplayeditor.handlers;

import creeperpookie.blockdisplayeditor.data.BlockDisplayData;
import creeperpookie.blockdisplayeditor.items.CustomItem;
import creeperpookie.blockdisplayeditor.items.ItemType;
import creeperpookie.blockdisplayeditor.util.DefaultTextColor;
import creeperpookie.blockdisplayeditor.util.TransformationType;
import creeperpookie.blockdisplayeditor.util.Utility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.HashSet;

public class BlockDisplayHandler implements Listener
{
	private static final HashSet<Player> blockDisplaySelectingPlayers = new HashSet<>();
	private static final HashMap<Player, TransformationType> playerTransformations = new HashMap<>();
	private static final HashSet<Player> areaSelectingPlayers = new HashSet<>();
	private static final HashMap<Player, BlockDisplayData> playerBlockDisplayData = new HashMap<>();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if (!blockDisplaySelectingPlayers.contains(player)) return;
		player.getNearbyEntities(10, 10, 10).stream().filter(entity -> entity instanceof BlockDisplay).map(entity -> (BlockDisplay) entity).forEach(blockDisplay ->
		{
			// TODO add interaction generator and anti-interaction cloning
		});
	}

	@EventHandler
	public void onItemInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (event.getItem() == null || !CustomItem.isCustomItem(event.getItem(), ItemType.SELECTION_STICK) || event.getAction() == Action.PHYSICAL) return;
		event.setCancelled(true);
		initializePlayer(player);
		if (areaSelectingPlayers.contains(player) && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK))
		{
			Location clickedLocation = event.getClickedBlock().getLocation().toBlockLocation();
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) playerBlockDisplayData.get(player).getArea().setPos1(clickedLocation);
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) playerBlockDisplayData.get(player).getArea().setPos2(clickedLocation);
			player.sendActionBar(Component.text("Selected position").appendSpace().append(Component.text(event.getAction() == Action.LEFT_CLICK_BLOCK ? 1 : 2)).append(Component.text(":")).appendSpace().append(Component.text(Utility.locationAsString(clickedLocation, true, false))));
			// if (playerBlockDisplayData.get(player).getArea().isValid()) removeAreaSelectingPlayer(player); // TODO move to user config setting
		}
		if (playerTransformations.containsKey(player) && (getPlayerTransformationType(player) == TransformationType.LEFT_ROTATION || getPlayerTransformationType(player) == TransformationType.RIGHT_ROTATION))
		{
			float yaw = player.getLocation().getYaw();
			float pitch = player.getLocation().getPitch();
			double x = Math.cos(pitch) * Math.cos(yaw);
			double y = Math.sin(pitch) * Math.sin(yaw);
			double z = Math.sin(pitch);
			Quaternionf rotation = new Quaternionf(x, y, z, getPlayerTransformationType(player) == TransformationType.LEFT_ROTATION ? getPlayerData(player).getLeftRotation().w : getPlayerData(player).getRightRotation().w);
			if (getPlayerTransformationType(player) == TransformationType.LEFT_ROTATION) getPlayerData(player).setLeftRotation(rotation);
			else getPlayerData(player).setRightRotation(rotation);
			player.sendActionBar(Component.text("Successfully updated block display").appendSpace().append(Component.text(getPlayerTransformationType(player).getFormattedName())).color(DefaultTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
			setPlayerTransformationType(player, null);
		}
		//if (player.getNearbyEntities(5, 5, 5).stream().noneMatch(entity -> entity instanceof BlockDisplay)) BlockDisplayGUIHandler.openBlockDisplayGUI(player);
		//RayTraceResult entityRayTraces = player.rayTraceEntities(5);
		//if (entityRayTraces == null || entityRayTraces.getHitEntity() == null || !(entityRayTraces.getHitEntity() instanceof BlockDisplay)) BlockDisplayGUIHandler.openBlockDisplayGUI(player);
		//else BlockDisplayGUIHandler.openBlockDisplayEditGUI(player, (BlockDisplay) entityRayTraces.getHitEntity());
	}

	@EventHandler
	public void onPlayerClickEntity(EntityDamageByEntityEvent event)
	{
		if (!(event.getEntity() instanceof BlockDisplay) && !(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		BlockDisplay blockDisplay = (BlockDisplay) event.getEntity();
		if (isBlockDisplaySelected(player, blockDisplay)) player.sendActionBar(Component.text("You have already selected this block display!"));
		blockDisplaySelectingPlayers.remove(player);
		player.sendActionBar(Component.text("Successfully selected block display at", DefaultTextColor.GREEN).appendSpace().append(Component.text(Utility.locationAsString(blockDisplay.getLocation(), true, false)).color(DefaultTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
	}

	@EventHandler
	public void onPlayerEntityInteract(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!blockDisplaySelectingPlayers.contains(player) || item.isEmpty() || !CustomItem.isCustomItem(item, ItemType.SELECTION_STICK) || !(event.getRightClicked() instanceof BlockDisplay blockDisplay)) return;
		event.setCancelled(true);
		if (isBlockDisplaySelected(player, blockDisplay)) player.sendActionBar(Component.text("You have already selected this block display!"));
		getPlayerData(player).copyFromBlockDisplay(blockDisplay);
		getPlayerData(player).setSelectedBlockDisplay(blockDisplay);
		blockDisplaySelectingPlayers.remove(player);
		player.sendActionBar(Component.text("Selected block display at", DefaultTextColor.LIGHT_PURPLE).appendSpace().append(Component.text(Utility.locationAsString(event.getRightClicked().getLocation(), true, false), DefaultTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
	}

	@Nullable
	public static TransformationType getPlayerTransformationType(Player player)
	{
		return playerTransformations.get(player);
	}

	public static void setPlayerTransformationType(Player player, @Nullable TransformationType type)
	{
		if (type == null) playerTransformations.remove(player);
		else playerTransformations.put(player, type);
	}

	public static boolean isSelectingPlayer(Player player)
	{
		return blockDisplaySelectingPlayers.contains(player);
	}

	public static boolean addSelectingPlayer(Player player)
	{
		return blockDisplaySelectingPlayers.add(player);
	}

	public static boolean addAreaSelectingPlayer(Player player)
	{
		return areaSelectingPlayers.add(player);
	}

	public static boolean removeAreaSelectingPlayer(Player player)
	{
		return areaSelectingPlayers.remove(player);
	}

	public static boolean initializePlayer(Player player)
	{
		if (!playerBlockDisplayData.containsKey(player))
		{
			playerBlockDisplayData.put(player, new BlockDisplayData());
			return true;
		}
		return false;
	}

	@Nullable
	public static BlockDisplayData getPlayerData(Player player)
	{
		return playerBlockDisplayData.get(player);
	}

	private boolean isBlockDisplaySelected(Player player, BlockDisplay blockDisplay)
	{
		if (blockDisplaySelectingPlayers.contains(player)) initializePlayer(player);
		if (getPlayerData(player).getSelectedBlockDisplay() == null || !getPlayerData(player).getSelectedBlockDisplay().equals(blockDisplay)) getPlayerData(player).setSelectedBlockDisplay(blockDisplay);
		else return getPlayerData(player).getSelectedBlockDisplay() != null && getPlayerData(player).getSelectedBlockDisplay().equals(blockDisplay);
		return false;
	}
}
