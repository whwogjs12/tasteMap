package com.example.tastemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

public class Add extends AppCompatActivity implements View.OnClickListener, MapReverseGeoCoder.ReverseGeoCodingResultListener
{

    final static String APIKey = "894a5114a4b85c075dd6b9d292bee563";
    ViewGroup mapViewContainer;
    MapEventListener eventListener;
    ListData addData;
    ListAdapter adapter;
    FileManager fileManager = new FileManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mapViewContainer = findViewById(R.id.mapView);
        mapViewContainer.removeAllViews();
        Button searchButton = findViewById(R.id.search);
        Button addButton = findViewById(R.id.save);
        searchButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.search: //검색 버튼 클릭
                EditText addressData = findViewById(R.id.search_addr);//주소창에서 주소 받기
                String address = addressData.getText().toString();//주소 넘기기

                //네트워크 상태 확인
                if(NetworkStatus.getConnectivityStatus(this)!=NetworkStatus.TYPE_NOT_CONNECTED)
                {
                    mapViewContainer.removeAllViews();
                    MapView mapView = new MapView(this);
                    mapViewContainer.addView(mapView,0);
                    eventListener = new MapEventListener();
                    mapView.setMapViewEventListener(eventListener);
                    mapSetting(address,mapView);
                }
                else
                {
                    Toast.makeText(this,"데이터 및 와이파이의 상태를 확인해주세요",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save: //추가 버튼 클릭
                if(eventListener==null) // 아직 검색 버튼을 누르지 않은 상태
                {
                    Toast.makeText(this,"아무런 정보가 없습니다. 주소를 검색해주세요",Toast.LENGTH_SHORT).show();
                }
                else if(eventListener.getMarker()!=null) //마커가 없어지는 에러 방지
                {
                    MapPoint.GeoCoordinate geoCoordinate = eventListener.getMarker().getMapPoint().getMapPointGeoCoord();
                     //위경도 불러오기용 코드
                    addData = new ListData(geoCoordinate.latitude, geoCoordinate.longitude);

                    MapReverseGeoCoder rgc = new MapReverseGeoCoder(APIKey, eventListener.getMarker().getMapPoint(), this, this);
                    rgc.startFindingAddress(); //좌표 -> 주소 ,비동기 방식
                }
                else
                {
                    Toast.makeText(this,"지도 문제가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class); //add 눌렀을 때 main으로 화면전환
                break;
        }
    }

    public void mapSetting(String address, MapView mapView)
    {
        if(!address.equals(""))
        {
            APIThread addressThread = new APIThread(address);
            addressThread.start(); //통신 관련 행동은 쓰레드를 이용
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            while(addressThread.error==-1 && ((end - start)/1000.0<5))
            {
                end = System.currentTimeMillis();
            }//쓰레드가 끝날때까지 대기

            //쓰레드가 끝나지 않는 에러 발생시 일정 시간 이후 강제 종료
            if((end - start)/1000.0>=5)
            {
                Toast.makeText(this,"지도를 불러오는 과정에서 문제가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            //주소를 찾은 경우 주소의 위치 반환
            if(addressThread.getCoodData()!=null)
            {
                ListData coodData = addressThread.getCoodData();
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(coodData.getLatitude(), coodData.getLongitude()),true);

                if(eventListener.getMarker()!=null)
                {
                    eventListener.markerUpdate(MapPoint.mapPointWithGeoCoord(coodData.getLatitude(), coodData.getLongitude()));
                    mapView.addPOIItem(eventListener.getMarker());
                }
            }
            else
            {
                int error = addressThread.error;
                errorMessage(error);
            }
        }
        else
        {
            //현 위치를 찾아주는 뷰어
            LocationViewer locationViewer = new LocationViewer(this);

            //현재 위치를 이용해 지도 띄우기
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(locationViewer.getLatitude(), locationViewer.getLongitude()),true);
            if(eventListener.getMarker()!=null)
            {
                eventListener.markerUpdate(MapPoint.mapPointWithGeoCoord(locationViewer.getLatitude(), locationViewer.getLongitude()));
                mapView.addPOIItem(eventListener.getMarker());
            }
        }
    }

    // 좌표-> 주소 비동기 변환시 등장하는 코드. 이 코드가 비동기식이라 메인에서 처리하는 경우 쓰레드가 늦어 주소를 반환하지 못하는 경우가 생김
    // 그래서 이 콜백 메소드 내에서 처리
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        if(addData!=null){
            EditText shopName = findViewById(R.id.input_name);

            addData.setAddress(s);

            if(shopName.getText().toString().equals(""))
            {
                Toast.makeText(this,"가게 이름을 입력해주세요!",Toast.LENGTH_SHORT).show();
            }
            else //가게 이름이 있는 경우 삽입
            {
                Log.d("TEST", "onReverseGeoCoderFoundAddress: 여기임");
                addData.setName(shopName.getText().toString());
                Log.d("TEST", "onReverseGeoCoderFoundAddress: 이름설정완료 : "+shopName.getText());

                fileManager.dataAdd(addData);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("ListData",addData);

                //startActivity(intent); //intent 값을 넘겨주는 부분
                setResult(RESULT_OK, intent);
                Toast.makeText(this,"저장이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void errorMessage(int error)
    {
        switch (error)
        {
            case 0:
                Toast.makeText(this,"주소가 정확하지 않습니다.",Toast.LENGTH_SHORT).show();
                break;
            case 1:
            case 2:
            case 3:
                Toast.makeText(this,"에러가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(this,"데이터 및 와이파이의 상태를 확인해주세요",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder)
    {
    }
}
