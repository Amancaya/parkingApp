package software.kaya.com.parkingapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.awt.font.TextAttribute;
import java.util.List;

import software.kaya.com.parkingapp.Modelo.Parkins;

/**
 * Created by root on 04-10-17.
 */

public class AdapterLista extends BaseAdapter {
    List<Parkins> parkinsList;
    Context context;
    LayoutInflater layoutInflater;

    public AdapterLista(Context context, List<Parkins> parkinsList){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.parkinsList = parkinsList;
    }

    @Override
    public int getCount() {
        return parkinsList.size();
    }

    @Override
    public Parkins getItem(int i) {
        return parkinsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = layoutInflater.inflate(R.layout.item_markers, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder =(ViewHolder) view.getTag();
        }

        Parkins parkins = parkinsList.get(i);

        viewHolder.name_list.setText(parkins.getName());
        viewHolder.address_list.setText(parkins.getAddress());
        viewHolder.space_list.setText("Espacios libres "+String.valueOf(parkins.getSpaces_quantity()));
        viewHolder.kilometros.setText(parkins.getKilometros());
        viewHolder.tiempo.setText(parkins.getTiempo());
        viewHolder.latitude.setText(String.valueOf(parkins.getLatitude()));
        viewHolder.longitud.setText(String.valueOf(parkins.getLongitude()));
        return view;
    }

    private class ViewHolder{
        TextView name_list, address_list, space_list, kilometros, tiempo, latitude, longitud;
        public ViewHolder(View view){
            name_list = (TextView) view.findViewById(R.id.name_list);
            address_list = (TextView) view.findViewById(R.id.address_list);
            space_list = (TextView) view.findViewById(R.id.space_list);
            kilometros = (TextView) view.findViewById(R.id.kilometros);
            tiempo = (TextView) view.findViewById(R.id.tiempo);
            latitude = (TextView) view.findViewById(R.id.latitude);
            longitud = (TextView) view.findViewById(R.id.longitud);
        }
    }
}
