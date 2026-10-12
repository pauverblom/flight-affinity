package net.baneina.flightaffinity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.baneina.flightaffinity.enchantment.ModEnchantments;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
//? if >=1.21 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import net.minecraft.world.item.enchantment.Enchantment;
*///?}

/**
 * Vanilla multiplies mining speed by 0.2 while the player is not on the ground. With Flight
 * Affinity on the helmet, the {@code onGround()} check inside that calculation reports true.
 */
@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    // Forge and NeoForge move the check from vanilla's getDestroySpeed(BlockState) into their own
    // position-aware overload: getDigSpeed until NeoForge 21.1, getDestroySpeed from 21.2.
    // require = 1 makes the game refuse to start if nothing matches, so a moved check shows up
    // as a crash in CI instead of an enchantment that silently does nothing.
    @ModifyExpressionValue(
            method = {
                    //? if fabric {
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"
                    //?} else {
                    /*"getDigSpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F",
                    "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"
                    *///?}
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"),
            require = 1
    )
    private boolean flightAffinity$ignoreAirbornePenalty(boolean onGround) {
        return onGround || flightAffinity$hasEnchantment();
    }

    @Unique
    private boolean flightAffinity$hasEnchantment() {
        //? if >=1.21.2 {
        return this.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.FLIGHT_AFFINITY)
                .map(enchantment -> EnchantmentHelper.getEnchantmentLevel(enchantment, this) > 0)
                .orElse(false);
        //?} else if >=1.21 {
        /*return this.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(ModEnchantments.FLIGHT_AFFINITY)
                .map(enchantment -> EnchantmentHelper.getEnchantmentLevel(enchantment, this) > 0)
                .orElse(false);
        *///?} else {
        /*Enchantment enchantment = ModEnchantments.flightAffinity.get();
        return enchantment != null && EnchantmentHelper.getEnchantmentLevel(enchantment, this) > 0;
        *///?}
    }
}
