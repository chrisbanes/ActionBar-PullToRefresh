# PLEASE NOTE, THIS PROJECT IS NO LONGER BEING MAINTAINED

* * *

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

## Usage and Integration
See the Quick Start guides for more information on how to achieve a simple integration:

* [Quick Start](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/QuickStart-Stock) for API v14 and above.
* [Quick Start: ActionBarCompat](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/QuickStart-ABC) when using ActionBarCompat (appcompat).
* [Quick Start: ActionBarSherlock](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/QuickStart-ABS) when using ActionBarSherlock.

Then we are some advanced integration information:

* [ListFragment](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/ListFragment) when integrating the library with a ListFragment.


## Customisation
See the [Customisation](https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/Customisation) page for more information.

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


![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/chrisbanes/actionbar-pulltorefresh/trend.png)
