package creeperpookie.blockdisplayeditor.items;
import creeperpookie.blockdisplayeditor.items.customitems.SelectionStickItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface CustomItem
{
	HashMap<Class<? extends CustomItem>, CustomItem> registeredItems = new HashMap<>();

	@NotNull ItemStack getItemStack();
	@NotNull String getName();
	
	static boolean isCustomItem(ItemStack item)
	{
		return registeredItems.values().stream().anyMatch(customItem -> customItem.getItemStack().isSimilar(item));
	}

	static boolean isCustomItem(ItemStack item, ItemType type)
	{
		CustomItem customItem = getItem(type);
		if (item == null || customItem == null) return false;
		return item.isSimilar(customItem.getItemStack());
	}
	
	static boolean isCustomItem(ItemStack item, Class<? extends CustomItem> $class)
	{
		return registeredItems.get($class).getItemStack().isSimilar(item);
	}
	
	static boolean isCustomItem(ItemStack item, String customItemName)
	{
		return getItem(customItemName) != null && getItem(customItemName).getItemStack().isSimilar(item);
	}
	
	static boolean isCustomItem(String customItemName)
	{
		return registeredItems.values().stream().anyMatch(customItem -> customItem.getName().equalsIgnoreCase(customItemName));
	}
	
	static boolean isCustomItem(String customItemName, ItemType type)
	{
		return isCustomItem(customItemName) && getItem(customItemName).equals(getItem(type));
	}

	static boolean hasCustomItem(Inventory inventory)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item)) return true;
		}
		return false;
	}

	static boolean hasCustomItem(Inventory inventory, ItemType type)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, type)) return true;
		}
		return false;
	}

	static boolean hasCustomItem(Inventory inventory, Class<? extends CustomItem> $class)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, $class)) return true;
		}
		return false;
	}
	
	static boolean hasCustomItem(Inventory inventory, String customItemName)
	{
		for (ItemStack item : inventory)
		{
			if (isCustomItem(item, customItemName)) return true;
		}
		return false;
	}

	@Nullable
	static CustomItem getItem(ItemType type)
	{
		return getItem(switch (type)
		{
			case SELECTION_STICK -> SelectionStickItem.class;
		});
	}
	
	@Nullable
	static CustomItem getItem(String customItemName)
	{
		return registeredItems.values().stream().filter(customItem -> customItem.getName().equalsIgnoreCase(customItemName)).findFirst().orElse(null);
	}

	@Nullable
	static CustomItem getItem(Class<? extends CustomItem> $class)
	{
		return registeredItems.get($class);
	}

	static CustomItem[] getCustomItems()
	{
		return registeredItems.values().toArray(new CustomItem[0]);
	}

	static int getRegisteredItemCount()
	{
		return registeredItems.size();
	}

	static void registerAll()
	{
		registerItem(new SelectionStickItem());
	}

	private static void registerItem(CustomItem customItem)
	{
		if (registeredItems.containsKey(customItem.getClass())) return;
		registeredItems.put(customItem.getClass(), customItem);
	}
}
