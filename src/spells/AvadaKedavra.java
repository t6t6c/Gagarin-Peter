package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;

public class AvadaKedavra extends ProjectileSpell {
	
	private String projName;
	private boolean destroy;
	private double damage;


	public AvadaKedavra(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, boolean isCanDestroyBlocks, double damage, String projectileName,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setCanDestroyBlocks(isCanDestroyBlocks);
		setDamage(damage);
		setProjectileName(projectileName);
	}
	
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		useOnVector(p, p.getLocation().getDirection(), 0.7, 1);
		playEffect(p, Effect.GHAST_SHOOT, 1);
		return true;
	}
	
	@Override
	public boolean useRight (Player p, int level) {
		if (level < 2) super.useRight(p, level);
		useOnVector(p, p.getLocation().getDirection(), 0.7, level);
		playEffect(p, Effect.GHAST_SHOOT, 1);
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		return standartUseOnEntity(ent, caster, level);
	}
	
	
	
	@Override
	public void useOnVector (Entity p, Vector v, double speed, int level) {
		boolean autoTarget = false;
		List<Entity> list = null;
		if (level > 1 && p instanceof Player) { 
			list = p.getNearbyEntities(50, 50, 50);
			autoTarget = true;
		}
		WitherSkull proj = createProj(p, v, speed, WitherSkull.class, projName, 5000, level);
		proj.setCharged(false);
		if (autoTarget) {
			Entity ent = Accessory.getTarget((Player) p, list);
			if (ent != null) {
				Thread t = new Thread(() -> {
					int k = 0;
					while (proj.isValid() && ent.isValid() && proj.getLocation().distance(ent.getLocation()) < 80 && k < 6) {
						Location end = ent.getLocation().add(new Vector(0, ent.getHeight()/2.0, 0));
						Vector vec = end.subtract(proj.getLocation()).toVector().normalize().multiply(speed);
						proj.setDirection(vec.normalize());
						proj.setVelocity(vec);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
						k++;
					}
					if (!proj.isValid()) {
						removeProjectileEntity(ent);
					}
				});
				t.setDaemon(true);
				t.start();
			}
		}
		proj.setCharged(false);
	}


	public double getDamage() {
		return damage;
	}


	public void setDamage(double damage) {
		this.damage = damage;
	}


	public boolean isCanDestroyBlocks() {
		return destroy;
	}


	public void setCanDestroyBlocks(boolean destroy) {
		this.destroy = destroy;
	}


	public String getProjectileName() {
		return projName;
	}


	public void setProjectileName(String projName) {
		this.projName = projName;
	}



	@Override
	public int getCost(int level, boolean isRight) {
		return 6;
	}
	

}
