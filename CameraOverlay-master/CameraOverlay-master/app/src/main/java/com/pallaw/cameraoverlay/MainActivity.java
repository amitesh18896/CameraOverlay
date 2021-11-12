package com.pallaw.cameraoverlay;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private boolean previewing=false;
    private Camera camera=null;
    private SurfaceHolder cameraSurfaceHolder;
    private SurfaceView cameraSurfaceView;
    private ImageView btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraSurfaceView = (SurfaceView)
                findViewById(R.id.cameraSurface);
        //  cameraSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(640, 480));
        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);
        //    cameraSurfaceHolder.setType(SurfaceHolder.
        //                                               SURFACE_TYPE_PUSH_BUFFERS);


        btnCapture = (ImageView) findViewById(R.id.btnCamera);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                camera.takePicture(cameraShutterCallback,
                        cameraPictureCallbackRaw,
                        cameraPictureCallbackJpeg);
            }
        });
    }

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };
    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
        }
    };
    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            int wid = cameraBitmap.getWidth();
            int hgt = cameraBitmap.getHeight();

            //  Toast.makeText(getApplicationContext(), wid+""+hgt, Toast.LENGTH_SHORT).show();
            Bitmap newImage = Bitmap.createBitmap
                    (wid, hgt, Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(newImage);

//            canvas.drawBitmap(cameraBitmap, 0f, 0f, null);

//            Drawable drawable = getResources().getDrawable
//                    (R.drawable.img_volkswagen);

//            drawable.setBounds(0, 0, 150, 75);
//            drawable.draw(canvas);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.img_volkswagen);



            c.drawBitmap(cameraBitmap, 0f, 0f, null);
            c.drawBitmap(Bitmap.createScaledBitmap(largeIcon, 200, 119, false), 0f, ((float) hgt)-119, null);





            File storagePath = new File(Environment.
                    getExternalStorageDirectory() + "/PhotoAR/");
            storagePath.mkdirs();

            File myImage = new File(storagePath,
                    Long.toString(System.currentTimeMillis()) + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newImage.compress(Bitmap.CompressFormat.JPEG, 80, out);


                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }

            camera.startPreview();


            newImage.recycle();
            newImage = null;

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
            startActivity(intent);

        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        // TODO Auto-generated method stub

        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(640, 480);
            parameters.setPictureSize(640, 480);
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);

            }

            // parameters.setRotation(90);
            camera.setParameters(parameters);

            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Device camera  is not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
}
