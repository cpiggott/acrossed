package co.acrossed.android;


import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    List<Task> tasks = new ArrayList<>();

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
            }
        });

        ParseQuery<Task> query = Task.getQuery();
        query.whereEqualTo(ParseConsts.Task.IsComplete, false).whereEqualTo(ParseConsts.Task.IsArchived, false);

        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> list, ParseException e) {
                if (e == null && list.size() > 0) {
                    tasks = list;
                    taskAdapter.addAll(tasks);
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









    public class TaskAdapter extends ArrayAdapter<Task> {



        public TaskAdapter(Context context, List<Task> messages) {
            super(context, R.layout.task_item_row, messages);

        }
        //TODO: Clean this up!
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final ViewHolder viewHolder;
            Task task = getItem(position);


            if(task instanceof Category){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.task_category_row, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.tvTaskName = (TextView) convertView.findViewById(R.id.textViewCategory);
                convertView.setTag(viewHolder);


            } else {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.task_item_row, parent, false);

                //initialize viewHolder
                viewHolder = new ViewHolder();
                viewHolder.tvTaskName = (TextView) convertView.findViewById(R.id.textViewTaskTitle);
                viewHolder.checkBoxTask = (CheckBox) convertView.findViewById(R.id.checkBoxTask);

                viewHolder.checkBoxTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked){
                            //change everything so that it is complete
                            viewHolder.tvTaskName.setPaintFlags(viewHolder.tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            viewHolder.tvTaskName.setTextColor(getResources().getColor(R.color.gray));
                        } else {
                            //change everything so that it is not complete
                            viewHolder.tvTaskName.setPaintFlags((viewHolder.tvTaskName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG));
                            viewHolder.tvTaskName.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
                });

                viewHolder.tvTaskName.setText(task.getTaskName());
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
        }


    }


}
