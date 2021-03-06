package com.example.phoneunlock;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BlockUnlock extends Fragment {
    private TableLayout table;
    private int height;
    private HashMap<Pair<Integer, Integer>, Integer> gridValues = new HashMap<>();
    private int[] currentCode = new int[9];
    private int[] securityCode = {2, 0, 3, 1, 4, 6, 7, 5, 8};
    private TableRow row;
    private ImageView gridSpace;
    private int[] locationOnScreen;
    private ImageButton[] buttons = new ImageButton[8];
    private int buttonHeight;
    private int minX,  maxX, minY, maxY;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.block_unlock, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_blockunlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BlockUnlock.this)
                        .navigate(R.id.action_BlockUnlock_self);
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    synchronized (this) {
                        try {
                            this.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for(ImageButton b : buttons){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            b.setForegroundTintList(ColorStateList.valueOf(Color.argb(100, 0,255,5)));
                        }
                    }
                    synchronized (this) {
                        try {
                            this.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.exit(0);
                }

            }
        }, 0, 500);

    }

    public void onSwipe(final View view){
        final float x = view.getX();
        final float y = view.getY();
        final Pair loc = new Pair<>((int)x, (int)y);
        int xLoc = (int)(x - minX )/height;
        int yLoc = (int)(y - minY)/height * 3;
        final int oldSquare = xLoc + yLoc;

        view.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                Pair newLoc = new Pair<>((int)x, (int) y - height);
                if(y >= minY && gridValues.get(newLoc).equals(0)){
                    gridValues.put(loc, 0);
                    gridValues.put(newLoc, view.getId());
                    view.animate().translationYBy(-height);
                    currentCode[oldSquare] = 0;
                    currentCode[oldSquare - 3] = view.getId();
                }
                return true;
            }
            public boolean onSwipeRight() {
                Pair newLoc = new Pair<>((int)x + height, (int) y);
                if(x <= maxX - height && gridValues.get(newLoc).equals(0)){
                    view.animate().translationXBy(height);
                    gridValues.put(loc, 0);
                    gridValues.put(newLoc, view.getId());
                    currentCode[oldSquare] = 0;
                    currentCode[oldSquare + 1] = view.getId();
                }
                return true;
            }
            public boolean onSwipeLeft() {
                Pair newLoc = new Pair<>((int)x - height, (int) y);
                if(x >= minX && gridValues.get(newLoc).equals(0)){
                    gridValues.put(loc, 0);
                    gridValues.put(newLoc, view.getId());
                    view.animate().translationXBy(-height);
                    currentCode[oldSquare] = 0;
                    currentCode[oldSquare - 1] = view.getId();
                }
                return true;
            }
            public boolean onSwipeBottom() {
                Pair newLoc = new Pair<>((int)x, (int) y + height);
                if(y <= maxY - height && gridValues.get(newLoc).equals(0)){
                    gridValues.put(loc, 0);
                    gridValues.put(newLoc, view.getId());
                    view.animate().translationYBy(height);
                    currentCode[oldSquare] = 0;
                    currentCode[oldSquare + 3] = view.getId();
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final RelativeLayout rl = getView().findViewById(R.id.relativeLayout);

        getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                getView().removeOnLayoutChangeListener(this);
                height = v.findViewById(R.id.grid1).getHeight();
                table = v.findViewById(R.id.tableLayout);
                int yOffset = 110;
                row = (TableRow) table.getChildAt(0);
                buttonHeight = row.getChildAt(0).getHeight();

                int count = 0;
                for(int i = 0;i< 3;i++){
                    row = (TableRow) table.getChildAt(i);
                    for(int j = 0;j < 3;j++){
                        gridSpace = (ImageView) row.getChildAt(j);
                        locationOnScreen = new int[2];

                        gridSpace.getLocationInWindow(locationOnScreen);
                        float x = locationOnScreen[0];
                        float y = locationOnScreen[1];
                        if(count < 8){
                            buttons[count] = new ImageButton(getContext());
                            buttons[count].setLayoutParams(new LinearLayout.LayoutParams(height-20, height-20));
                            buttons[count].setId(count + 1);
                            final int id_ =  buttons[count].getId();
                            Drawable number;
                            switch(id_){
                                case 1:{
                                    number = getResources().getDrawable(R.drawable.one);
                                }
                                break;
                                case 2:{
                                    number = getResources().getDrawable(R.drawable.two);
                                }
                                break;
                                case 3:{
                                    number = getResources().getDrawable(R.drawable.three);
                                }
                                break;
                                case 4:{
                                    number = getResources().getDrawable(R.drawable.four);
                                }
                                break;
                                case 5:{
                                    number = getResources().getDrawable(R.drawable.five);
                                }
                                break;
                                case 6:{
                                    number = getResources().getDrawable(R.drawable.six);
                                }
                                break;
                                case 7:{
                                    number = getResources().getDrawable(R.drawable.seven);
                                }
                                break;
                                case 8:{
                                    number = getResources().getDrawable(R.drawable.eight);
                                }
                                break;
                                default:{
                                    number = null;
                                }
                            }
                            buttons[count].setForeground(number);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                buttons[count].setForegroundTintList(ColorStateList.valueOf(Color.argb(100, 255,255,255)));
                            }
                            buttons[count].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                            onSwipe(view);
                            }
                            });
                            buttons[count].setBackgroundResource(R.drawable.woodblock);
                            buttons[count].setScaleType(ImageView.ScaleType.FIT_XY);
                            buttons[count].setLeft(0);
                            buttons[count].setTop(0);
                            buttons[count].setRight(0);
                            buttons[count].setBottom(0);
                            buttons[count].setX(locationOnScreen[0]);
                            buttons[count].setY(locationOnScreen[1]-yOffset);
                            rl.addView( buttons[count]);

                            gridValues.put(new Pair<>((int)buttons[count].getX(), (int)buttons[count].getY()),id_);
                            count++;
                        }else{
                            gridValues.put(new Pair<>((int)x, (int)y-yOffset),0);
                        }
                    }
                }
                  minX = (int)buttons[0].getX();
                  maxX = (int)buttons[2].getX();
                  minY = (int)buttons[0].getY();
                  maxY = (int)buttons[7].getY();
                int i = 0;
                for(int y = minY; y <= maxY;y+=buttonHeight){
                    for(int x = minX;x <= maxX;x+=buttonHeight){
                        currentCode[i] = gridValues.get(new Pair<>(x, y));
                        i++;
                    }
                }
            }
        });
    }
}
