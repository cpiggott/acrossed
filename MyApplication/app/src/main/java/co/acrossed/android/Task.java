package co.acrossed.android;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by CHRIS on 8/2/2015.
 */

@ParseClassName(ParseConsts.Task._Class)
public class Task extends ParseObject {

    public Task(){ }

    public static ParseQuery<Task> getQuery(){
        return ParseQuery.getQuery(Task.class);
    }

    public String getTaskName(){
        return getString(ParseConsts.Task.TaskName);
    }

    public void setTaskName(String taskName){
        put(ParseConsts.Task.TaskName, taskName);
    }

    public String getDescription(){
        return getString(ParseConsts.Task.Description);
    }

    public void setDescription(String description){
        put(ParseConsts.Task.Description, description);
    }

    public void setUser(){
        put(ParseConsts.Task.User, ParseUser.getCurrentUser());
    }

    public void setUser(String parseUserId){
        put(ParseConsts.Task.User, parseUserId);
    }

    public Date getCompletedAt(){
        return getDate(ParseConsts.Task.CompletedAt);
    }

    public void setCompletedAt(Date completeAt){
        put(ParseConsts.Task.CompletedAt, completeAt);
    }

    public boolean getIsComplete(){
        return getBoolean(ParseConsts.Task.IsComplete);
    }

    public void setIsComplete(boolean isComplete){
        put(ParseConsts.Task.IsComplete, isComplete);
    }

    public boolean getIsArchived(){
        return getBoolean(ParseConsts.Task.IsArchived);
    }

    public void setIsArchived(boolean isArchived){
        put(ParseConsts.Task.IsArchived, isArchived);
    }

    public String getCategory(){
        return getString(ParseConsts.Task.Category);
    }

    public void setCategory(String category){
        put(ParseConsts.Task.Category, category);
    }

    public Date getRemindAfter(){
        return getDate(ParseConsts.Task.RemindAfter);
    }

    public void setRemindAfter(Date date){
        put(ParseConsts.Task.RemindAfter, date);
    }


}



