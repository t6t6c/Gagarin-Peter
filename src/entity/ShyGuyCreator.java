package entity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Enderman;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import entity.looting.ShyGuyLootTable;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWrapped;
import net.minecraft.world.entity.monster.EntityEnderman;

public class ShyGuyCreator extends EntityCreator<Enderman> {

	public ShyGuyCreator() {
		super(new ShyGuyLootTable());
	}

	@Override
	public Enderman spawnEntity(Location loc) {
		ShyGuy entity = new ShyGuy(loc);
		Enderman ent = setDefaultSettings(entity, 200, ChatColor.RED + "" + ChatColor.BOLD + "SCP-096", loc);
		ent.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(60);
		ent.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(5);
		ent.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*10000, 1));
		return ent;
	}

	@Override
	public int getExpDrops() {
		return 75;
	}
	
	
	
	public class ShyGuy extends EntityEnderman {

		public ShyGuy(Location loc) {
			super(EntityTypes.w, ((CraftWorld) loc.getWorld()).getHandle());
			ArrayList<PathfinderGoalWrapped> goals = new ArrayList<>();
			for (PathfinderGoalWrapped g : bQ.c())
				if (g.i() == 1 || g.i() == 10 || g.i() == 11) goals.add(g);
			bQ.c().removeAll(goals);
		}
		
		@Override
		protected boolean t() {
			return false;
		}
	}

}
