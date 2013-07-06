/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.samples.actionbarsherlock;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

class FragmentTabPager extends FragmentPagerAdapter
        implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    private final Context mContext;
    private final ActionBar mActionBar;
    private final ViewPager mViewPager;

    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    private final SparseArray<WeakReference<Fragment>> mFragments
            = new SparseArray<WeakReference<Fragment>>();

    static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
    }

    interface Listener {
        /**
         * Called when the item has been selected in the ViewPager.
         */
        void onPageSelected();
    }

    FragmentTabPager(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mActionBar = activity.getSupportActionBar();

        mViewPager = pager;
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
        TabInfo info = new TabInfo(clss, args);
        tab.setTag(info);
        tab.setTabListener(this);
        mActionBar.addTab(tab);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        Fragment frag = Fragment.instantiate(mContext, info.clss.getName(), info.args);
        mFragments.append(position, new WeakReference<Fragment>(frag));
        return frag;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // NO-OP
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);

        WeakReference<Fragment> ref = mFragments.get(position);
        Fragment frag = null != ref ? ref.get() : null;

        // We need to notify the fragment that it is selected
        if (frag != null && frag instanceof Listener) {
            ((Listener) frag).onPageSelected();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // NO-OP
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Object tag = tab.getTag();
        for (int i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i) == tag) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // NO-OP
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // NO-OP
    }

    @Override
    public void notifyDataSetChanged() {
        mFragments.clear();
        super.notifyDataSetChanged();
    }
}