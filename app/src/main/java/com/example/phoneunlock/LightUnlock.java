package com.example.phoneunlock;

import android.app.Service;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class LightUnlock extends Fragment implements SensorEventListener {

    TextView textView;
    TextView msgView;
    TextView timerView;
    private SensorManager sensorManager;
    private Sensor sensor;
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

        view.findViewById(R.id.button_lightunlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        textView = (TextView) getView().findViewById(R.id.textView);
        msgView = (TextView) getView().findViewById(R.id.msgView);
        timerView = (TextView) getView().findViewById(R.id.timerView);

        sensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        new Timer().scheduleAtFixedRate(new TimerTask() {
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
        }, 0, 1500);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // When sensor is covered
        if (sensorEvent.values[0] < 10000){
            timerView.setBackgroundColor(Color.parseColor("#cccccc"));
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
            }
            textView.setText(String.valueOf(intervalStart));
        }
        // When sensor is not covered
        else{
            if (intervalStart > 0 && inputCode[2] == 0){
                timerView.setBackgroundColor(Color.parseColor("#FFFF00"));
                intervalEnd = time;
                intervalDiff = intervalEnd - intervalStart;
                inputCode[inputCodeIndex] = intervalDiff;
                inputCodeIndex ++;
                intervalStart = intervalEnd;
                textView.setText(String.valueOf(intervalStart));
            }
            // All digits of passcode entered
            if (inputCode[2] >0 ){
                timerView.setBackgroundColor(Color.parseColor("#0000ffff"));
                //Used for outputting passcode array for testing
                String[] strArray = new String[inputCode.length];

                for (int i = 0; i < inputCode.length; i++) {
                    strArray[i] = String.valueOf(inputCode[i]);
                }
                textView.setText(Arrays.toString(strArray));

                boolean correctCode = true;
                for (int i=0; i<inputCode.length; i++){
                    if (inputCode[i] != code[i]){
                        correctCode = false;
                        break;
                    }
                }
                if (correctCode){
                    msgView.setTextColor(Color.parseColor("#3CB043"));
                    msgView.setText("Unlock Successful");
                    System.exit(0);

                }
                else{
                    msgView.setTextColor(Color.parseColor("#990F02"));
                    msgView.setText("Incorrect Passcode! Please Try Again!");
                    intervalStart = 0;
                    intervalEnd = 0;
                    intervalDiff = 0;
                    inputCode = new int[3];
                    inputCodeIndex = 0;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}