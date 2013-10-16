package com.test;

import com.github.mttkay.memento.Memento;
import com.github.mttkay.memento.MementoCallbacks;
import com.github.mttkay.memento.Retain;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

class RetainedActivityWithCustomBase extends Base implements MementoCallbacks {

    @Retain
    String retainedString;

    @Retain
    AsyncTask asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Memento.retain(this);
    }

    @Override
    public void onLaunch() {
    }
}


class Base extends Activity {
    // processor should figure out that subclass is an Activity
}