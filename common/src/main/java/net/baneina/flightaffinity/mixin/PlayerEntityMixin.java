package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.baneina.flightaffinity.rules.MiningSpeedRules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Player.class)
abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    @SuppressWarnings("UnresolvedMixinReference") // Suppresses the IDE warning for the NeoForge-specific method
    @ModifyExpressionValue(
            method = {
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F",
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"),
            require=0
    )
    public boolean shouldTreatAsOnGroundDueToFlightAffinity(boolean trueIsOnGround) {
        boolean hasFlightAffinity = this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.FLIGHT_AFFINITY)
                .map(entry -> EnchantmentHelper.getEnchantmentLevel(entry, this) > 0)
                .orElse(false);

        MiningSpeedRules.Environment environment = flight_affinity$isInWaterLikeFluid() ? MiningSpeedRules.Environment.WATER : MiningSpeedRules.Environment.AIR;
        MiningSpeedRules.Stance stance = trueIsOnGround ? MiningSpeedRules.Stance.GROUNDED : MiningSpeedRules.Stance.AIRBORNE;

        return MiningSpeedRules.shouldTreatAsOnGround(environment, stance, hasFlightAffinity);
    }

    @Unique
    private boolean flight_affinity$isInWaterLikeFluid() {
        BlockPos feetPos = this.blockPosition();
        BlockPos eyePos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        return this.level().getFluidState(feetPos).is(FluidTags.WATER)
                || this.level().getFluidState(eyePos).is(FluidTags.WATER);
    }
}