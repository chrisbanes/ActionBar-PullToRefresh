# ActionBar-PullToRefresh

![ActionBar-PullToRefresh](https://github.com/chrisbanes/ActionBar-PullToRefresh/raw/master/header.png)

ActionBar-PullToRefresh provides an easy way to add a modern version of the pull-to-refresh interaction to your application.

Please note that this is __not__ an update to [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh), this has been created from new. You should think of this as Android-PullToRefresh's younger, leaner cousin.

### This is a Preview
Please note that this is currently in a preview state. This basically means that the API is not fixed and you should expect changes between releases.

### Todo
As this is a preview also means that function is missing. See the [Todo](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/Todo) page for more info on what needs to be added.

---

## Sample

Eventually the sample will be available to download on Google Play. As we're not stable yet you can find the APK [here](https://drive.google.com/folderview?id=0BxAFUoBj0OjaYTd3SUkzYjIydG8&usp=sharing).

---

## Supported Views

ActionBar-PullToRefresh has in-built support for:

 * AbsListView derivatives (ListView & GridView).
 * ScrollView
 * WebView

If the View you want to use is not listed above, you can easily add support by providing a `Delegate`. See the 'Delegates' section below for more info.

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

    mPullToRefreshHelper = new PullToRefreshAttacher(this, scrollableView);
    mPullToRefreshHelper.setRefreshListener(this);
}
```

__See the [ListView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/ListViewActivity.java) sample for more info.__
  
---

## Customisation    
    
### Delegates
TODO. See the [GridView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/GridViewActivity.java) sample for more info for now.

### HeaderTransformers
TODO. See the [GridView](https://github.com/chrisbanes/ActionBar-PullToRefresh/blob/master/samples/stock/src/uk/co/senab/actionbarpulltorefresh/samples/stock/GridViewActivity.java) sample for more info for now.

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
