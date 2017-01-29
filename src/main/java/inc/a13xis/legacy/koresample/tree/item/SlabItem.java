package inc.a13xis.legacy.koresample.tree.item;

import inc.a13xis.legacy.koresample.common.block.SlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class SlabItem extends ItemSlab
{
    // This provides a reminder that you must extend this class and change the constructor to accept your extension of
    // SlabBlock in the second and third  parameters
    protected SlabItem(Block block, SlabBlock singleSlab, SlabBlock doubleSlab)
    {
        super(block, singleSlab, doubleSlab);
    }
}
