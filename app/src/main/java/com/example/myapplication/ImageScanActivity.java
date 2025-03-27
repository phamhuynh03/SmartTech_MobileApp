package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.google.zxing.ResultPoint;

import java.util.List;

public class ImageScanActivity extends AppCompatActivity {
    private DecoratedBarcodeView barcodeScannerView;
    private TextView qrCodeResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_scan);

        ImageView backButton = findViewById(R.id.imageView);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ImageScanActivity.this, HomepageActivity.class);
            startActivity(intent);
            finish();
        });

        barcodeScannerView = findViewById(R.id.barcode_scanner);
        qrCodeResultTextView = findViewById(R.id.qr_code_result);

        setupQRScanner();
    }

    private void setupQRScanner() {
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    qrCodeResultTextView.setVisibility(TextView.VISIBLE);

                    String qrContent = result.getText();
                    if (Patterns.WEB_URL.matcher(qrContent).matches()) {
                        qrCodeResultTextView.setText(qrContent);
                        qrCodeResultTextView.setAutoLinkMask(Linkify.WEB_URLS);
                        qrCodeResultTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        qrCodeResultTextView.setText(qrContent);
                    }
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}