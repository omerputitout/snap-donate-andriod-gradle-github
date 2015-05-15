/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.snapdonate.app.vuforia.ui;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vuforia;
import com.snapdonate.app.database.DataBaseHelper;
import com.snapdonate.app.model.Charity;
import com.snapdonate.app.tabs.MainTabScreen;
import com.snapdonate.app.utils.SLog;
import com.snapdonate.app.utils.SUtils;
import com.snapdonate.app.vuforia.LoadingDialogHandler;
import com.snapdonate.app.vuforia.SampleApplicationSession;
import com.snapdonate.app.vuforia.SampleUtils;

// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer {
	private static final String LOGTAG = "ImageTargetRenderer";

	private SampleApplicationSession vuforiaAppSession;
	private ImageTargets mActivity;
	private Renderer mRenderer;
	boolean mIsActive = false;
	private DataBaseHelper mdb;

	public ImageTargetRenderer(ImageTargets activity,
			SampleApplicationSession session) {
		mActivity = activity;
		vuforiaAppSession = session;
		mdb = new DataBaseHelper(mActivity);
		mdb.openDataBase();
	}

	// Called to draw the current frame.
	@Override
	public void onDrawFrame(GL10 gl) {
		if (!mIsActive)
			return;
		// Call our function to render content
		renderFrame();
	}

	// Called when the surface is created or recreated.
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
		initRendering();
		// Call Vuforia function to (re)initialize rendering after first use
		// or after OpenGL ES context was lost (e.g. after onPause/onResume):
		vuforiaAppSession.onSurfaceCreated();
	}

	// Called when the surface changed size.
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
		// Call Vuforia function to handle render surface size changes:
		vuforiaAppSession.onSurfaceChanged(width, height);
	}

	// Function for initializing the renderer.
	private void initRendering() {
		mRenderer = Renderer.getInstance();
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
				: 1.0f);
		// Hide the Loading Dialog
		mActivity.loadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
	}

	// The render function.
	private void renderFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		State state = mRenderer.begin();
		mRenderer.drawVideoBackground();
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// handle face culling, we need to detect if we are using reflection
		// to determine the direction of the culling
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
			GLES20.glFrontFace(GLES20.GL_CW); // Front camera
		else
			GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
		// did we find any trackables this frame?
		for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
			TrackableResult result = state.getTrackableResult(tIdx);
			Trackable trackable = result.getTrackable();
			printUserData(trackable);
			// convert image name according to the Database format .
			String tackableName_str = trackable.getName();
			tackableName_str = tackableName_str.replace("_", " ");
			tackableName_str = tackableName_str.replace("1", "'");
			tackableName_str = tackableName_str.replace("2", "&");
			tackableName_str = tackableName_str.replace("3", "(");
			tackableName_str = tackableName_str.replace("4", ")");
			tackableName_str = tackableName_str.replace("5", "/");
			tackableName_str = tackableName_str.replace("6", "-");
			tackableName_str = tackableName_str.replace("7", ",");
			SLog.showLog("tackableName_str : " + tackableName_str);
			String vuforia_recognized_name = tackableName_str;
			String[] Filter_Duplicate = tackableName_str.split(" Duplicate ");
			if (Filter_Duplicate.length == 2) {
				SLog.showLog("Filter_Duplicate[0] : " + Filter_Duplicate[0]);
				SLog.showLog("Filter_Duplicate[1] : " + Filter_Duplicate[1]);
				tackableName_str = Filter_Duplicate[0];
			}
			// long name Issues checking for vuforia Limitation
			if (tackableName_str.equalsIgnoreCase("Long Name Issue One")) {
				tackableName_str = "Great Ormond Street Hospital Children's Charity (GOSHCC)";
				vuforia_recognized_name = "Great Ormond Street Hospital Children's Charity (GOSHCC) One";
			}

			// just giving flow handling
			Charity Charitydata = mdb.getCharityDataByName(tackableName_str);
			if (Charitydata != null) {
				SLog.showLog("Charityid : " + Charitydata.getId());
				// was snapped so saving info
				Charitydata.setMedium(SUtils.CHARITY_MEDIUM_SNAPPED);
				vuforia_recognized_name = vuforia_recognized_name.replace(
						"Duplicate ", "");
				Charitydata.setVuforiaRecognizedName(vuforia_recognized_name);
				Intent intent = new Intent(mActivity, MainTabScreen.class);
				intent.putExtra(SUtils.CHARITY_MODEL, Charitydata);
				intent.putExtra(SUtils.TAB_INDEX_TO_OPEN, SUtils.TAB_SNAP_INDEX);
				mActivity.startActivity(intent);
				return;
			}
			SampleUtils.checkGLError("Render Frame");
		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		mRenderer.end();
	}

	private void printUserData(Trackable trackable) {
		String userData = (String) trackable.getUserData();
		Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
	}

}
