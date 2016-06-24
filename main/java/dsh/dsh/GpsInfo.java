package dsh.dsh;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GpsInfo extends Service implements LocationListener {

    private final Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도

    // GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_UPDATES = 1;

    // GPS 정보 업데이트 시간 1/1000
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

    protected LocationManager locationManager;

    public GpsInfo(Context context) {
        this.mContext = context;
    }

    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_UPDATES,
                                MIN_DISTANCE_UPDATES, this);
                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }

                    if (locationManager != null) {
                        try {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        } catch (SecurityException se) {
                            se.printStackTrace();
                        }
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            try
                            {
                                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                                // 루트 엘리먼트 생성
                                Document doc = docBuilder.newDocument();
                                Element rootElement = doc.createElement("latlon");
                                doc.appendChild(rootElement);
                                // sn 엘리먼트 생성
                                Element lat_no = doc.createElement("lat");
                                lat_no.appendChild(doc.createTextNode(Double.toString(lat)));
                                rootElement.appendChild(lat_no);
                                //std_no 엘리먼트 생성
                                Element lon_no = doc.createElement("lon");
                                lon_no.appendChild(doc.createTextNode(Double.toString(lon)));
                                rootElement.appendChild(lon_no);
                                // XML 파일로 쓰기
                                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                Transformer transformer = transformerFactory.newTransformer();
                                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                DOMSource source = new DOMSource(doc);
                                StreamResult result = new StreamResult(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/GPSInfo/latlon.xml")));
                                transformer.transform(source, result);
                                System.out.println("success!");
                            }
                            catch (ParserConfigurationException e)
                            {
                                e.printStackTrace();
                            }
                            catch (TransformerException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager
                                    .requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER,
                                            MIN_TIME_UPDATES,
                                            MIN_DISTANCE_UPDATES,
                                            this);
                        } catch (SecurityException se) {
                            se.printStackTrace();
                        }
                        if (locationManager != null) {
                            try {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            } catch (SecurityException se) {
                                se.printStackTrace();
                            }
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                try
                                {
                                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                                    // 루트 엘리먼트 생성
                                    Document doc = docBuilder.newDocument();
                                    Element rootElement = doc.createElement("latlon");
                                    doc.appendChild(rootElement);
                                    // sn 엘리먼트 생성
                                    Element lat_no = doc.createElement("lat");
                                    lat_no.appendChild(doc.createTextNode(Double.toString(lat)));
                                    rootElement.appendChild(lat_no);
                                    //std_no 엘리먼트 생성
                                    Element lon_no = doc.createElement("lon");
                                    lon_no.appendChild(doc.createTextNode(Double.toString(lon)));
                                    rootElement.appendChild(lon_no);
                                    // XML 파일로 쓰기
                                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                    Transformer transformer = transformerFactory.newTransformer();
                                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                                    DOMSource source = new DOMSource(doc);
                                    StreamResult result = new StreamResult(new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/GPSInfo/latlon.xml")));
                                    transformer.transform(source, result);
                                    System.out.println("success!");
                                }
                                catch (ParserConfigurationException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (TransformerException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(GpsInfo.this);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * 위도값
     * */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값
     * */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                mContext);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog
                .setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status,
                                Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
}