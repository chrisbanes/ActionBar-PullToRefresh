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

/**
 * Allows you to specify a number of configuration options when setting up a {@link PullToRefreshLayout}.
 */
public class Options {

    /* Default configuration values */
    private static final int DEFAULT_HEADER_LAYOUT = R.layout.default_header;
    private static final float DEFAULT_REFRESH_SCROLL_DISTANCE = 0.5f;
    private static final boolean DEFAULT_REFRESH_ON_UP = false;
    private static final int DEFAULT_REFRESH_MINIMIZED_DELAY = 1 * 1000;
    private static final boolean DEFAULT_REFRESH_MINIMIZE = true;

    /**
     * EnvironmentDelegate instance which will be used. If null, we will
     * create an instance of the default class.
     */
    public EnvironmentDelegate environmentDelegate = null;

    /**
     * The layout resource ID which should be inflated to be displayed above
     * the Action Bar
     */
    public int headerLayout = DEFAULT_HEADER_LAYOUT;

    /**
     * The header transformer to be used to transfer the header view. If
     * null, an instance of {@link uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer} will be used.
     */
    public HeaderTransformer headerTransformer = null;

    /**
     * The percentage of the refreshable view that needs to be scrolled
     * before a refresh is initiated.
     */
    public float refreshScrollDistance = DEFAULT_REFRESH_SCROLL_DISTANCE;

    /**
     * Whether a refresh should only be initiated when the user has finished
     * the touch event.
     */
    public boolean refreshOnUp = DEFAULT_REFRESH_ON_UP;

    /**
     * The delay after a refresh is started in which the header should be
     * 'minimized'. By default, most of the header is faded out, leaving
     * only the progress bar signifying that a refresh is taking place.
     */
    public int refreshMinimizeDelay = DEFAULT_REFRESH_MINIMIZED_DELAY;

    /**
     * Enable or disable the header 'minimization', which by default means that the majority of
     * the header is hidden, leaving only the progress bar still showing.
     * <p/>
     * If set to true, the header will be minimized after the delay set in
     * {@link #refreshMinimizeDelay}. If set to false then the whole header will be displayed
     * until the refresh is finished.
     */
    public boolean refreshMinimize = DEFAULT_REFRESH_MINIMIZE;
}
