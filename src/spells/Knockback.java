package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.MagicItem;

public class Knockback extends Spell {

	private double range;
	private String notFoundMessage;



	public Knockback(GagarinPeter plugin) {
		super(plugin, "Депульсо", 10, ChatColor.DARK_AQUA, Material.ENDER_PEARL, null);
		getLore().add("Отталкивает существо в указанном направлении.");
		getLore().add("Применяется на другую сущность (ЛКМ)");
		setCommand("де");
	}
	
	public Knockback(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, double range, String onNotFoundTarget,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setNotFoundMessage(onNotFoundTarget);
		setRange(range);
	}

	
	
	@Override
	public boolean useLeft (Player p, int level) {
		if (level > 2 && p.isSneaking()) {
			List<Entity> list = p.getNearbyEntities(6, 6, 6);
			for (Entity ent : list) {
				if (isLocationProtectFlag(ent.getLocation())) continue;
				if (!isProtegoEntity(ent)) {
					useByVectorOnEntity(ent, ent.getLocation().subtract(p.getLocation()).toVector(), level);
				} else ent.sendMessage(Protego.getBlockMessage(this));
			}
			playSound(p, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1);
			p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 100);
		} else {
			playSound(p, Sound.ENTITY_SPLASH_POTION_THROW, 1);
			List<Entity> list = p.getNearbyEntities(range, range, range);
			Entity ent = Accessory.getTarget(p, list);
			if (ent != null) {
				if (isLocationProtectFlag(ent.getLocation())) return true;
				if (isProtegoEntity(ent)) {
					useOnEntity(p, ent, level);
					ent.sendMessage(Protego.getBlockMessage(this));
				} else useOnEntity(ent, p, level);	
				playSound(p, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1);
			} else p.sendMessage(notFoundMessage);
		}
		return true;
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		useOnEntity(p, p, level);
		playSound(p, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1);
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		return useByVectorOnEntity(ent, caster.getLocation().getDirection(), level);
	}
	
	
	public boolean useByVectorOnEntity(Entity ent, Vector vec, int level) {
		if (isBoss(ent)) return true;
		float strength =  1 + (1+2*level) * MagicItem.getModifierArmorOnStrengthSpell(ent);
		vec = vec.multiply(strength);
		vec.setY(1);
		vec.setX(10);
		vec.setZ(10);
		ent.setVelocity(vec);
		ent.sendMessage(ent.getVelocity().toString());
		sayUse(ent);
		return true;
	}

	
	
	
	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public String getNotFoundMessage() {
		return notFoundMessage;
	}

	public void setNotFoundMessage(String notFoundMessage) {
		this.notFoundMessage = notFoundMessage;
	}

	@Override
	public int getCost(int level, boolean isRight) {
		return 2;
	}
}
