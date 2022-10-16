package entity.looting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import magic.MagicItem;

public class KolobokLootTable extends GagarinLootTable {

	private EnchantmentWrapper[] enchs;
	
	
	public KolobokLootTable(String kolobokType, EnchantmentWrapper ... enchs) {
		super("kolobok"+kolobokType);
		this.enchs = enchs;
	}

	
	
	@Override
	public Collection<ItemStack> populateLoot(Random random, LootContext context) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		int lootMod = context.getLootingModifier();
		items.add(new ItemStack(Material.BREAD, random.nextInt(1, 3+lootMod)));
		if (random.nextInt(15) + lootMod > 11) {
			EnchantmentWrapper ench = Accessory.randomChoice(enchs);
			int level;
			if (random.nextInt(10)+lootMod > 8) level = ench.getMaxLevel();
			else level = random.nextInt(1, lootMod+2 <= ench.getMaxLevel() ? lootMod+2 : ench.getMaxLevel());
			items.add(MagicItem.createEnchantingBook(ench, level));
		}
		return items;
	}

}
