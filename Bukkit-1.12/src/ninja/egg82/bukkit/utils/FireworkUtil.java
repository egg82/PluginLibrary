package ninja.egg82.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkUtil {
    // vars

    // constructor
    public FireworkUtil() {

    }

    // public
    public static void createFirework(Location loc, int power, boolean silent, FireworkEffect... effects) {
        if (!Bukkit.isPrimaryThread()) {
            TaskUtil.runSync(new Runnable() {
                public void run() {
                    createFirework(loc, power, silent, effects);
                }
            });
            return;
        }

        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(effects);
        meta.setPower(power);
        firework.setFireworkMeta(meta);
        firework.setSilent(silent);

        TaskUtil.runSync(new Runnable() {
            public void run() {
                firework.detonate();
            }
        }, 10L * power);
    }

    public static void instantFirework(Location loc, boolean silent, FireworkEffect... effects) {
        if (!Bukkit.isPrimaryThread()) {
            TaskUtil.runSync(new Runnable() {
                public void run() {
                    instantFirework(loc, silent, effects);
                }
            });
            return;
        }

        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(effects);
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);

        TaskUtil.runSync(new Runnable() {
            public void run() {
                if (!silent) {
                    firework.playEffect(EntityEffect.FIREWORK_EXPLODE);
                }
                firework.remove();
            }
        }, 2L);
    }

    // private

}
