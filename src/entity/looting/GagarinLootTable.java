package entity.looting;

import java.util.Random;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import magic.MagicItem;

public abstract class GagarinLootTable implements LootTable {
	
	private NamespacedKey key;
	private String name;
	

	public GagarinLootTable(String entityName) {
		key = new NamespacedKey(MagicItem.plugin, entityName+"_looting");
		name = entityName;
	}
	
	public String getEntityName() {
		return name;
	}
	
	@Override
	public NamespacedKey getKey() {
		return key;
	}

	@Override
	public void fillInventory(Inventory inventory, Random random, LootContext context) {
		for (ItemStack item : populateLoot(random, context)) inventory.addItem(item);
	}

}
