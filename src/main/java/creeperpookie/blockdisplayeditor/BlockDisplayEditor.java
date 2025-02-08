package creeperpookie.blockdisplayeditor;

import creeperpookie.blockdisplayeditor.commands.BlockDisplayCommand;
import creeperpookie.blockdisplayeditor.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class BlockDisplayEditor extends JavaPlugin
{
	private static BlockDisplayEditor instance;
	private static final Random random = new Random();

	@Override
	public void onEnable()
	{
		instance = this;
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		reloadConfig();
		CustomItem.registerAll();
		getCommand("blockdisplay").setExecutor(new BlockDisplayCommand());
	}

	@Override
	public void onDisable()
	{
		getCommand("blockdisplay").unregister(Bukkit.getCommandMap());
		// Plugin shutdown logic
	}

	public static void reloadConfigs()
	{
		getInstance().reloadConfig();
	}

	public static BlockDisplayEditor getInstance()
	{
		return instance;
	}

	public static Random getRandom()
	{
		return random;
	}
}
