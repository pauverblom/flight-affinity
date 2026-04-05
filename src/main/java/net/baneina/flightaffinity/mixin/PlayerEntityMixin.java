package net.baneina.flightaffinity.mixin;

import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Intercepts the on-ground check inside {@code getDigSpeed}
 * so that the airborne mining penalty is skipped when Flight Affinity is active.
 *
 * <p>In Forge 1.20.1, {@code Player.getDestroySpeed(BlockState)} delegates to
 * {@code getDigSpeed(BlockState, BlockPos)}, which contains the actual
 * mining-speed logic including the on-ground check. The check uses the
 * {@code onGround()} <em>method</em> (not the field), so we target an
 * {@code INVOKE} via {@code @Redirect}.
 */
@Mixin(value = Player.class)
abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Redirects the {@code this.onGround()} method call inside
     * {@code Player.getDigSpeed(BlockState, BlockPos)}.
     * If the player has the Flight Affinity enchantment, returns {@code true},
     * preventing the ×0.2 airborne penalty.
     */
    @Redirect(
            method = "getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/entity/player/Player;onGround()Z")
    )
    private boolean flightAffinity$shouldTreatAsOnGround(Player instance) {
        return instance.onGround() || flightAffinity$hasEnchantment();
    }

    @Unique
    private boolean flightAffinity$hasEnchantment() {
        if (ModEnchantments.FLIGHT_AFFINITY == null) return false;
        return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.FLIGHT_AFFINITY, this) > 0;
    }
}
