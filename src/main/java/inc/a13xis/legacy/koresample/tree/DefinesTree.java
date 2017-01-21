package inc.a13xis.legacy.koresample.tree;

import inc.a13xis.legacy.koresample.tree.block.LeavesBlock;
import inc.a13xis.legacy.koresample.tree.block.LogBlock;
import inc.a13xis.legacy.koresample.tree.block.SaplingBlock;
import net.minecraft.world.gen.feature.WorldGenerator;

@SuppressWarnings("InterfaceNeverImplemented")
public interface DefinesTree
{
    LeavesBlock leavesBlock();

    int leavesSubBlockIndex();

    LogBlock logBlock();

    int logSubBlockIndex();

    SaplingBlock saplingBlock();

    int saplingSubBlockIndex();

    WorldGenerator saplingTreeGenerator();

    WorldGenerator worldTreeGenerator();
}
