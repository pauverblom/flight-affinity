package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity; // Added Entity import for the target
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Player.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = {
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
            },
            // Changed target class from Player to Entity
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"),
            require=0
    )
    public boolean shouldTreatAsOnGroundDueToFlightAffinity(boolean trueIsOnGround) {
        // If player is in the air and has flight affinity, treat as on ground (return true)
        if (trueIsOnGround) return true;

        return this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.FLIGHT_AFFINITY)
                .map(entry -> EnchantmentHelper.getEnchantmentLevel(entry, this) > 0)
                .orElse(false);
    }
}