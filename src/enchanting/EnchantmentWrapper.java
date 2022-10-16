package enchanting;

import java.util.ArrayList;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import magic.MagicItem;
import spells.Spell;

public class EnchantmentWrapper extends Enchantment {

	
	private final String name;
	private final int maxlvl;
	private static ArrayList<EnchantmentWrapper> list = new ArrayList<EnchantmentWrapper>();
	
	
	public EnchantmentWrapper(String namespace, String name, int maxlvl) {
		super(NamespacedKey.minecraft(namespace));
		this.name = name;
		this.maxlvl = maxlvl;
		list.add(this);
	}

	@Override
	public boolean canEnchantItem(ItemStack arg0) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.WEAPON;
	}

	@Override
	public int getMaxLevel() {
		// TODO Auto-generated method stub
		return maxlvl;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getStartLevel() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isCursed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTreasure() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Spell getSpell() {
		String n = getKey().getKey().split("[.]")[1].replaceAll("_", "");
		for (Spell s : MagicItem.LIST_OF_SPELLS) {
			if (s.getClass().getSimpleName().equalsIgnoreCase(n)) return s;
		}
		return null;
	}
	
	
	public static boolean isEnchantmentWrapper(Enchantment ench) {
		return list.contains(ench);
	}
	
	public static EnchantmentWrapper getEnchantmentWrapper(Enchantment ench) {
		for (EnchantmentWrapper e : list) {
			if (e.equals(ench)) return e;
		}
		return null;
	}
	
	
	public static ArrayList<EnchantmentWrapper> getEnchants() {
		return list;
	}

}
