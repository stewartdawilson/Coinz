package com.example.s1636431.coinz;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText(email));
        onView(withId(R.id.etRpass))
                .perform(replaceText(password));
        onView(withId(R.id.etRweight))
                .perform(replaceText(weight_text));
        onView(withId(R.id.etRheight))
                .perform(replaceText(height_text));
        onView(withId(R.id.btSignUp)).perform(click());

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();


        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btContinue)).perform(click());

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);
        dRef.delete();

    }

    @Test
    public void depositOver25() {

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText(email));
        onView(withId(R.id.etRpass))
                .perform(replaceText(password));
        onView(withId(R.id.etRweight))
                .perform(replaceText(weight_text));
        onView(withId(R.id.etRheight))
                .perform(replaceText(height_text));
        onView(withId(R.id.btSignUp)).perform(click());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        Map<String, Object> data = new HashMap<>();
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);
        wallet.put("test4", 60.6);
        wallet.put("test5", 60.6);
        wallet.put("test6", 60.6);
        wallet.put("test7", 60.6);
        wallet.put("test8", 60.6);
        wallet.put("test9", 60.6);
        wallet.put("test10", 60.6);
        wallet.put("test11", 60.6);
        wallet.put("test12", 60.6);
        wallet.put("test13", 60.6);
        wallet.put("test14", 60.6);
        wallet.put("test15", 60.6);
        wallet.put("test16", 60.6);
        wallet.put("test17", 60.6);
        wallet.put("test18", 60.6);
        wallet.put("test19", 60.6);
        wallet.put("test20", 60.6);
        wallet.put("test21", 60.6);
        wallet.put("test22", 60.6);
        wallet.put("test23", 60.6);
        wallet.put("test24", 60.6);
        wallet.put("test25", 60.6);
        wallet.put("test26", 60.6);

        Integer bank = 0;

        data.put("wallet", wallet);
        data.put("bank", bank);

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        dRef.set(data, SetOptions.merge());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btContinue)).perform(click());

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


        onView(withId(R.id.etBank)).perform(typeText("26"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        onView(withText("Deposit"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText(R.string.toastBankedLimit)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));

        dRef.delete();

    }

    @Test
    public void numberExceedsDepositLimit() {

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText(email));
        onView(withId(R.id.etRpass))
                .perform(replaceText(password));
        onView(withId(R.id.etRweight))
                .perform(replaceText(weight_text));
        onView(withId(R.id.etRheight))
                .perform(replaceText(height_text));
        onView(withId(R.id.btSignUp)).perform(click());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        Map<String, Object> data = new HashMap<>();
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);

        Integer bank = 0;
        Integer amount_banked = 23;


        data.put("wallet", wallet);
        data.put("bank", bank);
        data.put("amount_banked", amount_banked);

        try {
            Thread.sleep(5000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        dRef.set(data, SetOptions.merge());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btContinue)).perform(click());

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


        onView(withId(R.id.etBank)).perform(typeText("26"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        onView(withText("Deposit"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText(R.string.toastBankedLimit)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));

        dRef.delete();

    }

    @Test
    public void numberExceedsWallet() {

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText(email));
        onView(withId(R.id.etRpass))
                .perform(replaceText(password));
        onView(withId(R.id.etRweight))
                .perform(replaceText(weight_text));
        onView(withId(R.id.etRheight))
                .perform(replaceText(height_text));
        onView(withId(R.id.btSignUp)).perform(click());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        Map<String, Object> data = new HashMap<>();
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);


        Integer bank = 0;

        data.put("wallet", wallet);
        data.put("bank", bank);

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        dRef.set(data, SetOptions.merge());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btContinue)).perform(click());

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


        onView(withId(R.id.etBank)).perform(typeText("4"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        onView(withText("Deposit"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Can't bank " + 4 + " coins because number exceeds wallet size.")).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));

        dRef.delete();

    }

    @Test
    public void depositSuccess() {

        String email = "test@test.com";
        String password = "123456";
        String weight_text = "80";
        String height_text = "1.8";

        // Sign Up new user
        onView(withId(R.id.etRemail))
                .perform(replaceText(email));
        onView(withId(R.id.etRpass))
                .perform(replaceText(password));
        onView(withId(R.id.etRweight))
                .perform(replaceText(weight_text));
        onView(withId(R.id.etRheight))
                .perform(replaceText(height_text));
        onView(withId(R.id.btSignUp)).perform(click());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().delete();


        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("User").document(email);

        Map<String, Object> data = new HashMap<>();
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("test1", 60.6);
        wallet.put("test2", 60.6);
        wallet.put("test3", 60.6);


        Integer bank = 0;

        data.put("wallet", wallet);
        data.put("bank", bank);

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }


        dRef.set(data, SetOptions.merge());

        try {
            Thread.sleep(7000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btContinue)).perform(click());

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


        onView(withId(R.id.etBank)).perform(typeText("3"),closeSoftKeyboard());

        Activity activity = getCurrentActivity();

        onView(withText("Deposit"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Deposited " + 3 + " coins!")).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));

        dRef.delete();

    }




}
