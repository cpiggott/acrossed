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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                displaySnackbar(itemData.getTaskName() + " removed", "Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTask(pos, itemData);
                    }
                });
                return true; //true will move the front view to its starting position
            }

            @Override
            public boolean swipeRight(Task itemData) {
                //do something
                displaySnackbar("Item will eventually be recycled");
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



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeToAction.swipeRight(2);
//            }
//        }, 3000);

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
                    tasks = list;
                    adapter = new TaskAdapter(tasks);
                    recyclerView.setAdapter(adapter);
                    String t = "test";
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
