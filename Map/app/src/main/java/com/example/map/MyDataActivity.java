package com.example.map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class MyDataActivity extends Fragment {
    // 스피너 데이터
    public List<String> monDate, dayDate;

    // 뷰
    public EditText EditText_Name, EditText_Year, EditText_Address, EditText_Phone, EditText_Phone_Second, EditText_Phone_Third;
    public Button Button_Man, Button_Girl, Button_Request_User_Data, TextView_Complete , Button_Erase;

    // 성별
    public String sx = "";

    // 날짜 스피너
    public Spinner Spinner_Month, Spinner_Day;
    public int day = 1, month = 1;

    // 스패닝 어뎁터
    public ArrayAdapter<String> monAdaptor, dayAdaptor;

    private int delay = 0;

    private View rootView;
    private Context con;

    private BluetoothSPP bt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_userdata, container, false);
        init();
        return rootView;
    }

    void init(){
        con = getActivity();
        bt = ((HomeActivity)getActivity()).bt;

        Button_Erase = rootView.findViewById(R.id.Button_Erase);
        Button_Request_User_Data= rootView.findViewById(R.id.Button_Request_User_Data);
        EditText_Name = rootView.findViewById(R.id.EditText_Name);
        EditText_Year = rootView.findViewById(R.id.EditText_Year);
        EditText_Address = rootView.findViewById(R.id.EditText_Address);
        EditText_Phone = rootView.findViewById(R.id.EditText_Phone);
        EditText_Phone_Second = rootView.findViewById(R.id.EditText_Phone_Second);
        EditText_Phone_Third = rootView.findViewById(R.id.EditText_Phone_Third);
        Button_Man = rootView.findViewById(R.id.Button_Man);
        Button_Girl = rootView.findViewById(R.id.Button_Girl);
        TextView_Complete = rootView.findViewById(R.id.TextView_Complete);
        TextView_Complete.setClickable(true);

        Spinner_Month = rootView.findViewById(R.id.Spinner_Month); // 월
        Spinner_Day = rootView.findViewById(R.id.Spinner_Day);    // 날짜

        Button_Man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sx = "male"; // 성별
            }
        });

        Button_Girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sx = "female"; // 성별
            }
        });

        monDate = new ArrayList<>(); // 스피너 리스트 초기화
        dayDate = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            String date = Integer.toString(i) + " mon"; // 달 12개
            monDate.add(date);
        }

        for (int i = 1; i <= 31; i++) {
            String date = Integer.toString(i) + " day"; // 날짜 31개
            dayDate.add(date);
        }

        monAdaptor = new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, monDate);
        Spinner_Month.setAdapter(monAdaptor);

        dayAdaptor = new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1, dayDate);
        Spinner_Day.setAdapter(dayAdaptor);


        Spinner_Month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner_Day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = (position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView_Complete.setOnClickListener(new View.OnClickListener() { // 데이터 보내기
            @Override
            public void onClick(View view) {

                String name = EditText_Name.getText().toString();
                String address = EditText_Address.getText().toString();
                String hp1 = EditText_Phone.getText().toString();
                String hp2 = EditText_Phone_Second.getText().toString();
                String hp3 = EditText_Phone_Third.getText().toString();

                String year = EditText_Year.getText().toString();
                String gdr = "",birth = "";
                if (!hp1.isEmpty())  hp1 = "@HP1:"+ hp1;   // 전화번호
                if (!name.isEmpty()) name = "@NAME:"+name; // 이름
                if (!address.isEmpty()) address = "@ADDR:"+address; // 주소
                if(!hp2.isEmpty()) hp2 = "@HP2:" + hp2; // 전화번호2
                if(!hp3.isEmpty()) hp3 = "@HP3:" + hp3; // 전화번호3

                if(!year.isEmpty()){
                    birth = "@BR:" + year +"-"+ cvtNum(Integer.toString(month)) + "-" + cvtNum(Integer.toString(day)); // 날짜
                }
                if(!sx.isEmpty()) gdr = "@GDR:" + sx; // 성별


                final String str[] = {name,gdr,birth,address,hp1,hp2,hp3};

                final Handler mHandler = new Handler();

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        bt.send(str[delay++],true);  // 입력된 모든 데이터를 보냄
                        if(delay == 7) {
                            delay = 0;
                            return;
                        }
                        mHandler.postDelayed(this,500);
                    }
                }, 0); // 0.5초

            }
        });

        Button_Erase.setOnClickListener(new View.OnClickListener() { // 모든 데이터를 삭제하는 명령어를 보냄
            @Override
            public void onClick(View view) {
                bt.send("@ERASE:",true);
            }
        });

        Button_Request_User_Data.setOnClickListener(new View.OnClickListener() { // 데이터 요청
            @Override
            public void onClick(View view) {
                final String str[] = {"@NAME?", "@GDR?","@BR?","@ADDR?", "@HP1?","@HP2?","@HP3?"};  // 해당 키워드들을 아두이노로 보내면 데이터가 날아옴

                final Handler mHandler = new Handler();

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        bt.send(str[delay++],true);
                        if(delay == 7) {
                            delay = 0;
                            return;
                        }
                        mHandler.postDelayed(this,500);
                    }
                }, 0); // 0.5초
            }
        });
    }

    private String cvtNum(String n) {
        if (n.length() == 1) {
            return "0" + n;
        }
        return n;
    }
}
