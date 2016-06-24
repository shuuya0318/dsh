package dsh.dsh;


import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.ConnectException;

import dsh.dsh.example.ServiceExample;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener {

    private int timer = 0;
    private GoogleMap mMap;
    private LatLng ex_point;
    private LatLng current_point;
    private Double lat, lon;
    private boolean isInit = false;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        // BitmapDescriptionFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());
        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
        mMap = mySupportMapFragment.getMap();
        ex_point = new LatLng(0,0);
        current_point = new LatLng(0,0);

        final Button btn_start = (Button)this.findViewById(R.id.btn_start);
        final Button btn_pause = (Button)this.findViewById(R.id.btn_pause);
        final Button btn_finish = (Button)this.findViewById(R.id.btn_finish);

        init();
        mMap.setOnMapClickListener(this);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn_start = (Button)findViewById(R.id.btn_start);
                btn_start.setVisibility(View.INVISIBLE);
                btn_start.setClickable(false);
                btn_pause.setVisibility(View.VISIBLE);
                btn_pause.setClickable(true);
                if(view.getId() == R.id.btn_start) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            handler.sendEmptyMessageDelayed(0, 1000);
                            TextView tv = (TextView)findViewById(R.id.tv_timer);
                            timer++;
                            tv.setText("Timer: " + timer);

                            GpsInfo gps = new GpsInfo(MapsActivity.this);
                            // GPS 사용 시 카메라 이동하며 마커가 이동한 line 그리기
                            if(gps.isGetLocation()) {

                                Log.d("Gps Usable: ", "Using.");
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                LatLng latLng = new LatLng(latitude, longitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                                current_point = latLng;
                                mMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(30.0f).geodesic(true).add(latLng).add(ex_point));
                                ex_point = latLng;

                                MarkerOptions optFirst = new MarkerOptions();
                                optFirst.alpha(0.5f);
                                optFirst.anchor(0.5f, 0.5f);
                                optFirst.position(latLng);
                                optFirst.title("현재 위치");
//                                optFirst.snippet("Shuuya");

                                optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                                mMap.addMarker(optFirst).showInfoWindow();
                                TextView tvLatLng = (TextView)findViewById(R.id.latLng);
                            }
                        }
                    };
                    handler.sendEmptyMessage(0);
                }
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btn_pause) {
                    if(handler != null) {
                        handler.removeMessages(0);
                        btn_start.setVisibility(View.VISIBLE);
                        btn_start.setClickable(true);
                        btn_pause.setVisibility(View.INVISIBLE);
                        btn_pause.setClickable(false);
                    }
                }
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn_start = (Button)findViewById(R.id.btn_start);
                btn_start.setVisibility(View.VISIBLE);
                btn_start.setClickable(true);
                btn_pause.setVisibility(View.INVISIBLE);
                btn_pause.setClickable(false);
                if(view.getId() == R.id.btn_finish) {
                    if(handler != null)
                        handler.removeMessages(0);
                    TextView tv = (TextView)findViewById(R.id.tv_timer);
                    timer = 0;
                    tv.setText("Timer: " + timer);
                }

            }
        });

        WifiManager wm;
        ScanResult sr;

    }

    @Override
    public void onMapClick(LatLng point) {
        if(!isInit)
            init();

        Log.d("Touch event", "touched");
        Point screenPt = mMap.getProjection().toScreenLocation(point);
        GpsInfo gps = new GpsInfo(MapsActivity.this);
        if(gps.isGetLocation()) {

        }

    }

    private void init() {
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);
        GoogleMap mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        GpsInfo gps = new GpsInfo(MapsActivity.this);

        gps.getLocation();

        if(gps.isGetLocation()) {
            // 출발지 객체 초기화
            Location locationStart = new Location("start");
            LatLng startLatLng = new LatLng(37.4102739, 127.1202895);
            ex_point = startLatLng;

            // 출발 지점 위도 경도 지정
            locationStart.setLatitude(startLatLng.latitude);
            locationStart.setLongitude(startLatLng.longitude);

            // 도착지 객체 초기화
            Location locationObjective = new Location("objective");
            LatLng objectiveLatLng = new LatLng(37.4110000, 127.1304000);

            // 도착 지점 위도 경도 지정
            locationObjective.setLatitude(objectiveLatLng.latitude);
            locationObjective.setLongitude(objectiveLatLng.longitude);

            // 거리 측정 결과

            double distance = locationStart.distanceTo(locationObjective);
            //String tmpDist = Double.toString(distance);
            //String dist = tmpDist.substring(0, tmpDist.lastIndexOf(".", 0));
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            current_point = new LatLng(lat, lon);

            // 현재 내 위치로 이동
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(current_point));

            // Map을 Zoom
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            Marker start = mMap.addMarker(new MarkerOptions().position(startLatLng).title("출발지").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            Marker objective = mMap.addMarker(new MarkerOptions().position(objectiveLatLng).title("목적지 / 거리: " + Double.toString(distance) + "m").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            Marker mk1 = mMap.addMarker(new MarkerOptions().position(current_point).title("위도: " + Double.toString(lat) + " " + "경도: " + Double.toString(lon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            Marker mk2 = mMap.addMarker(new MarkerOptions().position(new LatLng(37.4102360, 127.129864)).title("위도: " + Double.toString(37.4102360) + " " + "경도: " + Double.toString(127.129864)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            Marker mk3 = mMap.addMarker(new MarkerOptions().position(new LatLng(37.4098745, 127.130264)).title("위도: " + Double.toString(37.4098745) + " " + "경도: " + Double.toString(127.130264)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(37.4102739, 127.1202895))
                    .add(new LatLng(37.4093452, 127.1233000))
                    .add(new LatLng(37.4095501, 127.1263513))
                    .add(new LatLng(37.4099985, 127.1293912))
                    .add(new LatLng(37.4102739, 127.1304000))
                    .add(new LatLng(37.4110000, 127.1304000))
                    .color(Color.RED);
            Polyline polyline = mMap.addPolyline(rectOptions);

            // 마커 설정
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.alpha(0.5f);
            optFirst.anchor(0.5f, 0.5f);
            optFirst.position(current_point); // 현재 위도, 경도
            optFirst.title("현재 위치");
            optFirst.snippet("Snippet");
            optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mMap.addMarker(optFirst).showInfoWindow();
                new Thread() {
                    ServiceExample example = RpcClient.lookupService("192.168.1.64", 4885, "example", ServiceExample.class);
                    TextView tVRPC = (TextView) findViewById(R.id.TVRPC);

                        public void run () {
                            patchEOFException();
                            int i = 0;
                            try {
                                BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (mBTAdapter == null) {
                                    tVRPC.setText("블루투스가 지원 안되요 ㅠㅠ 망했어요..");
                                    return;
                                } else {
                                    tVRPC.setText(mBTAdapter.getAddress());
                                }
                                while (i < 10) {
                                    final String str = example.concat(Integer.toString(i++) + " 위도: " + Double.toString(lat) + " 경도" + Double.toString(lon));
                                    Log.d("RPC Example: ", str);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //tVRPC.setText(str);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tVRPC.setText("RPC 서버에 연결하지 못했습니다. 이유 : 시간초과");
                                    }
                                });
                            }
                        }

                    private void patchEOFException() {
                        System.setProperty("http.keepAlive", "false");
                    }

                    //                runOnUiThread(new Runnable() {
                    //
                    //                    @Override
                    //                    public void run() {
                    //                        Log.d("RPC", "Example");
                    //
                    //                        String str = example.hello("foo", " ", "bar", " ", "baz");
                    //
                    //                        Log.d("RPC Example: ", str);
                    //                    }
                    //                });
                }.start();

            isInit = true;
        }
    }
}
