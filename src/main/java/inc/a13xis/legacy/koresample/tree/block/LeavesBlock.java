package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesLeaves;
import inc.a13xis.legacy.koresample.tree.DefinesWood;
import inc.a13xis.legacy.koresample.tree.item.LeavesItem;
import net.minecraft.block.Block;
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

public abstract class LeavesBlock extends Block implements net.minecraftforge.common.IShearable
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
        super(Material.LEAVES);
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        this.setTickRandomly(true);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setSoundType(SoundType.PLANT);
        setUnlocalizedName("leaves");
    }

    private static int mask(int metadata) {return metadata & METADATA_MASK;}

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    protected final List<DefinesLeaves> subBlocks() { return Collections.unmodifiableList(subBlocks); }

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

    @Deprecated
    @SideOnly(Side.CLIENT)
    public final boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        final IBlockState state = blockAccess.getBlockState(pos);
        return (side.getIndex() == 0 && state.getBoundingBox(blockAccess,pos).minY > 0.0D || side.getIndex() == 1 && state.getBoundingBox(blockAccess,pos).maxY < 1.0D || side.getIndex() == 2 && state.getBoundingBox(blockAccess,pos).minZ > 0.0D ||
                        side.getIndex() == 3 && state.getBoundingBox(blockAccess,pos).maxZ < 1.0D || side.getIndex() == 4 && state.getBoundingBox(blockAccess,pos).minX > 0.0D || side.getIndex() == 5 && state.getBoundingBox(blockAccess,pos).maxX < 1.0D ||
                        !blockAccess.getBlockState(pos).getBlock().isNormalCube(blockAccess.getBlockState(pos),blockAccess,pos));
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

                        if (iblockstate1.getBlock().isLeaves(state,worldIn,blockpos1))
                        {
                            iblockstate1.getBlock().beginLeavesDecay(state,worldIn, blockpos1);
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

                                if (!block.canSustainLeaves(state,worldIn, tmp))
                                {
                                    if (block.isLeaves(state,worldIn, tmp))
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
        if (canLightningStrike(worldIn,pos.up()) && !state.getBlock().isSideSolid(state,worldIn, pos.down(),EnumFacing.UP) && rand.nextInt(15) == 1)
        {
            double d0 = (double)((float)pos.getX() + rand.nextFloat());
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    public boolean canLightningStrike(World world,BlockPos pos)
    {
        if (!world.isRaining())
        {
            return false;
        }
        else if (!world.canSeeSky(pos))
        {
            return false;
        }
        else if (world.getPrecipitationHeight(pos).getY() > pos.getY())
        {
            return false;
        }
        else
        {
            Biome biomegenbase = world.getBiomeGenForCoords(pos);
            return biomegenbase.getEnableSnow() ? false : (this.canSnowAtBody(world,pos, false) ? false : biomegenbase.canRain());
        }
    }

    public boolean canSnowAtBody(World w,BlockPos pos, boolean checkLight)
    {
        Biome biomegenbase = w.getBiomeGenForCoords(pos);
        float f = biomegenbase.getFloatTemperature(pos);

        if (f > 0.15F)
        {
            return false;
        }
        else if (!checkLight)
        {
            return true;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && w.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                Block block = w.getBlockState(pos).getBlock();

                if (block.isAir(w.getBlockState(pos),w, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(w, pos))
                {
                    return true;
                }
            }

            return false;
        }
    }

    private void destroy(World worldIn, BlockPos pos)
    {
        this.dropBlockAsItemWithChance(worldIn, pos, worldIn.getBlockState(pos), 100,0);
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

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean isVisuallyOpaque()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public abstract Enum getWoodType(int meta);

    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    public boolean isLeaves(IBlockAccess world, BlockPos pos){ return true; }

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

    public static String getRawUnlocalizedName(LeavesBlock leaves) {
        String unwrapped=getUnwrappedUnlocalizedName(leaves.getUnlocalizedName());
        return unwrapped.substring(unwrapped.indexOf(":")+1);
    }

    /*@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return Colorizer.getRenderColor(this.leaves.getStateFromMeta(stack.getMetadata()));
    }*/
}
