package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerEntity.class, priority = 2000)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @ModifyExpressionValue(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"))
    public boolean flightAffinityEnchantmentAndIsOnGround(boolean originalIsOnGround) {
        boolean returnValue = originalIsOnGround;
        if (!originalIsOnGround && EnchantmentHelper.getEquipmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0) {
            returnValue = true; // If player is in the air and has flight affinity, treat as on ground
        }
        return returnValue;
    }
}