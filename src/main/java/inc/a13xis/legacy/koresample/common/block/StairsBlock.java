package inc.a13xis.legacy.koresample.common.block;

import inc.a13xis.legacy.koresample.tree.DefinesStairs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

@SuppressWarnings("AbstractClassNeverImplemented")
public abstract class StairsBlock extends BlockStairs
{
    Enum variant;
    protected StairsBlock(DefinesStairs model)
    {
        super(model.stairsModelBlock().getStateFromMeta(model.stairsModelSubBlockVariant().ordinal()));
        this.variant=model.stairsModelSubBlockVariant();
        setUnlocalizedName("stairs");
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
        String[] pair = getUnwrappedUnlocalizedName(getUnlocalizedName()).split(":");
        pair[1]=pair[1].split("\\.")[0]+i;
        Item itemStairsBlock = GameRegistry.findItem(pair[0],pair[1]);
        ModelResourceLocation typeLocation = new ModelResourceLocation(pair[0]+":"+pair[1]);
        ModelLoader.setCustomModelResourceLocation(itemStairsBlock, 0, typeLocation);
    }
}
