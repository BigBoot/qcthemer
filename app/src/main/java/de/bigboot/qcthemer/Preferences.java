package de.bigboot.qcthemer;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marco Kirchner
 */
public class Preferences {
    private SharedPreferences sharedPrefs;
    private static final String FIRST_START = "first_start";
    private static final String CLOCKS = "clocks";
    private static final String ACTIVE_CLOCK = "active_clock";

    public Preferences (Context context) {
        sharedPrefs = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_WORLD_READABLE);
    }

    public boolean isFirstStart() {
        return sharedPrefs.getBoolean(FIRST_START, true);
    }

    public Set<Clock> getClocks() {
        Set<Clock> clocks = new HashSet<Clock>();
        for(String s : sharedPrefs.getStringSet(CLOCKS, new HashSet<String>())) {
            Clock c = null;
            try {
                c = Clock.fromXML(s);
                if(c != null)
                    clocks.add(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return clocks;
    }

    public Clock getActiveClock() {
        try {
            return Clock.fromXML(sharedPrefs.getString(ACTIVE_CLOCK, ""));
        } catch (IOException e) {
            return null;
        }
    }

    public void addClock(Clock clock) {
        Set<Clock> clocks = getClocks();
        clocks.add(clock);

        Set<String> set = new HashSet<String>(clocks.size());
        for(Clock c : clocks) {
            set.add(c.toXML());
        }
        sharedPrefs.edit().putStringSet(CLOCKS, set).apply();
    }

    public void removeClock(Clock clock) {
        Set<Clock> clocks = getClocks();
        clocks.remove(clock);

        Set<String> set = new HashSet<String>(clocks.size());
        for(Clock c : clocks) {
            set.add(c.toXML());
        }
        sharedPrefs.edit().putStringSet(CLOCKS, set).apply();

        if(clock != null && clock.equals(getActiveClock()))
            setActiveClock(null);
    }

    public void setActiveClock(Clock activeClock) {
        if(activeClock == null)
            sharedPrefs.edit().putString(ACTIVE_CLOCK, null).apply();
        else
            sharedPrefs.edit().putString(ACTIVE_CLOCK, activeClock.toXML()).apply();
    }
}