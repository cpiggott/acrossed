package co.acrossed.android;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    List<Task> tasks = new ArrayList<>();
    List<String> categories = new ArrayList<>();

    private AbsListView mListView;
    private TaskAdapter taskAdapter;



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

        taskAdapter = new TaskAdapter(getActivity(), tasks);

        mListView = (AbsListView) rootView.findViewById(R.id.list_tasks);

        ((AdapterView< ListAdapter>)mListView).setAdapter(taskAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //@TODO: Open a dialog with info
                String t = "sd";

            }
        });

        ParseQuery<Task> query = Task.getQuery();
        query.whereEqualTo(ParseConsts.Task.IsComplete, false).whereEqualTo(ParseConsts.Task.User, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> list, ParseException e) {
                if (e == null && list.size() > 0) {


                    Collections.sort(list, new CategoryComparotor());//Sort list based on category
                    String category = list.get(0).getCategory();//Name of first category
                    categories.add(category);

                    tasks.add(new Category(category));


                    for(Task task : list){
                        if(task.getCategory().equals(category)){
                            tasks.add(task);
                        } else {
                            category = task.getCategory();
                            categories.add(category);

                            tasks.add(new Category(category));
                            tasks.add(task);
                        }
                    }

                    //taskAdapter.addAll(tasks);
                    taskAdapter.notifyDataSetChanged();
                }
            }
        });

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
                        addBool = false;
                        description.setError("Please enter a description");
                    }
                    if (categoryString.isEmpty()){
                        addBool = false;
                        category.setError("Please enter a category");
                    }

                    if(addBool){
                        final Task newTask = new Task();
                        newTask.setTaskName(task);
                        newTask.setDescription(descriptionString);
                        newTask.setCategory(categoryString);
                        newTask.setIsArchived(false);
                        newTask.setIsComplete(false);
                        newTask.setUser();

//                        if(categoryString.charAt(categoryString.length()-1) == ' '){
//                            categoryString = categoryString.substring(0, categoryString.length()-1);
//                        }

                        boolean addCategory = true;

                        for(int i = 0; i < categories.size(); i++){
                            String checkString = categories.get(i);
                            if((checkString.equals(categoryString))){
                                addCategory = false;
                                break;
                            }
                        }


                        if(addCategory){
                            tasks.add(new Category(newTask.getCategory()));
                            categories.add(newTask.getCategory());
                        }

                        newTask.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                tasks.add(newTask);

                                taskAdapter.notifyDataSetChanged();

                                taskAdapter.sort(new CategoryComparotor());

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

            return true;
        }

        return super.onOptionsItemSelected(item);
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




    public class TaskAdapter extends ArrayAdapter<Task> {



        public TaskAdapter(Context context, List<Task> messages) {
            super(context, R.layout.task_item_row, messages);

        }
        //TODO: Clean this up!
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final ViewHolder viewHolder;
            final Task task = getItem(position);


            if(task instanceof Category){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.task_category_row, parent, false);


                TextView categoryText = (TextView) convertView.findViewById(R.id.textViewCategory);
                categoryText.setText(((Category) task).getCategoryTitle());

                ImageButton addFromCategory = (ImageButton) convertView.findViewById(R.id.imageButtonAdd);
                addFromCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.add_task_dialog);

                        final EditText taskName = (EditText) dialog.findViewById(R.id.editTextTaskName);
                        final EditText description = (EditText)dialog.findViewById(R.id.editTextDescription);
                        final EditText category = (EditText) dialog.findViewById(R.id.editTextCategory);
                        category.setText(((Category) task).getCategoryTitle());



                        Button addButton = (Button) dialog.findViewById(R.id.buttonAdd);
                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean addBool = true;
                                String task = taskName.getText().toString();
                                String descriptionString = description.getText().toString();
                                String categoryString = category.getText().toString();

                                if(task.isEmpty()){
                                    addBool = false;
                                    taskName.setError("Please enter a task name");
                                }
                                if(descriptionString.isEmpty()){
                                    addBool = false;
                                    description.setError("Please enter a description");
                                }
                                if (categoryString.isEmpty()){
                                    addBool = false;
                                    category.setError("Please enter a category");
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
                                            taskAdapter.notifyDataSetChanged();

                                            taskAdapter.sort(new CategoryComparotor());
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            }
                        });

                        Button cancenlButton = (Button) dialog.findViewById(R.id.buttonCancel);
                        cancenlButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                    }
                });

            } else {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.task_item_row, parent, false);

                //initialize viewHolder
                viewHolder = new ViewHolder();
                viewHolder.tvTaskName = (TextView) convertView.findViewById(R.id.textViewTaskTitle);
                viewHolder.checkBoxTask = (CheckBox) convertView.findViewById(R.id.checkBoxTask);
                viewHolder.rlLayout = (RelativeLayout) convertView.findViewById(R.id.layoutRelative);

                viewHolder.rlLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //                Task currentTask = tasks.get(i);


                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.custom_dialog);

                        TextView description = (TextView) dialog.findViewById(R.id.textViewDescriptionDialog);
                        description.setText(task.getDescription());

                        TextView category = (TextView) dialog.findViewById(R.id.textViewCategoryDialog);
                        category.setText(task.getCategory());

                        TextView title = (TextView) dialog.findViewById(R.id.textViewTitleDialog);
                        title.setText(task.getTaskName());

                        Button okButton = (Button) dialog.findViewById(R.id.buttonOkDialog);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });



                viewHolder.checkBoxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            //change everything so that it is complete
                            task.setIsComplete(true);
                            task.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    viewHolder.tvTaskName.setPaintFlags(viewHolder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    viewHolder.tvTaskName.setTextColor(getResources().getColor(R.color.gray));
                                }
                            });

                        } else {
                            //change everything so that it is not complete
                            task.setIsComplete(false);
                            task.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    viewHolder.tvTaskName.setPaintFlags((viewHolder.tvTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG));
                                    viewHolder.tvTaskName.setTextColor(getResources().getColor(R.color.black));
                                }
                            });

                        }
                    }
                });

                viewHolder.tvTaskName.setText(task.getTaskName());

                if(task.getIsComplete()){
                    viewHolder.tvTaskName.setPaintFlags(viewHolder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.tvTaskName.setTextColor(getResources().getColor(R.color.gray));
                }
                convertView.setTag(viewHolder);
            }

            return convertView;

        }


        /**
         * The view holder design pattern prevents using findViewById()
         * repeatedly in the getView() method of the adapter.
         *
         * @see //developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
         */
        private class ViewHolder {
            CheckBox checkBoxTask;
            TextView tvTaskName;
            TextView tvCategory;
            RelativeLayout rlLayout;
        }


    }


}
