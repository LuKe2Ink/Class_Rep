package com.example.classrep.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.database.entity.Institute;
import com.example.classrep.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class InstituteAdapter extends RecyclerView.Adapter<InstituteAdapter.InstituteViewHolder> {

    private List<Institute> institutes = new ArrayList<>();
    private Context context;

    private onInstituteListener mOnInstituteListener;

    public InstituteAdapter(Context context, List<Institute> institutes, onInstituteListener onInstituteListener) {
        this.context = context;
        this.institutes = institutes;

        this.mOnInstituteListener = onInstituteListener;
    }

    @Override
    public InstituteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_selection, parent, false);
        InstituteViewHolder viewHolder = new InstituteViewHolder(view, mOnInstituteListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InstituteViewHolder holder, int position) {
        holder.bindInstitute(institutes.get(position));
    }

    @Override
    public int getItemCount() {
        return institutes.size();
    }

    public class InstituteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        onInstituteListener onInstituteListener;

        @BindView(R.id.grade) TextView gradeTextView;
        @BindView(R.id.school) TextView schoolTextView;
        @BindView(R.id.profileIcon) CircleImageView profileIcon;
        private Context mContext;

        public InstituteViewHolder(View itemView, onInstituteListener onInstituteListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            this.onInstituteListener = onInstituteListener;

            itemView.setOnClickListener(this);
        }

        public void bindInstitute(Institute institute) {
            gradeTextView.setText(institute.getGrade());
            schoolTextView.setText(institute.getInstitute());
            System.out.println(institute.getImage());
            if(!institute.getImage().contains("nada")){
                profileIcon.setImageURI(Uri.parse(institute.getImage()));
            } else {
                profileIcon.setImageResource(R.drawable.splashscreen);
            }
            //schoolTextView.setTextColor(Color.parseColor("#000000"));
        }

        @Override
        public void onClick(View view) {
            onInstituteListener.onInstituteClick(getAdapterPosition());
        }
    }

    public interface onInstituteListener{
        void onInstituteClick(int position);
    }
}
