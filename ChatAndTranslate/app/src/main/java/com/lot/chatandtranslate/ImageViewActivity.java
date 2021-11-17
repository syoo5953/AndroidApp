package com.lot.chatandtranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageViewActivity extends AppCompatActivity {

    ImageView imageView, btnDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        imageView = findViewById(R.id.imageView);
        btnDownload = findViewById(R.id.btn_download);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN| WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String uri = getIntent().getStringExtra("uri");
        Picasso.get().load(uri).into(imageView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(uri);
            }
        });
    }

    private void DownloadImage(String uri) {
        String filename = "Test.jpg";
        Uri downloadUrlOfImage = Uri.parse(uri);
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + "Download" + "/");


        if (!direct.exists()) {
            direct.mkdir();
        }

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrlOfImage.toString());
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + "Download" + File.separator + filename);

        dm.enqueue(request);


        Toast.makeText(this, "다운로드 시작", Toast.LENGTH_SHORT).show();
    }
}