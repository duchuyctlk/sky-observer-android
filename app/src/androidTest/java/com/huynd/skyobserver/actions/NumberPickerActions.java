package com.huynd.skyobserver.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.NumberPicker;

import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by HuyND on 4/19/2018.
 */

public class NumberPickerActions {
    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker numberPicker = (NumberPicker) view;

                try {
                    Method setValueInternalMethod = NumberPicker.class
                            .getDeclaredMethod("setValueInternal", int.class, boolean.class);
                    setValueInternalMethod.setAccessible(true);
                    setValueInternalMethod.invoke(numberPicker, num, true);
                    setValueInternalMethod.setAccessible(false);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }
}
