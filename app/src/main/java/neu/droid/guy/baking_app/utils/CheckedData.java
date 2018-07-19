package neu.droid.guy.baking_app.utils;

import java.util.HashMap;

public class CheckedData {

    private static CheckedData checkedDataInstance;
    private HashMap<Integer, HashMap<Integer, Boolean>> mIngredientsHashMap;
    private HashMap<Integer, HashMap<Integer, Boolean>> mStepsHashMap;

    private CheckedData() {
        mIngredientsHashMap = new HashMap<>();
        mStepsHashMap = new HashMap<>();
    }


    public static CheckedData getInstance() {
        if (checkedDataInstance == null) {
            checkedDataInstance = new CheckedData();
        }
        return checkedDataInstance;
    }


    public HashMap getIngredientsCompleted(int recipeNumber) {
        if (mIngredientsHashMap.get(recipeNumber) == null) {
            mIngredientsHashMap.put(recipeNumber, new HashMap<Integer, Boolean>());
        }
        return mIngredientsHashMap.get(recipeNumber);
    }

    public HashMap getStepsCompleted(int recipeNumber) {
        if (mStepsHashMap.get(recipeNumber) == null) {
            mStepsHashMap.put(recipeNumber, new HashMap<Integer, Boolean>());
        }
        return mStepsHashMap.get(recipeNumber);
    }
}
