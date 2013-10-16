package com.github.mttkay.memento;

import android.support.v4.app.FragmentActivity;

public interface MementoMethods {

    void retain(FragmentActivity source);
    void restore(FragmentActivity target);

}
