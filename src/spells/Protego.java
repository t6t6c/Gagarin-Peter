package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.EffectParams;
import magic.MagicScheduler;



public class Protego extends Spell {
	
	private int duration;
	private double range;
	private static String blockMessage;
	private String notFoundMessage;
	
	
	public Protego(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, double range, int duration, String blockMessage,  String onNotFoundTarget,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setNotFoundMessage(onNotFoundTarget);
		setDuration(duration);
		setRange(range);
		setBlockMessage(blockMessage);
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		playSound(p, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1);
		return useOnEntity(p, p, level);
	}
	
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		playSound(p, Sound.ENTITY_SPLASH_POTION_THROW, 1);
		List<Entity> list = p.getNearbyEntities(30, 30, 30);
		Entity ent = Accessory.getTarget(p, list);
		if (ent != null) {
			if (isLocationProtectFlag(ent.getLocation())) return true;
			useOnEntity(ent, p, level);
		}
		else p.sendMessage(notFoundMessage);
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		ent.setGlowing(true);
		sayUse(ent);
		MagicScheduler.addNewRow(ent, "protego_effect#" + level, new EffectParams(duration, () -> ent.setGlowing(false)));
		return true;
	}
	
	
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
		replaceTag("dur", duration);
	}


	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}


	public static String getBlockMessage() {
		return blockMessage;
	}
	
	public static String getBlockMessage (Spell spell) {
		return blockMessage.replaceAll("%spell", spell.getColor() + spell.getName());
	}

	public static void setBlockMessage(String blockMessage) {
		Protego.blockMessage = blockMessage;
	}
	
	public static int getProtegoLevel(Entity ent) {
		if (!isProtegoEntity(ent)) return 0;
		return MagicScheduler.getLevel(ent, "protego_effect");
	}



	public String getNotFoundMessage() {
		return notFoundMessage;
	}



	public void setNotFoundMessage(String notFoundMessage) {
		this.notFoundMessage = notFoundMessage;
	}



	@Override
	public int getCost(int level, boolean isRight) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
