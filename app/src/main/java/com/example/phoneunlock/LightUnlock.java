package com.example.phoneunlock;

import android.app.Service;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

public class LightUnlock extends Fragment implements SensorEventListener {

    TextView textView;
    TextView msgView;
    TextView timerView;
    TextView codeView;
    TimerTask timerTask;
    private SensorManager sensorManager;
    private Sensor sensor;
    private final int isCoveredLux = 10000;
    private boolean isCovered = false;
    private int time;
    private int[] inputCode = new int[3];
    private final int[] code = {3, 4, 3};
    private int inputCodeIndex;
    private int intervalStart;
    private int intervalEnd;
    private int intervalDiff;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.light_unlock, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = (TextView) getView().findViewById(R.id.textView);
        msgView = (TextView) getView().findViewById(R.id.msgView);
        timerView = (TextView) getView().findViewById(R.id.timerView);
        codeView = (TextView) getView().findViewById(R.id.codeView);

        //Setting up light sensor on device. Code referenced from: https://developer.android.com/guide/topics/sensors/sensors_overview
        sensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        beginTimer();
    }

    /**
     * Sensor detecting light changes. And calculating time intervals between sensor covered versus not covered.
     * @param sensorEvent the lux value sensor is detecting
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //When light sensor is covered and was not covered previously
        if (sensorEvent.values[0] <= isCoveredLux && isCovered == false){
            isCovered = true;
            timerView.setBackgroundColor(Color.parseColor("#cccccc"));
            codeView.setTextColor(Color.parseColor("#000000"));
            codeView.setText("");
            //First digit of passcode input
            if(intervalStart == 0){
                msgView.setText("");
                intervalStart = time;
            }
            else{
                intervalEnd = time;
                intervalDiff = intervalEnd - intervalStart;
                inputCode[inputCodeIndex] = intervalDiff;
                inputCodeIndex ++;
                intervalStart = intervalEnd;
                codeView.setText(printCode(inputCode));
            }
        }
        //When light sensor is not covered and was covered previously
        else if (sensorEvent.values[0] > isCoveredLux && isCovered == true){
            isCovered = false;
            //Not all digits of passcode have been entered
            if (intervalStart > 0 && inputCode[2] == 0){
                timerView.setBackgroundColor(Color.parseColor("#FFFF00"));
                intervalEnd = time;
                intervalDiff = intervalEnd - intervalStart;
                inputCode[inputCodeIndex] = intervalDiff;
                inputCodeIndex ++;
                intervalStart = intervalEnd;
                codeView.setText(printCode(inputCode));
            }
            // All digits of passcode entered
            if (inputCode[2] >0 ){
                timerView.setBackgroundColor(Color.parseColor("#0000ffff"));
                codeView.setText(printCode(inputCode));

                boolean correctCode = true;
                //Checking input passcode with the supposed passcode
                for (int i=0; i<inputCode.length; i++){
                    if (inputCode[i] != code[i]){
                        correctCode = false;
                        break;
                    }
                }
                //Unlock phone if passcode entered is correct
                if (correctCode){
                    msgView.setTextColor(Color.parseColor("#3CB043"));
                    msgView.setText("Unlock Successful");
                    codeView.setTextColor(Color.parseColor("#3CB043"));
                    timerTask.cancel();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    }, 1000);
                }
                //Resetting timer when passcode is incorrect. Can re-enter passcode.
                else{
                    msgView.setTextColor(Color.parseColor("#990F02"));
                    msgView.setText("Incorrect Passcode! Please Try Again!");
                    codeView.setTextColor(Color.parseColor("#990F02"));
                    timerTask.cancel();
                    intervalStart = 0;
                    intervalEnd = 0;
                    intervalDiff = 0;
                    inputCode = new int[3];
                    inputCodeIndex = 0;
                    time = 0;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            msgView.setText("");
                            codeView.setText("");
                            beginTimer();
                        }
                    }, 1000);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        //Code referenced from: https://developer.android.com/guide/topics/sensors/sensors_overview
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        //Code referenced from: https://developer.android.com/guide/topics/sensors/sensors_overview
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Starting and displaying counter on screen
     */
    public void beginTimer(){
        new Timer().scheduleAtFixedRate(timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time ++;
                        timerView.setText(String.valueOf(time));
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Printing the passcode digits entered as dots
     */
    public static String printCode (int[] codeArray){
        String codeString = "";
        for (int i=0; i<codeArray.length; i++){
            if (codeArray[i] != 0){
                codeString = codeString + "\u2B24     ";
            }
        }
        return codeString;
    }
}