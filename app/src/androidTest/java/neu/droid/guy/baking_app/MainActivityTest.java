package neu.droid.guy.baking_app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import neu.droid.guy.baking_app.views.MainActivity;
import neu.droid.guy.baking_app.views.StepsView;
import neu.droid.guy.baking_app.views.StepsViewFragment;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSubstring;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private HashMap<Integer, String> map = new HashMap<>();

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void testSetup() {
        map.put(0, "Nutella Pie");
        map.put(1, "Brownies");
        map.put(2, "Yellow Cake");
        map.put(3, "Cheesecake");
    }

    @Test
    public void testRv0() {
        onView(allOf(withId(R.id.recipe_name), withText(map.get(0)))).perform(click());
    }

    @Test
    public void testRv2() {
        onView(allOf(withId(R.id.recipe_name), withSubstring(map.get(1)))).perform(click());
    }

//    @Test
//    public void testSteps(){
//        onView(withId(R.id.step_short_description)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
//        onView(allOf(withId(R.id.step_short_description), withText("Recipe Introduction"))).perform(click());
//    }

    @Test
    public void testRv3() {
        onView(allOf(withId(R.id.recipe_name), withText(map.get(2)))).perform(click());
    }

    @Test
    public void testRv4() {
        onView(allOf(withId(R.id.recipe_name), withText(map.get(2)))).perform(click());
    }


    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("neu.droid.guy.baking_app", appContext.getPackageName());
    }


    //    @Before
//    public void beforeTestSteps() {
//        onView(allOf(withId(R.id.recipe_name), withText("Nutella"))).perform(click());
//    }

//    @Test
//    public void testSteps() {
//        onView(allOf(withId(R.id.step_short_description), withText("Recipe Introduction")))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
//    }
//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        assertEquals("neu.droid.guy.baking_app", appContext.getPackageName());
//    }


}
