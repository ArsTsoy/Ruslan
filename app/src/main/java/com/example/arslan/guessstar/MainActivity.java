package com.example.arslan.guessstar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    private Button button1, button2, button3, button4;
    private ImageView imageViewStar;

    private ArrayList<Bitmap> allImages;
    public static final String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageViewStar = findViewById(R.id.starPicture);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        CodeDownloader codeDownloader = new CodeDownloader();
        ImageDownloader imageDownloader = new ImageDownloader();
        try {
            String pageCode = codeDownloader.execute("http://www.posh24.se/kandisar").get();
//            allImages = new ArrayList<>(imageDownloader.execute(pageCode).get());
            Log.i(TAG, allImages.toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Glide.with(this).load("sdad").into(imageViewStar);

    }


    public void click(View view) {
        imageViewStar.setImageBitmap(allImages.get(0));
    }


    private class CodeDownloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder pageCode = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    pageCode.append(line);
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "Ошибка в подключении во время чтения кода");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (urlConnection == null) {
                    urlConnection.disconnect();
                }

            }


            return pageCode.toString();
        }
    }

    private class ImageDownloader extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> allUrls = new ArrayList<>();
            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcherImg = patternImg.matcher(strings[0]);

            while (matcherImg.find()) {
                String downloadAddress = matcherImg.group(1);
                allUrls.add(downloadAddress);
            }

            return allUrls;
        }
    }

    private class NameDownloader extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> allNames = new ArrayList<>();
            Pattern patternName = Pattern.compile("alt=\"(.*?)\"");
            Matcher matcherName = patternName.matcher(strings[0]);
            while (matcherName.find()) {
                String downloadAddress = matcherName.group(1);
                allNames.add(downloadAddress);
            }
            return allNames;
        }
    }
}
