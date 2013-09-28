package com.github.mttkay.memento;

import android.support.v4.app.FragmentActivity;

public interface MementoMethods {

    void to(FragmentActivity target);
    void from(FragmentActivity source);

}
