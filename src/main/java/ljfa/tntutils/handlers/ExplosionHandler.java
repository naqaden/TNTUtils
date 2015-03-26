package ljfa.tntutils.handlers;

import ljfa.tntutils.Config;
import ljfa.tntutils.util.ListHelper;
import ljfa.tntutils.util.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExplosionHandler {
    @SubscribeEvent
    public void onExplosionStart(ExplosionEvent.Start event) {
        if(Config.disableExplosions)
            event.setCanceled(true);
        else
            event.explosion.explosionSize *= Config.sizeMultiplier;
    }

    @SubscribeEvent
    public void onExplosionDetonate(final ExplosionEvent.Detonate event) {
        if(event.world.isRemote)
            return;
        //Block damage
        if(Config.disableBlockDamage || (Config.disableCreeperBlockDamage && event.explosion.exploder instanceof EntityCreeper))
            event.getAffectedBlocks().clear();
        else if(Config.spareTileEntities || Config.blacklistActive) {
            //Remove blacklisted blocks and tile entities (if configured) from the list
            ListHelper.removeIf(event.getAffectedBlocks(), new Predicate<BlockPos>() {
                @Override
                public boolean test(BlockPos pos) {
                    return shouldBePreserved(event.world.getBlockState(pos));
                }
            });
        }
        
        //Entity damage
        if(Config.disableEntityDamage)
            event.getAffectedEntities().clear();
        else if(Config.disablePlayerDamage || Config.disableNPCDamage || Config.preventChainExpl) {
            //Remove certain from the list
            ListHelper.removeIf(event.getAffectedEntities(), new Predicate<Entity>() {
                @Override
                public boolean test(Entity ent) {
                    return (Config.disableNPCDamage && ent instanceof EntityLivingBase && !(ent instanceof EntityPlayer))
                        || (Config.disablePlayerDamage && ent instanceof EntityPlayer)
                        || (Config.preventChainExpl && ent instanceof EntityMinecartTNT);
                }
            });
        }
    }

    public static boolean shouldBePreserved(IBlockState state) {
        return Config.spareTileEntities && state.getBlock().hasTileEntity(state)
            || Config.blacklist.contains(state.getBlock());
    }
}
