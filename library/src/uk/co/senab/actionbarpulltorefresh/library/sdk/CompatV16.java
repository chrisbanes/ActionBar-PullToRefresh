package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.view.View;

class CompatV16 {

    public static void postOnAnimation(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }

}
