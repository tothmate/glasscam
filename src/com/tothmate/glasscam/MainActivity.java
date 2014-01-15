package com.tothmate.glasscam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;


// credit: https://github.com/jaredsburrows/OpenQuartz

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private JavaCameraView mCameraView;
    private CascadeClassifier mJavaDetector;
    
    static {
    	OpenCVLoader.initDebug();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try 
		{
			InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
			File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);

			//File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
			File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");

			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) 
			{
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());

			cascadeDir.delete();

		} catch (IOException e) {}
		
		
		mCameraView = new JavaCameraView(this, -1);
		mCameraView.setCvCameraViewListener(this);
		mCameraView.enableView();
		mCameraView.enableFpsMeter();
        FrameLayout layout = (FrameLayout) findViewById(R.id.FrameLayout);
        layout.addView(mCameraView);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
	}

	@Override
	public void onCameraViewStopped() {
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat mRgba = inputFrame.rgba();
		Mat mGray = inputFrame.gray();

		MatOfRect faces = new MatOfRect();

		mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
					new Size(120, 120), new Size());

		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
		{
			Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 255, 255), 3);
		}
		
		return mRgba;
	}
}
