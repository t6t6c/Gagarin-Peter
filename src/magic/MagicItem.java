package magic;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import enchanting.EnchantmentWrapper;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import net.citizensnpcs.api.event.NPCDeathEvent;
import spells.ArrestoMomentum;
import spells.AvadaKedavra;
import spells.Bombardo;
import spells.CustomManaUsing;
import spells.FlyingSpell;
import spells.IgnoringProtect;
import spells.InvisibilitySpell;
import spells.Konfringo;
import spells.MagicFireSpell;
import spells.Morsmordre;
import spells.PartisTemporus;
import spells.ProjectileSpell;
import spells.Protego;
import spells.ProtegoDiabolica;
import spells.Spell;

public class MagicItem implements Listener{
	
	public static GagarinPeter plugin;
	public static RegionContainer regContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
	
	
	public static String itemID, itemName;
	public static List<String> itemLore;
	public static Material material;
	public static int slot;
	public static final ArrayList<Spell> LIST_OF_SPELLS = new ArrayList<Spell>();
	public static ArrayList<Inventory> list_inventory = new ArrayList<Inventory>();
	public static HashMap<Inventory, ItemStack> map_inventory_binds = new HashMap<Inventory, ItemStack>();
	public static HashMap<String, Spell> list_players = new HashMap<>();
	public static HashMap<Object, Boolean> list_kostil = new HashMap<>();
	public static HashMap<Player, ArrayList<ItemStack>> list_held_items = new HashMap<>();
	public static boolean debug = true;
	
	public static EnchantmentWrapper MAGICWAND_ENCH = new EnchantmentWrapper("gagarin.magic_wand", "magicwand", 1);
	public static EnchantmentWrapper MORSMORDRE_ENCH = new EnchantmentWrapper("gagarin.morsmordre", "Морсмордре", 1);
	public static EnchantmentWrapper ALARTE_ASCENDARE_ENCH = new EnchantmentWrapper("gagarin.alarte_ascendare", "Аларте Аскендаре", 3);
	public static EnchantmentWrapper ARRESTO_MOMENTUM_ENCH = new EnchantmentWrapper("gagarin.arresto_momentum", "Арресто Моментум", 3);
	public static EnchantmentWrapper KNOCKBACK_ENCH = new EnchantmentWrapper("gagarin.knockback", "Депульсо", 3);
	public static EnchantmentWrapper AVADA_KEDAVRA_ENCH = new EnchantmentWrapper("gagarin.avada_kedavra", "Авада Кедавра", 3);
	public static EnchantmentWrapper PROTEGO_ENCH = new EnchantmentWrapper("gagarin.protego", "Протего", 5);
	public static EnchantmentWrapper FLYING_SPELL_ENCH = new EnchantmentWrapper("gagarin.flying_spell", "Заклинание полета", 3);
	public static EnchantmentWrapper BOMBARDO_ENCH = new EnchantmentWrapper("gagarin.bombardo", "Бомбардо", 5);
	public static EnchantmentWrapper INVISIBILITY_SPELL_ENCH = new EnchantmentWrapper("gagarin.invisibility_spell", "Невидимость", 2);
	public static EnchantmentWrapper PROTEGO_DIABOLICA_ENCH = new EnchantmentWrapper("gagarin.protego_diabolica", "Протего Диаболика", 3);
	public static EnchantmentWrapper KONFRINGO_ENCH = new EnchantmentWrapper("gagarin.konfringo", "Конфринго", 3);
	public static EnchantmentWrapper PARTIS_TEMPORUS_ENCH = new EnchantmentWrapper("gagarin.partis_temporus", "Партис Темпорус", 3);
	public static EnchantmentWrapper ALOHOMORA_ENCH = new EnchantmentWrapper("gagarin.alohomora", "Алохомора", 3);
	
	private static String installedSpellMessage, useSpellMessage, spellReloadingMessage, leftClickOnly, rightClickOnly, nameInventory;
	private static String enableMagicFormMessage = "§6Магическая форма §aАктивирована",
							disableMagicFormMessage = "§6Магическая форма §cДеактивирована",
							nameInventoryBind = "Настройка биндов";
	
	
	
	public static void print(Object ... msg) {
		String res = "";
		for (Object o : msg) {
			res += " " + String.valueOf(o);
		}
		plugin.getLogger().info(res.substring(1));
	}
	
	
	public static NamespacedKey createNamespace(String name) {
		return new NamespacedKey(plugin, name);
	}
	
	
	public static void registerEnchantment(Enchantment ench) {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			Enchantment.registerEnchantment(ench);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static void startSetting (GagarinPeter gagarin) {
		plugin = gagarin;
		
		String id = plugin.getConfig().getString("linking_to_lore"),
				mat = plugin.getConfig().getString("linking_to_material"),
				slot = plugin.getConfig().getString("linking_to_slot");
		itemName = plugin.getConfig().getString("magic_item.name");
		itemLore = plugin.getConfig().getStringList("magic_item.lore");
		
		installedSpellMessage = Loader.getOnPlayerInstalledSpellMessage();
		useSpellMessage = Loader.getOnPlayerUseSpellMessage();
		spellReloadingMessage = Loader.getOnSpellReloadingMessage();
		leftClickOnly = Loader.getOnSpellLeftClickOnlyMessage();
		rightClickOnly = Loader.getOnSpellRightClickOnlyMessage();
		nameInventory = Loader.getNameInventoryOfSpells();
		
		itemID = id.equals("false") ? null : id;
		MagicItem.slot = slot.equals("false") ? -1 : Integer.valueOf(slot);
		material = mat.equals("false") ? null : Material.valueOf(mat);
		
		LIST_OF_SPELLS.clear();
		LIST_OF_SPELLS.add(Loader.createProtego(plugin));
		LIST_OF_SPELLS.add(Loader.createArrestoMomentum(plugin));
		LIST_OF_SPELLS.add(Loader.createInvisibilitySpell(plugin));
		LIST_OF_SPELLS.add(Loader.createAvadaKedavra(plugin));
		LIST_OF_SPELLS.add(Loader.createBombardo(plugin));
		LIST_OF_SPELLS.add(Loader.createKnockback(plugin));
		LIST_OF_SPELLS.add(Loader.createAlarteAscendare(plugin));
		LIST_OF_SPELLS.add(Loader.createFlyingSpell(plugin));
		LIST_OF_SPELLS.add(Loader.createProtegoDiabolica(plugin));
		
		LIST_OF_SPELLS.add(Loader.createMorsmordre(plugin));
		LIST_OF_SPELLS.add(Loader.createKonfringo(plugin));
		LIST_OF_SPELLS.add(Loader.createPartisTemporus(plugin));
		LIST_OF_SPELLS.add(Loader.createAlohomora(plugin));
		
		
		for (EnchantmentWrapper ench : EnchantmentWrapper.getEnchants()) {
			registerEnchantment(ench);
		}
	}
	
	
	
	public static void addKostil(Object obj) {
		list_kostil.put(obj, true);
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
			list_kostil.remove(obj);
		});
		t.setDaemon(true);
		t.start();
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static <T> T findSpell (Class<T> cls) throws IllegalArgumentException {
		for (Spell s : LIST_OF_SPELLS) {
			if (s.getClass().equals(cls)) return (T) s;
		}
		throw new IllegalArgumentException ("Don't find this spell.");
	}
	
	
	
	public static boolean isMagicItem (Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (slot == -1 || p.getInventory().getHeldItemSlot() == slot) {
			return isMagicItem(item);
		}
		return false;
	}
	
	
	
	public static boolean isMagicItem (ItemStack item) {
		return MagicWand.isMagicWand(item);
	}
	
	
	
	public static ItemStack createItem () {
		return new MagicWand();
	}
	
	
	
	public static ArrayList<Spell> getAvailableSpells(ItemStack wand) {
		ArrayList<Spell> spells = new ArrayList<Spell>();
		for (Spell s : LIST_OF_SPELLS) {
			if (s.canUseSpell(wand)) spells.add(s);
		}
		return spells;
	}
	
	
	public static Inventory createInventoryOfSpells(ItemStack wand) {
		Inventory inv = Bukkit.createInventory(null, 27, nameInventory);
		for (Spell s : getAvailableSpells(wand)) {
			inv.addItem(s.createItemOfSpell());
		}
		inv.setItem(18, wand);
		ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a§lНастроить бинды");
		ArrayList<String> lore = new ArrayList<>();
		lore.add("Открыть меню привязки заклинаний");
		lore.add("палочки к конкретным слотам хотбара");
		lore.add("при активации магической формы.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(26, item);
		return inv;
	}
	
	public static ItemStack createFreeSlotItem() {
		ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§c§lСвободный слот");
		item.setItemMeta(meta);
		return item;
	}
	
	public static Inventory createInventoryOfBind(ItemStack wand) {
		Inventory inv = Bukkit.createInventory(null, 54, nameInventoryBind);
		for (Spell s : getAvailableSpells(wand)) {
			inv.addItem(s.createItemOfSpell());
		}
		ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemMeta meta = accept.getItemMeta();
		meta.setDisplayName("§6§lСохранить");
		ArrayList<String> lore = new ArrayList<>();
		lore.add("Нажмите, чтобы сохранить настройки");
		lore.add("биндов и закрыть окно.");
		meta.setLore(lore);
		accept.setItemMeta(meta);
		for (int i = 45; i < 54; i++)
			inv.setItem(i, accept);
		ItemStack yellowPane = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		meta = yellowPane.getItemMeta();
		meta.setDisplayName(" ");
		yellowPane.setItemMeta(meta);
		for (int i = 27; i < 36; i++) {
			inv.setItem(i, yellowPane);
		}
		ItemStack tip = new ItemStack(Material.PAPER);
		meta = tip.getItemMeta();
		lore.clear();
		lore.add("Сверху располагаются доступные заклинания");
		lore.add("Снизу - список заклинаний, закреплённых за");
		lore.add("соответствующими слотами");
		lore.add("Чтобы закрепить заклинания за слотом");
		lore.add("нажмите на строку снизу, а");
		
		inv.setItem(31, new ItemStack(Material.AIR));
		ItemStack items[] = MagicWand.createMagicFormItems(wand);
		ItemStack w = new ItemStack(wand);
		meta = w.getItemMeta();
		meta.setDisplayName("§c§lСлот под палочку");
		meta.setLore(null);
		w.setItemMeta(meta);
		items[0] = w;
		for (int i = 36; i < 45; i++) {
			if (items[i-36].getType() == Material.BARRIER) {
				items[i-36] = createFreeSlotItem();
			}
			inv.setItem(i, items[i-36]);
		}
		return inv;
	}
	
	
	
	public static void nextSpell (Player p, ItemStack item) throws IllegalArgumentException {		
		ArrayList<Spell> spells = getAvailableSpells(item);
		int number = spells.indexOf(getSpell(p));
		if (++number == spells.size()) number = 0;
		setSpell(p, spells.get(number));
	}
	
	
	
	public static void setSpell (Player p, Spell s) {
		list_players.put(p.getName(), s);
		p.sendMessage(installedSpellMessage.replaceAll("%spell", s.getColor() + s.getName()));
	}
	
	
	public static Spell getSpell (Player p) throws IllegalArgumentException {
		return list_players.getOrDefault(p.getName(), LIST_OF_SPELLS.get(0));
	}
	
	
	
	public static void sayUseSpellMessage (Player p, Spell spell) {
		String msg = useSpellMessage.replaceAll("%spell", spell.getColor() + spell.getName());
		p.chat(msg);
	}
	
	
	// Как броня влияет на цену спелла
	public static float getModifierArmorOnCostSpell(Player p) {
		float per = 1;
		for (ItemStack armor : p.getInventory().getArmorContents()) {
			if (armor == null) continue;
			switch (armor.getType()) {
			case IRON_HELMET:
			case IRON_CHESTPLATE:
			case IRON_LEGGINGS:
			case IRON_BOOTS:
				per += 0.1;
				break;
			case DIAMOND_HELMET:
			case DIAMOND_CHESTPLATE:
			case DIAMOND_LEGGINGS:
			case DIAMOND_BOOTS:
				per += 0.15;
				break;
			case NETHERITE_HELMET:
			case NETHERITE_CHESTPLATE:
			case NETHERITE_LEGGINGS:
			case NETHERITE_BOOTS:
				per += 0.2;
				break;
			case GOLDEN_HELMET:
			case GOLDEN_CHESTPLATE:
			case GOLDEN_LEGGINGS:
			case GOLDEN_BOOTS:
				per -= 0.1;
				break;
			default:
				break;
			}
		}
		return per;
	}
	
	
	// Как броня влияет на отталкивающие заклинания
	public static float getModifierArmorOnStrengthSpell(Entity ent) {
		float per = 1;
		if (ent.getType() == EntityType.PLAYER) {
			Player p = (Player) ent;
			for (ItemStack armor : p.getInventory().getArmorContents()) {
				if (armor == null) continue;
				switch (armor.getType()) {
				case IRON_HELMET:
				case IRON_CHESTPLATE:
				case IRON_LEGGINGS:
				case IRON_BOOTS:
					per -= 0.1;
					break;
				case DIAMOND_HELMET:
				case DIAMOND_CHESTPLATE:
				case DIAMOND_LEGGINGS:
				case DIAMOND_BOOTS:
					per -= 0.1;
					break;
				case NETHERITE_HELMET:
				case NETHERITE_CHESTPLATE:
				case NETHERITE_LEGGINGS:
				case NETHERITE_BOOTS:
					per -= 0.15;
					break;
				case GOLDEN_HELMET:
				case GOLDEN_CHESTPLATE:
				case GOLDEN_LEGGINGS:
				case GOLDEN_BOOTS:
					per += 0.05;
					break;
				default:
					break;
				}
			}
		}
		return per;
	}
	
	
	public static void useSpell (Player p, Spell spell, boolean isRight, @Nullable ItemStack item) {
		if (item != null && !spell.canUseSpell(item)) {
			p.sendMessage(ChatColor.RED + "Вы не можете использовать заклинание " + spell.getColor() + spell.getName());
			return;
		}
		if (containsFlagValue(p.getLocation(), plugin.useSpellFlag, StateFlag.State.DENY) && !(spell instanceof IgnoringProtect)) {
			p.sendMessage("§cЭто место находится под сильными чарами, которые не дают использовать Вам заклинания.");
			return;
		}
		if (spell == findSpell(Konfringo.class)) {
			Konfringo s = (Konfringo) spell;
			if (s.changeHellFireLocation(p, isRight)) return;
		}
		if (MagicScheduler.isCooldown(p, spell)) {
			p.sendMessage(spellReloadingMessage.replaceAll("%reload", String.valueOf(MagicScheduler.getCooldown(p, spell))));
			return;
		}
		
		int level = spell.getLevelSpell(item);
		
		
		boolean isUse = false;
		try {
			isUse = isRight ? spell.useRight(p, spell.getLevelSpell(item)) : spell.useLeft(p, spell.getLevelSpell(item));
			if (isUse) {
				if (!(spell instanceof CustomManaUsing)) {
					Spell.useMana(p, spell.getCost(level, isRight));
				}
				MagicScheduler.addCooldown(p, spell);
				if (spell.isVerbal()) sayUseSpellMessage(p, spell);
			}
		} catch (IllegalStateException exc) {
			p.sendMessage(isRight ? leftClickOnly : rightClickOnly);	
			return;
		}
	}
	
	
	
	//Метод, отражающий заклинания обратно в кастера
	public static <T extends ProjectileSpell> boolean onProtegoBlock (ProjectileHitEvent e, Class<T> cls) {
		if (!findSpell(cls).isProjectileEntity(e.getEntity())) return false;
		Entity ent = e.getHitEntity();
		if (ent == null) return false;
		try {
			if (Spell.isProtegoEntity(ent)) {
				int lvl = Protego.getProtegoLevel(ent);
				T spell = findSpell(cls);
				if (spell.getClass() == AvadaKedavra.class && lvl < 3) return false;
				if (lvl > 1 && e.getEntity().getShooter() instanceof Entity) {
					spell.useOnEntity((Entity) e.getEntity().getShooter(), 
							e.getHitEntity(), 1);
				}
				ent.sendMessage(Protego.getBlockMessage(spell));
				e.getEntity().remove();
				return true;
			} else findSpell(cls);
		} catch (IllegalArgumentException | IllegalStateException exc) {
			
		}
		return false;
	}
	
	
	
	
	// Создаём книгу чар
	public static ItemStack createEnchantingBook(EnchantmentWrapper ench, int level) {
		MagicItem.print(ench, level);
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(ench, level, true);
		item.setItemMeta(meta);
		MagicWand.updateBookLore(item);
		return item;
	}
	
	
	
	// Возвращает список приватов для точки
	public static Set<ProtectedRegion> getRegions(Location loc) {
		RegionManager regions = regContainer.get(BukkitAdapter.adapt(loc.getWorld()));
		return regions.getApplicableRegions(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).getRegions();
	}
	
	
	
	// Проверяем, содержится ли флаг в локации
	public static boolean containsFlagValue(Location loc, Flag<?> flag, StateFlag.State state) {
		boolean f = false;
		for (ProtectedRegion reg : getRegions(loc)) {
			StateFlag.State s = (State) reg.getFlag(flag);
			if (s == state) return true;
			else if (s != null) f = true;
		}
		return f ? false : flag.getDefault() == state;
	}
	
	public static boolean containsFlagValue(Location loc, Flag<?> flag, boolean state) {
		return containsFlagValue(loc, flag, state ? StateFlag.State.ALLOW : StateFlag.State.DENY);
	}
	
	public static boolean containsFlagValue(Location loc) {
		return containsFlagValue(loc, plugin.useSpellFlag, true);
	}
	
	
	
	// Зачаровывает итем, соответствующий выбранному спеллу игрока
	public static void selectCorrentSpellItem(Player p, ItemStack items[]) {
		Spell s = getSpell(p);
		for (ItemStack item : items) {
			if (item != null && item.hasItemMeta()) {
				if (s.isItemOfSpell(item)) {
					item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, -1);
				} else {
					item.removeEnchantment(Enchantment.ARROW_FIRE);
				}
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
		}
	}
	
	
	public static boolean isUsingMagicForm(Player p) {
		return list_held_items.containsKey(p);
	}
	
	
	public static void enableMagicForm(Player p, ItemStack wand) {
		if (isUsingMagicForm(p)) return;
		if (p.getInventory().getContents()[0] != null) {
			// Если палочка располагается не в первом слоте, то перемещаем её в первый слот
			int k = p.getInventory().getHeldItemSlot();
			ItemStack[] conts = p.getInventory().getContents();
			conts[k] = p.getInventory().getContents()[0];
			conts[0] = null;
			p.getInventory().setContents(conts);
		}
		p.getInventory().setHeldItemSlot(0);
		ArrayList<ItemStack> items = new ArrayList<>();
		for (int i = 0; i <= 7; i++) {
			ItemStack item = p.getInventory().getContents()[i+1];
			items.add(item);
			if (item != null) p.getInventory().removeItem(item);
		}
		list_held_items.put(p, items);
		ItemStack inv[] = MagicWand.createMagicFormItems(wand);
		selectCorrentSpellItem(p, inv);
		ItemStack contents[] = p.getInventory().getContents();
		for (int i = 0; i < inv.length; i++) {
			contents[i] = inv[i];
		}
		p.getInventory().setContents(contents);
		p.sendMessage(enableMagicFormMessage);
	}
	
	public static void disableMagicForm(Player p) {
		if (!isUsingMagicForm(p)) return;
		ItemStack[] conts = p.getInventory().getContents();
		for (int i = 1; i <= list_held_items.get(p).size(); i++) {
			conts[i] = list_held_items.get(p).get(i-1);
		}
		p.getInventory().setContents(conts);
		list_held_items.remove(p);
		p.sendMessage(disableMagicFormMessage);
		if (findSpell(Konfringo.class).stopUsingHellFire(p)) {
			p.sendMessage(ChatColor.GOLD + "Заклинание адского пламени закончило свое действие, т.к. Вы вышли из магической формы");
		}
	}
	
	// Переключает магический режим у игрока
	public static void toggleMagicForm(Player p, ItemStack wand) {
		if (!list_held_items.containsKey(p)) enableMagicForm(p, wand);
		else disableMagicForm(p);
	}
	
	
	
	@EventHandler
	//Инвентарь спеллов
	public static void onInventorySpellClick (InventoryClickEvent e) {
		if (!list_inventory.contains(e.getClickedInventory())) return;
		if (e.getWhoClicked().getType() != EntityType.PLAYER) return;
		for (Spell s : LIST_OF_SPELLS) {
			if (s.isItemOfSpell(e.getCurrentItem())) {
				setSpell((Player) e.getWhoClicked(), s);
				break;
			}
		}
		e.setCancelled(true);
		if (MagicWand.isMagicWand(e.getCurrentItem())) return;
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.COMMAND_BLOCK) {
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				Inventory inv = createInventoryOfBind(e.getInventory().getItem(18));
				map_inventory_binds.put(inv, e.getInventory().getItem(18));
				e.getWhoClicked().openInventory(inv);
			}, 1);
			return;
		}
		Bukkit.getScheduler().runTaskLater(plugin, () -> e.getWhoClicked().closeInventory(), 1);
	}
	
	@EventHandler
	// Инвентарь биндов
	public static void onInventoryBindClick (InventoryClickEvent e) {
		if (!map_inventory_binds.containsKey(e.getClickedInventory())) return;
		if (e.getWhoClicked().getType() != EntityType.PLAYER) return;
		e.setCancelled(true);
		Inventory inv = e.getClickedInventory();
		int selItemSlot = 31;
		ItemStack current = e.getCurrentItem(),
				selItem = inv.getItem(selItemSlot);
		if (e.getRawSlot() > 35 && e.getRawSlot() < 45) {
			if (e.getRawSlot() == 36) {
				e.getWhoClicked().sendMessage("§cНельзя изменить слот под палочку.");
			} else {
				e.setCurrentItem(selItem != null && selItem.getType() != Material.AIR ? selItem : createFreeSlotItem());
				inv.setItem(selItemSlot, null);
			}
			return;
		} else if (e.getRawSlot() < 27) {
			inv.setItem(selItemSlot, current);
		} else if (current != null && current.getType() == Material.LIME_STAINED_GLASS_PANE) {
			ItemStack items[] = new ItemStack[9];
			for (int i = 37; i < 45; i++) {
				items[i-36] = e.getInventory().getItem(i);
			}
			map_inventory_binds.get(e.getClickedInventory());
			MagicWand.bindKeys(e.getWhoClicked().getInventory().getItemInMainHand(), items);
			e.getWhoClicked().sendMessage("§aБинды успешно сохранены в палочке");
			Bukkit.getScheduler().runTaskLater(plugin, () -> e.getWhoClicked().closeInventory(), 1);
		}
	}
	
	
	
	@EventHandler
	public static void onInteract (PlayerInteractEvent e) {
		if (!isMagicItem(e.getItem())) return;
		e.setCancelled(true);
		Boolean d = list_kostil.get(e.getPlayer().getName());
		if (d != null && d.equals(true)) return;
		boolean rigth = e.getAction() == Action.RIGHT_CLICK_AIR ||
				e.getAction() == Action.RIGHT_CLICK_BLOCK;
		useSpell(e.getPlayer(), getSpell(e.getPlayer()), rigth, e.getItem());
		
	}

	
	
	
	@EventHandler
	public static void onDrop (PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if ((p.getOpenInventory().getType() != InventoryType.CRAFTING && 
				p.getOpenInventory().getType() != InventoryType.CREATIVE) || 
				(slot != -1 && p.getInventory().getHeldItemSlot() != slot) || 
				!isMagicItem(e.getItemDrop().getItemStack())) return;
		e.setCancelled(true);
		ItemStack wand = e.getItemDrop().getItemStack();
		if (p.isSneaking()) toggleMagicForm(p, wand);
		else {
			Inventory inv = createInventoryOfSpells(e.getItemDrop().getItemStack());
			list_inventory.add(inv);
			selectCorrentSpellItem(p, inv.getContents());
			p.openInventory(inv);
		}
		// Костыль)
		addKostil(p.getName());
	}
	
	
	
	@EventHandler
	public static void onLeftClick (EntityDamageByEntityEvent e) {
		if (e.getCause() != DamageCause.ENTITY_ATTACK || 
				!(e.getDamager().getType() == EntityType.PLAYER)) return;
		Player p = (Player) e.getDamager();
		if (!isMagicItem(p)) return;
		e.setCancelled(true);
		useSpell(p, getSpell(p), false, p.getInventory().getItemInMainHand());
	}
	
	
	
	@EventHandler
	public static void onPlayerMove (PlayerMoveEvent e) {
		Location lF = e.getFrom(),
				lT = e.getTo();
		//Проверяем, являлось ли движение обычным поворотом
		if (lF.getX() == lT.getX() && lF.getY() == lT.getY() && lF.getZ() == lT.getZ()) return;
		
		if (!MagicScheduler.hasEffect(e.getPlayer(), ArrestoMomentum.EFFECT_NAME) &&
				!MagicScheduler.hasEffect(e.getPlayer(), ArrestoMomentum.SELF_EFFECT_NAME)) return;
		int dur = Math.max(MagicScheduler.getDuration(e.getPlayer(), ArrestoMomentum.EFFECT_NAME),
				MagicScheduler.getDuration(e.getPlayer(), ArrestoMomentum.SELF_EFFECT_NAME));
		//Защита от спама сообщениями.
		if (!MagicScheduler.hasEffect(e.getPlayer(), "arresto_spam_lock")) {
			if (MagicScheduler.hasEffect(e.getPlayer(), ArrestoMomentum.EFFECT_NAME)) {
				e.getPlayer().sendMessage(findSpell(ArrestoMomentum.class).getOnPlayerMove(dur));
			} else {
				e.getPlayer().sendMessage(findSpell(ArrestoMomentum.class).getOnPlayerMoveSelf(dur));
			}
			MagicScheduler.addNewRow(e.getPlayer(), "arresto_spam_lock", new EffectParams(1));
		}
		e.setCancelled(true);
		lF.setPitch(lT.getPitch());
		lF.setYaw(lT.getYaw());
	}
	
	
	
	@EventHandler
	public static void onSpellChat (AsyncPlayerChatEvent e) {
		if (isMagicItem(e.getPlayer())) {
			for (Spell spell : LIST_OF_SPELLS) {
				if (spell.getCommand() != null && 
						e.getMessage().equalsIgnoreCase(spell.getCommand())) {
					Bukkit.getScheduler().runTask(plugin, () -> useSpell(e.getPlayer(), spell, true, e.getPlayer().getInventory().getItemInMainHand()));
					e.setCancelled(true);
				}
			}
		}
	}
	
	
	
	@EventHandler
	public static void onFireworkDetonate (FireworkExplodeEvent e) {
		if (e.getEntity().getCustomName() == null || !e.getEntity().getCustomName().equals(Morsmordre.NAME_PROJ)
				|| !containsFlagValue(e.getEntity().getLocation())) return;
		Location loc = e.getEntity().getLocation();
		loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 1);
	}
	
	
	
	@EventHandler
	//Отслеживание попадания авадой
	public static void onAvadaHit (ProjectileHitEvent e) {
		if (!findSpell(AvadaKedavra.class).isProjectileEntity(e.getEntity())) return;
		if (e.getHitBlock() != null || !containsFlagValue(e.getEntity().getLocation())) {
			e.getEntity().remove();
		} else onProtegoBlock(e, AvadaKedavra.class);
	}
	
	
	
	@EventHandler
	//Отключение взрыва блоков от заклинаний
	public static void onExplosive (EntityExplodeEvent e) {
		AvadaKedavra avada = findSpell(AvadaKedavra.class);
		Bombardo bombardo = findSpell(Bombardo.class);
		if ((!avada.isCanDestroyBlocks() && avada.isProjectileEntity(e.getEntity()))
				|| !bombardo.isCanDestroyBlocks() && bombardo.isProjectileEntity(e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	
	
	@EventHandler
	//Отслеживание дамага черепа
	public static void onWitherDamage (EntityDamageByEntityEvent e) {
		AvadaKedavra s = findSpell(AvadaKedavra.class);
		if (findSpell(AvadaKedavra.class).isProjectileEntity(e.getDamager())) {
			if (e.getCause() == DamageCause.ENTITY_EXPLOSION || !containsFlagValue(e.getEntity().getLocation())
					|| containsFlagValue(e.getEntity().getLocation(), Flags.PVP, false)) {
				e.setCancelled(true);
				return;
			}
			e.setDamage(s.getDamage());
			if (Spell.isProtegoEntity(e.getEntity())) e.setCancelled(true);
			else {
				switch (e.getEntity().getType()) {
				case ENDER_DRAGON:
				case WITHER:
				case GIANT:
					e.setCancelled(true);
					break;
				default:
					break;
				}
			}
		}
	}
	
	
	
	@EventHandler
	//Отслеживание дамага бомбарды
	public static void onBombardaDamage (EntityDamageByEntityEvent e) {
		Bombardo spell = findSpell(Bombardo.class);
		if (!spell.isProjectileEntity(e.getDamager())) return;
		if (!containsFlagValue(e.getEntity().getLocation())
				|| containsFlagValue(e.getEntity().getLocation(), Flags.PVP, false)) {
			e.setCancelled(true);
			return;
		}
		try {
			e.setDamage(spell.getDamage(spell.getLevelByProj((Projectile) e.getDamager())));
		} catch (ClassCastException exc) {}
		if (e.getEntity().isInvulnerable()) e.setCancelled(true);
	}
	
	
	
	@EventHandler
	//Отслеживание попадания бобмардой
	public static void onBombardoHit (ProjectileHitEvent e) {
		Bombardo b = findSpell(Bombardo.class);
		if (!b.isProjectileEntity(e.getEntity()) || list_kostil.containsKey(e.getEntity())
				|| !containsFlagValue(e.getEntity().getLocation())) return;
		if (!onProtegoBlock(e, Bombardo.class)) {
			print(b.getLevelByProj(e.getEntity()));
			if (b.getLevelByProj(e.getEntity()) == 666) supernova(e.getEntity().getLocation(), e.getEntity());
			else {
				e.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, 
						e.getEntity().getLocation(), 5);
			}
			addKostil(e.getEntity());
		}
	}
	
	
	
	@EventHandler
	//Отключаем агр мобов на невидимого игрока
	public static void onMobAgrInvisibilityPlayer (EntityTargetLivingEntityEvent e) {
		if (e.getTarget() != null && e.getTarget().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getTarget();
			if (MagicScheduler.hasEffect(p, InvisibilitySpell.EFFECT_NAME)) e.setCancelled(true);
		}
		
	}
	
	
	
	public static void onMagicFire (EntityDamageByEntityEvent e, MagicFireSpell spell) {
		if (spell.isCloud(e.getDamager())) {
			AreaEffectCloud protego = (AreaEffectCloud) e.getDamager();
			e.setCancelled(true);
			if (spell.isDamageEntity(protego, e.getEntity())) {
				try {
					int level = spell.getLevelByCloud(protego);
					LivingEntity ent = (LivingEntity) e.getEntity();
					if (!spell.isIgnoreFireResistance(level) && ent.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return;
					ent.setFireTicks(100);
					ent.damage(spell.getDamage(level, ent), protego);
					ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, spell.getSloveDuration(), level));
					// Высасывание маны
					Random r = new Random();
					int mana;
					if (ent.getType() == EntityType.PLAYER) {
						Player p = (Player) ent;
						mana = r.nextInt(1);
						p.setFoodLevel(p.getFoodLevel() - mana);
					} else mana = r.nextInt(-1, 2);
					if (mana > 0 && protego.getSource() instanceof Player) {
						Player p = (Player) protego.getSource();
						p.setFoodLevel(p.getFoodLevel() + 1);
					}
				} catch (ClassCastException exc) {}
			}
		}
	}
	
	@EventHandler
	//Отслеживание урона от диаблоика
	public static void onProtegoDiabolica (EntityDamageByEntityEvent e) {
		ProtegoDiabolica spell = findSpell(ProtegoDiabolica.class);
		onMagicFire(e, spell);
	}
	
	@EventHandler
	//Отслеживание урона от партис темпорус
	public static void onPartisTemporus (EntityDamageByEntityEvent e) {
		PartisTemporus spell = findSpell(PartisTemporus.class);
		onMagicFire(e, spell);
	}
	
	@EventHandler
	//Отслеживание урона от конфринго
	public static void onKonfringoFire (EntityDamageByEntityEvent e) {
		Konfringo spell = findSpell(Konfringo.class);
		onMagicFire(e, spell.magicFire);
	}
	
	
	
	@EventHandler
	//Отслеживание попадания конфринго
	public static void onKonfringoHit (ProjectileHitEvent e) {
		Konfringo kof = findSpell(Konfringo.class);
		if (!kof.isProjectileEntity(e.getEntity())) return;
		if (!containsFlagValue(e.getEntity().getLocation())) {
			e.setCancelled(true);
			return;
		}
		if (!onProtegoBlock(e, Konfringo.class)) {
			if (!e.getEntity().isValid()) return;
			e.getEntity().getWorld().spawnParticle(Particle.FLAME, 
					e.getEntity().getLocation(), kof.getParticles());
			if (e.getHitEntity() != null) {
				try {
					LivingEntity ent = (LivingEntity) e.getHitEntity();
					ent.damage(kof.getDamage(), e.getEntity());
				} catch (ClassCastException exc) {}
			}
			double radius = kof.getRadius();
			int k = 0;
			for (Entity ent : e.getEntity().getNearbyEntities(radius, radius, radius)) {
				if (ent != e.getEntity().getShooter() && !Protego.isProtegoEntity(ent) && containsFlagValue(ent.getLocation())
						&& (ent.getType() == EntityType.PLAYER || ent instanceof Monster)) {
					ent.setFireTicks(kof.getFireTicks());
					if (kof.getLevelByProj(e.getEntity()) > 1 && k++ <= 10) {
						try {
							kof.createMagicFire((LivingEntity) e.getEntity().getShooter(), ent);
						} catch (ClassCastException exc) {}
					}
				}
			}
			e.getEntity().remove();
		}
	}
	
	
	
	@EventHandler
	//Отслеживание урона от шара конфринго
	public static void onKonfringoHitEntity (EntityDamageByEntityEvent e) {
		if (!findSpell(Konfringo.class).isProjectileEntity(e.getDamager())) return;
		e.setCancelled(true);
	}
	
	
	
	// Спавнит супернову в данной локации
	public static void supernova(Location loc, Entity source) {
		print(1);
		Bukkit.getScheduler().runTask(plugin, e -> {
			for (int i = 2; i < 20; i+=3) {
				ArrayList<Location> locs = Accessory.getLocationsInRadious(loc, i, 4);
				for (Location location : locs) {
					if (!containsFlagValue(location)) continue;
					location.getWorld().createExplosion(location, (int)( 6 - (float)i/7), false, false, source);
					location.getWorld().spawnParticle(Particle.FLAME, 
							location, 10);
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException exc) {}
				
			}
		});
	}
	
	
	
	// Отслеживаем открытие точильни
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onGrindStoneInv(InventoryClickEvent event) {

	    if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.GRINDSTONE &&
	    		event.getSlotType() == SlotType.CRAFTING) {
	    	if (MagicWand.isMagicWand(event.getCursor())) {
	    		ItemStack item = new ItemStack(event.getCursor());
	    		event.setCursor(event.getCurrentItem());
	    		event.setCurrentItem(item);
	    		MagicWand.clearEnchanting(item);
	    		event.getClickedInventory().setItem(2, item);
	    		event.setCancelled(true);
	    	}
	    }
	}
	
	
	@EventHandler
	public void onAnvil(PrepareAnvilEvent e) {
		try {;
			e.setResult( MagicWand.createResultAnvil(e.getInventory().getItem(0), e.getInventory().getItem(1)) );
	
			if (e.getResult() != null) {
				e.getInventory().setRepairCost(15);
			}
		} catch (IllegalArgumentException exc) {}
	}
	
	
	// Игроки не перестают летать под заклинанием
	@EventHandler
	public void onGlideToggle(EntityToggleGlideEvent e) {
		if (!e.isGliding()) {
			Player p = (Player) e.getEntity();
			if (findSpell(FlyingSpell.class).isFly(p)) e.setCancelled(true);
		}
	}
	
	
	// На шифт прекращает полёт
	@EventHandler
	public void onSneakToggleFly(PlayerToggleSneakEvent e) {
		if (!e.isSneaking()) {
			FlyingSpell s = findSpell(FlyingSpell.class);
			if (s.isFly(e.getPlayer())) s.stopFly(e.getPlayer());
		}
	}
	
	
	// На шифт снимаем эффект арресто
	@EventHandler
	public void onSneakToggleArresto(PlayerToggleSneakEvent e) {
		if (e.isSneaking() && MagicScheduler.hasEffect(e.getPlayer(), ArrestoMomentum.SELF_EFFECT_NAME)) {
			MagicScheduler.removeEffect(e.getPlayer(), ArrestoMomentum.SELF_EFFECT_NAME);
			e.getPlayer().setFallDistance(0);
		}
	}
	
	
	
	@EventHandler
	public void onDeath(NPCDeathEvent e) {
		if (findSpell(InvisibilitySpell.class).getMobs().contains(e.getNPC().getEntity())) {
			((PlayerDeathEvent) e.getEvent()).setDeathMessage("");
			@SuppressWarnings("unused")
			Player p = (Player) e.getNPC().getEntity();
			e.getNPC().destroy();
		}
	}
	
	
	@EventHandler
	public void onSwapSlot(PlayerItemHeldEvent e) {
		if (!list_held_items.containsKey(e.getPlayer())) return;
		e.setCancelled(true);
		ItemStack items[] = e.getPlayer().getInventory().getContents();
		ItemStack item = items[e.getNewSlot()];
		for (Spell s : LIST_OF_SPELLS) {
			if (s.isItemOfSpell(item)) {
				setSpell(e.getPlayer(), s);
				selectCorrentSpellItem(e.getPlayer(), items);
				break;
			};
		}
	}
	
	@EventHandler
	public void onInvCLick(InventoryClickEvent e) {
		if (e.getWhoClicked().getType() != EntityType.PLAYER) return;
		Inventory clickInv = e.getClickedInventory();
		Inventory openInv = e.getWhoClicked().getOpenInventory().getTopInventory();
		if (!list_inventory.contains(openInv) && !map_inventory_binds.containsKey(openInv)) return;
		if (!openInv.equals(clickInv)) e.setCancelled(true);
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (!list_inventory.contains(e.getInventory()) && !map_inventory_binds.containsKey(e.getInventory())) return;
		e.getPlayer().setItemOnCursor(null);
		list_inventory.remove(e.getInventory());
		map_inventory_binds.remove(e.getInventory());
	}
	
	
	// Отслеживаем урон, который должно заблокировать протего
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (!ent.isValid()) return;
		if (!Protego.isProtegoEntity(ent)) return;
		ArrayList<DamageCause> blockDamageCause = new ArrayList<>();
		Collections.addAll(blockDamageCause, DamageCause.BLOCK_EXPLOSION, DamageCause.CONTACT, DamageCause.DRAGON_BREATH, DamageCause.ENTITY_ATTACK,
				DamageCause.ENTITY_EXPLOSION, DamageCause.ENTITY_SWEEP_ATTACK, DamageCause.FALLING_BLOCK, DamageCause.FIRE, DamageCause.FIRE_TICK,
				DamageCause.HOT_FLOOR, DamageCause.LAVA, DamageCause.LIGHTNING, DamageCause.MAGIC, DamageCause.PROJECTILE, DamageCause.THORNS);
		if (blockDamageCause.contains(e.getCause())) e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onProjectileProtegoBlock(ProjectileHitEvent e) {
		if (Protego.isProtegoEntity(e.getHitEntity())) e.setCancelled(true);
	}
	
	
	
	
	@EventHandler
	public void onDeathPlayer(PlayerDeathEvent e) {
		if (!list_held_items.containsKey(e.getEntity())) return;
		if (!e.getKeepInventory()) {
			ArrayList<ItemStack> items = new ArrayList<>();
			for (ItemStack item : e.getDrops()) {
				for (Spell s : LIST_OF_SPELLS) {
					if (s.isItemOfSpell(item)) {
						items.add(item);
						break;
					}
				}
			}
			e.getDrops().removeAll(items);
			e.getDrops().addAll(list_held_items.get(e.getEntity()));
		}
		disableMagicForm(e.getEntity());
	}
	
	@EventHandler
	public void onPlayerInvOpen(InventoryOpenEvent e) {
		if (e.getPlayer().getType() == EntityType.PLAYER) disableMagicForm((Player) e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		disableMagicForm(e.getPlayer());
	}
	
}
