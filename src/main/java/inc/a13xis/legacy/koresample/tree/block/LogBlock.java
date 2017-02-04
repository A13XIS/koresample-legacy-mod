package inc.a13xis.legacy.koresample.tree.block;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import inc.a13xis.legacy.koresample.tree.DefinesLeaves;
import inc.a13xis.legacy.koresample.tree.DefinesLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class LogBlock extends BlockLog
{
    public static final int CAPACITY = 4;
    private final ImmutableList<DefinesLog> subBlocks;

    protected LogBlock(Collection<? extends DefinesLog> subBlocks)
    {
        checkArgument(!subBlocks.isEmpty());
        checkArgument(subBlocks.size() <= CAPACITY);
        this.subBlocks = ImmutableList.copyOf(subBlocks);
        setUnlocalizedName("log");
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
    }

    protected final List<DefinesLog> subBlocks() { return Collections.unmodifiableList(subBlocks); }

    public final ImmutableList<String> getSubBlockNames()
    {
        final List<String> names = Lists.newArrayList();
        for (final DefinesLog subBlock : subBlocks)
            names.add(subBlock.speciesName());
        return ImmutableList.copyOf(names);
    }

    @Override
    public final String getUnlocalizedName()
    {
        return String.format("tile.%s%s", resourcePrefix(), getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    @Override
    public final void getSubBlocks(Item item, CreativeTabs unused, List subblocks)
    {
        for (int i = 0; i < subBlocks.size(); i++)
            //noinspection ObjectAllocationInLoop
            subblocks.add(new ItemStack(item, 1, i));
    }

    public final void registerBlockModels(int i)
    {
        String[] pair = getUnwrappedUnlocalizedName(getUnlocalizedName()).split(":");
        Item itemWoodBlock = GameRegistry.findItem(pair[0],pair[1]+i);
        for (DefinesLog define : subBlocks())
        {
            ModelResourceLocation typeLocation = new ModelResourceLocation(getUnwrappedUnlocalizedName(getUnlocalizedName())+"_"+define.logSubBlockVariant().name().toLowerCase());
            ModelLoader.setCustomModelResourceLocation(itemWoodBlock, define.logSubBlockVariant().ordinal(), typeLocation);
        }
    }

    protected abstract String resourcePrefix();

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("subBlocks", subBlocks).toString();
    }
}
