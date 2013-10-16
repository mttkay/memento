package com.github.mttkay.memento;

import android.app.Activity;

public interface MementoMethods {

    void retain(Activity source);
    void restore(Activity target);

}
