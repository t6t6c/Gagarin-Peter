package magic;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import gagarin.GagarinPeter;
import spells.AlarteAscendare;
import spells.Alohomora;
import spells.ArrestoMomentum;
import spells.AvadaKedavra;
import spells.Bombardo;
import spells.FlyingSpell;
import spells.InvisibilitySpell;
import spells.Knockback;
import spells.Konfringo;
import spells.MagicFireSpell;
import spells.Morsmordre;
import spells.PartisTemporus;
import spells.Protego;
import spells.ProtegoDiabolica;
import spells.Spell;

public abstract class Loader {
	
	public static FileConfiguration config;
	public static String SPELLS_PATH = "spells.";
	private static String sayUseMessage;
	
	
	
	// Функция, возвращающая значение из конфига.
	// Если значение равняется false, то возвращает значение аргумента onFalse
	@SuppressWarnings("unchecked")
	private static <T> T getPseudoType (String path, T onFalse) {
		Object obj = config.get(path);
		if (obj.equals(false)) return onFalse;
		else return (T) obj;
	}
	
	// Привязка по слоту
	public static int getLinkingToSlot () {
		return getPseudoType("linking_to_slot", -1);
	}
	
	// Привязка по материалу
	public static Material getLinkingToMaterial () {
		try {
			return Material.valueOf(getPseudoType("linking_to_material", ""));
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}
	
	// Привязка по лору
	public static String getLinkingToLore () {
		return getPseudoType("linking_to_lore", null);
	}
	
	
	
	// Имя итема
	public static String getItemName () {
		return config.getString("magic_item.name");
	}
	
	
	// Описание предмета
	public static List<String> getItemLore () {
		return config.getStringList("magic_item.lore");
	}
	
	
	
	// Функция, возвращающая путь к заклинанию
	public static <T extends Spell> String getPathToSpell (Class<T> cls) {
		return SPELLS_PATH + cls.getSimpleName() + ".";
	}
	
	
	// Функция, возвращающая имя заклинания
	public static <T extends Spell> String getSkillName (Class<T> cls) {
		return config.getString(getPathToSpell(cls) + "name");
	}
	
	
	// Функция, возвращающая итем заклинания
	public static <T extends Spell> Material getSkillItem (Class<T> cls) {
		return Material.valueOf(config.getString(getPathToSpell(cls) + "item"));
	}
	
	
	// Функция, возвращающая цвет заклинания
	public static <T extends Spell> ChatColor getSkillColor (Class<T> cls) {
		return ChatColor.valueOf(config.getString(getPathToSpell(cls) + "color"));
	}
	
	
	// Функция, возвращающая перезарядку заклинания
	public static <T extends Spell> int getSkillCooldown (Class<T> cls) {
		return config.getInt(getPathToSpell(cls) + "cooldown");
	}
	
	
	// Функция, возвращающая вербальноть заклинания
	public static <T extends Spell> boolean getSkillVerbal (Class<T> cls) {
		return config.getBoolean(getPathToSpell(cls) + "verbal");
	}
	
	
	// Функция, возвращающая команду заклинания
	public static <T extends Spell> String getSkillCommand (Class<T> cls) {
		return config.getString(getPathToSpell(cls) + "command");
	}
	
	
	// Функция, возвращающая дальность применения заклинания
	public static <T extends Spell> double getSkillRange (Class<T> cls) {
		return config.getDouble(getPathToSpell(cls) + "range");
	}
	
	
	// Функция, возвращающая описание заклинания
	public static <T extends Spell> List<String> getSkillLore (Class<T> cls) {
		return config.getStringList(getPathToSpell(cls) + "lore");
	}
	
	
	// Функция, возвращающая длительность эффекта заклинания
	public static <T extends Spell> int getSkillDuration (Class<T> cls) {
		return config.getInt(getPathToSpell(cls) + "duration");
	}
	
	
	// Функция, возвращающая длительность эффекта заклинания
	public static <T extends Spell> String getSkillProjectileName (Class<T> cls) {
		return config.getString(getPathToSpell(cls) + "projectile_name");
	}
	
	
	// Функция, возвращающая длительность эффекта заклинания
	public static <T extends Spell> boolean getSkillCanDestroyBlock (Class<T> cls) {
		return config.getBoolean(getPathToSpell(cls) + "destroy_blocks");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда не найдена цель заклинания
	public static String getNotFoundTargetMessage () {
		return config.getString("on_not_found_target_of_spell");
	}
	
	
	// Загружаем из конфига строку, которая пишется в конце любого заклинания и показывает его кулдаун. 
	public static String getSpellReloadString() {
		return config.getString("reload_string");
	}
	
	
	// Загружаем из конфига строку, которая пишется в конце любого заклинания и показывает его кулдаун. 
	public static String getSpellCommandString() {
		return config.getString("command_string");
	}
	
	
	
	// Функция, возвращающая стандартные свойства заклинания
	private static <T extends Spell> HashMap<String, Object> getSkillStandartProperty (
			Class<T> cls) {
		HashMap<String, Object> map = new HashMap<>(6);
		map.put("name", getSkillName(cls));
		map.put("mat", getSkillItem(cls));
		map.put("color", getSkillColor(cls));
		map.put("cooldown", getSkillCooldown(cls));
		map.put("verbal", getSkillVerbal(cls));
		map.put("lore", getSkillLore(cls));
		return map;
	}
	
	
	// Функция, возвращающая стандартные свойства магического огня
	private static HashMap<String, Object> getMagicFireStandartProperty() {
		HashMap<String, Object> map = new HashMap<>(4);
		String path = getPathToSpell(MagicFireSpell.class);
		map.put("damage", config.getDouble(path + "damage"));
		map.put("sloveLevel", config.getInt(path + "slove_lvl"));
		map.put("sloveDuration", config.getInt(path + "slove_dur"));
		map.put("ignoreFireResistance", config.getBoolean(path + "ignore_fire_resistance"));
		return map;
	}
	
	
	
	// Загружаем из конфига Морсмордре
	@SuppressWarnings("unchecked")
	public static Morsmordre createMorsmordre (GagarinPeter plugin) {
		HashMap<String, Object> map = getSkillStandartProperty(Morsmordre.class);
		return new Morsmordre(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), (List<String>) map.get("lore"), MagicItem.MORSMORDRE_ENCH);
	}
	
	
	// Загружаем из конфига Аларте Аскендаре
	@SuppressWarnings("unchecked")
	public static AlarteAscendare createAlarteAscendare (GagarinPeter plugin) {
		Class<AlarteAscendare> cls = AlarteAscendare.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls),
				onNotFoundTarget = getNotFoundTargetMessage();
		double range = config.getDouble(path + "range");
		return new AlarteAscendare(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, range, onNotFoundTarget, (List<String>) map.get("lore"), MagicItem.ALARTE_ASCENDARE_ENCH);
	}
	
	
	// Загружаем из конфига Арресто Моментум
	@SuppressWarnings("unchecked")
	public static ArrestoMomentum createArrestoMomentum (GagarinPeter plugin) {
		Class<ArrestoMomentum> cls = ArrestoMomentum.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls),
				onNotFoundTarget = getNotFoundTargetMessage(),
				onPlayerMove = config.getString(path + "on_move"),
				onPlayerMoveSelf = config.getString(path + "on_move_self");
		double range = config.getDouble(path + "range");
		return new ArrestoMomentum(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, range, onNotFoundTarget, onPlayerMove, onPlayerMoveSelf, (List<String>) map.get("lore"), MagicItem.ARRESTO_MOMENTUM_ENCH);
	}
	
	
	// Загружаем из конфига Депульсо
	@SuppressWarnings("unchecked")
	public static Knockback createKnockback (GagarinPeter plugin) {
		Class<Knockback> cls = Knockback.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls),
				onNotFoundTarget = getNotFoundTargetMessage();
		double range = config.getDouble(path + "range");
		return new Knockback(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), command, range, onNotFoundTarget, (List<String>) map.get("lore"), MagicItem.KNOCKBACK_ENCH);
	}
	
	
	// Загружаем из конфига Авада Кедавра
	@SuppressWarnings("unchecked")
	public static AvadaKedavra createAvadaKedavra (GagarinPeter plugin) {
		Class<AvadaKedavra> cls = AvadaKedavra.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls);
		boolean destroy = config.getBoolean(path + "destroy_blocks");
		double damage = config.getDouble(path + "damage");
		String project = config.getString(path + "projectile_name");
		return new AvadaKedavra(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), command, destroy, damage, project, (List<String>) map.get("lore"), MagicItem.AVADA_KEDAVRA_ENCH);
	}
	
	
	
	// Загружаем из конфига Протего
	@SuppressWarnings("unchecked")
	public static Protego createProtego (GagarinPeter plugin) {
		Class<Protego> cls = Protego.class; 
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls),
				onNotFoundTarget = getNotFoundTargetMessage();
		double range = config.getDouble(path + "range");
		int duration = config.getInt(path + "duration");
		String blockMes = config.getString(path + "block_message");
		return new Protego(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, range, duration, blockMes, onNotFoundTarget, (List<String>) map.get("lore"), MagicItem.PROTEGO_ENCH);
	}
	
	
	
	// Загружаем из конфига Спелл полёта
	@SuppressWarnings("unchecked")
	public static FlyingSpell createFlyingSpell (GagarinPeter plugin) {
		Class<FlyingSpell> cls = FlyingSpell.class; 
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls);
		double speed = config.getDouble(path + "speed");
		int duration = config.getInt(path + "duration");
		int reduceCooldown = config.getInt(path + "reduce_cooldown");
		return new FlyingSpell(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, duration, speed, reduceCooldown, (List<String>) map.get("lore"), MagicItem.FLYING_SPELL_ENCH);
	}
	
	
	
	// Загружаем из конфига Бомбардо
	@SuppressWarnings("unchecked")
	public static Bombardo createBombardo (GagarinPeter plugin) {
		Class<Bombardo> cls = Bombardo.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls);
		boolean destroy = config.getBoolean(path + "destroy_blocks");
		float yield = (float) config.getDouble(path + "yield");
		String project = config.getString(path + "projectile_name");
		return new Bombardo(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), command, destroy, yield, project, (List<String>) map.get("lore"), MagicItem.BOMBARDO_ENCH);
	}
	
	
	
	// Загружаем из конфига Скилл обмана
	@SuppressWarnings("unchecked")
	public static InvisibilitySpell createInvisibilitySpell (GagarinPeter plugin) {
		Class<InvisibilitySpell> cls = InvisibilitySpell.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls);
		int duration = config.getInt(path + "duration");
		return new InvisibilitySpell(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, duration, (List<String>) map.get("lore"), MagicItem.INVISIBILITY_SPELL_ENCH);
	}
	
	
	
	// Загружаем из конфига Протего Диаболика
	@SuppressWarnings("unchecked")
	public static ProtegoDiabolica createProtegoDiabolica (GagarinPeter plugin) {
		Class<ProtegoDiabolica> cls = ProtegoDiabolica.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls),
				magicMap = getMagicFireStandartProperty();
		String command = getSkillCommand(cls);
		int fireDuration = config.getInt(path + "duration");
		double circleRadius = config.getDouble(path + "radius"),
				interval = config.getDouble(path + "interval");
		float thickness = (float) config.getDouble(path + "thickness");
		return new ProtegoDiabolica(plugin, (double) magicMap.get("damage"), (int) magicMap.get("sloveLevel"), (int) magicMap.get("sloveDuration"),
				(boolean) magicMap.get("ignoreFireResistance"), (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, thickness, fireDuration, circleRadius, interval, (List<String>) map.get("lore"), MagicItem.PROTEGO_DIABOLICA_ENCH);
	}
	
	
	
	// Загружаем из конфига Конфринго
	@SuppressWarnings("unchecked")
	public static Konfringo createKonfringo (GagarinPeter plugin) {
		Class<Konfringo> cls = Konfringo.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls);
		String command = getSkillCommand(cls);
		double damage = config.getDouble(path + "damage"),
				radius = config.getDouble(path + "radius");
		int fireTicks = config.getInt(path + "fire_ticks"),
				particles = config.getInt(path + "particles");
		String project = config.getString(path + "projectile_name");
		return new Konfringo(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), command, damage, radius, fireTicks, particles, project, (List<String>) map.get("lore"), MagicItem.KONFRINGO_ENCH);
	}
	
	
	
	// Загружаем из конфига Партис Темпорус
	@SuppressWarnings("unchecked")
	public static PartisTemporus createPartisTemporus (GagarinPeter plugin) {
		Class<PartisTemporus> cls = PartisTemporus.class;
		String path = getPathToSpell(cls);
		HashMap<String, Object> map = getSkillStandartProperty(cls),
				magicMap = getMagicFireStandartProperty();
		String command = getSkillCommand(cls);
		int fireDuration = config.getInt(path + "duration");
		double length = config.getDouble(path + "length"),
				width = config.getDouble(path + "width"),
				interval = config.getDouble(path + "interval");
		float thickness = (float) config.getDouble(path + "thickness");
		return new PartisTemporus(plugin, (double) magicMap.get("damage"), (int) magicMap.get("sloveLevel"), (int) magicMap.get("sloveDuration"),
				(boolean) magicMap.get("ignoreFireResistance"), (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"), (int) map.get("cooldown"),
				(boolean) map.get("verbal"), command, thickness, fireDuration, length, width, interval, (List<String>) map.get("lore"), MagicItem.PARTIS_TEMPORUS_ENCH);
	}
	
	
	
	// Загружаем из конфига Алохомора
	@SuppressWarnings("unchecked")
	public static Alohomora createAlohomora (GagarinPeter plugin) {
		HashMap<String, Object> map = getSkillStandartProperty(Alohomora.class);
		return new Alohomora(plugin, (String) map.get("name"), (Material) map.get("mat"), (ChatColor) map.get("color"),
				(int) map.get("cooldown"), (boolean) map.get("verbal"), (List<String>) map.get("lore"), MagicItem.ALOHOMORA_ENCH);
	}
	
	
	
	
	// Загружаем из конфига названия инвенторя выбора заклинаний
	public static String getNameInventoryOfSpells() {
		return config.getString("inventory_of_spells.name");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда игрок устанавливает новую способность
	public static String getOnPlayerInstalledSpellMessage() {
		return config.getString("on_player_installed_spell_message");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда игрок устанавливает новую способность
	public static String getOnPlayerUseSpellMessage() {
		return config.getString("on_player_use_spell_message");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда способность находится на перезарядке
	public static String getOnSpellReloadingMessage() {
		return config.getString("on_spell_reloading_message");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда игрок пытается применить способность, которая применяется только ЛКМ
	public static String getOnSpellLeftClickOnlyMessage() {
		return config.getString("on_spell_left_click_only");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда игрок пытается применить способность, которая применяется только ПКМ
	public static String getOnSpellRightClickOnlyMessage() {
		return config.getString("on_spell_right_click_only");
	}
	
	
	// Загружаем из конфига сообщение, возникающее, когда игрок пытается применить способность, которая применяется только ПКМ
	public static String getOnSpellUseOnPlayerMessage() {
		if (sayUseMessage == null) sayUseMessage = config.getString("on_spell_was_cast_on_player");
		return sayUseMessage;
	}

	
	
	
}
