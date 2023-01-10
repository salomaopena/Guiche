package com.fenixinnovation.guiche;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity{

    private RelativeLayout noConnection, mainLayout;
    private Button  submitButton, consultButton;
    private TextView websiteTextView;
    private TextView sendMailView;
    private TextView callTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        noConnection = findViewById(R.id.no_conection);
        mainLayout = findViewById(R.id.main_layout);
        submitButton = findViewById(R.id.bt_submit);
        consultButton =  findViewById(R.id.bt_consutl);
        websiteTextView = findViewById(R.id.tx_website);
        sendMailView = findViewById(R.id.send_mail);
        callTextView = findViewById(R.id.call);


        getRuntimePermissions();

        if (!haveNetworkConnection()) {
            noConnection.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        } else {
            noConnection.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }

                submitButton.setOnClickListener(view -> {
           Intent intent = SubmitActivity.getStartIntent(HomeActivity.this);
           startActivity(intent);
        });

        consultButton.setOnClickListener(view -> {
            Intent intent = ConsultActivity.getStartIntent(HomeActivity.this);
            startActivity(intent);
        });

        websiteTextView.setOnClickListener(view -> {

            Intent browserIntent;
            if (haveNetworkConnection()) {
                try {
                    browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.guichegpn.gov.ao/"));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }else{
                Toast.makeText(this,
                        "Verifica a sua conexão de internet",
                        Toast.LENGTH_SHORT).show();
            }
        });

        sendMailView.setOnClickListener(view -> {
            String body = "";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(intent, getString(R.string.choose_email_client)));
        });

        callTextView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+callTextView.getText()));
            startActivity(intent);
        });
    }

    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(this,new String[]{
                            permission.INTERNET,
                            permission.ACCESS_NETWORK_STATE,
                    }, 0);
                }
            }
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectionWifi = false;
        boolean haveConnectionMobile = false;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfos) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected())
                    haveConnectionWifi = true;
            }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected())
                    haveConnectionMobile = true;
            }
        }
        return haveConnectionWifi || haveConnectionMobile;
    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }

}