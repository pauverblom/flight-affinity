package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.baneina.flightaffinity.enchantment.ModEnchantments;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    @ModifyExpressionValue(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"))
    private boolean flightAffinityEnchantmentAndIsOnGround(boolean originalIsOnGround) {
        if (!originalIsOnGround && EnchantmentHelper.getEquipmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0) {
            return false; // If player isn't in the air and has flight affinity, treat isOnGround as false
        }
        return originalIsOnGround;
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float modifyMiningSpeed(float originalSpeed, BlockState block)
    {
        if (!isOnGround())
        {
            originalSpeed = originalSpeed * 5;  // Remove mining speed in the air penalty by multiplying penalty times 5
        }
        System.out.println("Enviado");
        return originalSpeed;
    }
    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

}