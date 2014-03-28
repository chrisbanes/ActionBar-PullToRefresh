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
public final class Options {

    /* Default configuration values */
    private static final int DEFAULT_HEADER_LAYOUT = R.layout.default_header;
    private static final float DEFAULT_REFRESH_SCROLL_DISTANCE = 0.5f;
    private static final boolean DEFAULT_REFRESH_ON_UP = false;
    private static final int DEFAULT_REFRESH_MINIMIZED_DELAY = 1 * 1000;
    private static final boolean DEFAULT_REFRESH_MINIMIZE = true;

    public static Builder create() {
        return new Builder();
    }

    Options() {}

    EnvironmentDelegate environmentDelegate = null;
    int headerLayout = DEFAULT_HEADER_LAYOUT;
    HeaderTransformer headerTransformer = null;
    float refreshScrollDistance = DEFAULT_REFRESH_SCROLL_DISTANCE;
    boolean refreshOnUp = DEFAULT_REFRESH_ON_UP;
    int refreshMinimizeDelay = DEFAULT_REFRESH_MINIMIZED_DELAY;

    /**
     * Enable or disable the header 'minimization', which by default means that the majority of
     * the header is hidden, leaving only the progress bar still showing.
     * <p/>
     * If set to true, the header will be minimized after the delay set in
     * {@link #refreshMinimizeDelay}. If set to false then the whole header will be displayed
     * until the refresh is finished.
     */
    boolean refreshMinimize = DEFAULT_REFRESH_MINIMIZE;

    public static class Builder {
        final Options mOptions = new Options();

        /**
         * EnvironmentDelegate instance which will be used. If null, we will
         * create an instance of the default class.
         */
        public Builder environmentDelegate(EnvironmentDelegate environmentDelegate) {
            mOptions.environmentDelegate = environmentDelegate;
            return this;
        }

        /**
         * The layout resource ID which should be inflated to be displayed above
         * the Action Bar
         */
        public Builder headerLayout(int headerLayoutId) {
            mOptions.headerLayout = headerLayoutId;
            return this;
        }

        /**
         * The header transformer to be used to transfer the header view. If
         * null, an instance of {@link DefaultHeaderTransformer} will be used.
         */
        public Builder headerTransformer(HeaderTransformer headerTransformer) {
            mOptions.headerTransformer = headerTransformer;
            return this;
        }

        /**
         * The percentage of the refreshable view that needs to be scrolled
         * before a refresh is initiated.
         */
        public Builder scrollDistance(float refreshScrollDistance) {
            mOptions.refreshScrollDistance = refreshScrollDistance;
            return this;
        }

        /**
         * Whether a refresh should only be initiated when the user has finished
         * the touch event.
         */
        public Builder refreshOnUp(boolean enabled) {
            mOptions.refreshOnUp = enabled;
            return this;
        }

        /**
         * Disable the header 'minimization', which by default means that the majority of
         * the header is hidden, leaving only the progress bar still showing.
         */
        public Builder noMinimize() {
            mOptions.refreshMinimize = false;
            return this;
        }

        /**
         * Enable header 'minimization', which by default means that the majority of
         * the header is hidden, leaving only the progress bar still showing.
         */
        public Builder minimize() {
            return minimize(DEFAULT_REFRESH_MINIMIZED_DELAY);
        }

        /**
         * Enable header 'minimization' and set the delay.
         */
        public Builder minimize(int delay) {
            mOptions.refreshMinimizeDelay = delay;
            mOptions.refreshMinimize = true;
            return this;
        }

        /**
         * @return the built {@link Options} instance.
         */
        public Options build() {
            return mOptions;
        }
    }
}
