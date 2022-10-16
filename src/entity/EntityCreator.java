package entity;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import entity.looting.GagarinLootTable;
import magic.MagicItem;
import net.minecraft.world.entity.EntityCreature;

public abstract class EntityCreator<T extends Mob> {
	
	protected GagarinLootTable lootTable;
	public static NamespacedKey NAME_KEY = MagicItem.createNamespace("gagarin_entity_key");

	public EntityCreator(GagarinLootTable lootTable) {
		this.lootTable = lootTable;
	}

	
	public static boolean isGagarinEntity(Entity ent) {
		try {
			return ent.getPersistentDataContainer().has(NAME_KEY, PersistentDataType.STRING);
		} catch (NullPointerException exc) { return false; }
	}
	
	public static String getUniqueKey(Entity ent) {
		try {
			return ent.getPersistentDataContainer().get(NAME_KEY, PersistentDataType.STRING);
		} catch (NullPointerException | IllegalArgumentException exc) { return null; }
		
	}
	
	
	public String getUniqueKey() {
		return lootTable.getEntityName();
	}
	
	public boolean isEntity(Entity ent) {
		return getUniqueKey().equals(getUniqueKey(ent));
	}
	
	
	public GagarinLootTable getLootTable() {
		return lootTable;
	}
	
	
	protected T setDefaultSettings(EntityCreature entity, int health, String name, Location loc) {
		entity.b(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		((CraftWorld) loc.getWorld()).addEntity(entity, SpawnReason.CUSTOM);
		@SuppressWarnings("unchecked")
		T ent = (T) entity.getBukkitEntity();
		ent.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
		ent.setHealth(health);
		ent.setCanPickupItems(false);
		ent.setCustomName(name);
		ent.setCustomNameVisible(true);
		ent.setSilent(true);
		ent.setRemoveWhenFarAway(true);
		EntityEquipment equip = ent.getEquipment();
		ItemStack[] items = {null, null, null, null};
		equip.setArmorContents(items);
		equip.setItemInMainHand(null);
		equip.setItemInOffHand(null);
		ent.getPersistentDataContainer().set(NAME_KEY, PersistentDataType.STRING, getUniqueKey());
		return ent;
	}
	
	
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {}
	
	public abstract T spawnEntity(Location loc);
	public abstract int getExpDrops();
}
