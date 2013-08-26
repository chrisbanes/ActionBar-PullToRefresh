# ActionBar-PullToRefresh

![ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh/raw/master/header.png)

ActionBar-PullToRefresh provides an easy way to add a modern version of the pull-to-refresh interaction to your application.

Please note that this is __not__ an update to [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh), this has been created from new. You should think of this as Android-PullToRefresh's younger, leaner cousin.

### This is a Preview
Please note that this is currently in a preview state. This basically means that the API is not fixed and you should expect changes between releases.

---

## Sample Apps

There are two sample applications, the stock sample which uses the standard library and is therefore has a `minSdkVersion` of 14. There is also a sample which uses the ActionBarSherlock extra so has a `minSdkVersion` of 7.

### Stock Sample
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](http://play.google.com/store/apps/details?id=uk.co.senab.actionbarpulltorefresh.samples.stock)

### ActionBarSherlock Sample
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](http://play.google.com/store/apps/details?id=uk.co.senab.actionbarpulltorefresh.samples.actionbarsherlock)

## Video

[![Sample Video](http://img.youtube.com/vi/YOYtPF-4RPg/0.jpg)](https://www.youtube.com/watch?v=YOYtPF-4RPg)

---

## Supported Views

ActionBar-PullToRefresh has in-built support for:

 * AbsListView derivatives (ListView & GridView).
 * ScrollView
 * WebView

If the View you want to use is not listed above, you can easily add support in your own code by providing a `ViewDelegate`. See the `ViewDelegate` section below for more info.

---

## Usage

There are two ways to use this library.

### PullToRefreshAttacher only

This is the simplest method, as it's just two lines of code. You just need to create an instance of `PullToRefreshAttacher`, giving it the Activity and the View for which will scroll.

``` java
private PullToRefreshAttacher mPullToRefreshAttacher;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    // Get View for which the user will scroll…
    View scrollableView = findViewById(R.id.blah); 

    // Create a PullToRefreshAttacher instance
    mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

    // Add the Refreshable View and provide the refresh listener
    mPullToRefreshAttacher.addRefreshableView(scrollableView, this);
}
```
See the [ListView](samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/ListViewActivity.java) sample for more info.

### PullToRefreshLayout

Using a `PullToRefreshLayout` gives the library better control over the touch events, and should be used if you find that using the above method is not working correctly.

Examples of when you would use `PullToRefreshLayout` are:

* Clickable view with refreshable View
* Not being able to pull from 'empty' space within the refreshable view.

The first thing you need to do is wrap your refreshable view in a `PullToRefreshLayout`:

```xml
<uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/ptr_layout"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent">
    
    <!-- Your content, here we're using a ScrollView -->

    <ScrollView
        android:layout_width="fill_parent"
        android:layout_height="fill_parent">
            
    </ScrollView>

</uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout>
```

Then in your Activity, get a `PullToRefreshAttacher` and give it to the `PullToRefreshLayout`, along with the `OnRefreshListener`.

``` java
private PullToRefreshAttacher mPullToRefreshAttacher;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    // Create a PullToRefreshAttacher instance
    mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

    // Retrieve the PullToRefreshLayout from the content view
    PullToRefreshLayout ptrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

    // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with a refresh listener.
    ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);
    
}
```
See the [ScrollView](samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/ScrollViewActivity.java) sample for more info.

--

## Fragments

One thing to note is that the `PullToRefreshAttacher` **needs** to be created in the `onCreate()` phase of the Activity. If you plan on using this library with Fragments then the best practice is for your Activity to create the `PullToRefreshAttacher`, and then have your fragments retrieve it from the Activity.

An example is provided in the [Fragment & Tabs](samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/FragmentTabsActivity.java) sample.

## Customisation

See the [Customisation](Customisation.md) page for more information.

## Gradle

Action Bar is now pushed to Maven Central as a AAR, so you just need to add the following dependency to your `build.gradle`.
    
    dependencies {
        compile 'com.github.chrisbanes.actionbarpulltorefresh:library:0.7.+'
    }
    
Or if you are using one of the ActionBarSherlock or ActionBarCompat extras, you would use the following:

    dependencies {
        // Use ONE of the following, depending on which library you are using
        compile 'com.github.chrisbanes.actionbarpulltorefresh:extra-abc:0.7.+'
        compile 'com.github.chrisbanes.actionbarpulltorefresh:extra-abs:0.7.+'
    }

## License

    Copyright 2013 Chris Banes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
