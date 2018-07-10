package neu.droid.guy.baking_app.Pojo;

import org.json.JSONArray;

import java.util.List;

public class Baking {
    private String name;
    private String image;
    private int servings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

}
