package com.example.sojucoaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread; // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer; // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치
    private TextView counter; // 수신 된 데이터를 표시하기 위한 텍스트 뷰
    private TextView speed;
    private EditText timing;
    private LinearLayout mainLayout;
    private SeekBar seekRed;
    private SeekBar seekGreen;
    private SeekBar seekBlue;
    private Button buttonSend; // 송신하기 위한 버튼
    private Button buttonRank;
    private Button buttonRankView;
    private TextView rankCount;
    private Boolean rankOn = false;
    private EditText nameEdit;


    private long startTime;
    private long LatestDrunkTime; // 가장 최근에 술을 마신 시간을 저장한다.
    private int totalDrinking = 0; // 여태까지 마신 술의 양을 의미한다.
    private long rankOnTime;
    private int remainTime;
    private int rankDrink = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEdit = new EditText(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        counter = (TextView)findViewById(R.id.receive);
        buttonSend = (Button)findViewById(R.id.led_submit);
        speed = (TextView)findViewById(R.id.speed);
        timing = (EditText)findViewById(R.id.time);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        seekRed = (SeekBar)findViewById(R.id.seekRed);
        seekBlue = (SeekBar)findViewById(R.id.seekBlue);
        seekGreen = (SeekBar)findViewById(R.id.seekGreen);
        buttonRank = (Button)findViewById(R.id.rankingButton);
        buttonRankView = (Button)findViewById(R.id.rankingView);
        rankCount = (TextView)findViewById(R.id.rankTimer);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int red = (int)(seekRed.getProgress() * 2.55);
                int blue = (int)(seekBlue.getProgress() * 2.55);
                int green = (int)(seekGreen.getProgress() * 2.55);
                sendData(red +"," + green + "," + blue);
            }
        });

        buttonRank.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buttonRank.setVisibility(View.INVISIBLE);
                buttonRankView.setVisibility(View.INVISIBLE);
                rankOnTime = System.currentTimeMillis();
                rankOn = true;
            }
        });

        buttonRankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String url = "https://iwpk4i3gxl.execute-api.ap-northeast-2.amazonaws.com/test1/ranking";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        // 블루투스 활성화하기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정
        if(bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
            // 여기에 처리 할 코드를 작성하세요.
            finish();
        }

        else { // 디바이스가 블루투스를 지원 할 때
            if(bluetoothAdapter.isEnabled()) { // 블루투스가 활성화 상태 (기기에 블루투스가 켜져있음)
                selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
            }

            else { // 블루투스가 비 활성화 상태 (기기에 블루투스가 꺼져있음)
                // 블루투스를 활성화 하기 위한 다이얼로그 출력
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값이 onActivityResult 함수에서 콜백된다.
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Long.toString(System.currentTimeMillis()));
                timing.setText(Long.toString((System.currentTimeMillis() - LatestDrunkTime) / 1000));
                speed.setText(Long.toString( ((totalDrinking / 45) *3600)
                                                / ((System.currentTimeMillis() - startTime) / 1000)) + "잔/h");

                if (rankOn) {
                    remainTime = (int)(30 - (System.currentTimeMillis() - rankOnTime) / 1000);
                    rankCount.setText(Integer.toString(remainTime));
                    if (remainTime <= 0){
                        rankOn = false;
                        builder.setTitle("Ranking 기록!!!");
                        builder.setMessage(rankDrink + "만큼 마셨습니다.\nRanking 등록을 위해 이름과 번호를 입력해주세요.\nex)01012345678 홍길동");
                        builder.setView(nameEdit);
                        builder.setPositiveButton("입력",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                rankPost(rankDrink,
                                                        nameEdit.getText().toString().substring(12),
                                                        nameEdit.getText().toString().substring(0, 11));
                                            }});
                                    }
                                });
                        builder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        ;
                                    }
                                });
                        builder.show();
                        rankDrink = 0;
                        rankOn = false;
                        buttonRank.setVisibility(View.VISIBLE);
                        buttonRankView.setVisibility(View.VISIBLE);
                        rankCount.setText("");
                    }
                }
            }
        };

        class NewRunnable implements Runnable {
            @Override
            public void run() {
                LatestDrunkTime = System.currentTimeMillis();
                startTime = System.currentTimeMillis();
                while(true){
                    try{
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    runOnUiThread(runnable);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BT :
                if(requestCode == RESULT_OK) { // '사용'을 눌렀을 때
                    selectBluetoothDevice(); // 블루투스 디바이스 선택 함수 호출
                }
                else { // '취소'를 눌렀을 때
                    // 여기에 처리 할 코드를 작성하세요.
                    Toast.makeText(getApplicationContext(), "취소",Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        int pairedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if(pairedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG);
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");

            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }

            list.add("취소");

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });
            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for(BluetoothDevice tempDevice : devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출

            startTime = System.currentTimeMillis();
            LatestDrunkTime = System.currentTimeMillis();
            receiveData();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 데이터를 수신하기 위한 쓰레드 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // 데이터를 수신했는지 확인합니다.
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신 된 경우
                        if (byteAvailable > 0) {
                            System.out.println("get byte");
                            // 입력 스트림에서 바이트 단위로 읽어 옵니다.
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);

                            // 입력 스트림 바이트를 한 바이트씩 읽어 옵니다.
                            for (int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음(한줄)
                                if (tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    // 가장 최근 시간으로 마신 시간을 초기화
                                    LatestDrunkTime = System.currentTimeMillis();
                                    // 소주를 마셨는가??
                                    // 마신 양을 저장한다.
                                    totalDrinking += Integer.parseInt(text);

                                    // ranking 기능이 켜진 경우
                                    if (rankOn)
                                        rankDrink += Integer.parseInt(text);

                                    System.out.println(text);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 텍스트 뷰에 출력
                                            if (totalDrinking < 100)
                                                mainLayout.setBackgroundColor(Color.rgb(241, 182, 161));
                                            else if (totalDrinking < 300)
                                                mainLayout.setBackgroundColor(Color.rgb(249, 194, 217));
                                            else if (totalDrinking < 1000)
                                                mainLayout.setBackgroundColor(Color.rgb(241, 102, 129));
                                            else
                                                mainLayout.setBackgroundColor(Color.rgb(255, 0, 0));
                                            System.out.println(text);
                                            counter.setText(totalDrinking/45 + "잔("  + totalDrinking + "ml)");
                                        }
                                    });
                                } // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }

    void sendData(String text) {
        // 문자열에 개행문자("\n")를 추가해줍니다.
        text += "\n";
        try{
            // 데이터 송신
            outputStream.write(text.getBytes());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    void rankPost(int drinking, String name, String phoneNumber){
        StringBuilder sb = new StringBuilder();
        HttpURLConnection rankConnection= null;
        try {
            URL rankApi = new URL("https://iwpk4i3gxl.execute-api.ap-northeast-2.amazonaws.com/test1/ranking");
            rankConnection = (HttpURLConnection)rankApi.openConnection();
            rankConnection.setRequestProperty("Content-Type", "application/json");
            rankConnection.setRequestProperty("Accept-Charset","UTF-8");
            rankConnection.setDoOutput(true);
            rankConnection.setDoInput(true);
            rankConnection.setRequestMethod("POST");
            rankConnection.connect();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("drinking",drinking);
            jsonObject.put("name", name);
            jsonObject.put("phoneNumber", phoneNumber);
            OutputStream out = rankConnection.getOutputStream();
            out.write(jsonObject.toString().getBytes("UTF-8"));
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(rankConnection.getInputStream(), "UTF-8"));
            String inputLine = null;
            while((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            out.close();
            System.out.println(jsonObject);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        finally{
            if(rankConnection != null)
                rankConnection.disconnect();
        }
    }
}
