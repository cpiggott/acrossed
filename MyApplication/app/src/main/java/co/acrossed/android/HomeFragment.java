package co.acrossed.android;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.dift.ui.SwipeToAction;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    TaskAdapter adapter;
    SwipeToAction swipeToAction;

    List<Task> tasks = new ArrayList<>();





    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Pull all the tasks

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);



        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<Task>() {
            @Override
            public boolean swipeLeft(final Task itemData) {
                final int pos = removeTask(itemData);
                displaySnackbar(itemData.getTaskName() + " complete", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTask(pos, itemData);
                    }
                });
                return true; //true will move the front view to its starting position
            }

            @Override
            public boolean swipeRight(final Task itemData) {
                //do something
                final int pos = removeTask(itemData);
                remindDialog(itemData, pos);

                displaySnackbar(itemData.getTaskName() + " set to remind", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTask(pos, itemData);
                        itemData.setRemindAfter(new Date());
                        itemData.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                            }
                        });
                    }
                });
                return true;
            }

            @Override
            public void onClick(Task itemData) {
                //do something
                Toast.makeText(getActivity(), "Info will show", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(Task itemData) {
                //do something
                Toast.makeText(getActivity(), "Edit info", Toast.LENGTH_SHORT).show();
            }
        });



        getTasks();



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.action_add) {

            addOptionsDialog();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addOptionsDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_task_dialog);

        final EditText taskName = (EditText) dialog.findViewById(R.id.editTextTaskName);
        final EditText description = (EditText)dialog.findViewById(R.id.editTextDescription);
        final EditText category = (EditText) dialog.findViewById(R.id.editTextCategory);



        Button addButton = (Button) dialog.findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean addBool = true;
                final String task = taskName.getText().toString();
                String descriptionString = description.getText().toString();
                String categoryString = category.getText().toString();

                if(task.isEmpty()){
                    addBool = false;
                    taskName.setError("Please enter a task name");
                }
                if(descriptionString.isEmpty()){
                    descriptionString = "";
                }
                if (categoryString.isEmpty()){
                    categoryString = "";
                }

                if(addBool){
                    final Task newTask = new Task();
                    newTask.setTaskName(task);
                    newTask.setDescription(descriptionString);
                    newTask.setCategory(categoryString);
                    newTask.setIsArchived(false);
                    newTask.setIsComplete(false);
                    newTask.setUser();


                    newTask.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            tasks.add(newTask);
                            adapter.notifyDataSetChanged();


                            dialog.dismiss();
                        }
                    });

                }
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void getTasks(){
        ParseQuery<Task> query = Task.getQuery();
        query.whereEqualTo(ParseConsts.Task.IsComplete, false).whereEqualTo(ParseConsts.Task.User, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> list, ParseException e) {
                if (e == null && list.size() > 0) {
                    Collections.sort(list, new CategoryComparotor());//Sort list based on category
                    Date now = new Date();
                    for(Task t : list){
                        if(t.getRemindAfter() == null){
                            tasks.add(t);
                        } else if(now.after(t.getRemindAfter())){
                            tasks.add(t);
                        }
                    }
                    adapter = new TaskAdapter(tasks);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.gray));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private void displaySnackbar(String text){
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.gray));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private int removeTask(Task task) {
        int pos = tasks.indexOf(task);
        tasks.remove(task);
        adapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addTask(int pos, Task book) {
        tasks.add(pos, book);
        adapter.notifyItemInserted(pos);
    }

    private void remindDialog(final Task task, final int position){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_remind);

        final Button laterButton = (Button) dialog.findViewById(R.id.buttonLater);
        final Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 3); // adds 5 hours
        //laterButton.setText("Later at: " + new SimpleDateFormat("hh:mm").format(cal));

        final Button tomorrowButton = (Button) dialog.findViewById(R.id.buttonTomorrow);
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 0);
        //tomorrowButton.setText("Tomorrow at: " + new SimpleDateFormat("EEE hh:mm").format(c));


        final Button nextWeekButton = (Button) dialog.findViewById(R.id.buttonWeek);
        final Calendar date1 = Calendar.getInstance();
        date1.setTime(new Date());

        while (date1.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            date1.add(Calendar.DATE, 1);
        }

        date1.set(Calendar.HOUR_OF_DAY, 8);
        date1.set(Calendar.MINUTE, 0);

        //nextWeekButton.setText("New week at: " + new SimpleDateFormat("EEE hh:mm").format(c));



        final Button cancelButton = (Button) dialog.findViewById(R.id.buttonCancelDialog);

        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                task.setRemindAfter(cal.getTime());

                laterButton.setEnabled(false);
                tomorrowButton.setEnabled(false);
                nextWeekButton.setEnabled(false);

                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                    }
                });
            }
        });

        tomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                task.setRemindAfter(c.getTime());

                laterButton.setEnabled(false);
                tomorrowButton.setEnabled(false);
                nextWeekButton.setEnabled(false);

                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                    }
                });
            }
        });


        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                task.setRemindAfter(date1.getTime());

                laterButton.setEnabled(false);
                tomorrowButton.setEnabled(false);
                nextWeekButton.setEnabled(false);

                task.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dialog.dismiss();
                    }
                });
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(position, task);
                dialog.dismiss();
            }
        });



        dialog.show();
    }




    public class CategoryComparotor implements Comparator {


        @Override
        public int compare(Object o, Object t1) {
            String task1= ((Task)o).getCategory();
            String task2 = ((Task)t1).getCategory();

            // ascending order (descending order would be: task2.compareTo(name1))
            return task1.compareTo(task2);
        }
    }






}
