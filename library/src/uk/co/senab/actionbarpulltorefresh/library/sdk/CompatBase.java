package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.view.View;

class CompatBase {

    public static void postOnAnimation(View view, Runnable runnable) {
        view.postDelayed(runnable, 10l);
    }

}
