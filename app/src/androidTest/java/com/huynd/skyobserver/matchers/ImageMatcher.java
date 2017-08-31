package com.huynd.skyobserver.matchers;

import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by HuyND on 9/1/2017.
 */

public class ImageMatcher extends TypeSafeMatcher<View> {
    public static Matcher<View> noDrawable() {
        return new ImageMatcher();
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)) {
            return false;
        }

        return ((ImageView) target).getDrawable() == null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ImageView with no drawable");
    }
}
