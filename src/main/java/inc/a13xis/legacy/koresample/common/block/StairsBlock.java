package inc.a13xis.legacy.koresample.common.block;

import inc.a13xis.legacy.koresample.tree.DefinesStairs;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@SuppressWarnings("AbstractClassNeverImplemented")
public abstract class StairsBlock extends BlockStairs
{
    protected StairsBlock(DefinesStairs model)
    {
        super(model.stairsModelBlock().getStateFromMeta(model.stairsModelSubBlockVariant().ordinal()));
    }

    @Override
    public boolean getUseNeighborBrightness()
    {
        // Fix lighting bugs
        return true;
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    @Override
    public final String getUnlocalizedName()
    {
        //noinspection StringConcatenationMissingWhitespace
        return "tile." + resourcePrefix() + getUnwrappedUnlocalizedName(super.getUnlocalizedName());
    }

    protected abstract String resourcePrefix();

    public void registerBlockModel(int i) {
        String iconName = getUnlocalizedName().substring(getUnlocalizedName().indexOf('.')+1);
        iconName = iconName.substring(0,iconName.indexOf('.'))+i;
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),0,new ModelResourceLocation(iconName,"inventory"));
    }
}
