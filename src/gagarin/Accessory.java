package gagarin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

public abstract class Accessory {
	
	public static GagarinPeter plugin;
	public static Random random = new Random();
	
	
	//Проверка на наличие ентити в списки игнора слепоты
	public static boolean checkOnBlindnessIgnore (Entity ent) {
		if (ent instanceof Player) {
			Player p = (Player) ent;
			String name = p.getName();
			if (p.getInventory().getHeldItemSlot() == 0 && 
					plugin.getConfig().getStringList("blindness_ignore_list").
					contains(name)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	public static <T extends Entity> T getTarget(Player p,
	           List<T> entities) {
	    return getTarget(p, entities, 2);   
	}
	
	
	
	public static <T extends Entity> T getTarget(Player p,
	           List<T> entities, double threshold) {
		if (p == null)
            return null;
        T target = null;
        for (T other : entities) {
        	double threshold_1 = ((other.getHeight() < 1 && other.getWidth() < 1) || 
        			other instanceof Player) ? threshold+1 : threshold;
        	
            Vector n = other.getLocation().toVector()
                    .subtract(p.getLocation().toVector());
            if (p.getLocation().getDirection().normalize().crossProduct(n)
                    .lengthSquared() < threshold_1
                    && n.normalize().dot(
                            p.getLocation().getDirection().normalize()) >= 0) {
                if (target == null
                        || target.getLocation().distanceSquared(
                                p.getLocation()) > other.getLocation()
                                .distanceSquared(p.getLocation()))
                    target = other;
            }
        }
        //Проверяет, стоит ли между двумя ентитями блок
        if (target != null) {
        	Block block = getTargetBlock(p, 50);
        	if (block.getType() != Material.AIR) {
        		double r1 = block.getLocation().distance(p.getLocation());
        		double r2 = target.getLocation().distance(p.getLocation());
        		if (r1 < r2) target = null;
        	}
        }
        
        return target;
	}
	
	
	
	//Игнорирует непрочные блоки
	public static Block getTargetBlock (Player p, int maxDistance) {
		HashSet<Material> set = new HashSet<>();
    	for (Material m : Material.values()) {
    		if (m.isBlock() && !m.isSolid()) {
    			set.add(m);
    		}
    	}
    	Block block = p.getTargetBlock(set, maxDistance);
    	return block;
	}
	
	
	
	//Дать локации в окружности с указанным радиусом
	public static ArrayList<Location> getLocationsInRadious (Location loc, int radious, 
			double spawnRate) {
		double x0 = loc.getX(), y0 = loc.getY(), z0 = loc.getZ();
		ArrayList<Location> locations = new ArrayList<>();
		World world = loc.getWorld();
		for (double x = -radious; x <= radious+spawnRate; x+=spawnRate) {
			double sqrZ = radious*radious - x*x;
			double z1 = Math.sqrt(sqrZ),
					z2 = -z1;
			Location loc1 = new Location(world, x+x0, y0, z1+z0),
					loc2 = loc1.clone();
			loc2.setZ(z2+z0);
			locations.add(loc1);
			locations.add(loc2);
		}
		return locations;
	}
	
	
	
	// Дать локации в шаре с указанным радиусом
	public static ArrayList<Location> getLocationsInKonus (Location loc, int radious, 
			double spawnRate) {
		double y0 = loc.getY();
		World world = loc.getWorld();
		ArrayList<Location> locations = new ArrayList<>();
		for (double y = -radious; y < radious; y += spawnRate) {
			int rad = (int) Math.round(radious - Math.abs(y));
			Location l = new Location(world, loc.getX(), y+y0, loc.getZ());
			locations.addAll(getLocationsInRadious(l, rad, spawnRate));
		}
		return locations;
	}
	
	
	
	public static ArrayList<Location> getLocationsInSphere(Location loc, int radious,
			double step) {
		World world = loc.getWorld();
		ArrayList<Location> locations = new ArrayList<>();
		for (double i = 0; i <= Math.PI; i += Math.PI / step) {
		   double radius = Math.sin(i)*radious;
		   double y = Math.cos(i)*radious;
		   for (double a = 0; a < Math.PI * 2; a+= Math.PI / step) {
		      double x = Math.cos(a) * radius;
		      double z = Math.sin(a) * radius;
		      Location l = new Location(world, x, y, z);
		      locations.add(l.add(loc));
		   }
		}
		return locations;
	}
	
	
	
	//Дать локации, составляющие 2 линии, коллиниарные вектору и равноудалённые от него
	public static ArrayList<Location> getLocationsInPath (Location loc, double direction, 
			double width, double step) {
		ArrayList<Location> locations = new ArrayList<>();
		World world = loc.getWorld();
		//Находим главную линию
		Vector v = loc.getDirection().normalize().multiply(direction);
		double mainX = v.getX(),
				mainZ = v.getZ();
		//Находим точку смещения
		double dz, dx;
		try {
			dz = width / Math.sqrt((mainZ*mainZ)/(mainX*mainX) + 1);
			dx = -(mainZ*dz / mainX);
		} catch (ArithmeticException exc) {
			dz = 0;
			dx = width;
		}
		Location leftLoc = new Location(world, 
						loc.getX()+dx, loc.getY(), loc.getZ()+dz),
				rightLoc = new Location(world,
						loc.getX()-dx, loc.getY(), loc.getZ()-dz);
		leftLoc.setDirection(v);
		rightLoc.setDirection(v);
		
		locations.addAll(getLocationInLine(leftLoc, direction, step));
		locations.addAll(getLocationInLine(rightLoc, direction, step));
		
		return locations;
	}
	
	
	
	// Даёт локации в виде линии указанной длины.
	public static ArrayList<Location> getLocationInLine (Location loc, double length, double step) {
		ArrayList<Location> locations = new ArrayList<>();
		Vector v = loc.getDirection().normalize().multiply(length);
		for (double x = 0; x <= Math.abs(v.getX()); x += Math.abs(v.getX()/length * step)) {
			double newX = v.getX() < 0 ? -x : x;
			double z = newX*v.getZ()/v.getX();
			locations.add(new Location(loc.getWorld(), newX + loc.getX(), loc.getY(), 
					z + loc.getZ()));
		}
		return locations;
	}
	
	
	
	
	@SafeVarargs
	public static <T> T randomChoice(T ... arr) {
		
		if (arr.length > 0) {
			return arr[random.nextInt(arr.length)];
		}
		return null;
	}
	
	
	
	public static PlayerProfile getProfile(String url) {
		UUID RANDOM_UUID = UUID.fromString("92864446-51c5-4c3b-9039-517c9927d1b4");
	    PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID); // Get a new player profile
	    PlayerTextures textures = profile.getTextures();
	    URL urlObject;
	    try {
	        urlObject = new URL("https://textures.minecraft.net/texture/"+url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
	    } catch (MalformedURLException exception) {
	        throw new RuntimeException("Invalid URL", exception);
	    }
	    textures.setSkin(urlObject);
	    return profile;
	}
	
	public static ItemStack getPlayerHead(String url) {
	    PlayerProfile profile = getProfile(url);
	    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta meta = (SkullMeta) head.getItemMeta();
	    meta.setOwnerProfile(profile); // Set the owning player of the head to the player profile
	    head.setItemMeta(meta);
	    return head;
	}
	
}
