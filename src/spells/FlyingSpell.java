package spells;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import magic.MagicItem;

public class FlyingSpell extends Spell implements CustomManaUsing{
	
	private double speed;
	private int duration;
	private HashMap<Player, HashMap<String, Double>> flyPlayers = new HashMap<>();
	
	
	public FlyingSpell(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, int duration, double speed, int reduceCooldown,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setSpeed(speed);
		setDuration(duration);
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Player p : flyPlayers.keySet()) {
				if (p.isValid()) {
					if (p.getVelocity().length() > 0.8) return;
					HashMap<String, Double> meta = flyPlayers.get(p);
					double lenVector = 0.25 + (p.isSprinting() ? meta.get("lvl")*0.15 : 0);
					double mana = (p.isSprinting() ? 0.1d : (meta.get("lvl") < 3 ? 0.067d : 0d));
					mana *= MagicItem.getModifierArmorOnCostSpell(p);
					meta.put("mana", meta.get("mana") + mana);
					Vector direct = p.getLocation().getDirection().normalize();
					Vector veloc = direct.multiply(lenVector);
					p.setVelocity(veloc);
					p.getWorld().spawnParticle((p.isSprinting() ? Particle.CLOUD : Particle.SPELL), p.getLocation().subtract(direct.multiply(5)),
							(p.isSprinting() ? 5 : 3), 0, 0, 0);
					if (meta.get("mana") >= 1) {
						meta.put("mana", 0d);
						p.setFoodLevel(p.getFoodLevel()-1);
					}
					if (p.getFoodLevel() < 0) {
						stopFly(p);
						p.sendMessage(ChatColor.RED + "Вы не можете больше лететь, так как закончилась манна.");
					}
				} else stopFly(p);
			}
		}, 20, 2);
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		int duration = 15+level*5;
		Runnable r = () -> {
			try {
				Thread.sleep(duration*1000);
			} catch (InterruptedException e) {}
			stopFly(p);
		};
		Thread t = new Thread (r);
		t.setDaemon(true);
		t.start();
		playEffect(p, Effect.GHAST_SHOOT, 1);
		startFly(p, level);
		p.setFallDistance(0);
		return true;
	}	
	
	
	
	public boolean isFly (Player ent) {
		return flyPlayers.containsKey(ent);
	}
	
	public void stopFly(Player ent) {
		if (isFly(ent)) {
			flyPlayers.remove(ent);
			ent.setFallDistance(0);
			ent.setGliding(false);
		}
	}
	
	public void startFly(Player ent, int level) {
		HashMap<String, Double> meta = new HashMap<>();
		meta.put("lvl", (double) level);
		meta.put("mana", 0d);
		flyPlayers.put(ent, meta);
		ent.setGliding(true);
	}
	

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
		replaceTag("dur", duration);
	}



	@Override
	public int getCost(int level, boolean isRight) {
		return 0;
	}

}
