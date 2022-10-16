package spells;

import java.util.ArrayList;
import java.util.List;
import static gagarin.Accessory.randomChoice;

import javax.annotation.Nonnull;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import enchanting.EnchantmentWrapper;
import gagarin.GagarinPeter;
import magic.Loader;
import magic.MagicItem;
import magic.MagicScheduler;
import magic.MagicWand;

public abstract class Spell {
	
	private String name, command = null;
	private int cooldown;
	private ChatColor color;
	private GagarinPeter plugin;
	protected List<String> lore;
	private boolean verbal = true;
	private Material material;
	private EnchantmentWrapper enchant;
	
	
	public Spell (GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, List<String> lore, EnchantmentWrapper ench) {
		this(plugin, name, material, color, cooldown, isVerbal, null, lore, ench);
	}
	
	
	public Spell (GagarinPeter plugin, String name, Material material, ChatColor color,
			int cooldown, boolean isVerbal, String command, List<String> lore, EnchantmentWrapper ench) {
		setName(name);
		setCooldown(cooldown);
		setColor(color);
		setPlugin(plugin);
		setMaterial(material);
		setVerbal(isVerbal);
		setCommand(command);
		setEnchant(ench);
		if (command != null) {
			lore.add(Loader.getSpellCommandString().replaceAll("%command", String.valueOf(command)));
		}
		lore.add(Loader.getSpellReloadString().replaceAll("%reload", String.valueOf(cooldown)));
		setLore(lore);
	}
	
	
	public Spell (GagarinPeter plugin, String name, int cooldown, ChatColor color, Material material,
			EnchantmentWrapper ench) {
		this(plugin, name, material, color, cooldown, true, null, new ArrayList<String>(), ench);
	}
	
	
	
	public boolean useLeft (Player caster, int level) throws IllegalStateException {
		throw new IllegalStateException("This method not redefined.");
	}
	
	public boolean useRight (Player caster, int level) throws IllegalStateException {
		throw new IllegalStateException("This method not redefined.");
	}
	
	public boolean useOnEntity (Entity ent, Entity caster, int level) throws IllegalStateException {
		throw new IllegalStateException("This method not redefined.");
	}
	
	
	
	public static boolean isProtegoEntity (Entity ent) {
		return MagicScheduler.hasEffect(ent, "protego_effect#");
	}
	
	public static boolean isLocationProtectFlag(GagarinPeter plugin, Location loc) {
		return MagicItem.containsFlagValue(loc, plugin.useSpellFlag, false);
	}
	
	public boolean isLocationProtectFlag(Location loc) {
		return isLocationProtectFlag(plugin, loc);
	}
	
	
	
	
	
	protected void playEffect (Entity ent, Effect eff, int mod) {
		playEffect(ent.getLocation(), eff, mod);
	}
	
	protected void playEffect (Location loc, Effect eff, int mod) {
		loc.getWorld().playEffect(loc, eff, mod);
	}
	
	
	protected void playSound (Entity ent, Sound sound, int mod) {
		playSound(ent.getLocation(), sound, mod);
	}
	
	protected void playSound (Location loc, Sound sound, int mod) {
		loc.getWorld().playSound(loc, sound, 3, mod);
	}
	
	
	
	public void sayUse (Entity ent) {
		ent.sendMessage(Loader.getOnSpellUseOnPlayerMessage().replaceAll("%spell", color + name));
	}
	
	
	protected boolean isOpMessage (Player p) {
		if (!p.isOp()) {
			p.sendMessage(ChatColor.RED + "Данную способность могут использовать только"
					+ " достойные.");
			return false;
		}
		return true;
	}
	
	
	
	protected void replaceTag(String tag, Object value) {
		String v = String.valueOf(value);
		tag = "%" + tag;
		ArrayList<String> lore = new ArrayList<>();
		for (String row : this.lore) {
			lore.add(row.replaceAll(tag, v));
		}
		setLore(lore);
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getCooldown() {
		return cooldown;
	}
	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public GagarinPeter getPlugin() {
		return plugin;
	}
	public void setPlugin(GagarinPeter plugin) {
		this.plugin = plugin;
	}

	public ChatColor getColor() {
		return color;
	}
	public void setColor(ChatColor color) {
		this.color = color;
	}

	public List<String> getLore() {
		return new ArrayList<String>(lore);
	}
	public void setLore(List<String> lore) {
		this.lore = new ArrayList<>(lore);
	}

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	public boolean isVerbal () {
		return verbal;
	}
	public void setVerbal (boolean verbal) {
		this.verbal = verbal;
	}



	public Material getMaterial() {
		return material;
	}



	public void setMaterial(Material material) {
		this.material = material;
	}


	public EnchantmentWrapper getEnchant() {
		return enchant;
	}


	public void setEnchant(EnchantmentWrapper enchant) {
		this.enchant = enchant;
	}
	
	
	public ItemStack createItemOfSpell() {
		ItemStack item = new ItemStack(getMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		meta.setDisplayName(getColor() + "" + ChatColor.BOLD + getName());
		List<String> lore = new ArrayList<String>(getLore());
		lore.add(0, ChatColor.MAGIC + getClass().getName());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public boolean isItemOfSpell(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			try {
				String name = lore.get(0).substring(2, lore.get(0).length()-2);
				if (getClass().getCanonicalName().equals(name)) {
					return true;
				}
			} catch (StringIndexOutOfBoundsException exc) {}
		}
		return false;
	}
	
	
	public boolean canUseSpell(ItemStack item) {
		return MagicWand.isMagicWand(item) && item.getEnchantments().containsKey(enchant);
	}
	
	public int getLevelSpell(@Nonnull ItemStack item) {
		return item.getEnchantments().get(enchant);
	}
	
	public abstract int getCost(int level, boolean isRight);
	
	
	
	public static void useMana(Player p, int value) {
		if (!p.getGameMode().equals(GameMode.CREATIVE)) {
			// Отнимаем манну
			int cost = Math.round( value*MagicItem.getModifierArmorOnCostSpell(p) );
			cost = cost == 0 ? 1 : cost;
			int res = Math.round(p.getFoodLevel() - cost);
			if (res >= 0) p.setFoodLevel(res);
			else {
				p.sendMessage(ChatColor.RED + "Вы истощены, применение заклинаний поглащает здоровье и накладывает негативные эффекты.");
				double newHealth = p.getHealth()+res;
				p.damage(0.1);
				p.setHealth(newHealth >= 0 ? newHealth : 0);
				p.setFoodLevel(0);
				if (newHealth <= 0) {
					p.sendMessage(ChatColor.RED + "Заклинание полностью истощило и убило Вас.");
				} else {
					PotionEffectType[] p0 = {PotionEffectType.POISON, PotionEffectType.WEAKNESS, PotionEffectType.SLOW},
										p1 = {PotionEffectType.CONFUSION, PotionEffectType.BLINDNESS},
										p2 = {PotionEffectType.WITHER};
					ArrayList<PotionEffectType> potions = new ArrayList<>();
					int level = -Math.round(res / 3);
					if (level > 4) level = 4;
					switch (level) {
					default:
					case 2:
						potions.add(randomChoice(p2));
					case 1:
						potions.add(randomChoice(p1));
					case 0:
						potions.add(randomChoice(p0));
					}
					for (PotionEffectType potion : potions) {
						new PotionEffect(potion, (3+level)*20, level).apply(p);
					}
				}
			}
		}
	}
	
	
	protected boolean isBoss(Entity ent) {
		switch (ent.getType()) {
		case ENDER_DRAGON:
		case WITHER:
		case GIANT:
			return true;
		default:
			return false;
		}
	}
	

}
