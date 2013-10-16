package com.test;

import com.github.mttkay.memento.MementoCallbacks;
import com.github.mttkay.memento.Retain;

import android.app.Fragment;

class NotAnActivity extends Fragment implements MementoCallbacks {

    @Retain
    String retainedString;

    @Override
    public void onLaunch() {
    }
}