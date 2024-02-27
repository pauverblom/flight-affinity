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
    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float modifyMiningSpeed(float originalSpeed, BlockState block)
    {
        if (!isOnGround() && EnchantmentHelper.getEquipmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0)
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