package spells;

import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import magic.MagicItem;

public abstract class ProjectileSpell extends Spell {
	
	protected final HashSet<Projectile> projects = new HashSet<>();
	private NamespacedKey levelKey = MagicItem.createNamespace("level_project");

	public ProjectileSpell(GagarinPeter plugin, String name, int cooldown, ChatColor color, Material material, EnchantmentWrapper ench) {
		super(plugin, name, cooldown, color, material, ench);
	}
	
	
	public ProjectileSpell(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, lore, ench);
	}
	
	
	public ProjectileSpell(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
	}
	
	
	
	public <T extends Projectile> T createProj (Entity p, Vector v, double speed, 
			Class<T> cls, String name, int timeLive, int level) 
					throws IllegalArgumentException {
		if (!(p instanceof ProjectileSource)) {
			throw new IllegalArgumentException("p isn't ProjectileSource");
		}
		v.normalize().multiply(speed);
		T proj = ((ProjectileSource) p).launchProjectile(cls, v);
		proj.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
		proj.setCustomName(name);
		proj.setCustomNameVisible(true);
		proj.setGravity(false);
		proj.setSilent(true);
		Runnable r = () -> {
			try {
				try {
					Thread.sleep(timeLive);
				} catch (InterruptedException e) {}
				removeProjectileEntity(proj);
			} catch (Exception e) {}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
		projects.add(proj);
		return proj;
	}
	
	
	
	public boolean isProjectileEntity (Entity ent) {
		return projects.contains(ent);
	}
	
	protected boolean removeProjectileEntity (Entity ent) {
		if (projects.remove(ent)) {
			ent.remove();
			return true;
		}
		return false;
	}
	
	public int getLevelByProj(Projectile proj) throws IllegalArgumentException {
		if (!isProjectileEntity(proj)) throw new IllegalArgumentException("Is not spell projectile entity");
		return proj.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
	}
	
	
	
	public boolean standartUseOnEntity (Entity ent, Entity caster, int level) {
		if (ent == caster) return false;
		Location loc = caster.getLocation();
		loc = new Location(caster.getWorld(), -loc.getX(), -loc.getY(), -loc.getZ());
		loc = ent.getLocation().add(loc);
		useOnVector(caster, loc.toVector(), 2, level);
		return true;
	}
	
	
	
	public void useOnVector (Entity caster, Vector v, double speed, int level) throws IllegalStateException {
		throw new IllegalStateException("This method not redefined.");
	}

}
