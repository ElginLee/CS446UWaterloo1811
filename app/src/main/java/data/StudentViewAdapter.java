package data;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.uwaterloo.ewslee.boardcast.R;
import data.model.Student;

/**
 * Created by kianl on 2/24/2018.
 */

public class StudentViewAdapter extends RecyclerView.Adapter<StudentViewAdapter.StudentViewHolder>  {

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final TextView studentItemView;

        private StudentViewHolder(View itemView) {
            super(itemView);
            studentItemView = itemView.findViewById(R.id.title);
        }
    }

    private final LayoutInflater mInflater;
    private List<Student> studentList; // Cached copy of words

    public StudentViewAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.student_layout, parent, false);
        return new StudentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        if (studentList != null) {
            Student current = studentList.get(position);
            holder.studentItemView.setText(current.getStudentId());
        } else {
            // Covers the case of data not being ready yet.
            holder.studentItemView.setText("No Word");
        }
    }

    public void setStudent(List<Student> student){
        studentList = student;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,

    @Override
    public int getItemCount() {
        if (studentList != null)
            return studentList.size();
        else return 0;
    }
}
