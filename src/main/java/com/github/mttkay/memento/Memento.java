package com.github.mttkay.memento;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class Memento {

    public static void bind(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        final String fragmentTag = activity.getClass().getCanonicalName() + "_state";
        log("Obtaining " + fragmentTag);
        MementoMethods memento = (MementoMethods) fragmentManager.findFragmentByTag(fragmentTag);

        if (memento == null) {
            log("No memento found; storing...");
            memento = createMemento(activity);
            ((MementoCallbacks) activity).onFirstCreate();
            memento.restore(activity);
            fragmentManager.beginTransaction().add((Fragment) memento, fragmentTag).commit();
        } else {
            log("Found memento; restoring...");
            memento.retain(activity);
        }
    }

    private static MementoMethods createMemento(FragmentActivity activity) {
        final String fragmentClassName = activity.getComponentName().getClassName() + "_Memento";
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

    private static void log(String message) {
        Log.i(Memento.class.getSimpleName(), message);
    }
}
