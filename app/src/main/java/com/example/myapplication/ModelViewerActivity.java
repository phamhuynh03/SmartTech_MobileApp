package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ModelViewerActivity extends AppCompatActivity {

    private ImageView centerImageView;
    private SeekBar zoomSlider;
    private View joystickBase;
    private View joystickHandle;

    private float baseScale = 1.0f;
    private float objPosX = 0, objPosY = 0;
    private boolean joystickActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.model_viewer);

        ImageView backButton = findViewById(R.id.imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModelViewerActivity.this, HomepageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        centerImageView = findViewById(R.id.centerImageView);

        zoomSlider = findViewById(R.id.zoomSlider);
        zoomSlider.setProgress(50);
        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float scale = 0.5f + (progress / 150.0f);
                centerImageView.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setDuration(0)
                        .start();
                baseScale = scale;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        joystickBase = findViewById(R.id.joystickBase);
        joystickHandle = findViewById(R.id.joystickHandle);

        joystickBase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                final float baseRadius = joystickBase.getWidth() / 2;

                if (action != MotionEvent.ACTION_DOWN &&
                        action != MotionEvent.ACTION_MOVE &&
                        action != MotionEvent.ACTION_UP &&
                        action != MotionEvent.ACTION_CANCEL) {
                    return false;
                }

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        joystickActive = true;

                        float touchX = event.getX() - baseRadius;
                        float touchY = event.getY() - baseRadius;

                        float distanceSquared = touchX * touchX + touchY * touchY;
                        float baseRadiusSquared = baseRadius * baseRadius;

                        if (distanceSquared > baseRadiusSquared) {
                            float ratio = (float) Math.sqrt(baseRadiusSquared / distanceSquared);
                            touchX *= ratio;
                            touchY *= ratio;
                        }

                        joystickHandle.animate()
                                .translationX(touchX)
                                .translationY(touchY)
                                .setDuration(0)
                                .start();

                        float moveFactorX = touchX / 9f;
                        float moveFactorY = touchY / 9f;

                        objPosX += moveFactorX;
                        objPosY += moveFactorY;

                        centerImageView.animate()
                                .translationX(objPosX)
                                .translationY(objPosY)
                                .setDuration(0)
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        joystickActive = false;
                        joystickHandle.animate()
                                .translationX(0)
                                .translationY(0)
                                .setDuration(100)
                                .start();
                        return true;
                }
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<String> buttonLabels = Arrays.asList("Notebook", "Pencil case", "Schoolbag", "Globe model", "Scissors");

        ButtonAdapter adapter = new ButtonAdapter(this, buttonLabels, new ButtonAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(int position) {
                updateCenterImage(position);
                objPosX = 0;
                objPosY = 0;

                centerImageView.animate()
                        .translationX(0)
                        .translationY(0)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(200)
                        .start();

                zoomSlider.setProgress(50);
                baseScale = 1.0f;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void updateCenterImage(int position) {
        int drawableId = 0;
        switch (position) {
            case 0:
                drawableId = R.drawable.object_1;
                break;
            case 1:
                drawableId = R.drawable.object_2;
                break;
            case 2:
                drawableId = R.drawable.object_3;
                break;
            case 3:
                drawableId = R.drawable.object_4;
                break;
            case 4:
                drawableId = R.drawable.object_5;
                break;
        }
        centerImageView.setImageResource(drawableId);
    }
}