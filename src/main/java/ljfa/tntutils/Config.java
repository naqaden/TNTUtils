package ljfa.tntutils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import ljfa.tntutils.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {
    public static Configuration conf;

    public static final String CATEGORY_GENERAL = "general";
    
    public static boolean replaceTNT;
    public static boolean explosionCommand;
    public static boolean spareTileEntities;
    public static String[] blacklistArray;
    public static Set<Block> blacklist = null;
    public static boolean blacklistActive = false;
    public static boolean disableBlockDamage;
    public static boolean disableEntityDamage;
    public static boolean disableNPCDamage;
    
    public static void loadConfig(File file) {
        if(conf == null)
            conf = new Configuration(file);
        
        conf.load();
        loadValues();
        
        FMLCommonHandler.instance().bus().register(new ChangeHandler());
    }
    
    public static void loadValues() {
        replaceTNT = conf.get(CATEGORY_GENERAL, "preventChainExplosions", true, "Prevent explosions from triggering TNT").setRequiresMcRestart(true).getBoolean();
        explosionCommand = conf.get(CATEGORY_GENERAL, "addExplosionCommand", true, "Adds the \"/explosion\" command").setRequiresMcRestart(true).getBoolean();
        spareTileEntities = conf.get(CATEGORY_GENERAL, "spareTileEntites", false, "Makes explosions not destroy tile entities").getBoolean();
        blacklistArray = conf.get(CATEGORY_GENERAL, "destructionBlacklist", new String[0], "A list of blocks that will never be destroyed by explosions").getStringList();
        disableBlockDamage = conf.get(CATEGORY_GENERAL, "disableBlockDamage", false, "Disables all block damage from explosions").getBoolean();
        disableEntityDamage = conf.get(CATEGORY_GENERAL, "disableEntityDamage", false, "Disables all entity damage from explosions").getBoolean();
        disableNPCDamage = conf.get(CATEGORY_GENERAL, "disableNPCDamage", false, "No entities besides players get damage from explosions").getBoolean();
        //----------------
        if(conf.hasChanged())
            conf.save();
    }
    
    public static void createBlacklistSet() {
        blacklist = new HashSet<Block>();
        for(String name: blacklistArray) {
            Block block = (Block)Block.blockRegistry.getObject(name);
            if(block == Blocks.air || block == null) {
                LogHelper.error("Block not found, ignoring invalid blacklist entry: %s", name);
            } else {
                blacklist.add(block);
                LogHelper.debug("Added block to blacklist: %s", name);
            }
        }
        blacklistActive = blacklist.size() != 0;
    }
    
    /** Reloads the config values upon change */
    public static class ChangeHandler {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.modID.equals(Reference.MODID)) {
                loadValues();
                createBlacklistSet();
            }
        }
    }
}
