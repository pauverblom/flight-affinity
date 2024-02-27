package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.baneina.flightaffinity.FlightAffinity;
import net.minecraft.block.Block;
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
    @ModifyExpressionValue(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"))
    private boolean flightAffinityEnchantmentAndIsOnGround(boolean originalIsOnGround) {
        if (!originalIsOnGround && EnchantmentHelper.getEquipmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0) {
            return false; // If player is in the air and has flight affinity, treat as not on ground
        }
        return originalIsOnGround;
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float modifyMiningSpeed(float original, BlockState block)
    {
        if (!isOnGround())
        {
            original = original * 100;
            System.out.println("Multiplicado");
        }
        System.out.println("Enviado");
        return original;
    }
    public PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

}