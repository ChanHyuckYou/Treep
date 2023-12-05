//package com.example.myapplication.kakao;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.myapplication.R;
//import java.util.ArrayList;
//
//public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.ViewHolder> {
//
//    private ArrayList<ListLayout> itemList;
//
//    public ListAdapter2(ArrayList<ListLayout> itemList) {
//        this.itemList = itemList;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        ListLayout item = itemList.get(position);
//        holder.name.setText(item.getName());
//        holder.road.setText(item.getRoad());
//        holder.address.setText(item.getAddress());
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    public void setItemList(ArrayList<ListLayout> itemList) {
//        this.itemList = itemList;
//        notifyDataSetChanged();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView name;
//        public TextView road;
//        public TextView address;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.tv_list_name);
//            road = itemView.findViewById(R.id.tv_list_road);
//            address = itemView.findViewById(R.id.tv_list_address);
//        }
//    }
//}
