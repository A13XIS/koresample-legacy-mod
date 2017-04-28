package inc.a13xis.legacy.koresample.tree;

import inc.a13xis.legacy.koresample.common.block.FenceBlock;
import net.minecraft.block.Block;

public interface DefinesFence {
    void assignFenceBlock(FenceBlock block);

    FenceBlock fenceBlock();

    Block fenceModelBlock();

    Enum fenceModelSubBlockVariant();

    String fenceName();
}
