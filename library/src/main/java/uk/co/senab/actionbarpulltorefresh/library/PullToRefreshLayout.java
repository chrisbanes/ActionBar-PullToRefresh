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

package uk.co.senab.actionbarpulltorefresh.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * A layout which allows you to declare a child scrollable view to be used to trigger
 * Pull-to-Refresh interactions via XML.
 */
public class PullToRefreshLayout extends FrameLayout {

    private static final String LOG_TAG = "PullToRefreshLayout";

    private final PullToRefreshAttacher.Options mOptions;
    private PullToRefreshAttacher mAttacher;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mOptions = new PullToRefreshAttacher.Options();

        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);

        if (values.hasValue(R.styleable.PullToRefreshLayout_headerLayout)) {
            mOptions.headerLayout = values
                    .getResourceId(R.styleable.PullToRefreshLayout_headerLayout,
                            mOptions.headerLayout);
        }

        if (values.hasValue(R.styleable.PullToRefreshLayout_headerInAnimation)) {
            mOptions.headerInAnimation = values
                    .getResourceId(R.styleable.PullToRefreshLayout_headerInAnimation,
                            mOptions.headerInAnimation);
        }

        if (values.hasValue(R.styleable.PullToRefreshLayout_headerOutAnimation)) {
            mOptions.headerInAnimation = values
                    .getResourceId(R.styleable.PullToRefreshLayout_headerOutAnimation,
                            mOptions.headerOutAnimation);
        }

        if (values.hasValue(R.styleable.PullToRefreshLayout_refreshScrollDistance)) {
            mOptions.refreshScrollDistance = values
                    .getFloat(R.styleable.PullToRefreshLayout_refreshScrollDistance,
                            mOptions.refreshScrollDistance);
        }

        if (values.hasValue(R.styleable.PullToRefreshLayout_delegateClass)) {
            String className = values.getString(R.styleable.PullToRefreshLayout_delegateClass);
            mOptions.delegate = InstanceCreationUtils.instantiateDelegate(getContext(),
                    className, null);
        }

        if (values.hasValue(R.styleable.PullToRefreshLayout_headerTransformerClass)) {
            String className = values
                    .getString(R.styleable.PullToRefreshLayout_headerTransformerClass);
            mOptions.delegate = InstanceCreationUtils.instantiateTransformer(getContext(),
                    className, null);
        }

        values.recycle();
    }

    /**
     * @return PullToRefreshAttacher linked with this view.
     */
    public PullToRefreshAttacher getAttacher() {
        return mAttacher;
    }

    /**
     * @return The PullToRefreshAttacher linked with the {@link PullToRefreshLayout} found with
     * the id given at <code>viewId</code>
     */
    public static PullToRefreshAttacher getAttacher(Activity activity, int viewId) {
        PullToRefreshLayout layout = (PullToRefreshLayout) activity.findViewById(viewId);
        if (layout != null) {
            return layout.getAttacher();
        }
        return null;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, index, params);
            createAttacher(child);
        } else {
            throw new IllegalArgumentException("PullToRefreshLayout can only have one child.");
        }
    }

    void createAttacher(View view) {
        mAttacher = new PullToRefreshAttacher((Activity) getContext(), view, mOptions);
    }

}
