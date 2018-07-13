package neu.droid.guy.baking_app.NetworkingUtils;
//https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import neu.droid.guy.baking_app.model.Baking;

public class ParseJson {

    private getJsonResponseAsync mResponseAsync;

    public ParseJson(getJsonResponseAsync responseAsync) {
        mResponseAsync = responseAsync;
    }

    /**
     * @param urlToHit
     * @param context
     */
    public void makeNetworkRequest(String urlToHit, Context context) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlToHit,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Use Gson to parse response
                        Gson gson = new Gson();
                        Type bakingResponseListType = new TypeToken<ArrayList<Baking>>() {
                        }.getType();
                        // Change the JsonArray to ArrayList of Baking objects
                        List<Baking> listOfBakingObjects = gson.fromJson(response.toString(), bakingResponseListType);
                        // To send data to calling activity async
                        mResponseAsync.getResponse(listOfBakingObjects);
                    }
                },
                // TODO: Handle Error properly
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(this.getClass().getSimpleName(), error.toString());
                    }
                });

        VolleyNetworkQueue.getInstance(context).getRequestQueue().add(arrayRequest);
    }


    /**
     * An interface to transfer data to calling activity in an async manner
     */
    public interface getJsonResponseAsync {
        void getResponse(List<Baking> listOfBaking);
    }
}
