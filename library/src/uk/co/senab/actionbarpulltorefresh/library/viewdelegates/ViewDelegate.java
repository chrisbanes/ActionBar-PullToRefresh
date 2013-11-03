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

package uk.co.senab.actionbarpulltorefresh.library.viewdelegates;

import android.view.View;

/**
 * ViewDelegates are what are used to de-couple the Attacher from the different types of
 * scrollable views.
 */
public interface ViewDelegate {

    /**
     * Allows you to provide support for View which do not have built-in
     * support. In this method you should cast <code>view</code> to it's
     * native class, and check if it is scrolled to the top.
     *
     * @param view
     *            The view which has should be checked against.
     * @param x The X co-ordinate of the touch event
     * @param y The Y co-ordinate of the touch event
     * @return true if <code>view</code> is scrolled to the top.
     */
    public boolean isReadyForPull(View view, float x, float y);

}
