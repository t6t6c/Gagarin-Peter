package spells;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;

public class Bombardo extends ProjectileSpell {
	
	private String projectileName;
	private boolean destroy;
	private float yield;


	public Bombardo(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, boolean isCanDestroyBlocks, float yield, String projectileName,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setCanDestroyBlocks(isCanDestroyBlocks);
		setYield(yield);
		setProjectileName(projectileName);
	}
	
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		useOnVector(p, p.getLocation().getDirection(), 1, level);
		playEffect(p, Effect.GHAST_SHOOT, 1);
		return true;
	}
	
	@Override
	public boolean useRight (Player p, int level) {
		if (level < 5) return super.useRight(p, level);
		useOnVector(p, p.getLocation().getDirection(), 1, 666);
		playEffect(p, Effect.GHAST_SHOOT, 1);
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) {
		return standartUseOnEntity(ent, caster, level);
	}
	
	
	
	@Override
	public void useOnVector (Entity p, Vector v, double speed, int level) {
		Fireball proj = createProj(p, v, speed, Fireball.class, projectileName, 15000, level);	
		proj.setYield(yield);
	}



	public String getProjectileName() {
		return projectileName;
	}



	public void setProjectileName(String projectileName) {
		this.projectileName = projectileName;
	}



	public boolean isCanDestroyBlocks() {
		return destroy;
	}



	public void setCanDestroyBlocks(boolean destroy) {
		this.destroy = destroy;
	}



	public double getDamage(int level) {
		double dmg = 0;
		switch(level) {
		case 1:
			dmg = 10;
		break;
		case 2:
			dmg = 15;
		break;
		case 3:
			dmg = 20;
		break;
		case 4:
		case 5:
			dmg = 40;
		break;
		case 666:
			dmg = 30;
		break;
		}
		return dmg;
	}



	public float getYield() {
		return yield;
	}



	public void setYield(float yield) {
		this.yield = yield;
	}



	@Override
	public int getCost(int level, boolean isRight) {
		return isRight ? 10 : 5;
	}

}
