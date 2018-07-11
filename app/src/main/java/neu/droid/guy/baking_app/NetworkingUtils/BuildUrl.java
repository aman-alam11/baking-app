package neu.droid.guy.baking_app.NetworkingUtils;

import android.net.Uri;

import java.net.URL;

public class BuildUrl {
//https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json

    public static String buildRecipeUrl() {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("https")
                .authority("d17h27t6h515a5.cloudfront.net")
                .appendPath("topher")
                .appendPath("2017")
                .appendPath("May")
                .appendPath("59121517_baking")
                .appendPath("baking.json")
                .build().toString();
    }
}
