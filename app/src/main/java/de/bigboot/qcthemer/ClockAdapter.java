package de.bigboot.qcthemer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

/**
 * Created by Marco Kirchner
 */
@EBean
public class ClockAdapter extends FragmentStatePagerAdapter {
    @RootContext
    protected Context context;
    private ArrayList<Clock> clocks;
    private Preferences prefs;


    public ClockAdapter(Context context) {
        super(((Activity) context).getFragmentManager());
        prefs = new Preferences(context);
        clocks = new ArrayList<Clock>();
        for(Clock clock : prefs.getClocks()) {
            clocks.add(clock);
        }
    }

    @Override
    public Fragment getItem(int i) {
        return ClockFragment_.builder().previewImage(context.getFilesDir() + "/" + clocks.get(i).getId() + "/preview.png").build();
    }

    @Override
    public int getCount() {
        return clocks.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void addClock (Clock clock) {
        prefs.addClock(clock);
        clocks.add(clock);
        notifyDataSetChanged();
    }

    public void deleteClock (Clock clock) {
        prefs.removeClock(clock);
        clocks.remove(clock);
        notifyDataSetChanged();
    }

    public Clock getClock(int i) {
        return clocks.get(i);
    }
}
