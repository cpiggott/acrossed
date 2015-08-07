package co.acrossed.android;

import com.parse.ParseClassName;

/**
 * Created by CHRIS on 8/6/2015.
 */

@ParseClassName("Category")
public class Category extends Task {

    private String categoryTitle;

    public Category(){}

    public Category(String category){
        this.categoryTitle = category;
    }

    public String getCategoryTitle(){
        return categoryTitle;
    }
}
