package creeperpookie.blockdisplayeditor.handlers;

import creeperpookie.blockdisplayeditor.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class BlockDisplayGUIHandler implements Listener
{
	private static final HashSet<Player> playersInCreationGUI = new HashSet<>();
	private static final HashSet<Player> playersInEditGUI = new HashSet<>();

	public static void openBlockDisplayGUI(@NotNull Player player)
	{
		if (playersInCreationGUI.contains(player)) return;
		playersInCreationGUI.add(player);
		Inventory inventory = Bukkit.createInventory(null, 9, Component.text("Block Display Creator", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
		//player.open
	}

	public static void openBlockDisplayEditGUI(@NotNull Player player, @NotNull BlockDisplay blockDisplay)
	{

	}
}
