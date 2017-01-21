package inc.a13xis.legacy.koresample.common.util.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;

@SuppressWarnings({ "AbstractClassNeverImplemented", "WeakerAccess" })
public abstract class ForgeEventListener
{
    public void listen(EventBus eventBus)
    {
        eventBus.register(this);
    }
}
