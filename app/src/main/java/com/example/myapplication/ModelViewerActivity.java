package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ModelViewerActivity extends AppCompatActivity {

    private ImageView centerImageView; // ImageView để hiển thị ảnh

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

        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<String> buttonLabels = Arrays.asList("Notebook", "Pencil case", "Schoolbag", "Globe model", "Scissors");

        ButtonAdapter adapter = new ButtonAdapter(this, buttonLabels, new ButtonAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(int position) {
                updateCenterImage(position);
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
