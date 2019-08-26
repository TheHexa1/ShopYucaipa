package com.yucaipa.shop.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.yucaipa.shop.R;
import com.yucaipa.shop.utils.Constants;
import com.yucaipa.shop.utils.MySingleton;
import com.yucaipa.shop.utils.Utils;




public class QRCodeScanner extends AppCompatActivity {

//    ZXingScannerView mScannerView;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;


    Utils utils;
    SharedPreferences myPref;

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrcode_scanner);

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);

//        barcodeScannerView.setTorchListener(this);

        /*switchFlashlightButton = (Button)findViewById(R.id.switch_flashlight);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }*/

        toolbar = (Toolbar) findViewById(R.id.scanner_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        utils = Utils.getInstance(this);

        myPref = getSharedPreferences(Constants.myPrefKey,MODE_PRIVATE);

        populatePurchases(myPref.getInt("purchase_counter",0));

        Glide.with(this).load(R.drawable.game_logo_home).into((ImageView)findViewById(R.id.iv_yucaipa_logo));

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void populatePurchases(int total){
        for(int i=1; i<total+1; i++){
            String iv = "iv_"+i;
            int resID = getResources().getIdentifier(iv, "id", "com.yucaipa.shop");
            ImageView imageView = (ImageView) findViewById(resID);
            imageView.setImageResource(R.drawable.ic_check_circle_green_24dp);
//            Glide.with(this).load(R.drawable.ic_check_circle_green_24dp).into(imageView);
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    /*private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }*/
}
