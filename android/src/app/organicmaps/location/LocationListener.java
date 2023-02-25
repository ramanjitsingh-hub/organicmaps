package app.organicmaps.location;

import android.app.PendingIntent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

public interface LocationListener
{
  class Simple implements LocationListener
  {
    @Override
    public void onLocationUpdated(@NonNull Location location) {}

    @Override
    public void onCompassUpdated(double north) {}

    @Override
    public void onLocationDisabled() {}

    @Override
    public void onLocationResolutionRequired(@NonNull PendingIntent pendingIntent) {}
  }

  void onLocationUpdated(@NonNull Location location);

  void onCompassUpdated(double north);

  void onLocationDisabled();

  void onLocationResolutionRequired(@NonNull PendingIntent pendingIntent);
}
