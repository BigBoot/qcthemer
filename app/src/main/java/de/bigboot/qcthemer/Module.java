package de.bigboot.qcthemer;

import android.content.res.XResources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by Marco Kirchner.
 */
public class Module implements IXposedHookInitPackageResources {

	@Override
	public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resParam) throws Throwable {
		if (!resParam.packageName.equals("com.lge.clock")) return;

        final String moduleName = QuickcirclemodSettings.class.getPackage().getName();

        final Clock c = getActiveClock();

        if(c == null)
            return;

        for(final String file : c.getFiles()) {
            try {
                resParam.res.setReplacement("com.lge.clock", "raw", removeExtension(file), new XResources.DrawableLoader() {
                    @Override
                    public Drawable newDrawable(XResources xResources, int i) throws Throwable {
                        BitmapDrawable drawable = new BitmapDrawable("/data/data/" + moduleName + "/files/" + c.getId() + "/" + file);
                        drawable.setTargetDensity(640);
                        return drawable;
                    }
                });

            } catch (Throwable ex) {
            }
        }
	}

    public static Clock getActiveClock() {
        final String moduleName = QuickcirclemodSettings.class.getPackage().getName();

        XSharedPreferences prefs = new XSharedPreferences(moduleName);
        prefs.makeWorldReadable();

        String value = prefs.getString("active_clock", "");

        if(value.isEmpty())
            return null;

        try {
            return Clock.fromXML(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String removeExtension(String s) {

        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }
}
