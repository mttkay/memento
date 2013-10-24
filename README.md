# Memento
Memento is an annotation processor for Android that gives your activities a memory. 
It introduces the `@Retain` annotation, which allows you to retain arbitrary fields
of an activity across configuration changes in a simple and type-safe manner.

![Memento](https://raw.github.com/mttkay/memento/master/project/logo_400w.png)

# Overview
On Android, `Activity` instances will get destroyed by the runtime whenever configuration changes occur such as a change in screen orientation. Since this will cause the activity to go through an `onDestroy`/`onCreate` cycle, all instance state is lost.

This is especially troublesome when one needs to retain concurrent objects, such as running `AsyncTasks`. Without retaining their state when the activity gets destroyed, results will be lost unless cached and redelivered to the activity.
In earlier days, one was to use the `onRetainNonConfigurationInstance` callback, which is not type-safe, is cumbersome to use, and is today deprecated in favour of using fragments that `setRetainInstance(true)`.

While introducing fragments helps retaining state on a per-fragment basis, shared data that is to be delivered to any number of embedded fragments must be duplicated in each of them, or manually shifted to a shared background fragment.

Memento solves all these issues in an elegant, simple, and type-safe way by generating companion fragments for your activities that retain shared state.

# Setup
Memento is published as a Maven artifact. If you're using Android Studio, add the following config to your `build.gradle`:

```groovy
configurations {
  apt // create a new classpath for Java APT processors
}

dependencies {
  // pulls in dependency for the client library
  compile 'com.github.mttkay.memento:memento:0.1'
  
  // pulls in dependency for APT
  apt 'com.github.mttkay.memento:memento-processor:0.1'
}

android {
  applicationVariants.all {
    javaCompile.options.compilerArgs.addAll(
      '-processorpath', configurations.apt.asPath,
      '-processor', 'com.github.mttkay.memento.MementoProcessor'
    )
  }
}
```

# Usage
Using the library involves just three steps:

### 1. Annotate fields
In your activity, use the `@Retain` annotation to annotate fields that are to survive configuration
changes. **Annotated fields cannot be private.**

```java
// in your Activity class
@Retain AsyncTask mTask;
...
```
    
### 2. Implement launch hook
Memento adds a new life-cycle hook to your activies: `onLaunch`. This event signals that your activity
was created for the first time, contrary to e.g. being recreated due to a configuration change.
In `onLaunch`, initialize any member fields that are supposed to be created just once, when the activity
first starts.

```java
public class MyActivity extends Activity implements MementoCallbacks {

    @Override
    public void onLaunch() {
       mTask = new AsyncTask() { ... };
    }
}
```
    
**NOTE:** If you're targeting an API level before Honeycomb (11), you must use the support-v4 package 
and inherit from `FragmentActivity` instead.
    
### 3. Bind Activity
Execute Memento to either initialize or restore annotated fields in `onCreate`:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Memento.retain(this);
    ...
}
```
    
On the first call to `onCreate`, this will invoke the `onLaunch` hook. Otherwise, it will restore
any fields previously initialized in that hook.

# License
```
Copyright 2013 Matthias Käppler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
