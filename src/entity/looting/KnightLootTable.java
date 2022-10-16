package entity.looting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

public class KnightLootTable extends GagarinLootTable {

	public KnightLootTable() {
		super("knight");
	}

	@Override
	public Collection<ItemStack> populateLoot(Random random, LootContext context) {
		return new ArrayList<ItemStack>();
	}

}
