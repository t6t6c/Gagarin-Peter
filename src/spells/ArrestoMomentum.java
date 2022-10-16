package spells;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.MagicScheduler;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ArrestoMomentum extends Spell {
	
	public static final String EFFECT_NAME = "arresto_effect", SELF_EFFECT_NAME = "arresto_effect_self";
	private double range;
	private String notFoundMessage, onPlayerMove, onPlayerMoveSelf;

	
	public ArrestoMomentum(GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, double range, String onNotFoundTarget, String onPlayerMove, String onPlayerMoveSelf,
			List<String> lore, EnchantmentWrapper ench) {
		super(plugin, name, material, color, cooldown, isVerbal, command, lore, ench);
		setNotFoundMessage(onNotFoundTarget);
		setOnPlayerMove(onPlayerMove);
		setOnPlayerMoveSelf(onPlayerMoveSelf);
		setRange(range);
	}
	
	
	
	@Override
	public boolean useRight (Player p, int level) {
		playSound(p, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1);
		return useOnEntity(p, p, level);
	}
	
	
	
	@Override
	public boolean useLeft (Player p, int level) {
		if (level > 2 && p.isSneaking()) {
			List<Entity> list = p.getNearbyEntities(6, 6, 6);
			for (Entity ent : list) {
				if (isLocationProtectFlag(ent.getLocation())) continue;
				if (!isProtegoEntity(ent)) {
					useOnEntity(ent, p, level);
				} else ent.sendMessage(Protego.getBlockMessage(this));
			}
			playSound(p, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1);
			p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 100, 6, 1, 6);
		} else {
			playSound(p, Sound.ENTITY_SPLASH_POTION_THROW, 1);
			List<Entity> list = p.getNearbyEntities(range, range, range);
			Entity ent = Accessory.getTarget(p, list);
			if (ent != null) {
				if (isLocationProtectFlag(ent.getLocation())) return true;
				playSound(p, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1);
				if (isProtegoEntity(ent)) {
					useOnEntity(p, ent, level);
					ent.sendMessage(Protego.getBlockMessage(this));
				} else useOnEntity(ent, p, level);
			} else p.sendMessage(notFoundMessage);
		}
		return true;
	}
	
	
	
	@Override
	public boolean useOnEntity (Entity ent, Entity caster, int level) throws IllegalArgumentException {
		NPCRegistry reg = CitizensAPI.getNPCRegistry();
		Runnable r = null;
		if (reg.isNPC(ent)) {
			NPC npc = (NPC) ent;
			boolean ai = npc.useMinecraftAI();
			npc.setUseMinecraftAI(false);
			r = () -> {
				npc.setUseMinecraftAI(ai);
				((Entity) npc).setFallDistance(0);
			};
		} else if (ent.getType() == EntityType.PLAYER) {
			MagicScheduler.addNewRow(ent, ent == caster ? SELF_EFFECT_NAME : EFFECT_NAME, 2+level);
			sayUse(ent);
			r = () -> {
				ent.setFallDistance(0);
			};
		} else {
			try {
				LivingEntity mob = (LivingEntity) ent;
				boolean ai = mob.hasAI();
				mob.setAI(false);
				r = () -> {
					mob.setAI(ai);
					mob.setFallDistance(0);
				};
			} catch (ClassCastException exc) {return true;}
		}
		Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), r, (2+level) * 20);
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



	public String getOnPlayerMove() {
		return onPlayerMove;
	}
	
	
	public String getOnPlayerMove(int dur) {
		return onPlayerMove.replaceAll("%dur", String.valueOf(dur));
	}



	public void setOnPlayerMove(String onPlayerMove) {
		this.onPlayerMove = onPlayerMove;
	}



	public String getOnPlayerMoveSelf(int dur) {
		return onPlayerMoveSelf.replaceAll("%dur", String.valueOf(dur));
	}



	public void setOnPlayerMoveSelf(String onPlayerMoveSelf) {
		this.onPlayerMoveSelf = onPlayerMoveSelf;
	}



	@Override
	public int getCost(int level, boolean isRight) {
		return isRight && level > 2 ? 0 : 5;
	}

}
