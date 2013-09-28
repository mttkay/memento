package com.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public final class RetainedActivity_Memento extends Fragment
        implements com.github.mttkay.memento.MementoMethods {

    String retainedString;
    android.os.AsyncTask asyncTask;

    public RetainedActivity_Memento() {
        setRetainInstance(true);
    }

    @Override
    public void restore(FragmentActivity source) {
        RetainedActivity activity = (RetainedActivity) source;
        this.retainedString = activity.retainedString;
        this.asyncTask = activity.asyncTask;
    }

    @Override
    public void retain(FragmentActivity target) {
        RetainedActivity activity = (RetainedActivity) target;
        activity.retainedString = this.retainedString;
        activity.asyncTask = this.asyncTask;
    }
}
