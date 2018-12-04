package com.example.s1636431.coinz;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class SignUpActivityTest {

    @Rule
    public ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule<>(SignUpActivity.class);

    @Test
    public void signUpFail() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("lordjeb@bush.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withText(R.string.toastSignUpFail)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void signUpSuccess() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withText(R.string.toastSignUpSuccess)).inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        // Delete user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document("john@test.com");
        dRef.delete();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.getCurrentUser().delete();
    }


    @Test
    public void passwordTooShort() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("1"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRpass)).check(matches(hasErrorText("Minimum length of password should be 6")));
    }

    @Test
    public void noInputWeight() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText(""), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRweight)).check(matches(hasErrorText("Weight is required")));
    }

    @Test
    public void noInputHeight() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText(""), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRheight)).check(matches(hasErrorText("Height is required")));
    }

    @Test
    public void noInputPassword() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@test.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText(""), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRpass)).check(matches(hasErrorText("Password is required")));
    }

    @Test
    public void emailNotValid() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText("john@.com"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRemail)).check(matches(hasErrorText("Valid email is required")));
    }

    @Test
    public void noInputEmail() {
        // Type text and then press the button.
        onView(withId(R.id.etRemail))
                .perform(typeText(""), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRpass))
                .perform(typeText("123456"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRweight))
                .perform(typeText("80"), closeSoftKeyboard());
        // Type text and then press the button.
        onView(withId(R.id.etRheight))
                .perform(typeText("1.8"), closeSoftKeyboard());
        // Press button
        onView(withId(R.id.btSignUp)).perform(click());
        // Check error message matches.
        onView(withId(R.id.etRemail)).check(matches(hasErrorText("Email is required")));
    }
}

