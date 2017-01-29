package inc.a13xis.legacy.koresample.tree;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.IBlockAccess;

public interface ProvidesColor
{
    @SideOnly(Side.CLIENT)
    int getLeavesInventoryColor();

    @SideOnly(Side.CLIENT)
    int getLeavesColor(IBlockAccess blockAccess, BlockPos pos);
}
