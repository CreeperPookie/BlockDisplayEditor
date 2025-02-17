package creeperpookie.blockdisplayeditor.util;

public enum TransformationType
{
	LEFT_ROTATION,
	RIGHT_ROTATION,
	SCALE,
	TRANSLATION;

	public String getLowerCaseName()
	{
		return name().toLowerCase();
	}

	public String getFormattedName()
	{
		return Utility.formatText(name());
	}
}
