package inc.a13xis.legacy.koresample.common.block;

import inc.a13xis.legacy.koresample.tree.DefinesStairs;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public abstract class StairsBlock extends BlockStairs
{
    protected StairsBlock(DefinesStairs model)
    {
        super(Blocks.oak_stairs.getStateFromMeta(model.stairsModelSubBlockIndex()));
    }

    @Override
    public boolean getUseNeighborBrightness()
    {
        // Fix lighting bugs
        return true;
    }

    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    @Override
    public final String getUnlocalizedName()
    {
        return "tile." + resourcePrefix() + getUnwrappedUnlocalizedName(super.getUnlocalizedName());
    }

    public final void registerBlockModel()
    {
            final String iconName = String.format("slab_%s",  getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),0,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
    }

    protected abstract String resourcePrefix();
}
