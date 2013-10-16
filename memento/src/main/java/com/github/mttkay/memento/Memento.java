package com.github.mttkay.memento;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class Memento {

    public static void retain(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        final String fragmentTag = getMementoFragmentTag(activity);
        log("Obtaining " + fragmentTag);
        MementoMethods memento = (MementoMethods) fragmentManager.findFragmentByTag(fragmentTag);

        if (memento == null) {
            log("No memento found; storing...");
            memento = createMemento(activity);
            ((MementoCallbacks) activity).onLaunch();
            memento.restore(activity);
            fragmentManager.beginTransaction().add((Fragment) memento, fragmentTag).commit();
        } else {
            log("Found memento; restoring...");
            memento.retain(activity);
        }
    }

    private static MementoMethods createMemento(FragmentActivity activity) {
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
        Log.i(Memento.class.getSimpleName(), message);
    }
}
