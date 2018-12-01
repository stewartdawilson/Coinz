package com.example.s1636431.coinz;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.core.internal.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static android.support.test.runner.lifecycle.Stage.RESUMED;

public class MenuActivityTest {

    Activity currentActivity;

    @Rule
    public ActivityTestRule<SignUpActivity> SignUpActivityRule =
            new ActivityTestRule<>(SignUpActivity.class);

    public static Activity getCurrentActivity() throws IllegalStateException {
        // The array is just to wrap the Activity and be able to access it from the Runnable.
        final Activity[] resumedActivity = new Activity[1];

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    resumedActivity[0] = (Activity) resumedActivities.iterator().next();
                } else {
                    throw new IllegalStateException("No Activity in stage RESUMED");
                }
            }
        });
        return resumedActivity[0];
    }


    @Test
    public void emptyWallet() {

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText("joooohnnnnn@test.com"));
        onView(withId(R.id.etRpass))
                .perform(replaceText("123456"));
        onView(withId(R.id.etRweight))
                .perform(replaceText("80"));
        onView(withId(R.id.etRheight))
                .perform(replaceText("1.8"));
        onView(withId(R.id.btSignUp)).perform(click());


        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btGo)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btMenu)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        onView(withId(R.id.btBank)).perform(click());


        onView(withId(R.id.etBank)).perform(typeText("5"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        onView(withText("Deposit"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());


        onView(withText(R.string.toastEmptyWallet)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

}
