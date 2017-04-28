package inc.a13xis.legacy.koresample.common.block;

import inc.a13xis.legacy.koresample.tree.DefinesSlab;
import inc.a13xis.legacy.koresample.tree.DefinesStairs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.rmi.registry.Registry;

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

    public void registerBlockModel()
    {
            ModelResourceLocation typeLocation = new ModelResourceLocation(new ResourceLocation(getRegistryName().getResourceDomain(),getRegistryName().getResourcePath()),"inventory");
            //ModelResourceLocation typeItemLocation = new ModelResourceLocation(getRegistryName().toString().substring(0,getRegistryName().toString().length()-1)+"_"+define.leavesSubBlockVariant().name().toLowerCase(),"inventory");
            Item blockItem = Item.getItemFromBlock(this);
            ModelLoader.setCustomModelResourceLocation(blockItem,0,typeLocation);
    }
}
