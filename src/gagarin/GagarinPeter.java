package gagarin;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import commands.Commander;
import entity.EntityController;
import magic.Loader;
import magic.MagicItem;
import magic.MagicScheduler;
import magic.MagicWand;

public class GagarinPeter extends JavaPlugin {
	
	
	public StateFlag useSpellFlag = new StateFlag("using-spells", true);
	
	public void saveConfig() {
		try {
			getConfig().save(getFileConfig());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public File getFileConfig () {
		return new File(getDataFolder() + File.separator + "config.yml");
	}

	
	
	@Override
	public void onEnable() {
		File config = getFileConfig();
		if (!config.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
			getLogger().info("Create standart config.");
		}
		Accessory.plugin = this;
		MagicScheduler.plugin = this;
		MagicWand.plugin = this;
		Loader.config = getConfig();
		MagicItem.startSetting(this);
		EntityController.startSettings(this);
		Bukkit.getPluginManager().registerEvents(new Handler(this), this);
		Bukkit.getPluginManager().registerEvents(new MagicItem(), this);
		Bukkit.getPluginManager().registerEvents(new EntityController(), this);
		Commander com = new Commander(this);
		getCommand("gagarin").setExecutor(com);
		getCommand("gagarin").setTabCompleter(com);
		craft();
	}
	
	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			MagicItem.disableMagicForm(p);
		}
	}
	
	
	@Override
	public void onLoad() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
	    try {
	        registry.register(useSpellFlag);
	    } catch (FlagConflictException e) {
	        // some other plugin registered a flag by the same name already.
	        // you can use the existing flag, but this may cause conflicts - be sure to check type
	        Flag<?> existing = registry.get(useSpellFlag.getName());
	        if (existing instanceof StateFlag) {
	        	useSpellFlag = (StateFlag) existing;
	        }
	    }
	}
	
	
	public void craft() {
		ItemStack item = MagicItem.createItem();
		ShapedRecipe s = new ShapedRecipe(item);
		s.shape(new String[] {"EDL", "DSD", "IHG"});
		s.setIngredient('E', Material.EMERALD_BLOCK);
		s.setIngredient('D', Material.DIAMOND);
		s.setIngredient('L', Material.LAPIS_BLOCK);
		s.setIngredient('S', Material.BLAZE_ROD);
		s.setIngredient('I', Material.IRON_BLOCK);
		s.setIngredient('H', Material.ZOMBIE_HEAD);
		s.setIngredient('G', Material.GOLD_BLOCK);
		Bukkit.getServer().addRecipe(s);
	}
	
	
	
	
	
	
}
