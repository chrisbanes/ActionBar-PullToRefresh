package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.view.View;

class CompatV16 {

    static void postOnAnimation(View view, Runnable runnable) {
        view.postOnAnimation(runnable);
    }

}
