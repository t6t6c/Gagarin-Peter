package entity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import entity.looting.KolobokLootTable;
import magic.MagicItem;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityZombie;

public class KolobokDemonCreator extends KolobokAbstractCreator<Zombie> {

	public KolobokDemonCreator() {
		super(new KolobokLootTable("demon_kolobok", MagicItem.KONFRINGO_ENCH,
				MagicItem.PROTEGO_DIABOLICA_ENCH, MagicItem.PARTIS_TEMPORUS_ENCH),
				ChatColor.RED + "Адский Колобок", 12, 7,
				"98e6b9db87b643312f984d45ddbe19802ca525e0473bc8e92661f727a4a809dd");
	}

	@Override
	protected EntityMonster createEntity(Location loc) {
		EntityMonster monster = new KolobokDemon(loc);
		Zombie z = (Zombie) monster.getBukkitEntity();
		z.setVisualFire(true);
		z.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*36000, 1, false, false));
		z.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20*36000, 1, false, false));
		z.setConversionTime(-1);
		return monster;
	}
	
	
	
	@Override
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		super.onEntityDamageByEntity(e);
		if (e.getCause() != DamageCause.ENTITY_ATTACK) return;
		e.getEntity().setFireTicks(100);
	}
	
	
	private class KolobokDemon extends EntityZombie {

		public KolobokDemon(Location loc) {
			super(((CraftWorld) loc.getWorld()).getHandle());
		}
		
		@Override
		public void u() {
			updatePathfinder(this, bR);
			bR.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true, false));
		}
		
	}

}
