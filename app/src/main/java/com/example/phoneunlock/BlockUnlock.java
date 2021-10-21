package com.example.phoneunlock;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class BlockUnlock extends Fragment {
    TableLayout table;
    int height;

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

        view.findViewById(R.id.block1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                doSomething(view);
            }
        });

        view.findViewById(R.id.block2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                doSomething(view);
            }
        });





    }

    public void doSomething(final View view){
        view.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                if(view.getY() > 0)
                    view.animate().translationYBy(-height);
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
                if(view.getY() < 722)
                    view.animate().translationYBy(height);
                Log.d("Y", String.valueOf(view.getY()));
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                getView().removeOnLayoutChangeListener(this);
                height = v.findViewById(R.id.grid1).getHeight();
                table = v.findViewById(R.id.tableLayout);

                TableRow row;
                ImageView gridSpace;
                int[] locationOnScreen;

                for(int i = 0;i< 3;i++){
                    row = (TableRow) table.getChildAt(i);
                    for(int j = 0;j < 3;j++){
                        gridSpace = (ImageView) row.getChildAt(j);
                        locationOnScreen = new int[2];
                        gridSpace.getLocationInWindow(locationOnScreen);
                        Log.d("Grid " + String.valueOf(j), locationOnScreen[0] + ", " + locationOnScreen[1]);
                    }

                }
            }
        });


    }
}
