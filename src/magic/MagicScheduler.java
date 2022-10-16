package magic;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import gagarin.GagarinPeter;
import spells.Spell;


public abstract class MagicScheduler {
	
	public static final HashMap<Entity, HashMap<String, EffectParams>> cool = new HashMap<>();
	public static GagarinPeter plugin;
	
	
	
	//Создаёт новую запись в списке кулдауна. Является основным методом записи.
	public static void addNewRow (Entity ent, String effectName, @Nonnull EffectParams eff) {
		if (!cool.containsKey(ent)) cool.put(ent, new HashMap<>());			
		cool.get(ent).put(effectName, eff);
		Runnable r = () -> {
			try {
				HashMap<String, EffectParams> effsList = cool.get(ent);
				int k = eff.getDuration();
				while (k > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
					k = eff.tick();
				}
				effsList.remove(effectName);
			} catch (NullPointerException exc) {}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
	}
	
	public static void addNewRow (Entity ent, String effectName, int duration) {
		addNewRow(ent, effectName, new EffectParams(duration));
	}
	
	
	//Выдаёт запись
	public static int getDuration (Entity ent, String effectName) {
		if (!cool.containsKey(ent)) return 0;
		if (effectName.endsWith("#")) {
			int max = 0;
			for (int i = 1; i < 10; i++) {
				int res = getDuration(ent, effectName+i);
				if (res > max) max = res;
			}
			return max;
		} else if (!cool.get(ent).containsKey(effectName)) return 0;
		else return cool.get(ent).get(effectName).getDuration();
	}
	
	public static int getLevel(Entity ent, String effectName) throws IllegalArgumentException {
		if (!hasEffect(ent, effectName)) throw new IllegalArgumentException("Entity hasn't efffect \"" + effectName + "\".");
		int level = 0;
		for (int i = 1; i < 10; i++) {
			if (hasEffect(ent, effectName+"#"+i)) level = i;
		}
		return level;
	}
	
	
	public static boolean hasEffect (Entity ent, String effectName) {
		return getDuration(ent, effectName) > 0;
	}
	
	
	
	public static void addCooldown (Player p, Spell spell) {
		EffectParams param = new EffectParams(!MagicItem.debug ? spell.getCooldown() : 1);
		addNewRow(p, spell.getName(), param);
	}
		
	public static int getCooldown (Player p, Spell spell) {
		return getDuration(p, spell.getName());
	}
	
	public static boolean isCooldown (Player p, Spell spell) {
		return hasEffect(p, spell.getName());
	}
	
	public static void removeEffect (Entity ent, String effect) {
		try {
			cool.get(ent).get(effect).kill();
			cool.get(ent).remove(effect);
		} catch (NullPointerException exc) {}
	}

}
