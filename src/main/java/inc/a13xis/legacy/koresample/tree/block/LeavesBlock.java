package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesLeaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class LeavesBlock extends BlockLeavesBase implements net.minecraftforge.common.IShearable
{
    public static final int CAPACITY = 4;
    private static final int METADATA_MASK = CAPACITY - 1;
    public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
    int[] surroundings;
    @SideOnly(Side.CLIENT)
    protected int iconIndex;
    @SideOnly(Side.CLIENT)
    protected boolean isTransparent;
    private final ImmutableList<DefinesLeaves> subBlocks;

    protected LeavesBlock(Collection<? extends DefinesLeaves> subBlocks)
    {
        super(Material.leaves, false);
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        this.setTickRandomly(true);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setStepSound(soundTypeGrass);
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
    public final int getRenderColor(IBlockState state) {
        int metadata=this.getMetaFromState(state);
        return subBlocks.get(mask(metadata)).getLeavesInventoryColor();
    }

    @SideOnly(Side.CLIENT)
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
        return subBlocks.get(mask(this.getMetaFromState(state))).saplingDefinition().saplingSubBlockVariant().ordinal();
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
            final String iconName = String.format("leaves_%s",  subBlocks.get(i).speciesName().toLowerCase().replace('.', '_'));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),i,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
        }
    }

    protected abstract String resourcePrefix();

    @SideOnly(Side.CLIENT)
    @Override
    public final boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        final Block block = blockAccess.getBlockState(pos).getBlock();
        return (side.getIndex() == 0 && minY > 0.0D || side.getIndex() == 1 && maxY < 1.0D || side.getIndex() == 2 && minZ > 0.0D ||
                        side.getIndex() == 3 && maxZ < 1.0D || side.getIndex() == 4 && minX > 0.0D || side.getIndex() == 5 && maxX < 1.0D ||
                        !blockAccess.getBlockState(pos).getBlock().isOpaqueCube());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }

    @SideOnly(Side.CLIENT)
    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        byte b0 = 1;
        int i = b0 + 1;
        int j = pos.getX();
        int k = pos.getY();
        int l = pos.getZ();

        if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i)))
        {
            for (int i1 = -b0; i1 <= b0; ++i1)
            {
                for (int j1 = -b0; j1 <= b0; ++j1)
                {
                    for (int k1 = -b0; k1 <= b0; ++k1)
                    {
                        BlockPos blockpos1 = pos.add(i1, j1, k1);
                        IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

                        if (iblockstate1.getBlock().isLeaves(worldIn, blockpos1))
                        {
                            iblockstate1.getBlock().beginLeavesDecay(worldIn, blockpos1);
                        }
                    }
                }
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue() && ((Boolean)state.getValue(DECAYABLE)).booleanValue())
            {
                byte b0 = 4;
                int i = b0 + 1;
                int j = pos.getX();
                int k = pos.getY();
                int l = pos.getZ();
                byte b1 = 32;
                int i1 = b1 * b1;
                int j1 = b1 / 2;

                if (this.surroundings == null)
                {
                    this.surroundings = new int[b1 * b1 * b1];
                }

                int k1;

                if (worldIn.isAreaLoaded(new BlockPos(j - i, k - i, l - i), new BlockPos(j + i, k + i, l + i)))
                {
                    int l1;
                    int i2;

                    for (k1 = -b0; k1 <= b0; ++k1)
                    {
                        for (l1 = -b0; l1 <= b0; ++l1)
                        {
                            for (i2 = -b0; i2 <= b0; ++i2)
                            {
                                BlockPos tmp = new BlockPos(j + k1, k + l1, l + i2);
                                Block block = worldIn.getBlockState(tmp).getBlock();

                                if (!block.canSustainLeaves(worldIn, tmp))
                                {
                                    if (block.isLeaves(worldIn, tmp))
                                    {
                                        this.surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = -2;
                                    }
                                    else
                                    {
                                        this.surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = -1;
                                    }
                                }
                                else
                                {
                                    this.surroundings[(k1 + j1) * i1 + (l1 + j1) * b1 + i2 + j1] = 0;
                                }
                            }
                        }
                    }

                    for (k1 = 1; k1 <= 4; ++k1)
                    {
                        for (l1 = -b0; l1 <= b0; ++l1)
                        {
                            for (i2 = -b0; i2 <= b0; ++i2)
                            {
                                for (int j2 = -b0; j2 <= b0; ++j2)
                                {
                                    if (this.surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1] == k1 - 1)
                                    {
                                        if (this.surroundings[(l1 + j1 - 1) * i1 + (i2 + j1) * b1 + j2 + j1] == -2)
                                        {
                                            this.surroundings[(l1 + j1 - 1) * i1 + (i2 + j1) * b1 + j2 + j1] = k1;
                                        }

                                        if (this.surroundings[(l1 + j1 + 1) * i1 + (i2 + j1) * b1 + j2 + j1] == -2)
                                        {
                                            this.surroundings[(l1 + j1 + 1) * i1 + (i2 + j1) * b1 + j2 + j1] = k1;
                                        }

                                        if (this.surroundings[(l1 + j1) * i1 + (i2 + j1 - 1) * b1 + j2 + j1] == -2)
                                        {
                                            this.surroundings[(l1 + j1) * i1 + (i2 + j1 - 1) * b1 + j2 + j1] = k1;
                                        }

                                        if (this.surroundings[(l1 + j1) * i1 + (i2 + j1 + 1) * b1 + j2 + j1] == -2)
                                        {
                                            this.surroundings[(l1 + j1) * i1 + (i2 + j1 + 1) * b1 + j2 + j1] = k1;
                                        }

                                        if (this.surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + (j2 + j1 - 1)] == -2)
                                        {
                                            this.surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + (j2 + j1 - 1)] = k1;
                                        }

                                        if (this.surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1 + 1] == -2)
                                        {
                                            this.surroundings[(l1 + j1) * i1 + (i2 + j1) * b1 + j2 + j1 + 1] = k1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                k1 = this.surroundings[j1 * i1 + j1 * b1 + j1];

                if (k1 >= 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, Boolean.valueOf(false)), 4);
                }
                else
                {
                    this.destroy(worldIn, pos);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.canLightningStrike(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1)
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    private void destroy(World worldIn, BlockPos pos)
    {
        this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
        worldIn.setBlockToAir(pos);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {}

    protected int getSaplingDropChance(IBlockState state)
    {
        return 20;
    }

    /**
     * Pass true to draw this block using fancy graphics, or false for fast graphics.
     */
    @SideOnly(Side.CLIENT)
    public void setGraphicsLevel(boolean fancy)
    {
        this.isTransparent = fancy;
        this.fancyGraphics = fancy;
        this.iconIndex = fancy ? 0 : 1;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    public boolean isVisuallyOpaque()
    {
        return false;
    }

    public abstract Enum getWoodType(int meta);

    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    @Override public boolean isLeaves(IBlockAccess world, BlockPos pos){ return true; }

    @Override
    public void beginLeavesDecay(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (!(Boolean)state.getValue(CHECK_DECAY))
        {
            world.setBlockState(pos, state.withProperty(CHECK_DECAY, true), 4);
        }
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
}
