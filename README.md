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
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](http://play.google.com/store/apps/details?id=uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock)

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
You just need to create an instance of `PullToRefreshAttacher`, giving it the Activity and the View for which will scroll.

``` java
private PullToRefreshAttacher mPullToRefreshHelper;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        
    // Get View for which the user will scroll…
    View scrollableView = findViewById(R.id.blah); 

    // Create a PullToRefreshAttacher instance
    mPullToRefreshHelper = new PullToRefreshAttacher(this);

    // Set the Refreshable View and provide the refresh listener
    mPullToRefreshAttacher.setRefreshableView(scrollableView, this);
}
```
See the [ListView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/ListViewActivity.java) sample for more info.

### Fragments

One thing to note is that the `PullToRefreshAttacher` **needs** to be created in the `onCreate()` phase of the Activity. If you plan on using this library with Fragments then the best practice is for your Activity to create the `PullToRefreshAttacher`, and then have your fragments retrieve it from the Activity.

An example is provided in the [Fragment & Tabs](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/FragmentTabsActivity.java) sample.

---

## Customisation

There are many ways you can customise the pull-to-refresh experience to your needs. See the [GridView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/GridViewActivity.java) sample for more info on all of these.
    
### ViewDelegate

ViewDelegates provide support for handling scrollable Views. The main use of a `ViewDelegate` is to being able to tell when a scrollable view is scrolled to the top. There is currently inbuilt support for:

 * AbsListView classes (through [AbsListViewDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/AbsListViewDelegate.java))
 * ScrollView (through [ScrollViewDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/ScrollViewDelegate.java))
 * WebView (through [WebViewDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/WebViewDelegate.java))

So what if you want the view you want to use a view which isn't in the list above? Well you can just provide your own `ViewDelegate`.

``` java
// Create a PullToRefresh Attacher
mPullToRefreshAttacher = new PullToRefreshAttacher(this);

// Create ViewDelegate which can handle your scrollable view.
// In this case we're creating a ficticious class
PullToRefreshAttacher.ViewDelegate delegate = new XYZViewDelegate();

// Set the Refreshable View, along with your ViewDelegate
mPullToRefreshAttacher.setRefreshableView(xyzView, delegate, listener);
```

### Options
When instatiating a `PullToRefreshAttacher` you can provide an `Options` instance which contains a number of configuration elements:

 * `headerLayout`: Layout resource to be inflated as the header view (see below).
 * `headerTransformer`: The HeaderTransformer for the heard view (see below).
 * `headerInAnimation`: The animation resource which is used when the header view is shown.
 * `headerOutAnimation`: The animation resource which is used when the header view is hidden.
 * `refreshScrollDistance`: The vertical distance (percentage of the scrollable view height) that the user needs to scroll for a refresh to start.

### HeaderTransformers
HeaderTransformers are responsible for updating the header view to match the current state. If you do not provide a HeaderTransformer, there is a default implementation created for you called `DefaultHeaderTransformer`. This default implementation is what provides the default behaviour (growing progress bar, etc).

### Customised Header View layout
If you feel that the default header view layout does not provide what you require, you can provide your own which is inflated for you. For the majority of cases, you will probably want to provide your own `HeaderTransformer` as well, to update your custom layout.

## Maven
For the standard library (APIv14+) add the following as a dependency in you `pom.xml`:
```
<dependency>
    <groupId>com.github.chrisbanes.actionbarpulltorefresh</groupId>
    <artifactId>library</artifactId>
    <version>0.4</version>
</dependency>
```

If you're using ActionBarSherlock (APIv7+) add the following as a dependency in you `pom.xml`:
```
<dependency>
    <groupId>com.github.chrisbanes.actionbarpulltorefresh</groupId>
    <artifactId>extra-abs</artifactId>
    <version>0.4</version>
</dependency>
```
---

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
