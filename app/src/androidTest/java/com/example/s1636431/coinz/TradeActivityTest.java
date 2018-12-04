package com.example.s1636431.coinz;


import android.app.Activity;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class TradeActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void emptyWallet() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btCreateAccount), withText("Create New Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.etRemail),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.etRpass),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.etRpass), withText(password),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.etRweight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(weight_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.etRweight), withText(weight_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.etRheight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText(height_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.etRheight), withText(height_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btSignUp), withText("Sign Up"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, String>> friends = new ArrayList<>();
        HashMap<String, String> friend = new HashMap<>();
        friend.put("email", "bib@k.com");
        friend.put("user_image", "");

        friends.add(friend);
        data.put("friends", friends);
        dRef.set(data, SetOptions.merge());


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btContinue), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatButtonTut = onView(
                allOf(withId(R.id.btLetsPlay), withText("Lets play!")));
        appCompatButtonTut.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btMenu), withText("MENU"),
                        childAtPosition(
                                allOf(withId(R.id.mapView), withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                1)),
                                5),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btCommunity), withText("Community"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatButton5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tabs),
                                0),
                        2),
                        isDisplayed()));
        tabView.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTrade)).perform(click());



        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTradePage)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.etTrade)).perform(typeText("5"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withText("Trade"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());


        onView(withText(R.string.toastEmptyWallet)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));


        dRef.delete();

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void tradeSuccess() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btCreateAccount), withText("Create New Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.etRemail),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.etRpass),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.etRpass), withText(password),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.etRweight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(weight_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.etRweight), withText(weight_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.etRheight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText(height_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.etRheight), withText(height_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btSignUp), withText("Sign Up"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, String>> friends = new ArrayList<>();
        HashMap<String, String> friend = new HashMap<>();
        friend.put("email", "bib@k.com");
        friend.put("user_image", "");

        friends.add(friend);
        data.put("friends", friends);
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);
        data.put("wallet", wallet);

        dRef.set(data, SetOptions.merge());


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btContinue), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatButtonTut = onView(
                allOf(withId(R.id.btLetsPlay), withText("Lets play!")));
        appCompatButtonTut.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btMenu), withText("MENU"),
                        childAtPosition(
                                allOf(withId(R.id.mapView), withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                1)),
                                5),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btCommunity), withText("Community"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatButton5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tabs),
                                0),
                        2),
                        isDisplayed()));
        tabView.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTrade)).perform(click());


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTradePage)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.etTrade)).perform(typeText("3"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withText("Trade"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        try {
            Thread.sleep(500);
        } catch(Exception e) {
            e.printStackTrace();
        }


        onView(withText(R.string.toastTradeSuccess)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));


        dRef.delete();

    }

    @Test
    public void numberExceedsWallet() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btCreateAccount), withText("Create New Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.etRemail),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(email), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.etRpass),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.etRpass), withText(password),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.etRweight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(weight_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.etRweight), withText(weight_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.etRheight),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText(height_text), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.etRheight), withText(height_text),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btSignUp), withText("Sign Up"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatButton2.perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> data = new HashMap<>();
        ArrayList<HashMap<String, String>> friends = new ArrayList<>();
        HashMap<String, String> friend = new HashMap<>();
        friend.put("email", "bib@k.com");
        friend.put("user_image", "");

        friends.add(friend);
        data.put("friends", friends);
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);
        data.put("wallet", wallet);

        dRef.set(data, SetOptions.merge());


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btContinue), withText("Continue"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton3.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatButtonTut = onView(
                allOf(withId(R.id.btLetsPlay), withText("Lets play!")));
        appCompatButtonTut.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btMenu), withText("MENU"),
                        childAtPosition(
                                allOf(withId(R.id.mapView), withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
                                        childAtPosition(
                                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                                1)),
                                5),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btCommunity), withText("Community"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatButton5.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction tabView = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.tabs),
                                0),
                        2),
                        isDisplayed()));
        tabView.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTrade)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btTradePage)).perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.etTrade)).perform(typeText("4"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withText("Trade"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());


        onView(withText("Can't trade " + 4 + " coins because number exceeds wallet size.")).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));


        dRef.delete();

    }

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
}
