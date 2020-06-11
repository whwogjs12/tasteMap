package com.example.tastemap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.kakao.util.maps.helper.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

//    tasteList.setOnItemLongClickListener(this);


    final public int ADD_ACTIVITY = 1;
    final public int SCORE_ACTIVITY = 2;

    String TAG = "MainActivity";

    ListAdapter adapter;
    ArrayList<ListData> dataList = new ArrayList<ListData>();
    FileManager fileManager = new FileManager(this);
    private LocationViewer locationViewer;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addButton = findViewById(R.id.save); //메인 > 추가 버튼 누를 시 Add로 화면전환

        adapter = new ListAdapter(dataList);
        adapter.setItem(dataList);
        adapter.notifyDataSetChanged();

        ListView tasteList = findViewById(R.id.tasteList);
        tasteList.setAdapter(adapter);

        addButton.setOnClickListener(this);

        fileManager.loadFile(dataList);
        distance();
        adapter.notifyDataSetChanged();

        //listdata add 에서 intent

        Log.d(TAG, "onCreate: intent 전");

        Intent intent = getIntent();

        Log.d(TAG, "onCreate: intent 후");
        ListData addData = (ListData) intent.getSerializableExtra("ListData");
        if (addData != null) {
            Log.d(TAG, "onCreate: 데이터 받아오기 성공");
            //addItem(addData);
            addItem(addData);
        }

        // 롱클릭시 팝업
        tasteList.setOnItemLongClickListener(this);
        tasteList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.save:
                intent = new Intent(getApplicationContext(), Add.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, ADD_ACTIVITY);
                break;
        }
    }

    //카카오 지도 API 사용을 위해 어플 키값 필요
    //키 값을 해시로 받기 위한 코드니 직접적인 필요X
    public String getKeyHash(final Context context) {
        PackageInfo packageInfo = Utility.getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;
        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
            }
        }
        return null;
    }

    public void addItem(ListData item) {
//      ListData item = new ListData(shopName, address);
        this.dataList.add(item);
        Log.d(TAG, "addItem: 아이템 add 완료");
        distance();
        adapter.notifyDataSetChanged();
    }

    //꾹 눌러서 메모 및 제거
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
        dlg.setTitle("맛집삭제 및 메모")
                .setMessage(" ")
                .setNeutralButton("취소", null)
                .setPositiveButton("메모&별점", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Test", "onClick: position " + position);
                        Intent intent, intent2;
                        intent = new Intent(getApplicationContext(), Score.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("pos", position);
                        intent.putExtra("name", dataList.get(position).getName());
                        intent.putExtra("rate", dataList.get(position).getRate());
                        intent.putExtra("memo", dataList.get(position).getMemo());
                        startActivityForResult(intent, SCORE_ACTIVITY);

                    }
                })
                .setNegativeButton("제거", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //제거
                        int count;

                        count = adapter.getCount();

                        if (count > 0) {
                            //checkIndex = tasteList.getCheckedItemPosition();
                            Log.d(TAG, "onClick: itemPosition" + position);
                            if (position > -1 && position < count) {
                                //아이템 삭제
                                dataList.remove(position);

                                //리스트뷰 갱신
                                adapter.notifyDataSetChanged();

                                //업데이트
                                fileManager.dataUpdate(dataList);
                                distance();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .show();
        return true;
    }

    // < 수정 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ACTIVITY) { //이후에 if else문으로 변경하기
            if (resultCode == RESULT_OK) {
                ListData addData = (ListData) data.getSerializableExtra("ListData");
                addItem(addData);
            }
        } else if (requestCode == SCORE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String memo = data.getExtras().getString("memo");
                int position = data.getExtras().getInt("position");
                Log.d("test", "onActivityResult: 넘어온 포지션 " + position);
                int rate = data.getExtras().getInt("rate");

                dataList.get(position).setRate(rate);
                dataList.get(position).setMemo(memo);

                for (int i = 0; i < dataList.size(); i++) {
                    Log.d("Test", "onActivityResult: 값 확인 " + dataList.get(i).getName() + " " + dataList.get(i).getMemo());

                }
                Log.d("TEST", "onActivityResult: 포지션 " + position);
                Log.d("TEST", "onActivityResult: dataList position "
                        + dataList.get(position).getRate());
                Log.d("TEST", "onActivityResult: dataList position "
                        + dataList.get(position).getMemo());
                Log.d("Test", "onActivityResult: 메모 내용 " + memo);

                fileManager.dataUpdate(dataList);
                adapter.notifyDataSetChanged();

            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ListData selectedData = dataList.get(position);
        final LocationViewer locationViewer = new LocationViewer(this);
        AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
        dlg.setTitle("길찾기");
        dlg.setMessage("원하시는 길 찾기 방법을 선택해주세요");
        dlg.setNeutralButton("취소", null);
        //네비게이션 켜기
        dlg.setPositiveButton("네비게이션", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uriString =
                        "kakaomap://route?sp="
                                + locationViewer.getLatitude() + "," + locationViewer.getLongitude()
                                + "&ep="
                                + selectedData.getLatitude() + "," + selectedData.getLongitude()
                                + "&by=CAR";
                uriSearch(uriString);
            }
        });

        //다음 지도 앱 켜기
        dlg.setNegativeButton("대중교통", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uriString =
                        "kakaomap://route?sp="
                                + locationViewer.getLatitude() + "," + locationViewer.getLongitude()
                                + "&ep="
                                + selectedData.getLatitude() + "," + selectedData.getLongitude()
                                + "&by=PUBLICTRANSIT";
                uriSearch(uriString);
            }
        });
        dlg.show();
    }

    public void uriSearch(String uriString)
    {
        try{
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Uri uri = Uri.parse("market://details?id=net.daum.android.map");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    public void distance() {

        LocationViewer userLocation = new LocationViewer(this);
        if (userLocation != null) {

            double userlatitude = userLocation.getLatitude();
            double userlongitude = userLocation.getLongitude();

            for(int x=0;x<dataList.size();x++)
            {
                ListData temp = dataList.get(x);
                double dist = Math.sqrt(Math.pow(userlatitude -temp.getLatitude(),2) +
                        Math.pow(userlongitude - temp.getLongitude(),2));
                temp.setDistance(dist);
                Log.d("TEST", "addItem: distance: " +dist);
            }
            Collections.sort(dataList);
            Log.d("TEST", "distance: 정렬 확인 "+dataList);
            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(this,"인터넷 상황을 확인해주세요", Toast.LENGTH_LONG).show();
        }
    }

}