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
import magic.MagicItem;

public class PartisTemporus extends MagicFireSpell {
	
	private static final String NAME_ALL = "Partis_Temporus_Area",
			NAME_MOBS = "Partis_Temporus_Area_No_Players";
	private double interval, length, width;

	public PartisTemporus(GagarinPeter plugin) {
		super(plugin, "Партис Темпорус", 60, ChatColor.DARK_RED, Material.BLAZE_POWDER, null);
		getLore().add("Создаёт путь, огороженный магическим огнём");
		getLore().add("Любой, кто попытается войти в этот путь");
		getLore().add("будет получать по %dmg единиц урона");
		getLore().add("и сильный эффект замедления каждую секунду,");
		getLore().add("а также начнёт гореть.");
		getLore().add("На игрока, призвавшего заклинание никакого");
		getLore().add("негативного влияния огонь не оказывает.");
		getLore().add("Если на пути применения заклинания есть магический");
		getLore().add("огонь, то он исчезает, освобождая путь.");
		getLore().add("При применении через (ПКМ) пламя будет игнорировать");
		getLore().add("всех игроков.");
		getLore().add("При применении через (ЛКМ) пламя будет дейстовать");
		getLore().add("на игроков (создателя по прежнему будет игнорировать).");
		setCommand("пт");
	}
	
	
	public PartisTemporus(GagarinPeter plugin, double damage, int sloveLevel, int sloveDuration, boolean ignoreFireResistance, String name,
			Material material, ChatColor color, int cooldown, boolean isVerbal, String command, float thickness, int fireDuration, double length, double width, 
			double interval, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, damage, sloveLevel, sloveDuration, ignoreFireResistance, name, material, color, cooldown, isVerbal, command, thickness, fireDuration, lore, ench);
		setInterval(interval);
		setLength(length);
		setWidth(width);
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
		Entity ent = Accessory.getTarget(p, p.getNearbyEntities(length, length, length));
		if (isCloud(ent) || MagicItem.findSpell(ProtegoDiabolica.class).isCloud(ent)) {
			for (Entity entity : ent.getNearbyEntities(width/2, width/2, width/2)) {
				if (isCloud(entity) || MagicItem.findSpell(ProtegoDiabolica.class).isCloud(entity)) {
					entity.remove();
				}
			}
			ent.remove();
		}
		
		for (double i = 0; i <= 4; i++) {
			if (i != 0) loc.setY(loc.getY() + 0.75);
			ArrayList<Location> locs = Accessory.getLocationsInPath(loc, length, width/2, interval);
			createMagicFires(locs, damagePlayer ? NAME_ALL : NAME_MOBS, p, level, damagePlayer);
		}
		
	}


	public double getInterval() {
		return interval;
	}


	public void setInterval(double interval) {
		this.interval = interval;
	}


	public double getLength() {
		return length;
	}


	public void setLength(double length) {
		this.length = length;
	}


	public double getWidth() {
		return width;
	}


	public void setWidth(double width) {
		this.width = width;
	}
	
	
	@Override
	public int getCost(int level, boolean isRight) {
		return 6;
	}

}
