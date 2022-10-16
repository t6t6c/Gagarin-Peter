package magic;

import org.bukkit.Bukkit;

public class EffectParams {
	
	
	private int duration;
	private Runnable onEnd, onTick;
	private boolean useTickOnEnd;
	
	public EffectParams(int dur, Runnable onEnd, Runnable onTick, boolean useTickOnEnd) {
		duration = dur;
		this.onEnd = onEnd;
		this.onTick = onTick;
		this.useTickOnEnd = useTickOnEnd;
	}
	public EffectParams(int dur, Runnable onEnd) {
		this(dur, onEnd, null, false);
	}
	public EffectParams(int dur) {
		this(dur, null);
	}
	
	public int tick() {
		if (onTick != null && (duration > 0 || useTickOnEnd)) Bukkit.getScheduler().runTask(MagicItem.plugin, onTick);
		if (onEnd != null && duration == 0) Bukkit.getScheduler().runTask(MagicItem.plugin, onEnd);
		return duration--;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void kill() {
		duration = -1;
	}
	
}