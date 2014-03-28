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

package uk.co.senab.actionbarpulltorefresh.library.listeners;

import android.view.View;

public interface HeaderViewListener {
    /**
     * The state when the header view is completely visible.
     */
    public static int STATE_VISIBLE = 0;

    /**
     * The state when the header view is minimized. By default this means
     * that the progress bar is still visible, but the rest of the view is
     * hidden, showing the Action Bar behind.
     * <p/>
     * This will not be called in header minimization is disabled.
     */
    public static int STATE_MINIMIZED = 1;

    /**
     * The state when the header view is completely hidden.
     */
    public static int STATE_HIDDEN = 2;

    /**
     * Called when the visibility state of the Header View has changed.
     *
     * @param headerView
     *            HeaderView who's state has changed.
     * @param state
     *            The new state. One of {@link #STATE_VISIBLE},
     *            {@link #STATE_MINIMIZED} and {@link #STATE_HIDDEN}
     */
    public void onStateChanged(View headerView, int state);
}
