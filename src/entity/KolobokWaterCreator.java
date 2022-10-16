package entity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import entity.looting.KolobokLootTable;
import magic.MagicItem;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityDrowned;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityZombieHusk;

public class KolobokWaterCreator extends KolobokAbstractCreator<Drowned> {

	public KolobokWaterCreator() {
		super(new KolobokLootTable("drowned_kolobok", MagicItem.INVISIBILITY_SPELL_ENCH),
				ChatColor.BLUE+"Водный Колобок", 15, 6,
				"edf43d7bc7e7cf998f80c2151a436c7f5b05e66b5d95be06107a93449f4866ae");
	}

	@Override
	protected EntityMonster createEntity(Location loc) {
		KolobokWater kol = new KolobokWater(loc);
		Drowned z = (Drowned) kol.getBukkitEntity();
		z.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20*36000, 3, false, false));
		z.setConversionTime(-1);
		return kol;
	}
	
	
	
	@Override
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		super.onEntityDamageByEntity(e);
		if (e.getCause() != DamageCause.ENTITY_ATTACK || !e.getEntity().isValid()) return;
		LivingEntity ent = (LivingEntity) e.getEntity();
		ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
	}

	
	private class KolobokWater extends EntityDrowned {

		public KolobokWater(Location loc) {
			super(EntityTypes.s, ((CraftWorld) loc.getWorld()).getHandle());
		}
		
		@Override
		public void u() {
			updatePathfinder(this, bR);
		}
		
	}
	
}
