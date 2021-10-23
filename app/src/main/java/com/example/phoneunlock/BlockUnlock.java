package com.example.phoneunlock;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BlockUnlock extends Fragment {

    TableLayout table;
    int height;
    HashMap<Pair<Integer, Integer>, Integer> gridValues = new HashMap<>();
    int currentCode[] = new int[9];
    private int securityCode[] = {1, 6, 3, 8, 4, 7, 5, 0, 2};
    Set<Pair<Integer, Integer>> keys;
    TableRow row;
    ImageView gridSpace;
    int[] locationOnScreen;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.block_unlock, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_blockunlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                boolean correctCode = true;

                for(int i = 0;i<9;i++){
                    if(securityCode[i] != currentCode[i]){
                        correctCode = false;
                        break;
                    }
                }
                if(correctCode){
                   // System.exit(0);
                    Log.d("Check", "This is the correct code");
                }else{
                    Log.d("Check", "This is not the correct code");
                }
            }
        }, 0, 1000);

    }

    public void doSomething(final View view){
        view.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                if(view.getY() > 402)
                    view.animate().translationYBy(-height);
                Log.d("Y", String.valueOf(view.getY()));
                return true;
            }
            public boolean onSwipeRight() {
                if(view.getX() < 722)
                    view.animate().translationXBy(height);
                Log.d("X", String.valueOf(view.getX()));
                return true;
            }
            public boolean onSwipeLeft() {
                if(view.getX() > 0)
                    view.animate().translationXBy(-height);
                return true;
            }
            public boolean onSwipeBottom() {
                if(view.getY() < 986)
                    view.animate().translationYBy(height);
                Log.d("Y", String.valueOf(view.getY()));
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final RelativeLayout rl = getView().findViewById(R.id.relativeLayout);

        getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                getView().removeOnLayoutChangeListener(this);
                height = v.findViewById(R.id.grid1).getHeight();
                table = v.findViewById(R.id.tableLayout);
                keys = gridValues.keySet();

                for(int i = 0;i< 3;i++){
                    row = (TableRow) table.getChildAt(i);
                    for(int j = 0;j < 3;j++){
                        gridSpace = (ImageView) row.getChildAt(j);
                        locationOnScreen = new int[2];
                        gridSpace.getLocationInWindow(locationOnScreen);
                        gridValues.put(new Pair<>(locationOnScreen[0], locationOnScreen[1]),0);
                        Log.d("Grid " + j, locationOnScreen[0] + ", " + locationOnScreen[1]);
                        Log.d("Size " + j, gridSpace.getHeight() + ", " + gridSpace.getWidth() + "\n");
                    }

                }

                int i = 1;
                for(Pair<Integer, Integer> key: keys){
                    Log.d("Value of "+key+" is", gridValues.get(key) + "\n");
                    gridValues.put(key, i);
                    Button myButton = new Button(getContext());
                    myButton.setId(i);
                    final int id_ = myButton.getId();
                    myButton.setText(String.valueOf(id_));
                    myButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            doSomething(view);
                        }
                    });
                    myButton.setHeight(361);
                    myButton.setWidth(361);
                    myButton.setLeft(0);
                    myButton.setTop(0);
                    myButton.setRight(0);
                    myButton.setBottom(0);
                    myButton.setX(key.first);
                    myButton.setY(key.second - 223);
                    rl.addView(myButton);

                    if(i >= 8){
                        break;
                    }
                    else{
                        i++;
                    }
                }

                int count = 0;
                for(int y = 625; y < 1708;y+=361){
                    for(int x = 0;x < 1083;x+=361){
                        currentCode[count] = gridValues.get(new Pair<>(x, y));
                        count++;
                    }
                }


//                for(int j = 0;j<9;j++){
//                    Log.d("Code", currentCode[j] + "\n");
//                }
            }
        });




    }
}
