package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ModelViewerActivity extends AppCompatActivity {

    private ImageView centerImageView; // ImageView to display image
    private SeekBar zoomSlider; // Slider for zoom control
    private View joystickBase; // Base of the joystick
    private View joystickHandle; // Handle of the joystick
    
    private float baseScale = 1.0f;
    private float lastTouchX, lastTouchY;
    private float objPosX = 0, objPosY = 0;
    private boolean joystickActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        
        // Set up zoom slider
        zoomSlider = findViewById(R.id.zoomSlider);
        zoomSlider.setProgress(50); // Default at middle
        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert 0-100 range to 0.5-1.5 scale factor
                float scale = 1.0f + (progress / 100.0f);
                centerImageView.setScaleX(scale);
                centerImageView.setScaleY(scale);
                baseScale = scale;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Set up joystick
        joystickBase = findViewById(R.id.joystickBase);
        joystickHandle = findViewById(R.id.joystickHandle);
        
        joystickBase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                final float baseRadius = joystickBase.getWidth() / 2;
                
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        joystickActive = true;
                        
                        // Calculate joystick position
                        float touchX = event.getX() - baseRadius;
                        float touchY = event.getY() - baseRadius;
                        
                        // Limit movement to the base circle
                        float distance = (float) Math.sqrt(touchX * touchX + touchY * touchY);
                        if (distance > baseRadius) {
                            touchX = touchX * baseRadius / distance;
                            touchY = touchY * baseRadius / distance;
                        }
                        
                        // Move joystick handle
                        joystickHandle.setTranslationX(touchX);
                        joystickHandle.setTranslationY(touchY);
                        
                        // Move the object based on joystick (scaled down movement)
                        float moveFactorX = touchX / 20;
                        float moveFactorY = touchY / 20;
                        
                        objPosX += moveFactorX;
                        objPosY += moveFactorY;
                        centerImageView.setTranslationX(objPosX);
                        centerImageView.setTranslationY(objPosY);
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        joystickActive = false;
                        // Return joystick to center
                        joystickHandle.setTranslationX(0);
                        joystickHandle.setTranslationY(0);
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
                // Reset position and scale when changing object
                centerImageView.setTranslationX(0);
                centerImageView.setTranslationY(0);
                objPosX = 0;
                objPosY = 0;
                zoomSlider.setProgress(50);
                centerImageView.setScaleX(1.0f);
                centerImageView.setScaleY(1.0f);
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