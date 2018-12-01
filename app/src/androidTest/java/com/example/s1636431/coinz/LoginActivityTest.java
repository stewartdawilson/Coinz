package com.example.s1636431.coinz;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginFail() {
        // Type text and then press the button.
        onView(withId(R.id.etLemail))
                .perform(typeText("no@user.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etLpass))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.btLogin)).perform(click());

        onView(withText(R.string.toastLoginFail)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void loginSuccess() {
        // Type text and then press the button.
        onView(withId(R.id.etLemail))
                .perform(typeText("lordjeb@bush.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etLpass))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.btLogin)).perform(click());

        onView(withText(R.string.toastLoginSuccess)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }


    @Test
    public void noInputPassword() {
        // Type text and then press the button.
        onView(withId(R.id.etLemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etLpass))
                .perform(typeText(""), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.btLogin)).perform(click());

        onView(withId(R.id.etLpass)).check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void noInputEmail() {
        // Type text and then press the button.
        onView(withId(R.id.etLemail))
                .perform(typeText(""), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etLpass))
                .perform(typeText("123456"), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.btLogin)).perform(click());

        onView(withId(R.id.etLemail)).check(matches(hasErrorText("Email is required")));
    }
}
