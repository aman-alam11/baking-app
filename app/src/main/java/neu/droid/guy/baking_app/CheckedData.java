package neu.droid.guy.baking_app;

import java.util.HashMap;

public class CheckedData {

    private static CheckedData checkedDataInstance;
    private HashMap<Integer, HashMap<Integer, Boolean>> mIngredientsHashMap;
    private HashMap<Integer, HashMap<Integer, Boolean>> mStepsHashMap;

    private CheckedData() {
        mIngredientsHashMap = new HashMap<>();
    }


    public static CheckedData newInstance() {
        if (checkedDataInstance == null) {
            checkedDataInstance = new CheckedData();
        }
        return checkedDataInstance;
    }


    public HashMap getIngredientsCompleted() {
        return mIngredientsHashMap;
    }

    public HashMap getStepsCompleted() {
        return mStepsHashMap;
    }
}
