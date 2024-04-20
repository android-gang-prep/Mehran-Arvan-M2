package com.example.mehranarvanm2;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mehranarvanm2.databinding.ItemBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<DataModel> dataModels;

    public ItemAdapter(List<DataModel> dataModels) {
        this.dataModels = dataModels;
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinding binding = ItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        float temp = ((dataModels.get(position).getTemp() - 32) / 1.8f);
        holder.binding.temperature.setText(String.format("%.0fºC", temp));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        SimpleDateFormat utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        long date = System.currentTimeMillis();
        try {
            ;
            date = utc.parse(dataModels.get(position).getDate()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.binding.date.setText(simpleDateFormat.format(date) + ".00");
        holder.binding.img.setImageResource(DataActivity.getImage(dataModels.get(position).getWeather_symbol()));

    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBinding binding;

        public ViewHolder(@NonNull ItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
