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
import android.content.res.Configuration;
import android.view.View;

/**
 * HeaderTransformers are what controls and update the Header View to reflect the current state
 * of the pull-to-refresh interaction. They are responsible for showing and hiding the header
 * view, as well as update the state.
 */
public abstract class HeaderTransformer {

    /**
     * Called whether the header view has been inflated from the resources
     * defined in {@link Options#headerLayout}.
     *
     * @param activity The {@link android.app.Activity} that the header view is attached to.
     * @param headerView The inflated header view.
     */
    public void onViewCreated(Activity activity, View headerView) {}

    /**
     * Called when the header should be reset. You should update any child
     * views to reflect this.
     * <p/>
     * You should <strong>not</strong> change the visibility of the header
     * view.
     */
    public void onReset() {}

    /**
     * Called the user has pulled on the scrollable view.
     *
     * @param percentagePulled value between 0.0f and 1.0f depending on how far the
     *                         user has pulled.
     */
    public void onPulled(float percentagePulled) {}

    /**
     * Called when a refresh has begun. Theoretically this call is similar
     * to that provided from {@link uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener} but is more suitable
     * for header view updates.
     */
    public void onRefreshStarted() {}

    /**
     * Called when a refresh can be initiated when the user ends the touch
     * event. This is only called when {@link Options#refreshOnUp} is set to
     * true.
     */
    public void onReleaseToRefresh() {}

    /**
     * Called when the current refresh has taken longer than the time
     * specified in {@link Options#refreshMinimizeDelay}.
     */
    public void onRefreshMinimized() {}

    /**
     * Called when the Header View should be made visible, usually with an animation.
     *
     * @return true if the visibility has changed.
     */
    public abstract boolean showHeaderView();

    /**
     * Called when the Header View should be made invisible, usually with an animation.
     *
     * @return true if the visibility has changed.
     */
    public abstract boolean hideHeaderView();

    /**
     * Called when the Activity's configuration has changed.
     *
     * @param activity The {@link android.app.Activity} that the header view is attached to.
     * @param newConfig New configuration.
     *
     * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
     */
    public void onConfigurationChanged(Activity activity, Configuration newConfig) {}
}
