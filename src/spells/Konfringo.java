package spells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import magic.MagicItem;

public class Konfringo extends ProjectileSpell {
		
	private static final String NAME_MAGIC_FIRE = "Konfringo_Area",
								NAME_HELL_FIRE = "Konfringo_Hell_Area";
	private HashSet<HellFireUser> usingHellFire = new HashSet<>();
	private double damage, radius;
	private int durationHellFireInTick = 300;
	private int fireTicks, particles;
	private String projectileName;
	public MagicFireSpell magicFire;
	
	
	public Konfringo(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, double damage, double radius, int fireTicks, int particles, String projectileName,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setFireTicks(fireTicks);
		setParticles(particles);
		setRadius(radius);
		setDamage(damage);
		setProjectileName(projectileName);
		magicFire = new MagicFireSpell(plugin, 10., 3, 25, true, null, null, color, 0, false, 1.25f, 5, new ArrayList<>(), null);
		@SuppressWarnings("unchecked")
		Runnable r = () -> {
			if (usingHellFire.isEmpty()) return;
			for (HellFireUser user : ((HashSet<HellFireUser>) usingHellFire.clone())) {
				if (user.dur <= 0) {
					stopUsingHellFire(user.p);
					continue;
				}
				user.dur -= 5;
				user.spawnNewFire(user.getNewLocation());
			}
		};
		Bukkit.getScheduler().runTaskTimer(plugin, r, 20, 5);
	}
	
	
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean isDamageEntity (AreaEffectCloud cloud, Entity ent) 
 			throws IllegalArgumentException, NullPointerException {
 		magicFire.isDamageEntity(cloud, ent);
 		return !ent.equals(cloud.getSource()) && NAME_MAGIC_FIRE.equals(cloud.getCustomName());
 	}
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		useOnVector(p, p.getLocation().getDirection(), 1, level);
		playEffect(p, Effect.GHAST_SHOOT, 1);
		return true;
	}
	
	
	@Override
	public boolean useRight(Player p, int level) {
		if (level < 3 ) return useRight(p, level);
		if (!MagicItem.isUsingMagicForm(p)) {
			p.sendMessage(ChatColor.RED + "Активируйте магическую форму " + ChatColor.WHITE +
					"(SHIFT+Q)" + ChatColor.RED + ", чтобы использовать это заклинание");
			return false;
		}
		usingHellFire.add(new HellFireUser(p, durationHellFireInTick));
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		return standartUseOnEntity(ent, caster, level);
	}
	
	
	
	@Override
	public void useOnVector (Entity p, Vector v, double speed, int level) {
		Fireball proj = createProj(p, v, speed, Fireball.class, projectileName, 15000, level);
		proj.setIsIncendiary(false);
		proj.setYield(0);
	}
	
	
	
	public boolean changeHellFireLocation(Player p, boolean isRight) {
		HellFireUser u = getUsingHellFire(p);
		if (u != null) {
			MagicItem.print(isRight);
			if (isRight) {
				if (u.length < 15) u.length += 1;
			} else if (u.length > 1) u.length -= 1;
			MagicItem.print(u.length);
			return true;
		}
		return false;
	}

	
	
	public void startUsingHellFire(Player p) {
		HellFireUser fire = new HellFireUser(p, durationHellFireInTick);
		usingHellFire.remove(fire);
		usingHellFire.add(fire);
	}
	
	
	
	public HellFireUser getUsingHellFire(Player p) {
		for (HellFireUser u : usingHellFire) {
			if (u.p == p) return u;
		}
		return null;
	}
	
	
	public void createMagicFire(LivingEntity source, Entity ent) {
		ArrayList<Location> locs = new ArrayList<Location>();
		locs.add(ent.getLocation().add(new Vector(0, ent.getHeight()/2., 0)));
		magicFire.createMagicFires(locs, NAME_MAGIC_FIRE, source, 3, true);
	}
	
	
	public boolean stopUsingHellFire(Player p) {
		return usingHellFire.remove(new HellFireUser(p));
	}


	public double getDamage() {
		return damage;
	}


	public void setDamage(double damage) {
		this.damage = damage;
	}


	public double getRadius() {
		return radius;
	}


	public void setRadius(double radius) {
		this.radius = radius;
	}


	public int getFireTicks() {
		return fireTicks;
	}


	public void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}


	public int getParticles() {
		return particles;
	}


	public void setParticles(int particles) {
		this.particles = particles;
	}


	public String getProjectileName() {
		return projectileName;
	}


	public void setProjectileName(String projectileName) {
		this.projectileName = projectileName;
	}



	@Override
	public int getCost(int level, boolean isRight) {
		return 4;
	}

	
	
	
	private class HellFireUser {
		
		public Player p;
		public int dur, length;
		public ArrayList<AreaEffectCloud> clouds = new ArrayList<>();

		public HellFireUser(Player p, int dur) {
			this.p = p;
			this.dur = dur;
			length = 5;
		}
		
		public HellFireUser(Player p) {
			this(p, 1);
		}
		
		public Location getNewLocation() {
			return p.getLocation().add(p.getLocation().getDirection().normalize().multiply(length));
		}
		
		public void spawnNewFire(Location loc) {
			removeNotValidClouds();
			for (AreaEffectCloud c : clouds) {
				if (c.getLocation().distance(loc) < 0.3) return;
			}
			ArrayList<Location> locs = new ArrayList<>();
			locs.add(loc);
			HashSet<AreaEffectCloud> c = magicFire.createMagicFires(locs, NAME_HELL_FIRE, p, 1, true);
			clouds.addAll(c);
		}
		
		public void removeNotValidClouds() {
			for (AreaEffectCloud c : new ArrayList<AreaEffectCloud>(clouds)) {
				if (!c.isValid()) clouds.remove(c);
			}
		}
		
		
		@Override
		public int hashCode() {
			return p.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof HellFireUser && obj.hashCode() == hashCode();
		}
	}
	
}
