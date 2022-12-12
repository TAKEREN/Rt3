package com.example.rt3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rt3.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private File file;

    SensorManager sensorManager;
    Sensor sensor;
    TextView xTextView;
    TextView yTextView;
    TextView zTextView;
    TextView sumTextView;
    TextView stepTextView;
    TextView tassei;

    boolean first =true;
    boolean up = false;
    float d0,d=0f;
    int stepcount=0;
    int takeshita=0;

    //フィルタリング係数 0<a<1
    float a=0.65f;



    String[] names=new String[]{"x-value","y-value","z-value"};
    int[] colors=new int[]{Color.RED, Color.GREEN,Color.BLUE};



    private ActivityMainBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

     /*   xTextView =(TextView) findViewById(R.id.x_value);
        yTextView=(TextView) findViewById(R.id.y_value);
        zTextView=(TextView) findViewById(R.id.z_value);
      */
       //sumTextView=(TextView) findViewById(R.id.sum_value);


        stepTextView=(TextView) findViewById(R.id.counter);
        tassei=(TextView) findViewById(R.id.m_tassei);

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Context context = getApplicationContext();

        String fileName = "TestFile.txt";  //ふぃある保存
        file = new File(context.getFilesDir(), fileName);

        int steper=0;
        //steper=readFile(); //ここでファイルの読み込みをする　しなければ歩数は0から
            stepcount=steper;


        tassei.setText("目標未達成");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }



  /*  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xTextView =(TextView) findViewById(R.id.x_value);
        yTextView=(TextView) findViewById(R.id.y_value);
        zTextView=(TextView) findViewById(R.id.z_value);
        sumTextView=(TextView) findViewById(R.id.sum_value);
        stepTextView=(TextView) findViewById(R.id.counter);
        tassei=(TextView) findViewById(R.id.m_tassei);

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



    }
   */

   public void onSensorChanged(SensorEvent sensorEvent){

        float value[] = sensorEvent.values;
   //     xTextView.setText("X軸の加速度:"+String.valueOf(value[0]));
   //     yTextView.setText("Y軸の加速度:"+String.valueOf(value[1]));
   //     zTextView.setText("Z軸の加速度:"+String.valueOf(value[2]));
        float sum=(float)Math.sqrt(Math.pow(value[0],2)+Math.pow(value[1],2)+Math.pow(value[2],2));
     //  sumTextView.setText("3軸加速度ベクトルの長さ:"+String.valueOf(sum));

        if(first){
            first=false;
            up=true;
            d0=a*sum;
        }else{
            //ローパスフィルタリング 時系列の細かいデータを平滑化
            d=a*sum+(1-a)*d0;
            if(up&&d<d0){
                up=false;
                takeshita++;


                if(takeshita>2) {
                    takeshita=0;
                    stepcount++;
                    saveFile(stepcount);
                }
            }else if(!up&& d>=d0){
                up=true;
                //d0=d ;
            }
            stepTextView.setText(String.valueOf(stepcount)+"歩");
            if(stepcount>30){
                tassei.setText("目標達成");
            }
        }


    }
    public void onAccuracyChanged(Sensor sensor,int accuracy){

    }

    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);

    }
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }
   /* public void clickStartButton(View view){
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME);
    }
    public void clickRestartButton(View view){
        stepTextView.setText("0歩");
        first=true;
        stepcount=0;
        tassei.setText("目標未達成");
    }
    public void clickStopButton(View view){
        this.onPause();
        //sensorManager.unregisterListener(this);
    }

    */

    public void saveFile(int i) {//ファイルを保存
        // try-with-resources
        String str = String.valueOf(i);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(str);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readFile() {
        String text = null;
        int b=0;

        // try-with-resources
        try(
                BufferedReader br = new BufferedReader(new FileReader(file))
        ){
            text = br.readLine();
             b =Integer.parseInt(text);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }
}



