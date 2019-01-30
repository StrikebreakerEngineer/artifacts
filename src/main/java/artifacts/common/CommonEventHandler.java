package artifacts.common;

import artifacts.common.entity.EntityHallowStar;
import artifacts.common.loot.functions.GenerateEverlastingFish;
import baubles.api.BaublesApi;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class CommonEventHandler {

    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (event.getAttacker() instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) event.getAttacker(), ModItems.POCKET_PISTON) != -1) {
            event.setStrength(event.getStrength() * 2);
        }
    }

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) event.getEntityLiving(), ModItems.PHILOSOPHERS_STONE) != -1) {
            if (event.getItem().getItem() == Items.POTIONITEM) {
                event.setDuration(event.getDuration() / 4);
            }
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (ModConfig.everlastingFishWeight > 0 && event.getName().toString().equals("minecraft:gameplay/fishing/fish")) {
            LootFunction[] functions = new LootFunction[1];
            functions[0] = new GenerateEverlastingFish(new LootCondition[0]);
            event.getTable().getPool("main").addEntry(new LootEntryItem(Items.FISH, ModConfig.everlastingFishWeight, 0, functions, new LootCondition[0], "everlasting_fish"));
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }

        if (event.getEntityLiving().getRNG().nextDouble() < ModConfig.everlastingFoodChance) {
            Item item;

            if (event.getEntity() instanceof EntityPig) {
                if (event.getEntityLiving().isBurning()) {
                    item = ModItems.EVERLASTING_COOKED_PORKCHOP;
                } else {
                    item = ModItems.EVERLASTING_PORKCHOP;
                }
            } else if (event.getEntity() instanceof EntityCow) {
                if (event.getEntityLiving().isBurning()) {
                    item = ModItems.EVERLASTING_COOKED_BEEF;
                } else {
                    item = ModItems.EVERLASTING_BEEF;
                }
            } else if (event.getEntity() instanceof EntitySheep) {
                if (event.getEntityLiving().isBurning()) {
                    item = ModItems.EVERLASTING_COOKED_MUTTON;
                } else {
                    item = ModItems.EVERLASTING_MUTTON;
                }
            } else if (event.getEntity() instanceof EntityChicken) {
                if (event.getEntityLiving().isBurning()) {
                    item = ModItems.EVERLASTING_COOKED_CHICKEN;
                } else {
                    item = ModItems.EVERLASTING_CHICKEN;
                }
            } else if (event.getEntity() instanceof EntityRabbit) {
                if (event.getEntityLiving().isBurning()) {
                    item = ModItems.EVERLASTING_COOKED_RABBIT;
                } else {
                    item = ModItems.EVERLASTING_RABBIT;
                }
            } else if (event.getEntity() instanceof EntityZombie) {
                item = ModItems.EVERLASTING_ROTTEN_FLESH;
            } else if (event.getEntity() instanceof EntitySpider) {
                item = ModItems.EVERLASTING_SPIDER_EYE;
            } else {
                return;
            }

            event.getDrops().add(new EntityItem(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, new ItemStack(item)));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof EntityDamageSourceIndirect) && !((EntityDamageSource) event.getSource()).getIsThornsDamage()) {
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
                if (BaublesApi.isBaubleEquipped(attacker, ModItems.MAGMA_STONE) != -1 || BaublesApi.isBaubleEquipped(attacker, ModItems.FIRE_GAUNTLET) != -1) {
                    if (!event.getEntity().isImmuneToFire()) {
                        event.getEntity().setFire(4);
                    }
                }
            }
        }
        if (!event.getEntity().world.isRemote && event.getEntityLiving() instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) event.getEntityLiving(), ModItems.STAR_CLOAK) != -1) {
            if (event.getEntityLiving().world.canSeeSky(event.getEntityLiving().getPosition())) {
                int stars = ModConfig.starCloakStarsMin;
                if (ModConfig.starCloakStarsMax > ModConfig.starCloakStarsMin) {
                    stars += event.getEntityLiving().getRNG().nextInt(ModConfig.starCloakStarsMax - ModConfig.starCloakStarsMin + 1);
                }
                for (int i = 0; i < stars; i++) {
                    event.getEntityLiving().world.spawnEntity(new EntityHallowStar(event.getEntityLiving().world, event.getEntityLiving()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (BaublesApi.isBaubleEquipped((EntityPlayer) event.getEntity(), ModItems.COBALT_SHIELD) != -1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (BaublesApi.isBaubleEquipped((EntityPlayer) event.getEntity(), ModItems.LUCKY_HORSESHOE) != -1) {
                if (event.getDistance() > 5) {
                    PotionEffect potioneffect = event.getEntityLiving().getActivePotionEffect(MobEffects.JUMP_BOOST);
                    float f = potioneffect == null ? 0.0F : (float)(potioneffect.getAmplifier() + 1);
                    int i = MathHelper.ceil((event.getDistance() - 3.0F - f) * event.getDamageMultiplier());
                    if (i > 0) {
                        event.getEntity().playSound(SoundEvents.ENTITY_GENERIC_SMALL_FALL, 1, 1);
                    }
                }
                event.setCanceled(true);
            }
        }
    }
}
