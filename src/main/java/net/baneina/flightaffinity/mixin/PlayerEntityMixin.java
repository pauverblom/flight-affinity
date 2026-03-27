package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @ModifyExpressionValue(
            method = "getBlockBreakingSpeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z")
    )
    public boolean shouldTreatAsOnGroundDueToFlightAffinity(boolean trueIsOnGround) {
        // If player is in the air and has flight affinity, treat as on ground (return true)
        if (trueIsOnGround) return true;

        return this.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOptional(ModEnchantments.FLIGHT_AFFINITY)
                .map(entry -> EnchantmentHelper.getEquipmentLevel(entry, this) > 0)
                .orElse(false);
    }
}