package artifacts.item.wearable.belt;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import artifacts.registry.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CloudInABottleItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.CLOUD_IN_A_BOTTLE_ENABLED.get();
    }

    public static void jump(Player player) {
        player.fallDistance = 0;

        double upwardsMotion = 0.5;
        if (player.hasEffect(MobEffects.JUMP)) {
            // noinspection ConstantConditions
            upwardsMotion += 0.1 * (player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }
        if (player.isSprinting()) {
            upwardsMotion *= 1 + ModGameRules.CLOUD_IN_A_BOTTLE_SPRINT_JUMP_VERTICAL_VELOCITY.get();
        }

        Vec3 motion = player.getDeltaMovement();
        double motionMultiplier = player.isSprinting() ? ModGameRules.CLOUD_IN_A_BOTTLE_SPRINT_JUMP_HORIZONTAL_VELOCITY.get() : 0;
        float direction = (float) (player.getYRot() * Math.PI / 180);
        player.setDeltaMovement(player.getDeltaMovement().add(
                -Mth.sin(direction) * motionMultiplier,
                upwardsMotion - motion.y,
                Mth.cos(direction) * motionMultiplier)
        );

        player.hasImpulse = true;

        player.awardStat(Stats.JUMP);
        if (player.isSprinting()) {
            player.causeFoodExhaustion(0.2F);
        } else {
            player.causeFoodExhaustion(0.05F);
        }

        if (ModItems.WHOOPEE_CUSHION.get().isEquippedBy(player)) {
            player.playSound(ModSoundEvents.FART.get(), 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
        } else {
            player.playSound(SoundEvents.WOOL_FALL, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
        }
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.BOTTLE_FILL_DRAGONBREATH;
    }

    public static float getReducedFallDistance(LivingEntity entity, float distance) {
        if (ModGameRules.CLOUD_IN_A_BOTTLE_ENABLED.get() && ModItems.CLOUD_IN_A_BOTTLE.get().isEquippedBy(entity)) {
            return Math.max(0, distance - 3);
        }
        return distance;
    }
}
