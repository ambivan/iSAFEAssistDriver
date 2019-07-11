package com.prateek.isafeassistdriver.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateek.isafeassistdriver.R;
import com.prateek.isafeassistdriver.dao.TripDetails;

import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

    Context context;
    List<TripDetails> list = new ArrayList<>();

    public TripAdapter(Context context, List<TripDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_binder, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        TripDetails details = list.get(i);
        String pricing= details.getPrice();
        if(pricing.equals("₹ 1200/-")){
            holder.price.setText("₹ 1200/-");
            holder.servicetype.setText("Towing Service");
        }else{
            holder.price.setText("₹ 500/-");
            holder.servicetype.setText("Call-Out Service");

        }
        holder.uphone.setText(details.getUserphone());
        holder.uname.setText(details.getUsername());
        holder.time.setText(details.getTimeservice());
        holder.locate.setText(details.getLocateservice());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView price, uname, uphone, time, locate, servicetype;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.serviceprice);
            uname = itemView.findViewById(R.id.serviceuname);
            uphone = itemView.findViewById(R.id.serviceuphone);
            time = itemView.findViewById(R.id.datetime);
            servicetype= itemView.findViewById(R.id.servicetype);
            locate = itemView.findViewById(R.id.locationofservice);
        }
    }
}
