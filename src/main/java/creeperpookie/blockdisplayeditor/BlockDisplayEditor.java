package creeperpookie.blockdisplayeditor;

import creeperpookie.blockdisplayeditor.commands.BlockDisplayCommand;
import creeperpookie.blockdisplayeditor.handlers.BlockDisplayGUIHandler;
import creeperpookie.blockdisplayeditor.handlers.BlockDisplayHandler;
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
		Bukkit.getPluginManager().registerEvents(new BlockDisplayHandler(), this);
		Bukkit.getPluginManager().registerEvents(new BlockDisplayGUIHandler(), this);
	}

	@Override
	public void onDisable()
	{
		getCommand("blockdisplay").unregister(Bukkit.getCommandMap());
	}

	public static void reloadConfigs()
	{
		// Currently only one config, but more configs could be added (this would reload them together)
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
