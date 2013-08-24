# Customisation

There are many ways you can customise the pull-to-refresh experience to your needs. See the [GridView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/GridViewActivity.java) sample for more info on all of these.
    
## ViewDelegate

ViewDelegates provide support for handling scrollable Views. The main use of a `ViewDelegate` is to being able to tell when a scrollable view is scrolled to the top. There is currently inbuilt support for:

 * AbsListView classes (through [AbsListViewDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/AbsListViewDelegate.java))
 * ScrollView (through [ScrollYDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/ScrollYDelegate.java))
 * WebView (through [WebViewDelegate](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/library/src/uk/co/senab/actionbarpulltorefresh/library/viewdelegates/WebViewDelegate.java))

So what if you want the view you want to use a view which isn't in the list above? Well you can just provide your own `ViewDelegate`.

``` java
// Create a PullToRefresh Attacher
mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

// Create ViewDelegate which can handle your scrollable view.
// In this case we're creating a ficticious class
PullToRefreshAttacher.ViewDelegate delegate = new XYZViewDelegate();

// Set the Refreshable View, along with your ViewDelegate
mPullToRefreshAttacher.setRefreshableView(xyzView, delegate, listener);
```

## Options
When instatiating a `PullToRefreshAttacher` you can provide an `Options` instance which contains a number of configuration elements:

 * `headerLayout`: Layout resource to be inflated as the header view (see below).
 * `headerTransformer`: The HeaderTransformer for the heard view (see below).
 * `headerInAnimation`: The animation resource which is used when the header view is shown.
 * `headerOutAnimation`: The animation resource which is used when the header view is hidden.
 * `refreshScrollDistance`: The vertical distance (percentage of the scrollable view height) that the user needs to scroll for a refresh to start.
 * `refreshOnUp`: Whether to wait to start the refresh until when the user has lifted their finger.
 * `refreshMinimize`: Whether to minimize the header so that the Action Bar isn't hidden for a long refresh.
 * `refreshMinimizeDelay`: The delay after a refresh starts after which the header should be minimized.

### HeaderTransformers
HeaderTransformers are responsible for updating the header view to match the current state. If you do not provide a HeaderTransformer, there is a default implementation created for you called `DefaultHeaderTransformer`. This default implementation is what provides the default behaviour (growing progress bar, etc).

### Customised Header View layout
If you feel that the default header view layout does not provide what you require, you can provide your own which is inflated for you. For the majority of cases, you will probably want to provide your own `HeaderTransformer` as well, to update your custom layout.