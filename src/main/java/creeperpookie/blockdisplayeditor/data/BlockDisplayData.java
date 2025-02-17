package creeperpookie.blockdisplayeditor.data;

import creeperpookie.blockdisplayeditor.BlockDisplayEditor;
import creeperpookie.blockdisplayeditor.util.Area;
import creeperpookie.blockdisplayeditor.util.exceptions.InvalidAreaException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class BlockDisplayData
{
	private Area area;
	private BlockData blockData;
	private BlockDisplay selectedBlockDisplay;
	private Quaternionf leftRotation = new Quaternionf();
	private Quaternionf rightRotation = new Quaternionf();
	private Vector3f scale = new Vector3f(1);
	private Vector3f translation = new Vector3f();

	public BlockDisplayData()
	{
		area = new Area();
		blockData = Bukkit.createBlockData(BlockDisplayEditor.getInstance().getConfig().getString("default-block-type", "minecraft:calcite"));
		selectedBlockDisplay = null;
	}

	@NotNull
	public Area getArea()
	{
		return area;
	}

	@Nullable
	public BlockData getBlockData()
	{
		return blockData;
	}

	public void setBlockData(BlockData blockData)
	{
		this.blockData = blockData;
	}

	@Nullable
	public BlockDisplay getSelectedBlockDisplay()
	{
		return selectedBlockDisplay;
	}

	public void setSelectedBlockDisplay(BlockDisplay selectedBlockDisplay)
	{
		this.selectedBlockDisplay = selectedBlockDisplay;
	}

	public Quaternionf getLeftRotation()
	{
		return leftRotation;
	}

	public void setLeftRotation(Quaternionf leftRotation)
	{
		this.leftRotation = leftRotation;
	}

	public Quaternionf getRightRotation()
	{
		return rightRotation;
	}

	public void setRightRotation(Quaternionf rightRotation)
	{
		this.rightRotation = rightRotation;
	}

	public Vector3f getScale()
	{
		return scale;
	}

	public void setScale(Vector3f scale)
	{
		this.scale = scale;
	}

	public Vector3f getTranslation()
	{
		return translation;
	}

	public void setTranslation(Vector3f translation)
	{
		this.translation = translation;
	}

	public void updateBlockDisplay(boolean create) throws InvalidAreaException
	{
		if (!area.isValid()) throw new InvalidAreaException(this);
		Location center = area.getCenter();
		if (create) selectedBlockDisplay = area.getPos1().getWorld().spawn(center, BlockDisplay.class);
		else selectedBlockDisplay.getLocation().set(center.getX(), center.getY(), center.getZ());
		selectedBlockDisplay.setBlock(blockData);
		selectedBlockDisplay.setTransformation(new Transformation(translation, leftRotation, scale, rightRotation));
		selectedBlockDisplay.setDisplayWidth(1); // TODO add width/height control
		selectedBlockDisplay.setDisplayHeight(1);
	}

	public void copyFromBlockDisplay(BlockDisplay blockDisplay)
	{
		area = new Area(blockDisplay.getLocation(), blockDisplay.getLocation());
		blockData = blockDisplay.getBlock();
		leftRotation = blockDisplay.getTransformation().getLeftRotation();
		rightRotation = blockDisplay.getTransformation().getRightRotation();
		scale = blockDisplay.getTransformation().getScale();
		translation = blockDisplay.getTransformation().getTranslation();
	}
}
