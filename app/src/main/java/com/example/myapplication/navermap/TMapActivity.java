//package com.example.myapplication;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.PointF;
//import android.os.Bundle;
//import android.os.PersistableBundle;
//import android.util.Log;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.skt.Tmap.TMapMarkerItem;
//import com.skt.Tmap.TMapPoint;
//import com.skt.Tmap.TMapView;
//import com.skt.Tmap.poi_item.TMapPOIItem;
//
//import java.util.ArrayList;
//
//public class TMapActivity extends AppCompatActivity {
//    TMapView mLlMapView;
//    TMapView tMapView = new TMapView(this);
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_kakaomap);
//
//        setTMapAuth();
//        initialize();
//
//
//
//
//    }
//
//    private void setTMapAuth() {
//
//        tMapView.setSKTMapApiKey("46SxPdJmP72EKAXWkT6Qaa6AZB9hAwGW7Jzq3z2G");
//    }
//
//    private void initialize() {
//        tMapView = findViewById(R.id.MapView222);
//
//        tMapView.setOnClickListenerCallBack(mOnClickListenerCallback);
//        tMapView.setZoomLevel(10);
//    }
//
//    TMapView.OnClickListenerCallback mOnClickListenerCallback
//            = new TMapView.OnClickListenerCallback() {
//        @Override
//        public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList,
//                                    ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
//            double latitude = tMapPoint.getLatitude();
//            double longitude = tMapPoint.getLongitude();
//
//            return false;
//        }
//
//        @Override
//        public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList,
//                                      ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
//            return false;
//        }
//    };
//
//    private void setMultiMarkers(ArrayList<TMapPoint> arrTPoint, ArrayList<String> arrTitle,
//                                 ArrayList<String> arrAddress) {
//        for (int i = 0; i < arrTPoint.size(); i++) {
//            Bitmap bitmapIcon = createMarkerIcon(R.drawable.pin_r_m_a);
//
//            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
//            tMapMarkerItem.setIcon(bitmapIcon);
//
//            tMapMarkerItem.setTMapPoint(arrTPoint.get(i));
//
//            tMapView.addMarkerItem("markerItem" + i, tMapMarkerItem);
//
//            setBalloonView(tMapMarkerItem, arrTitle.get(i), arrAddress.get(i));
//        }
//    }
//
//    private void setBalloonView(TMapMarkerItem marker, String title, String address) {
//        marker.setCanShowCallout(true);
//
//        if (marker.getCanShowCallout()) {
//            marker.setCalloutTitle(title);
//            marker.setCalloutSubTitle(address);
//
//            Bitmap bitmap = createMarkerIcon(R.drawable.arrow);
//            marker.setCalloutRightButtonImage(bitmap);
//        }
//    }
//
//    private Bitmap createMarkerIcon(int image) {
//        Log.e("MapViewActivity", "(F)   createMarkerIcon()");
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                image);
//        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
//
//        return bitmap;
//    }
//}
//
//
