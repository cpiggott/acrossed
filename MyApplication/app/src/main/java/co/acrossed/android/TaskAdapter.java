package co.acrossed.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by chrispiggott on 8/8/15.
 */
public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Task> items;



    public class TaskViewHolder extends SwipeToAction.ViewHolder<Task>{

        TextView taskTitle;

        public TaskViewHolder(View v) {
            super(v);

            taskTitle = (TextView) v.findViewById(R.id.taskTitle);
        }


    }


    public TaskAdapter(List<Task> items){
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_row, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Task item = items.get(position);
        TaskViewHolder vh = (TaskViewHolder) viewHolder;
        vh.taskTitle.setText(item.getTaskName());
        vh.data = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
