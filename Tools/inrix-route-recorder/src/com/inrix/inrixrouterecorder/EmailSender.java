package com.inrix.inrixrouterecorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class EmailSender {

	public static void sendRoute(Activity activity, String route) {
		String emailSubject = "InrixRouteRecorder: route ready!";
		String emailBody = "Route file attached";

		final Intent emailIntent;
		emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.setType("plain/text");
		emailIntent
				.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);

		File routeFile = null;

		File path = null;
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			path = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		}

		if (path != null) {
			routeFile = new File(path, "inrix_route.txt");
		}

		try {
			PrintWriter routeWriter = new PrintWriter(new BufferedWriter(new FileWriter(routeFile,
					true)));
			routeWriter.append(route);
			routeWriter.flush();
			routeWriter.close();
		} catch (Exception e) {

			if (routeFile != null) {
				routeFile.delete();
				routeFile = null;
			}
		}

		if (routeFile != null) {
			emailIntent.putExtra(android.content.Intent.EXTRA_STREAM,
					Uri.fromFile(routeFile));
		} else {
			Toast.makeText(activity,
					"Failed to create file :( Attached as a plain text",
					Toast.LENGTH_LONG).show();
			emailBody += "\n" + route;
		}
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		activity.startActivity(Intent
				.createChooser(emailIntent, "Send mail..."));
	}

}
