/**
 * 
 */
package magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import spells.Spell;

public class MagicWand extends ItemStack {
	
	public static GagarinPeter plugin;
	

	
	public MagicWand(Material type, int amount) {
		super(type, amount);
		addUnsafeEnchantment(MagicItem.MAGICWAND_ENCH, 1);
		updateWandLore(this);
		ItemMeta meta = getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
				ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		meta.setDisplayName(MagicItem.itemName);
		setItemMeta(meta);
	}

	public MagicWand(Material type) {
		this(type, 1);
	}
	
	public MagicWand() {
		this(Material.BLAZE_ROD);
	}

	public static boolean isMagicWand(ItemStack item) {
		if (item == null || !item.hasItemMeta()) return false;
		return item.getItemMeta().hasEnchant(MagicItem.MAGICWAND_ENCH);
	}
	
	public static boolean isMagicBook(ItemStack item) {
		if (item == null || !item.hasItemMeta()) return false;
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		for (Enchantment ench : meta.getStoredEnchants().keySet()) {
			if (EnchantmentWrapper.isEnchantmentWrapper(ench)) return true;
		}
		return false;
	}
	
	
	private static ArrayList<String> createEnchLore(Map<Enchantment, Integer> enchs) {
		ArrayList<String> lore = new ArrayList<String>();
		for (Enchantment ench : enchs.keySet()) {
			if (EnchantmentWrapper.isEnchantmentWrapper(ench)) {
				if (ench.getKey().getKey() == "gagarin.magic_wand") continue;
				String nameEnch = ChatColor.GRAY + ench.getName() + " ";
				switch (enchs.get(ench)) {
				case 1:
					nameEnch += "I";
					break;
				case 2:
					nameEnch += "II";
					break;
				case 3:
					nameEnch += "III";
					break;
				case 4:
					nameEnch += "IV";
					break;
				case 5:
					nameEnch += "V";
					break;
				}
				lore.add(nameEnch);
			}	
		}
		lore.add(null);
		return lore;
	}
	
	
	public static void updateWandLore(ItemStack item) {
		if (!isMagicWand(item)) throw new IllegalArgumentException("item is not MagicWand");
		ItemMeta meta = item.getItemMeta();
		List<String> lore = createEnchLore(meta.getEnchants());
		lore.addAll(MagicItem.itemLore);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	
	public static void updateBookLore(ItemStack item) {
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.setLore(createEnchLore(meta.getStoredEnchants()));
		item.setItemMeta(meta);
	}
	
	
	public static void clearEnchanting(ItemStack item) {
		if (!isMagicWand(item)) throw new IllegalArgumentException("item is not MagicWand");
		for (Enchantment ench : item.getEnchantments().keySet()) {
			if (EnchantmentWrapper.isEnchantmentWrapper(ench)) {
				if (ench.getKey().getKey() == "gagarin.magic_wand") continue;
				item.removeEnchantment(ench);
			}
		}
		updateWandLore(item);
	}
	
	
	public static boolean hasEnchants(ItemStack item) {
		if (!isMagicWand(item)) throw new IllegalArgumentException("item is not MagicWand");
		for (Enchantment ench : item.getEnchantments().keySet()) {
			if (EnchantmentWrapper.isEnchantmentWrapper(ench)) {
				if (ench.getKey().getKey() == "gagarin.magic_wand") continue;
				return true;
			} else return true;
		}
		return false;
	}
	
	
	public static ItemStack createResultAnvil(ItemStack wand, ItemStack book) {
		if (!isMagicWand(wand)) throw new IllegalArgumentException("item is not MagicWand");
		if (!isMagicBook(book)) throw new IllegalArgumentException("item is not MagicBook");
		ItemStack item = new ItemStack(wand);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
		for (Enchantment ench : meta.getStoredEnchants().keySet()) {
			if (EnchantmentWrapper.isEnchantmentWrapper(ench)) {
				if (item.getEnchantmentLevel(ench) < meta.getStoredEnchantLevel(ench)) {
					item.removeEnchantment(ench);
					item.addUnsafeEnchantment(ench, meta.getStoredEnchantLevel(ench));
					updateWandLore(item);
				}
			}
		}
		if (item.isSimilar(wand)) return null;
		else return item;
	}
	
	
	public static ItemStack[] createMagicFormItems(ItemStack wand) {
		if (!isMagicWand(wand)) throw new IllegalArgumentException("item is not MagicWand");
		PersistentDataContainer cont =  wand.getItemMeta().getPersistentDataContainer();
		ItemStack items[] = new ItemStack[9];
		items[0] = null;
		for (NamespacedKey key : cont.getKeys()) {
			for (Spell spell : MagicItem.LIST_OF_SPELLS) {
				if (spell.getClass().getSimpleName().toLowerCase().equals(key.getKey())) {
					items[cont.get(key, PersistentDataType.INTEGER)] = spell.createItemOfSpell();
				}
			}
		}
		for (int i = 1; i < 9; i++) {
			if (items[i] == null) {
				items[i] = new ItemStack(Material.BARRIER);
				ItemMeta meta = items[i].getItemMeta();
				meta.setDisplayName("§c§lСвободный слот");
				items[i].setItemMeta(meta);
			}
		}
		return items;
	}

	
	public static boolean canUseSpell(ItemStack wand, Spell s) {
		if (!isMagicWand(wand)) throw new IllegalArgumentException("item is not MagicWand");
		for (Enchantment ench : wand.getEnchantments().keySet()) {
			EnchantmentWrapper wrap = EnchantmentWrapper.getEnchantmentWrapper(ench);
			if (wrap != null && wrap.getSpell() == s) return true;
		}
		return false;
	}
	
	public static void createStandartBindKeys(ItemStack wand) {
		HashMap<Spell, Integer> map = new HashMap<>();
		for (int i = 1; i < 9; i++) {
			map.put(MagicItem.LIST_OF_SPELLS.get(i-1), i);
		}
		bindKeys(wand, map, true);
	}
	
	
	public static void bindKeys(ItemStack wand, Map<Spell, Integer> keys, boolean preClear) {
		if (!isMagicWand(wand)) throw new IllegalArgumentException("item is not MagicWand");
		ItemMeta meta = wand.getItemMeta();
		PersistentDataContainer cont = meta.getPersistentDataContainer();
		if (preClear) {
			ArrayList<NamespacedKey> del = new ArrayList<>();
			for (NamespacedKey key : cont.getKeys()) {
				String val = key.getKey();
				for (Spell s : MagicItem.LIST_OF_SPELLS) {
					if (s.getClass().getSimpleName().equalsIgnoreCase(val)) {
						del.add(key);
						break;
					}
				}
			}
			for (NamespacedKey key : del) {
				cont.remove(key);
			}
		}
		for (Spell s : keys.keySet()) {
			if (!canUseSpell(wand, s)) continue;
			NamespacedKey key = new NamespacedKey(plugin, s.getClass().getSimpleName());
			cont.set(key, PersistentDataType.INTEGER, keys.get(s));
		}
		wand.setItemMeta(meta);
	}
	
	public static void bindKeys(ItemStack wand, Map<Spell, Integer> keys) {
		bindKeys(wand, keys, true);
	}
	
	public static void bindKeys(ItemStack wand, ItemStack items[]) {
		HashMap<Spell, Integer> map = new HashMap<>();
		for (int i = 0; i < items.length; i++) {
			for (Spell s : MagicItem.LIST_OF_SPELLS) {
				if (s.isItemOfSpell(items[i])) {
					map.put(s, i);
				}
			}
		}
		bindKeys(wand, map);
	}
}
