package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesLeaves;
import inc.a13xis.legacy.koresample.tree.DefinesWood;
import inc.a13xis.legacy.koresample.tree.item.LeavesItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class LeavesBlock extends BlockLeaves
{
    public static final int CAPACITY = 4;
    private final ImmutableList<DefinesLeaves> subBlocks;

    protected LeavesBlock(Collection<? extends DefinesLeaves> subBlocks)
    {
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        this.setSoundType(SoundType.PLANT);
        setUnlocalizedName("leaves");
    }

    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    protected final List<DefinesLeaves> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    @Override
    public final Item getItemDropped(IBlockState state, Random unused, int unused2)
    {
        return Item.getItemFromBlock(subBlocks.get(this.getMetaFromState(state)).saplingDefinition().saplingBlock());
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return subBlocks.get(this.getMetaFromState(state)).saplingDefinition().saplingSubBlockVariant().ordinal();
    }

    public final String[] getSpeciesNames() //func_150125_e
    {
        final List<String> names = Lists.newArrayList();
        for (final DefinesLeaves subBlock : subBlocks)
            names.add(subBlock.speciesName());
        return names.toArray(new String[names.size()]);
    }

    public final String getUnlocalizedName()
    {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Deprecated
    public final int getDamageValue(World world, BlockPos pos) { return this.getMetaFromState(world.getBlockState(pos)) & 3; }

    public final void getSubBlocks(Item item, CreativeTabs unused, List subBlocks)
    {
        for (int i = 0; i < this.subBlocks.size(); i++)
            //noinspection ObjectAllocationInLoop
            subBlocks.add(new ItemStack(item, 1, i));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockModels()
    {
        for (DefinesLeaves define : subBlocks())
        {
            ModelResourceLocation typeLocation = new ModelResourceLocation(getRegistryName(),"check_decay=true,decayable=true,variant="+define.leavesSubBlockVariant().name().toLowerCase());
            //ModelResourceLocation typeItemLocation = new ModelResourceLocation(getRegistryName().toString().substring(0,getRegistryName().toString().length()-1)+"_"+define.leavesSubBlockVariant().name().toLowerCase(),"inventory");
            Item blockItem = Item.getItemFromBlock(define.leavesBlock());
            ModelLoader.setCustomModelResourceLocation(blockItem,define.leavesSubBlockVariant().ordinal(),typeLocation);
        }
    }

    protected abstract String resourcePrefix();

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
        return true;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }

    protected int getSaplingDropChance(IBlockState state)
    {
        return 20;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isVisuallyOpaque()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public java.util.List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        int chance = this.getSaplingDropChance(state);

        if (fortune > 0)
        {
            chance -= 2 << fortune;
            if (chance < 10) chance = 10;
        }

        if (rand.nextInt(chance) == 0)
            ret.add(new ItemStack(getItemDropped(state, rand, fortune), 1, damageDropped(state)));

        chance = 200;
        if (fortune > 0)
        {
            chance -= 10 << fortune;
            if (chance < 40) chance = 40;
        }

        this.captureDrops(true);
        if (world instanceof World)
            this.dropApple((World)world, pos, state, chance); // Dammet mojang
        ret.addAll(this.captureDrops(false));
        return ret;
    }

    @Override
    public abstract int getMetaFromState(IBlockState state);

    public static String getRawUnlocalizedName(LeavesBlock leaves) {
        String unwrapped=getUnwrappedUnlocalizedName(leaves.getUnlocalizedName());
        return unwrapped.substring(unwrapped.indexOf(":")+1);
    }
}
