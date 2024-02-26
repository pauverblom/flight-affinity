package net.baneina.flightaffinity.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.baneina.flightaffinity.enchantment.ModEnchantments;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {

    @Inject(method = "getBlockBreakingSpeed",at = @At("RETURN"),cancellable = true)
    private void flightAffinity(BlockState block, CallbackInfoReturnable<Float> cir){
        if (!this.isOnGround() && EnchantmentHelper.getEquipmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0)
        {
            cir.setReturnValue(cir.getReturnValue() * 5);
        }

    }

    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

}