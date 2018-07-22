package neu.droid.guy.baking_app.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import neu.droid.guy.baking_app.R;
import neu.droid.guy.baking_app.model.Ingredients;
import neu.droid.guy.baking_app.views.MainActivity;

import static neu.droid.guy.baking_app.utils.Constants.WIDGET_INDEX_RECIPE_KEY;
import static neu.droid.guy.baking_app.utils.Constants.WIDGET_STRING_SET_RECIPE_KEY;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidget extends AppWidgetProvider {
    private static List<Ingredients> mListIngredients;
    private static Set<String> mIngredientsSet;
    private static int mIndex;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        StringBuilder sb = new StringBuilder();

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        Intent openIngredientsFromWidgetIntent = new Intent(context, MainActivity.class);
        //Read from shared preferences
        readIngredientsFromSharedPref(context);
        openIngredientsFromWidgetIntent.putExtra(WIDGET_INDEX_RECIPE_KEY, mIndex);

        if (mIngredientsSet != null && mIngredientsSet.size() > 0) {
            for (String s : mIngredientsSet) {
                sb.append(s).append("\n");
            }
        }

        views.setTextViewText(R.id.list_ingredients, sb.toString());

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, openIngredientsFromWidgetIntent, 0);

        views.setOnClickPendingIntent(R.id.parent_root_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    @SuppressLint("ApplySharedPref")
    public static void writeIngredientsInSharedPref(Context context, int index, String mRecipeName, List<Ingredients> listOfIngredinets) {
        mListIngredients = new ArrayList<>(listOfIngredinets.size());
        mListIngredients.addAll(listOfIngredinets);
        mIndex = index;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getStringSet(WIDGET_STRING_SET_RECIPE_KEY, null) != null) {
            // Do it on main thread and hence .commit()
            preferences.edit().putStringSet(WIDGET_STRING_SET_RECIPE_KEY, null).commit();
        }

//         Get the index of selected recipe to show its ingredients
        preferences.edit().putInt(WIDGET_INDEX_RECIPE_KEY, index).apply();
        TreeSet<String> ingredientSet = new TreeSet<>(formatListIngredients(listOfIngredinets, mRecipeName));
        preferences.edit().putStringSet(WIDGET_STRING_SET_RECIPE_KEY, ingredientSet).apply();
    }

    static void readIngredientsFromSharedPref(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mIngredientsSet = preferences.getStringSet(WIDGET_STRING_SET_RECIPE_KEY, new TreeSet<String>());
        //If this is null, it will go to catch block
        if (mIngredientsSet.isEmpty()) {
            mIngredientsSet.add("Please first choose a recipe from app to show its ingredients.");
            mIngredientsSet.add("Then add a new widget for the recipe");
        }
        mIndex = preferences.getInt(WIDGET_INDEX_RECIPE_KEY, 0);
    }

    private static Set<String> formatListIngredients(List<Ingredients> listOfIngredinets, String mRecipeName) {
        TreeSet<String> ingredientSet = new TreeSet<>();
        ingredientSet.add(mRecipeName.toUpperCase());
        for (Ingredients ingredients : listOfIngredinets) {
            ingredientSet.add(ingredients.getIngredient() + ingredients.getQuantity() + ingredients.getMeasure());
        }
        return ingredientSet;
    }

}

