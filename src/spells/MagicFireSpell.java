package spells;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;

public class MagicFireSpell extends Spell {
	
	protected final HashSet<AreaEffectCloud> clouds = new HashSet<>();
	private float thickness;
	private int sloveDuration, fireDuration;
	

	public MagicFireSpell(GagarinPeter plugin, String name, int cooldown, ChatColor color, Material material,
			EnchantmentWrapper ench) {
		super(plugin, name, cooldown, color, material, ench);
	}
	
	
	public MagicFireSpell(GagarinPeter plugin, double damage, int sloveLevel, int sloveDuration, boolean ignoreFireResistance, String name,
			Material material, ChatColor color, int cooldown, boolean isVerbal, String command, float thickness, int fireDuration, List<String> lore,
			EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setSloveDuration(sloveDuration);
		setFireDuration(fireDuration);
		setThickness(thickness);
	}
	
	public MagicFireSpell(GagarinPeter plugin, double damage, int sloveLevel, int sloveDuration, boolean ignoreFireResistance, String name,
			Material material, ChatColor color, int cooldown, boolean isVerbal, float thickness, int fireDuration, List<String> lore,
			EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, lore, ench);
		setSloveDuration(sloveDuration);
		setFireDuration(fireDuration);
		setThickness(thickness);
	}
	
	
	
	public boolean isCloud (Entity cloud) {
		return clouds.contains(cloud);
	}
	
	
	
	protected boolean removeCloud (Entity cloud) {
		if (clouds.remove(cloud)) {
			cloud.remove();
			return true;
		}
		return false;
	}
	

	
	public boolean isDamageEntity (AreaEffectCloud cloud, Entity ent) 
 			throws IllegalArgumentException, NullPointerException {
 		if (!isCloud(cloud)) throw new IllegalArgumentException("cloud is n't this MagicFire");
 		if (ent == null) throw new NullPointerException("ent is null");
 		return true;
 	}
	
	
	
	public HashSet<AreaEffectCloud> createMagicFires (List<Location> locs, String name, LivingEntity source, int level, boolean isDamagePlayer) {
		HashSet<AreaEffectCloud> effects = new HashSet<>();
		for (Location location : locs) {
			if (isLocationProtectFlag(location)) continue; 
			AreaEffectCloud cloud = (AreaEffectCloud) source.getWorld().spawnEntity(location,
					EntityType.AREA_EFFECT_CLOUD);
			if (isDamagePlayer) cloud.setParticle(Particle.FLAME);
			else cloud.setParticle(Particle.SOUL_FIRE_FLAME);
			cloud.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 25, -1), true);
			cloud.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 25, -1), true);
			cloud.setCustomName(name+level);
			cloud.setDuration(fireDuration*20);
			cloud.setRadius(thickness);
			cloud.setSource(source);
			effects.add(cloud);
		}
		clouds.addAll(effects);
		Runnable r = () -> {
			for (AreaEffectCloud cloud : effects) removeCloud(cloud);
		};
		Bukkit.getScheduler().runTaskLater(getPlugin(), r, fireDuration*20);
		return effects;
	}


	public double getDamage(int level, LivingEntity ent) {
		if (ent.getType() == EntityType.PLAYER) {
			double armor = ent.getAttribute(Attribute.GENERIC_ARMOR).getValue();
			switch (level) {
			case 1:
				return 3 + armor / 4;
			case 2:
				return 4 + armor / 3;
			default:
				return 6 + armor / 2;
			}
		} else {
			switch (level) {
			case 1:
				return 4;
			case 2:
				return 6;
			default:
				return 8.5;
			}
		}
	}



	public int getSloveDuration() {
		return sloveDuration;
	}


	public void setSloveDuration(int sloveDuration) {
		this.sloveDuration = sloveDuration;
	}


	public boolean isIgnoreFireResistance(int level) {
		return level > 2;
	}

	public int getFireDuration() {
		return fireDuration;
	}


	public void setFireDuration(int fireDuration) {
		this.fireDuration = fireDuration;
		replaceTag("dur", fireDuration);
	}


	public float getThickness() {
		return thickness;
	}


	public void setThickness(float thickness) {
		this.thickness = thickness;
	}


	@Override
	public int getCost(int level, boolean isRight) {
		return 0;
	}
	
	
	public int getLevelByCloud(AreaEffectCloud cloud)  {
		if (!isCloud(cloud)) throw new IllegalArgumentException("Is not spell cloud entity");
		return Integer.valueOf(cloud.getCustomName().substring(cloud.getCustomName().length()-1));
	}

}
