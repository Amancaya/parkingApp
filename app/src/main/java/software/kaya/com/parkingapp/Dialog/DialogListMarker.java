package software.kaya.com.parkingapp.Dialog;

import android.app.DialogFragment;
import android.os.Bundle;

import java.util.List;

import software.kaya.com.parkingapp.Modelo.Parkins;

/**
 * Created by root on 04-10-17.
 */

public class DialogListMarker extends DialogFragment{

    private static final String TAG_LIST_PARKING = "Listas";

    public static DialogListMarker newInstance(List<Parkins> parkinsList){
        DialogListMarker dialogListMarker = new DialogListMarker();
        Bundle bundle = new Bundle();
        //bundle.putSerializable(TAG_LIST_PARKING, parkinsList);

        return dialogListMarker;
    }
}
