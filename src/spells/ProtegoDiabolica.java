package spells;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;

public class ProtegoDiabolica extends MagicFireSpell {
	
	private static final String NAME_ALL = "Protego_Diabolica_Area",
			NAME_MOBS = "Protego_Diabolica_Area_No_Players";
	private double interval, circleRadius;
	
	
	public ProtegoDiabolica(GagarinPeter plugin, double damage, int sloveLevel, int sloveDuration, boolean ignoreFireResistance, String name,
			Material material, ChatColor color, int cooldown, boolean isVerbal, String command, float thickness, int fireDuration, double circleRadius, 
			double interval, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, damage, sloveLevel, sloveDuration, ignoreFireResistance, name, material, color, cooldown, isVerbal, command, thickness, fireDuration, lore, ench);
		setInterval(interval);
		setCircleRadius(circleRadius);
	}

	
	
 	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean isDamageEntity (AreaEffectCloud cloud, Entity ent) 
 			throws IllegalArgumentException, NullPointerException {
 		super.isDamageEntity(cloud, ent);
 		if (!ent.equals(cloud.getSource())) {
 			if (NAME_ALL.equals(cloud.getCustomName())) return true;
 			else if (ent.getType() != EntityType.PLAYER) return true; 
 		}
 		return false;
 	}


 	
 	@Override
	public boolean useLeft (Player p, int level) {
		use (p, true, level);
		return true;
	}
	
	@Override
	public boolean useRight (Player p, int level) {
		use (p, false, level);
		return true;
	}
	
	
	
	public void use (Player p, boolean damagePlayer, int level) {
		Location loc = p.getLocation().clone();
		for (double i = 0; i <= 4; i++) {
			if (i != 0) loc.setY(loc.getY() + 0.75);
			ArrayList<Location> locs = Accessory.getLocationsInRadious(loc, 7, 1.25);
			createMagicFires(locs, damagePlayer ? NAME_ALL : NAME_MOBS, p, level, damagePlayer);
		}
		
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public double getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(double circleRadius) {
		this.circleRadius = circleRadius;
	}
	
	@Override
	public int getCost(int level, boolean isRight) {
		return 6;
	}
}
