package neu.droid.guy.baking_app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import neu.droid.guy.baking_app.model.Steps;
import neu.droid.guy.baking_app.views.MainActivity;
import neu.droid.guy.baking_app.views.StepsView;
import neu.droid.guy.baking_app.views.StepsViewFragment;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class StepsViewTest {

    @Rule
    public ActivityTestRule<StepsView> mStepsTestRule = new ActivityTestRule<>(StepsView.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("neu.droid.guy.baking_app", appContext.getPackageName());
    }


    @Test
    public void perform() {
        onData(anything()).inAdapterView(withId(R.id.view_recipe_rv))
                .atPosition(0)
                .onChildView(withId(R.id.recipe_name))
                .check(matches(withText(startsWith("Nu"))));
    }

}
