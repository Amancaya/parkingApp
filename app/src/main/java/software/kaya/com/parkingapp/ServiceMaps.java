package software.kaya.com.parkingapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import software.kaya.com.parkingapp.Modelo.CollectionRoutes;

/**
 * Created by root on 04-10-17.
 */

public interface ServiceMaps {
    //AIzaSyC22GfkHu9FdgT9SwdCWMwKX1a4aohGifM
    @GET("api/directions/json?key=AIzaSyAUKolaHNCItZXALsmUmWZbZ7w8iuwlH8c")
    Call<CollectionRoutes> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}
