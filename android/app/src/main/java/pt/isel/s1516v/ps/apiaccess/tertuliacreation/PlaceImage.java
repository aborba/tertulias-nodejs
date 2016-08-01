package pt.isel.s1516v.ps.apiaccess.tertuliacreation;

import android.graphics.Bitmap;

public class PlaceImage {

    final Bitmap image;
    final String attribution;

    public PlaceImage(Bitmap image, String attribution) {
        this.image = image;
        this.attribution = attribution;
    }
}
