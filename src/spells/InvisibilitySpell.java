package spells;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import magic.EffectParams;
import magic.MagicScheduler;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;



public class InvisibilitySpell extends Spell {
	
	public static final String EFFECT_NAME = "invisibility";
	private int duration;
	public ArrayList<Entity> mobs = new ArrayList<>();
	
	
	public InvisibilitySpell(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, int duration,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setDuration(duration);
	}
	
	
	
	public NPC spawnDenis (Player p) {
		NPCRegistry reg = CitizensAPI.getNPCRegistry();
		NPC npc = reg.createNPC(EntityType.PLAYER, p.getDisplayName());
		npc.spawn(p.getLocation());
		npc.setProtected(false);
		npc.setUseMinecraftAI(true);
		Player mob = (Player) npc.getEntity();
		mob.setCanPickupItems(false);
		mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.1);
		EntityEquipment eq = mob.getEquipment();
		eq.setBoots(p.getEquipment().getBoots());
		eq.setLeggings(p.getEquipment().getLeggings());
		eq.setChestplate(p.getEquipment().getChestplate());
		eq.setHelmet(p.getEquipment().getHelmet());
		mob.setCollidable(true);
		mobs.add(mob);
		return npc;
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		playSound(p, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1);
		p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 10);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration*20, 13));
		if (level < 2) {
			for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
				try {
					Mob mob = (Mob) ent;
					if (mob.getTarget() == p) mob.setTarget(null);
				} catch (ClassCastException exc) {}	
			}
			MagicScheduler.addNewRow(p, EFFECT_NAME, duration);
			return true;
		}
		NPC denis = spawnDenis(p);
		mobs.add(denis.getEntity());
		for (Entity ent : p.getNearbyEntities(50, 50, 50)) {
			try {
				Mob mob = (Mob) ent;
				if (mob.getTarget() == p) mob.setTarget((LivingEntity) denis.getEntity());
			} catch (ClassCastException exc) {}	
		}	
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player != p) player.hidePlayer(getPlugin(), p);
		}
		
		Runnable r = () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player != p) player.showPlayer(getPlugin(), p);
			}
			p.setCollidable(true);
			try {
				denis.getEntity().setInvulnerable(false);
				((LivingEntity) denis.getEntity()).damage(10000);
				mobs.remove(denis.getEntity());
			} catch (NullPointerException exc) {}
		};
		MagicScheduler.addNewRow(p, EFFECT_NAME, new EffectParams(duration, r));
		return true;
	}
	
	
	
	public List<Entity> getMobs() {
		return mobs;
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
		return 3;
	}

}
