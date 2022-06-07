package com.example.classrep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.classrep.Istituto;
import com.example.classrep.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IstitutiAdapter extends RecyclerView.Adapter<IstitutiAdapter.IstitutiViewHolder> {
    private List<Istituto> istituti = new ArrayList<>();
    private Context context;

    public IstitutiAdapter(Context context, List<Istituto> istituti) {
        this.context = context;
        this.istituti = istituti;
    }

    @Override
    public IstitutiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_layout, parent, false);
        IstitutiViewHolder viewHolder = new IstitutiViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IstitutiViewHolder holder, int position) {
        holder.bindIstituto(istituti.get(position));
    }

    @Override
    public int getItemCount() {
        return istituti.size();
    }

    public class IstitutiViewHolder extends RecyclerView.ViewHolder {
        //@Bind(R.id.restaurantImageView) ImageView mRestaurantImageView;
        //@BindView(R.id.restaurantNameTextView) TextView mNameTextView;
        @BindView(R.id.classe) TextView classeTextView;
        @BindView(R.id.school) TextView schoolTextView;
        private Context mContext;

        public IstitutiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindIstituto(Istituto istituto) {
            //mNameTextView.setText(restaurant.getName());
            classeTextView.setText(istituto.getClasse());
            schoolTextView.setText(istituto.getIstituto());
        }
    }
}
