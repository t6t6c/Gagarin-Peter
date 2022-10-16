package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.MagicItem;

public class AlarteAscendare extends Spell {
	
	private double range;
	private String notFoundMessage;

	public AlarteAscendare(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, double range, String onNotFoundTarget,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setNotFoundMessage(onNotFoundTarget);
		this.range = range;
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		if (isOpMessage(p)) {
			playEffect(p, Effect.DRAGON_BREATH, 1);
			return useOnEntity(p, p, level);
		}
		return false;
	}
	
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		playSound(p, Sound.ENTITY_SPLASH_POTION_THROW, 1);
		List<Entity> list = p.getNearbyEntities(range, range, range);
		Entity ent = Accessory.getTarget(p, list);
		if (ent != null) {
			if (isLocationProtectFlag(ent.getLocation())) return true;
			if (isProtegoEntity(ent)) {
				ent.sendMessage(Protego.getBlockMessage(this));
				useOnEntity(p, ent, level);
				playEffect(p, Effect.DRAGON_BREATH, 1);
			} else {
				playEffect(ent, Effect.DRAGON_BREATH, 1);
				useOnEntity(ent, p, level);
			}
		} else p.sendMessage(notFoundMessage);
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		if (isBoss(ent)) return true;
		double strength = 0;
		switch (level) {
		case 1:
			strength = 1;
			break;
		case 2:
			strength = 1.5;
			break;
		case 3:
			strength = 5*MagicItem.getModifierArmorOnStrengthSpell(ent);
			break;
		}
		ent.setVelocity(new Vector(0, strength, 0));
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
		return isRight && level > 2 ? 0 : 5;
	}

}
