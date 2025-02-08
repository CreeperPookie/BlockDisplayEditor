package creeperpookie.blockdisplayeditor.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Random;

public class DefaultTextColor
{
	public static final TextColor BLACK = TextColor.color(0);
	public static final TextColor DARK_BLUE = TextColor.color(170);
	public static final TextColor DARK_GREEN = TextColor.color(43520);
	public static final TextColor DARK_AQUA = TextColor.color(43690);
	public static final TextColor DARK_RED = TextColor.color(11141120);
	public static final TextColor DARK_PURPLE = TextColor.color(11141290);
	public static final TextColor GOLD = TextColor.color(16755200);
	public static final TextColor GRAY = TextColor.color(11184810);
	public static final TextColor DARK_GRAY = TextColor.color(5592405);
	public static final TextColor BLUE = TextColor.color(5592575);
	public static final TextColor GREEN = TextColor.color(5635925);
	public static final TextColor AQUA = TextColor.color(5636095);
	public static final TextColor RED = TextColor.color(16733525);
	public static final TextColor LIGHT_PURPLE = TextColor.color(16733695);
	public static final TextColor YELLOW = TextColor.color(16777045);
	public static final TextColor WHITE = TextColor.color(16777215);
	
	public static TextColor getRandom(Random random)
	{
		return TextColor.color(random.nextInt(WHITE.value() + 1));
	}

	public static Component gradient(String message, int fromColor, int toColor)
	{
		Component component = Component.empty();
		for (int i = 0; i < message.length(); i++)
		{
			component = component.append(Component.text(message.charAt(i)).color(TextColor.color(getCharColor(message.length(), i, fromColor, toColor))));
		}
		return component;
	}

	private static int getCharColor(int length, int index, int fromColor, int toColor)
	{
		int redDifference = ((toColor >> 16) & 0xFF) - ((fromColor >> 16) & 0xFF);
		int greenDifference = ((toColor >> 8) & 0xFF) - ((fromColor >> 8) & 0xFF);
		int blueDifference = (toColor & 0xFF) - (fromColor & 0xFF);

		int redStep = redDifference / (length - 1);
		int greenStep = greenDifference / (length - 1);
		int blueStep = blueDifference / (length - 1);

		int red = ((fromColor >> 16) & 0xFF) + (redStep * index);
		int green = ((fromColor >> 8) & 0xFF) + (greenStep * index);
		int blue = (fromColor & 0xFF) + (blueStep * index);

		return (red << 16) | (green << 8) | blue;
	}
}
