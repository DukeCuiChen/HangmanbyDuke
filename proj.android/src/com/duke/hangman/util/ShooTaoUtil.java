package com.duke.hangman.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ShooTaoUtil {
	
	public interface WarnDialogButtonCallback{
		public void isClick();
	}

	public static ProgressDialog showProgress(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.show();
		return dialog;
	}
	
	public static void warnDialog(final Activity context, final String message, 
			final String button, final boolean cancel, final WarnDialogButtonCallback callback){
		
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(context)
				.setMessage(message)
				.setPositiveButton(button, new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						callback.isClick();
					}
				}).setCancelable(cancel).show();
				
			}
		});
	}
}
