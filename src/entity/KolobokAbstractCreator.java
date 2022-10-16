package entity;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import entity.looting.KolobokLootTable;
import gagarin.Accessory;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;

public abstract class KolobokAbstractCreator<T extends Zombie> extends EntityCreator<T> {

	public static final HashSet<Entity> villagers = new HashSet<>();
	private ItemStack head;
	private int health, damage;
	private String name;
	
	
	public KolobokAbstractCreator(KolobokLootTable lootTable, String name, int health, int damage, String urlHead) {
		super(lootTable);
		this.health = health;
		this.damage = damage;
		try {
			head = Accessory.getPlayerHead(urlHead);
		} catch (RuntimeException exc) {
			head = new ItemStack(Material.PLAYER_HEAD);
		}
		this.name = name;
	}
	
	
	
	@Override
	public T spawnEntity(Location loc) {
		EntityCreature ent = createEntity(loc);
		T z = setDefaultSettings(ent, health, getUniqueKey(), loc);
		
		z.setBaby();
		z.setCustomName(name);
		z.setCustomNameVisible(true);
		z.setInvisible(true);
		z.setConversionTime(-1);
		z.getEquipment().setHelmet(new ItemStack(head));
		z.getEquipment().setHelmetDropChance(0);
		z.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);
		z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
		z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
		return z;
	}
	
	
	public static boolean isKolobok(Entity ent) {
		String key = getUniqueKey(ent);
		if (key != null) return key.contains("kolobok");
		else return false;
	}
	
	
	
	@Override
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getCause() != DamageCause.ENTITY_ATTACK || !e.getEntity().isValid()) return;
		if (e.getEntityType() != EntityType.VILLAGER && e.getEntityType() != EntityType.WANDERING_TRADER) return;
		LivingEntity ent = (LivingEntity) e.getEntity();
		if (ent.getHealth() <= e.getFinalDamage()) villagers.add(ent);	
	}


	@Override
	public int getExpDrops() {
		return 30;
	}
	
	
	protected void updatePathfinder(EntityMonster ent, PathfinderGoalSelector path) {
		path.a(0, new PathfinderGoalFloat(ent));
		path.a(2, new PathfinderGoalMeleeAttack(ent, 1, true));
		path.a(7, new PathfinderGoalRandomStroll(ent, 1.0D));
		path.a(8, new PathfinderGoalLookAtPlayer(ent, EntityHuman.class, 8.0F));
		path.a(8, new PathfinderGoalRandomLookaround(ent));
		path.a(1, new PathfinderGoalHurtByTarget(ent));
		path.a(2, new PathfinderGoalNearestAttackableTarget<EntityFox>(ent, EntityFox.class, true, false));
		path.a(2, new PathfinderGoalNearestAttackableTarget<EntityVillagerAbstract>(ent, EntityVillagerAbstract.class, true, true));
		path.a(2, new PathfinderGoalNearestAttackableTarget<EntityPlayer>(ent, EntityPlayer.class, true, false));
	}
	
	
	protected abstract EntityCreature createEntity(Location loc);
	
	
	
	

}
