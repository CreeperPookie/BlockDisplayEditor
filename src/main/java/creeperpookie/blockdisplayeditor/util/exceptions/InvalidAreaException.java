package creeperpookie.blockdisplayeditor.util.exceptions;

import creeperpookie.blockdisplayeditor.data.BlockDisplayData;

public class InvalidAreaException extends Exception
{
	public InvalidAreaException(BlockDisplayData data)
	{
		super("Creating a block display requires a valid area size");
	}
}
