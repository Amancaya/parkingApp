package software.kaya.com.parkingapp.Dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import software.kaya.com.parkingapp.Modelo.Parkins;
import software.kaya.com.parkingapp.R;

/**
 * Created by root on 03-10-17.
 */

public class DialogDetalle extends DialogFragment {
    public static final String TAG = "Dialogo";
    public static final String TAG_PARKING = "Parking";

    private Parkins parkins;
    private TextView name, space, address, attention;

    public DialogDetalle(){}

    public static DialogDetalle newInstance(Parkins parkins){
        DialogDetalle dialogDetalle = new DialogDetalle();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_PARKING, parkins);
        dialogDetalle.setArguments(bundle);
        return dialogDetalle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parkins = (Parkins) getArguments().getSerializable(TAG_PARKING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detalle, container, false);

        name = view.findViewById(R.id.name);
        space = view.findViewById(R.id.space);
        address = view.findViewById(R.id.address);
        attention = view.findViewById(R.id.attention);

        name.setText("Nombre: "+parkins.getName());
        space.setText("Espacios libres: "+parkins.getSpaces_quantity());
        attention.setText("Atencion: "+parkins.getWorking_hours());
        address.setText("Direccion: "+parkins.getAddress());
        return view;
    }
}
