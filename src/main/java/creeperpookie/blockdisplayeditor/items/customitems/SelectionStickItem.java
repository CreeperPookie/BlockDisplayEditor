package creeperpookie.blockdisplayeditor.items.customitems;

import creeperpookie.blockdisplayeditor.items.CustomItem;
import creeperpookie.blockdisplayeditor.util.DefaultTextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SelectionStickItem implements CustomItem
{
	@NotNull
	@Override
	public ItemStack getItemStack()
	{
		ItemStack selectionStick = new ItemStack(Material.STICK);
		selectionStick.editMeta(meta ->
		{
			meta.displayName(Component.text("Selection Stick", DefaultTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		});
		selectionStick.addUnsafeEnchantment(Enchantment.INFINITY, 1);
		return selectionStick;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "selection_stick";
	}
}
