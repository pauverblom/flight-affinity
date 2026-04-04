package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Intercepts the {@code player.onGround()} check inside {@code getDestroySpeed}
 * so that the airborne mining penalty is skipped when Flight Affinity is active.
 */
@Mixin(value = Player.class)
abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /*
     * In 1.20.6 Mojang mappings:
     *   - Vanilla: getDestroySpeed(BlockState)F contains onGround()
     *   - NeoForge 20.6.x: getDigSpeed(BlockState,BlockPos)F may also contain onGround()
     * require = 0 lets descriptors that don't match the running version fail silently.
     */
    @ModifyExpressionValue(
            method = {
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
                    "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"),
            require = 0
    )
    public boolean flightAffinity$shouldTreatAsOnGround(boolean originalOnGround) {
        return originalOnGround || flightAffinity$hasEnchantment();
    }

    @Unique
    private boolean flightAffinity$hasEnchantment() {
        if (ModEnchantments.FLIGHT_AFFINITY == null) return false;
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0;
    }
}