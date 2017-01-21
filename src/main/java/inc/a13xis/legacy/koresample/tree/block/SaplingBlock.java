package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSapling;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import scala.Int;

import java.util.*;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings("AbstractClassNeverImplemented")
public abstract class SaplingBlock extends BlockSapling
{
    public static final int CAPACITY = Integer.MAX_VALUE;
    private static final int METADATA_MASK = CAPACITY - 1;
    private final ImmutableList<DefinesSapling> subBlocks;

    protected SaplingBlock(Collection<? extends DefinesSapling> subBlocks)
    {
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);

        setUnlocalizedName("sapling");
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    private static int mask(int metadata) { return metadata & METADATA_MASK; }

    protected final List<DefinesSapling> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    public final List<String> subBlockNames() {
        final List<String> names = Lists.newArrayListWithCapacity(subBlocks.size());
        for (final DefinesSapling subBlock : subBlocks)
            names.add(subBlock.speciesName());
        return ImmutableList.copyOf(names);
    }

    @Override
    public void generateTree(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (!TerrainGen.saplingGrowTree(world, rand, pos)) return;

        final int metadata = mask(this.getMetaFromState(state));
        final WorldGenerator treeGen = subBlocks.get(metadata).saplingTreeGenerator();
        world.setBlockToAir(pos);
        if (!treeGen.generate(world, rand, pos)) world.setBlockState(pos,this.getStateFromMeta(metadata),4);
    }

    @Override
    public final int damageDropped(IBlockState state)
    {
        return mask(this.getMetaFromState(state));
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public final void getSubBlocks(Item item, CreativeTabs unused, List subBlocks)
    {
        for (int i = 0; i < this.subBlocks.size(); i++)
            //noinspection ObjectAllocationInLoop
            subBlocks.add(new ItemStack(item, 1, i));
    }

    public final void registerBlockModels()
    {

        for (int i = 0; i < subBlocks.size(); i++)
        {
            final String iconName = String.format("sapling_%s",  subBlocks.get(i).speciesName().replace('.', '_'));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),i,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
        }
    }

    @Override
    public final String getUnlocalizedName()
    {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    protected abstract String resourcePrefix();

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }
}
