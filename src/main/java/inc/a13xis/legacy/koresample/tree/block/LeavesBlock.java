package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.*;

public abstract class LeavesBlock extends BlockLeaves
{
    public static final int CAPACITY = Integer.MAX_VALUE;
    private static final int METADATA_MASK = CAPACITY - 1;
    private final ImmutableList<DefinesLeaves> subBlocks;

    protected LeavesBlock(Collection<? extends DefinesLeaves> subBlocks)
    {
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        setUnlocalizedName("leaves");
    }

    private static int mask(int metadata) {return metadata & METADATA_MASK;}

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    @SideOnly(Side.CLIENT)
    private static boolean isFancyGraphics() {return Minecraft.getMinecraft().gameSettings.fancyGraphics;}

    protected final List<DefinesLeaves> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    @SideOnly(Side.CLIENT)
    @Override
    public final int getRenderColor(IBlockState state) {
        int metadata=this.getMetaFromState(state);
        return subBlocks.get(mask(metadata)).getLeavesInventoryColor();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final int colorMultiplier(IBlockAccess blockAccess, BlockPos pos, int rp)
    {
        final int metadata = mask(this.getMetaFromState(blockAccess.getBlockState(pos)));
        return subBlocks.get(metadata).getLeavesColor(blockAccess, pos);
    }

    @Override
    public final Item getItemDropped(IBlockState state, Random unused, int unused2)
    {
        return Item.getItemFromBlock(subBlocks.get(mask(this.getMetaFromState(state))).saplingDefinition().saplingBlock());
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return subBlocks.get(mask(this.getMetaFromState(state))).saplingDefinition().saplingSubBlockIndex();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final boolean isOpaqueCube() { return !isFancyGraphics(); }

    public final String[] getSpeciesNames() //func_150125_e
    {
        final List<String> names = Lists.newArrayList();
        for (final DefinesLeaves subBlock : subBlocks)
            names.add(subBlock.speciesName());
        return names.toArray(new String[names.size()]);
    }

    @Override
    public final String getUnlocalizedName()
    {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public final int getDamageValue(World world, BlockPos pos) { return this.getMetaFromState(world.getBlockState(pos)) & 3; }

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
            final String iconName = String.format("leaves_%s",  subBlocks.get(i).speciesName().replace('.', '_'));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),i,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
        }
    }

    protected abstract String resourcePrefix();

    @SideOnly(Side.CLIENT)
    @Override
    public final boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        final Block block = blockAccess.getBlockState(pos).getBlock();
        return !(!isFancyGraphics() && block.equals(this)) &&
                (side.getIndex() == 0 && minY > 0.0D || side.getIndex() == 1 && maxY < 1.0D || side.getIndex() == 2 && minZ > 0.0D ||
                        side.getIndex() == 3 && maxZ < 1.0D || side.getIndex() == 4 && minX > 0.0D || side.getIndex() == 5 && maxX < 1.0D ||
                        !blockAccess.getBlockState(pos).getBlock().isOpaqueCube());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }
}
