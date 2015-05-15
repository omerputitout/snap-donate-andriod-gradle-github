package com.snapdonate.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.snapdonate.app.interfaces.OnDialogButtonClickListener;

public class AppDialog {

	public static void showAlert(Context context, String message,
			String negativeButtonLabel) {
		new AlertDialog.Builder(context).setMessage(message).setCancelable(false)
				.setNegativeButton(negativeButtonLabel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	public static void showAlert(Context context, String message,
			String positiveButtonLabel, String negativeButtonLabel,
			final OnDialogButtonClickListener dialogListener , final int requestCode) {
		new AlertDialog.Builder(context).setMessage(message).setCancelable(false)
				.setPositiveButton(positiveButtonLabel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogListener.onDialogPositiveButtonClick(requestCode);
						dialog.dismiss();
					}
				}).setNegativeButton(negativeButtonLabel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogListener.onDialogNegativeButtonClick(requestCode);
						dialog.dismiss();
					}
				}).show();
	}

}
