package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesWood;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class WoodBlock extends Block
{
    public static final int CAPACITY = 16;
    private final ImmutableList<DefinesWood> subBlocks;

    protected WoodBlock(Collection<? extends DefinesWood> subBlocks)
    {
        super(Material.wood);
        Preconditions.checkArgument(!subBlocks.isEmpty());
        Preconditions.checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        this.setUnlocalizedName("planks");
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    protected final List<DefinesWood> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    public final ImmutableList<String> getSubBlockNames()
    {
        final List<String> names = Lists.newArrayList();
        for (final DefinesWood subBlock : subBlocks)
            names.add(subBlock.speciesName());
        return ImmutableList.copyOf(names);
    }

    @Override
    public final String getUnlocalizedName() {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    public final void getSubBlocks(Item item, CreativeTabs unused, List subblocks)
    {
        for (int i = 0; i < subBlocks.size(); i++)
            //noinspection ObjectAllocationInLoop
            subblocks.add(new ItemStack(item, 1, i));
    }

    public final void registerBlockModels()
    {

        for (int i = 0; i < subBlocks.size(); i++)
        {
            final String iconName = String.format("planks_%s",  subBlocks.get(i).speciesName().replace('.', '_'));
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(this),i,new ModelResourceLocation(resourcePrefix()+iconName,"inventory"));
        }
    }

    protected abstract String resourcePrefix();

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }
}
