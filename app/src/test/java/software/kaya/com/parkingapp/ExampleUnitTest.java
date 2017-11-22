package software.kaya.com.parkingapp;

import android.app.AlertDialog;

import org.junit.Test;

import software.kaya.com.parkingapp.Activities.MapsActivity;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {

    private MapsActivity mapsActivity;
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Test(){
        mapsActivity = new MapsActivity();
        assertEquals(AlertDialog.class, mapsActivity.createSimpleDialog().getClass());
    }

}