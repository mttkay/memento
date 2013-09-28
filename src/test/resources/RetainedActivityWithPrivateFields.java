package com.test;

import com.github.mttkay.memento.Memento;
import com.github.mttkay.memento.MementoCallbacks;
import com.github.mttkay.memento.Retain;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.lang.Override;

class RetainedActivityWithPrivateFields extends FragmentActivity implements MementoCallbacks {

    @Retain
    private String retainedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Memento.retain(this);
    }

    @Override
    public void onFirstCreate() {
    }
}