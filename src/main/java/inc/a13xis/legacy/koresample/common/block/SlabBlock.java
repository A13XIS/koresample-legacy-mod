package inc.a13xis.legacy.koresample.common.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import inc.a13xis.legacy.koresample.common.util.slab.TheSingleSlabRegistry;
import inc.a13xis.legacy.koresample.tree.DefinesSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings("AbstractClassNeverImplemented")
public abstract class SlabBlock extends BlockSlab
{
    public boolean isdouble = true;
    public static final int CAPACITY = Integer.MAX_VALUE;
    private static final int METADATA_MASK = CAPACITY - 1;
    private static final TheSingleSlabRegistry slabRegistry = TheSingleSlabRegistry.REFERENCE;
    private final ImmutableList<DefinesSlab> subBlocks;

    protected SlabBlock(boolean isDouble, Collection<? extends DefinesSlab> subBlocks)
    {
        super(Material.wood);
        isdouble=isDouble;
        if(!isDouble){
         this.fullBlock=false;
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        setUnlocalizedName("slab");
    }

    private static int mask(int metadata) {return metadata & METADATA_MASK;}

    public static boolean isSingleSlab(Item item)
    {
        return slabRegistry.isSingleSlab(item);
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    @Override
    public boolean isDouble(){
        return isdouble;
    }

    @Override
    public final Item getItemDropped(IBlockState state, Random unused, int unused2)
    {
        final DefinesSlab subBlock = subBlocks.get(mask(this.getMetaFromState(state)));
        return Item.getItemFromBlock(subBlock.singleSlabBlock());
    }

    @Override
    public boolean getUseNeighborBrightness()
    {
        // Fix lighting bugs
        return true;
    }

    public final void registerBlockModels()
    {

        for (int i = 0; i < subBlocks.size(); i++)
        {
            final String iconName = String.format("slab_%s",  subBlocks.get(i).slabName().replace('.', '_'));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),i,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
        }
    }

    @Override
    protected final ItemStack createStackedBlock(IBlockState state)
    {
        final DefinesSlab subBlock = subBlocks.get(mask(this.getMetaFromState(state)));
        return new ItemStack(Item.getItemFromBlock(subBlock.singleSlabBlock()), 2, subBlock.slabSubBlockIndex());
    }

    @Override
    public final String getUnlocalizedName()
    {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public final void getSubBlocks(Item item, CreativeTabs unused, List subblocks) {
        if (isSingleSlab(item)) {
            for (int i = 0; i < subBlocks.size(); ++i) {
                //noinspection ObjectAllocationInLoop
                subblocks.add(new ItemStack(item, 1, i));
            }
        }
    }

    @Override
    public final String getUnlocalizedName(int metadata)
    {
        int metadata1 = metadata;
        if (metadata1 < 0 || metadata1 >= subBlocks.size())
        {
            metadata1 = 0;
        }

        return getUnlocalizedName() + '.' + subBlocks.get(metadata1).slabName();
    }

    protected abstract String resourcePrefix();

    protected final List<DefinesSlab> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    @Override
    public String toString() { return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString(); }
}
