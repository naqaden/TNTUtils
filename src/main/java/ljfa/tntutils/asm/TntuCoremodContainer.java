package ljfa.tntutils.asm;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import ljfa.tntutils.Reference;

public class TntuCoremodContainer extends DummyModContainer {
    public TntuCoremodContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "tnt_utilities_core";
        meta.name = "TNTUtils Core";
        meta.version = Reference.VERSION;
        meta.authorList.add("ljfa");
        meta.url = "http://minecraft.curseforge.com/mc-mods/227449-tntutils";
        meta.description = "The core mod belonging to TNTUtils";
        meta.parent = "tnt_utilities";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
