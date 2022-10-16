package gagarin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import magic.MagicItem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;


public class Handler implements Listener {
	
	private GagarinPeter plugin;
	public Handler (GagarinPeter gagarin) {
		plugin = gagarin;
	}
	
	
	
	@EventHandler
	public void onChat (AsyncPlayerChatEvent e) {
		if (e.getPlayer().isOp()) {
			Player p = e.getPlayer();
			switch (e.getMessage().toLowerCase()) {
			case "chat":
				TextComponent text = new TextComponent("test");
				text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new net.md_5.bungee.api.chat.hover.content.Entity("Player", "2549489a-67db-303d-996f-80b4619e4c83", new TextComponent(p.getCustomName()))));
				p.spigot().sendMessage(text);
				break;
			case "selector":
				SelectorComponent sel = new SelectorComponent("@e[r=10]");
				sel.setSelector("@e[r=10]");
				p.spigot().sendMessage(sel);
				break;
			}
		}
	}
	
	
	
	@EventHandler
	public void onHChat (PlayerChatEvent e) {
		if (e.getMessage().equalsIgnoreCase("ender")) {
			Location loc = e.getPlayer().getLocation();
			EnderCrystal ent = (EnderCrystal) loc.getWorld().spawnEntity(loc, 
					EntityType.ENDER_CRYSTAL);
			ent.setShowingBottom(false);
			Player p = e.getPlayer();
			p.addPassenger(ent);
			ent.setCustomNameVisible(true);
			Runnable r = () -> {
				while (true) {
					double radius = 10;
					List<Entity> ents = p.getNearbyEntities(radius, radius, radius);
					Location l = ent.getLocation();
					l.setY(l.getY() - 0.05);
					Entity target = null;
					double min = radius * 3;
					for (Entity entity : ents) {
						if (entity.getType() != EntityType.ENDER_CRYSTAL && 
								entity.getType() != EntityType.DROPPED_ITEM) {
							double dist = p.getLocation().distance(entity.getLocation());
							if (dist < min) {
								min = dist;
								target = entity;
							}
						}
					}
					if (target != null) {
						l = target.getLocation();
						ent.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + target.getName());
					} else {
						ent.setCustomName(ChatColor.BLUE + "" + ChatColor.BOLD + "Никого нет рядом");
					}
					l.setY(l.getY() - 0.3);
					ent.setBeamTarget(l);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {}
				}
			};	
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.start();
		} else if (e.getMessage().equalsIgnoreCase("sas")) {
			ItemStack item = new ItemStack(Material.FIRE);
			e.getPlayer().getInventory().addItem(item);
		}
	}
	
	
	
	@EventHandler
	public void onExplosive(EntityExplodeEvent e) {
		//if (e.getEntity().getType() == EntityType.ENDER_CRYSTAL) e.setCancelled(true);
		
	}
	
	
	
		

}
