# Memento
Memento is an annotation processor for Android that gives your activities a memory. 
It introduces the `@Retain` annotation, which allows you to retain arbitrary fields
of an activity across configuration changes in a simple and type-safe manner.

## Rationale
On Android, `Activity` instances will get destroyed by the runtime whenever configuration changes occur such as
a change in screen orientation. Since this will cause the activity to go through an `onDestroy`/`onCreate` cycle,
all instance state is lost.

This is especially troublesome when one needs to retain state about concurrency, such as an ongoing
`AsyncTask`. Without retaining that state when the activity gets destroyed, all results would be lost.
In earlier days, one was to use the `onRetainNonConfigurationInstance` callback, which is not type-safe,
is cumbersome to use, and is today deprecated in favor of using fragments that `setRetainInstance(true)`.

While introducing fragments helps tremendously with this issue, it is often required to keep shared state
in the host activity when multiple fragments are embedded that share the same state or data. If, for instance,
you fetch data from an API that is supposed to backfill 3 fragments in a host activity, that state would have
to be duplicated in or brodcasted to these fragments.

Memento solves all these issues in a simple and type-safe way while adhering to platform guidelines and
constructs (i.e. not using any loop holes.)

# Usage
Using the library requires three steps:

## 1 - Annotate fields to be retained
In your activity, use the `@Retain` annotation to annotate fields that are to survive configuration
changes. **Annotated fields cannot be private.**

    // in your Activity class
    @Retain AsyncTask mTask;
    ...
    
## 2 - Implement Memento callbacks
Memento adds a new life-cycle hook to your activies: `onLaunch`. This event signals that your activity
was created for the first time, contrary to e.g. being recreated due to a configuration change.
In `onLaunch`, initialize any member fields that are supposed to be created just once, when the activity
first starts. **The activity must inherit from FragmentActivity; support for Honeycomb+ activities
with built in fragment support will be added shortly.**

    public class MyActivity extends FragmentActivity implements MementoCallbacks {
    
        @Override
        public void onLaunch() {
           mTask = new AsyncTask() { ... };
        }
    }
    
## 3 - Bind Activity
Execute Memento to either initialize or restore annotated fields in `onCreate`:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Memento.retain(this);
        ...
    }
    
On the first call to `onCreate`, this will invoke the `onLaunch` hook. Otherwise, it will restore
any fields previously initialized in that hook.

# Installation
TODO

# License
TODO
