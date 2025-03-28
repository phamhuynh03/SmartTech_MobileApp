package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import android.graphics.drawable.GradientDrawable;

public class ModelViewerActivity extends AppCompatActivity {

    private ImageView centerImageView;
    private SeekBar zoomSlider;
    private View joystickBase;
    private View joystickHandle;
    private TextView infoTextView;
    private Button infoButton;

    private float baseScale = 1.0f;
    private float objPosX = 0, objPosY = 0;
    private int currentObjectIndex = 0;
    private boolean isInfoVisible = false;

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
        joystickBase = findViewById(R.id.joystickBase);
        joystickHandle = findViewById(R.id.joystickHandle);
        infoTextView = findViewById(R.id.infoTextView);
        infoButton = findViewById(R.id.infoButton);

        infoTextView.post(() -> {
            GradientDrawable background = new GradientDrawable();
            background.setColor(0x80000000);
            background.setCornerRadius(50);
            infoTextView.setBackground(background);
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoVisible) {
                    infoTextView.setVisibility(View.GONE);
                } else {
                    updateInfoText(currentObjectIndex);
                    resetInfoTextViewPosition();
                    infoTextView.setVisibility(View.VISIBLE);
                }
                isInfoVisible = !isInfoVisible;
            }
        });

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

                        float moveFactor = 0.05f;
                        objPosX += touchX * moveFactor;
                        objPosY += touchY * moveFactor;

                        centerImageView.animate()
                                .translationX(objPosX)
                                .translationY(objPosY)
                                .setDuration(0)
                                .start();

                        updateInfoTextPosition();
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> buttonLabels = Arrays.asList(
                getString(R.string.info_obj1),
                getString(R.string.info_obj2),
                getString(R.string.info_obj3),
                getString(R.string.info_obj4),
                getString(R.string.info_obj5));

        ButtonAdapter adapter = new ButtonAdapter(this, buttonLabels, new ButtonAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(int position) {
                updateCenterImage(position);
                currentObjectIndex = position;

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

                resetInfoTextViewPosition();

                if (isInfoVisible) {
                    updateInfoText(position);
                    infoTextView.setVisibility(View.VISIBLE);
                }
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

    private void updateInfoText(int position) {
        String infoText = "";
        switch (position) {
            case 0:
                infoText = getString(R.string.info_obj1);
                break;
            case 1:
                infoText = getString(R.string.info_obj2);
                break;
            case 2:
                infoText = getString(R.string.info_obj3);
                break;
            case 3:
                infoText = getString(R.string.info_obj4);
                break;
            case 4:
                infoText = getString(R.string.info_obj5);
                break;
        }
        infoTextView.setText(infoText);
    }

    private void updateInfoTextPosition() {
        float imageX = centerImageView.getTranslationX();
        float imageY = centerImageView.getTranslationY();

        float offsetX = -200;
        float offsetY = -200;

        infoTextView.animate()
                .translationX(imageX + offsetX)
                .translationY(imageY + offsetY)
                .setDuration(0)
                .start();
    }

    private void resetInfoTextViewPosition() {
        float offsetX = -200;
        float offsetY = -200;

        infoTextView.animate()
                .translationX(offsetX)
                .translationY(offsetY)
                .setDuration(0)
                .start();
    }
}
