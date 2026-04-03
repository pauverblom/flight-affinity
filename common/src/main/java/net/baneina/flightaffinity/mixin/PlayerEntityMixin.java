package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
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
     * Three method descriptors are listed because:
     *   - Fabric + vanilla 1.21.0:  getDestroySpeed(BlockState)F  contains onGround()
     *   - NeoForge 21.0.x:          getDestroySpeed delegates to getDigSpeed(BlockState,BlockPos)F
     *   - NeoForge 21.2+ / 1.21.5+: getDestroySpeed(BlockState,BlockPos)F  contains onGround()
     * require = 0 lets descriptors that don't match the running version fail silently.
     */
    @ModifyExpressionValue(
            method = {
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F",
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
        return this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.FLIGHT_AFFINITY)
                .map(entry -> EnchantmentHelper.getEnchantmentLevel(entry, this) > 0)
                .orElse(false);
    }
}