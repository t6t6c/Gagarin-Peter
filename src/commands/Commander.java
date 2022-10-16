package commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import enchanting.EnchantmentWrapper;
import entity.EntityController;
import entity.EntityCreator;
import entity.KolobokCreator;
import entity.KolobokDemonCreator;
import entity.KolobokWaterCreator;
import entity.ShyGuyCreator;
import gagarin.Accessory;
import gagarin.GagarinPeter;
import magic.MagicItem;
import magic.MagicWand;

public class Commander implements CommandExecutor, TabCompleter{

	
	private GagarinPeter plugin;
	private HashMap<String, CommandAction> actions = new HashMap<String, CommandAction>();
	private TipTree tips = new TipTree();

	public Commander(GagarinPeter plugin) {
		this.plugin = plugin;
		
		addStandartBranches();
		
		// Переключение дебага
		actions.put("debug", (p, args) -> {
			String val;
			boolean value;
			try {
				val = args.get(1);
				if (val == null) value = !MagicItem.debug;
				else value = Boolean.valueOf(val);
				MagicItem.debug = value;
				p.sendMessage(ChatColor.GOLD + "Режим дебага " + (value ? ChatColor.GREEN + "включён" : ChatColor.RED + "отключён"));
			} catch (ClassCastException exc) {
				p.sendMessage(ChatColor.RED + "Неприемлимое значение: \"" + args.get(1) + "\". Допустимо: true или false");
			}
			return true;
		});
		
		
		// Айтемы
		actions.put("item", (p, args) -> {
			if (args.get(1).equals("wand")) {
				String type = args.get(2, "fullmax");
				MagicWand wand = new MagicWand();
				switch (type) {
				case "full":
					for (EnchantmentWrapper ench : EnchantmentWrapper.getEnchants()) {
						wand.addUnsafeEnchantment(ench, 1);
					};
					break;
				case "fullmax":
					for (EnchantmentWrapper ench : EnchantmentWrapper.getEnchants()) {
						wand.addUnsafeEnchantment(ench, ench.getMaxLevel());
					}
				}
				MagicWand.updateWandLore(wand);
				MagicWand.createStandartBindKeys(wand);
				p.getInventory().addItem(wand);
			} else {
				String name = args.get(1);
				int level = args.get(2, 1, 1);
				for (EnchantmentWrapper ench : EnchantmentWrapper.getEnchants()) {
					if (ench.getKey().getKey().equalsIgnoreCase("gagarin." + name) || ench.getKey().getKey().equalsIgnoreCase(name)) {
						p.getInventory().addItem(MagicItem.createEnchantingBook(ench, level));
						return true;
					}
				}
				p.sendMessage(ChatColor.RED + "Не существует зачарования с ключом '" + name + "'");
			}
			return true;
		});
		
		
		// Мобы
		actions.put("mob", (p, args) -> {
			int amount = args.get(2, 1, 1);
			Class<? extends EntityCreator<?>> creator;
			switch (args.get(1)) {
			default:
				p.sendMessage(ChatColor.RED + "Некорректное название моба: " + args.get(1));
				return true;
			case "kolobok":
				creator = KolobokCreator.class;
			break;
			case "demon_kolobok":
				creator = KolobokDemonCreator.class;
			break;
			case "water_kolobok":
				creator = KolobokWaterCreator.class;
			break;
			case "enderman":
				creator = ShyGuyCreator.class;
			break;
			}
			for (int i = 0; i < amount; i++) {
				EntityController.findCreator(creator).spawnEntity(p.getLocation());
			}
			return true;
		});
		
		
		// Доп функции
		actions.put("accessory", (p, args) -> {
			// Таргет блок
			switch (args.get(1)) {
			case "target":
				String type = args.get(2, "block");
				String elType;
				String elClass;
				int maxDistance = args.get(3, 50, 50);
				float distance;
				Block b;
				if (maxDistance > 100) maxDistance = 100;
				if (type.equals("entity")) {
					double threshold;
					try {
						threshold = Double.valueOf(args.get(3, "2"));
					}
					catch (NumberFormatException exc) {
						threshold = 2;
					}
					Entity ent = Accessory.getTarget(p, p.getNearbyEntities(maxDistance, maxDistance, maxDistance), threshold);
					if (ent == null) {
						p.sendMessage("Not found");
						return true;
					}
					b = ent.getLocation().getBlock();
					elType = ent.getType().toString();
					elClass = ent.getClass().toString();
				} else {
					b = Accessory.getTargetBlock(p, maxDistance);
					elType = b.getType().toString();
					elClass = b.getClass().toString();
				}
				distance = Math.round(p.getLocation().distance(b.getLocation()));
				p.sendMessage(elClass + " " + elType + "   " + b.getX() + " " + b.getY() + " " + b.getZ() + ";   Distance: " + distance);
				p.getWorld().strikeLightningEffect(b.getLocation());
			break;
			case "head":
				String url = args.get(2, "https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63");
				p.getInventory().addItem(Accessory.getPlayerHead(url));
			break;
			default:
				return false;
			}
			return true;
		});
	}
	
	
	
	public TipTree getTree() {
		return tips;
	}
	
	
	
	private void addStandartBranches() {
		// Создаем дерево подсказок
		tips.addBranch(new ArgumentsCollection("debug"), "true", "false");
		tips.addBranch(new ArgumentsCollection("item", "wand"), "full fullmax empty".split(" "));
		ArrayList<String> enchs = new ArrayList<>();
		for (EnchantmentWrapper ench : EnchantmentWrapper.getEnchants()) {
			enchs.add(ench.getKey().getKey().substring(8));
		}
		tips.addBranch(new ArgumentsCollection("item"), enchs);
		tips.addBranch(new ArgumentsCollection("accessory", "target"), "block", "entity");
		tips.addBranch(new ArgumentsCollection("mob"), "kolobok", "demon_kolobok", "water_kolobok", "enderman");
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("gagarin") && sender instanceof Player) {
			Player p = (Player) sender;
			ArgumentsCollection col = new ArgumentsCollection(args);
			actions.getOrDefault(col.get(0), (player, arguments) -> {return false;}).run(p, col);
		}
		return true;
	}
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("gagarin") && sender instanceof Player) {
			ArrayList<String> list = new ArrayList<String>();
			for (String s : args) {
				list.add(s);
			}
			HashSet<String> res = tips.getTips(new ArgumentsCollection(list));
			if (res != null) return new ArrayList<String>(res);
		}
		return null;
	}


}
