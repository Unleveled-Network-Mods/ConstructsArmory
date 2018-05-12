package c4.conarm.common.armor.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;

public class TraitBouncy extends AbstractArmorTrait {

    private static final float BOUNCE_STR = 1.5F;

    public TraitBouncy() {
        super("bouncy", 0xbcffbe);
    }

    @Override
    public void onKnockback(ItemStack armor, EntityPlayer player, LivingKnockBackEvent evt) {

        evt.setStrength(evt.getStrength() * BOUNCE_STR);
    }

    //Copy of Tinkers' Slime Boots behavior
    @Override
    public void onFalling(ItemStack armor, EntityPlayer player, LivingFallEvent evt) {

        if(EntityLiving.getSlotForItemStack(armor) != EntityEquipmentSlot.FEET) {
            return;
        }

        boolean isClient = player.getEntityWorld().isRemote;
        if(!player.isSneaking() && evt.getDistance() > 2) {
            evt.setDamageMultiplier(0);
            player.fallDistance = 0;
            if(isClient) {
                player.motionY *= -0.9;
                player.isAirBorne = true;
                player.onGround = false;
                double f = 0.91d + 0.04d;
                player.motionX /= f;
                player.motionZ /= f;
                TinkerNetwork.sendToServer(new BouncedPacket());
            }
            else {
                evt.setCanceled(true);
            }
            player.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1f, 1f);
            SlimeBounceHandler.addBounceHandler(player, player.motionY);
        }
        else if(!isClient && player.isSneaking()) {
            evt.setDamageMultiplier(0.2f);
        }
    }
}