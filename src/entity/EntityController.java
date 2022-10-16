package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import gagarin.GagarinPeter;
import magic.MagicItem;
import net.md_5.bungee.api.chat.TextComponent;

public class EntityController implements Listener {
	
	static GagarinPeter plugin;
	private Random r = new Random();
	
	public static ArrayList<EntityCreator<? extends Mob>> creators = new ArrayList<>();
	
	
	public static void startSettings(GagarinPeter gagarin) {
		plugin = gagarin;
		creators.add(new KolobokCreator());
		creators.add(new KolobokDemonCreator());
		creators.add(new KolobokWaterCreator());
		creators.add(new ShyGuyCreator());
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends EntityCreator> T findCreator (Class<T> cls) throws IllegalArgumentException {
		for (EntityCreator s : creators) {
			if (s.getClass().equals(cls)) return (T) s;
		}
		throw new IllegalArgumentException ("Don't find this entity.");
	}
	
	
	@SuppressWarnings("rawtypes")
	public static EntityCreator whoCreater(Entity ent) {
		for (EntityCreator cretor : creators) {
			if (cretor.isEntity(ent)) return cretor;
		}
		return null;
	}
	
	
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (!EntityCreator.isGagarinEntity(e.getEntity())) return;
		EntityCreator<?> creator = whoCreater(e.getEntity());
		Mob ent = (Mob) e.getEntity();
		LootContext.Builder builder = new LootContext.Builder(e.getEntity().getLocation());
		int lootEnch = 0;
		float luck = 0;
		if (ent.getKiller() != null) {
			Player p = ent.getKiller();
			builder.killer(p);
			luck = (float) p.getAttribute(Attribute.GENERIC_LUCK).getBaseValue();
			lootEnch = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
		}
		builder.lootedEntity(ent);
		builder.lootingModifier(lootEnch);
		builder.luck(luck);
		Collection<ItemStack> items = creator.lootTable.populateLoot(new Random(), builder.build());
		e.getDrops().clear();
		e.getDrops().addAll(items);
		e.setDroppedExp(whoCreater(ent).getExpDrops());
	}
	
	
	
	@EventHandler
	public void onDamageEntity(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (!EntityCreator.isGagarinEntity(ent)) return;
		if (KolobokAbstractCreator.isKolobok(ent)) {
			switch (e.getCause()) {
			case FALL:
			case HOT_FLOOR:
			case CONTACT:
				e.setCancelled(true);
			default:
			}
		} else if (findCreator(ShyGuyCreator.class).isEntity(ent)) {
			if (e.getCause() == DamageCause.DROWNING) e.setCancelled(true);
		}
	}
	
	 
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!EntityCreator.isGagarinEntity(e.getDamager())) return;
		for (EntityCreator<?> creator : creators) {
			if (creator.isEntity(e.getDamager())) {
				creator.onEntityDamageByEntity(e);
				return;
			}
		}
	}
	
	
	@EventHandler
	public void onEntityTransform(EntityTransformEvent e) {
		if (e.getTransformReason() == TransformReason.INFECTION && KolobokAbstractCreator.villagers.contains(e.getEntity())) e.setCancelled(true); 
	}
	
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		switch (e.getEntity().getType()) {
		case ZOMBIE:
			if ((MagicItem.debug && r.nextBoolean()) || r.nextInt(8) == 0) {
				if (e.getLocation().getY() > 50) {
					e.setCancelled(true);
					Class<? extends KolobokAbstractCreator<? extends Zombie>> cls;
					if (e.getLocation().getBlock().getBiome() == Biome.SWAMP) cls = KolobokWaterCreator.class;
					else cls = KolobokCreator.class;
					findCreator(cls).spawnEntity(e.getLocation());
				}
			}
		break;
		case DROWNED:
			if ((MagicItem.debug && r.nextBoolean()) || r.nextInt(6) == 0) {
				e.setCancelled(true);
				findCreator(KolobokWaterCreator.class).spawnEntity(e.getLocation());
			}
		break;
		case ZOMBIFIED_PIGLIN:
			if ((MagicItem.debug && r.nextBoolean()) || r.nextInt(8) == 0) {
				e.setCancelled(true);
				findCreator(KolobokDemonCreator.class).spawnEntity(e.getLocation());
			}
		break;
		default:
		}
	}
	
}
