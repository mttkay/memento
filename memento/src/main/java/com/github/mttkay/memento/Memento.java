package com.github.mttkay.memento;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class Memento {

    private static final String LOG_TAG = Memento.class.getSimpleName();

    private static final boolean USES_SUPPORT_FRAGMENTS;

    static {
        boolean supportFragmentsAvailable = false;
        try {
            Class.forName("android.support.v4.app.FragmentActivity");
            supportFragmentsAvailable = true;
        } catch (ClassNotFoundException e) {
        }
        USES_SUPPORT_FRAGMENTS = supportFragmentsAvailable;
    }

    public static void retain(Activity activity) {
        final String fragmentTag = getMementoFragmentTag(activity);
        log("Obtaining " + fragmentTag);

        if (USES_SUPPORT_FRAGMENTS && activity instanceof FragmentActivity) {
            retainSupportV4((FragmentActivity) activity, fragmentTag);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            retainNative(activity, fragmentTag);
        } else {
            throw new RuntimeException("For API levels < 11, Memento requires the support-v4 package and " +
                    "the target Activity must inherit from FragmentActivity");
        }
    }

    private static void retainNative(Activity activity, String fragmentTag) {
        log("Entering native fragments mode");
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        MementoMethods memento = (MementoMethods) fragmentManager.findFragmentByTag(fragmentTag);

        if (memento == null) {
            memento = buildMemento(activity);
            fragmentManager.beginTransaction().add((android.app.Fragment) memento, fragmentTag).commit();
        } else {
            restoreMemento(activity, memento);
        }
    }

    private static void retainSupportV4(FragmentActivity activity, String fragmentTag) {
        log("Entering support fragments mode");
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        MementoMethods memento = (MementoMethods) fragmentManager.findFragmentByTag(fragmentTag);

        if (memento == null) {
            memento = buildMemento(activity);
            fragmentManager.beginTransaction().add((Fragment) memento, fragmentTag).commit();
        } else {
            restoreMemento(activity, memento);
        }
    }

    private static MementoMethods buildMemento(Activity activity) {
        log("No memento found; storing...");
        MementoMethods memento = instantiateMementoFragment(activity);
        ((MementoCallbacks) activity).onLaunch();
        memento.retain(activity);
        return memento;
    }

    private static void restoreMemento(Activity activity, MementoMethods memento) {
        log("Found memento; restoring...");
        memento.restore(activity);
    }

    private static MementoMethods instantiateMementoFragment(Activity activity) {
        final String fragmentClassName = getMementoClassName(activity);
        try {
            final Class<?> fragmentClass = activity.getClassLoader().loadClass(fragmentClassName);
            return (MementoMethods) fragmentClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not load memento: " + fragmentClassName, e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Could not create memento: " + fragmentClassName, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not access memento: " + fragmentClassName, e);
        }
    }

    private static String getMementoClassName(Activity hostActivity) {
        return hostActivity.getComponentName().getClassName() + "$Memento";
    }

    private static String getMementoFragmentTag(Activity hostActivity) {
        // for simplicity, just use the Memento fragment's class name
        return getMementoClassName(hostActivity);
    }

    private static void log(String message) {
        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, message);
        }
    }
}
