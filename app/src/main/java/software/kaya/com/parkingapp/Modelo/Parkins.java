package software.kaya.com.parkingapp.Modelo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 03-10-17.
 */

public class Parkins implements Serializable {
    private String address;
    private int spaces_quantity;
    private double latitude;
    private double longitude;
    private String name;
    private boolean status;
    private String working_hours;
    private HashMap<String, String> visit;

    private String kilometros;
    private String tiempo;

    public Parkins() {}

    public HashMap<String, String> getVisit() {
        return visit;
    }

    public void setVisit(HashMap<String, String> visit) {
        this.visit = visit;
    }

    public String getKilometros() {
        return kilometros;
    }

    public void setKilometros(String kilometros) {
        this.kilometros = kilometros;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpaces_quantity() {
        return spaces_quantity;
    }

    public void setSpaces_quantity(int spaces_quantity) {
        this.spaces_quantity = spaces_quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }
}
