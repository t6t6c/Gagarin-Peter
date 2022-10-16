package entity.looting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import magic.MagicItem;

public class ShyGuyLootTable extends GagarinLootTable {

	public ShyGuyLootTable() {
		super("shyguy");
	}

	@Override
	public Collection<ItemStack> populateLoot(Random random, LootContext context) {
		ArrayList<ItemStack> loot = new ArrayList<>();
		EnchantmentWrapper ench = Accessory.randomChoice(MagicItem.KNOCKBACK_ENCH, MagicItem.ALARTE_ASCENDARE_ENCH,
				MagicItem.ARRESTO_MOMENTUM_ENCH, MagicItem.AVADA_KEDAVRA_ENCH);
		int level;
		if (ench == MagicItem.AVADA_KEDAVRA_ENCH) level = 1;
		else level = ench.getMaxLevel();
		loot.add(MagicItem.createEnchantingBook(ench, level));
		return loot;
	}

}
