package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.view.View;

class CompatBase {

    static void setAlpha(View view, float alpha) {
        // NO-OP
    }

    static void postOnAnimation(View view, Runnable runnable) {
        view.postDelayed(runnable, 10l);
    }

}
