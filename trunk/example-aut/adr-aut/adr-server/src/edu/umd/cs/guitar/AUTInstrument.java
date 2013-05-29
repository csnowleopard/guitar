package edu.umd.cs.guitar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.android.robotium.solo.Solo;

import edu.umd.cs.guitar.proxy.ADRActivity;
import edu.umd.cs.guitar.proxy.ADRView;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

public class AUTInstrument extends Instrumentation {

  final static String tag = AUTInstrument.class.getPackage().getName();

  // App Under Test
  Intent aut = null;
  String actName = null;

  @Override
  public void onCreate(Bundle arg) {
    super.onCreate(arg);
    actName = arg.getString("AUT");
    aut = new Intent();
    aut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    try {
      aut.setClass(getTargetContext(), Class.forName(actName));
      start();
    } catch (ClassNotFoundException e) {
      Log.e(tag, e.toString());
    }
  }

  /* A!737 */
  final static int port = 10737;

  Solo solo = null;

  @Override
  public void onStart() {
    super.onStart();
    Activity act = startActivitySync(aut);
    solo = new Solo(this, act);
    // sync with this i-level logcat
    // before sending an intent to the server, erase the logcat
    // adb.executeShell(`adb logcat -c`)
    // after sending the intent, filter out other messages
    // adb.executeShell(`adb logcat -d <pkg>:I *:S`)
    Log.i(tag, actName);
    new Thread(new Runnable() {
      public void run() {
        startServer();
      }
    }).start();
  }

  public void startServer() {
    ServerSocket listener = null;
    try {
      listener = new ServerSocket(port);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
    Socket server = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    while (true) { // start the socket server
      try {
        server = listener.accept();
        in =new BufferedReader(new InputStreamReader(server.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
        String cmd = null;
        while ((cmd = in.readLine()) != null) {
          try {
            switch(Command.valueOf(cmd)) {
              case getRootWindows: getRootWindows(out); break;
              case getContainer: getContainer(out); break;
              case getChildren: getChildren(in, out); break;
              case goBackTo: goBackTo(in, out); break;
              case getViews: getViews(out); break;
              case getAllViews: getAllViews(out); break;
              case getViewAtPoint: getViewAtPoint(in, out); break;
              case click: click(in, out, false); break;
              case clickLong: click(in, out, true); break;
              case edit: edit(in); break;
              case clear: clear(in); break;
              case back: back(out); break;
              case down: down(out); break;
              case up: up(out); break;
              case finish:
                       server.close();
                       finish_coverage();
                       break;
            }
          } catch (IllegalArgumentException e) {
            Log.d(tag, "unknown command : " + cmd);
          }
        }
      } catch (IOException e) {
        Log.e(tag, e.toString());
      } finally {
        Log.d(tag, "socket close");
        try {
          in.close(); out.close(); server.close();
        } catch (IOException e) {
          Log.e(tag, e.toString());
        }
      }
    }
  }

  private void printEND(BufferedWriter out) {
    try {
      out.write("END"); out.newLine();
      out.flush();
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  final Gson gson    = new Gson();
  final Type act_ty  = new TypeToken<ADRActivity>() {}.getType();
  final Type view_ty = new TypeToken<ADRView>() {}.getType();

  // GWindow = app.Activity
  protected void getRootWindows(BufferedWriter out) {
    Activity act = solo.getCurrentActivity();
    Log.d(tag, "getRootWindows: " + act.toString());
    String json = gson.toJson(new ADRActivity(act), act_ty);
    try {
      out.write(json); out.newLine();
      printEND(out);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  // GContainer = view.ViewGroup
  protected void getContainer(BufferedWriter out) {
    Activity act = solo.getCurrentActivity();
    View v = act.getWindow().getDecorView().findViewById(android.R.id.content);
    Log.d(tag, "getContainer: " + v.toString());
    String json = gson.toJson(new ADRView(v), view_ty);
    try {
      out.write(json); out.newLine();
      printEND(out);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected View searchView(int id) {
    View cur = null;
    boolean scrollable = scrollable();
    while (scrollable && solo.scrollUp());
    do {
      for (View v : solo.getCurrentViews()) {
        if (ADRView.extractID(v) == id)
          cur = v;
      }
    } while (scrollable && solo.scrollDown());
    while (scrollable && solo.scrollUp());
    return cur;
  }

  // GComponent = view.View
  protected void getChildren(BufferedReader in, BufferedWriter out) {
    try {
      int id = Integer.valueOf(in.readLine());
      View cur = searchView(id);
      if (cur == null) {
        Log.d(tag, "getChildren: serach failed");
        printEND(out);
        return;
      }
      Log.d(tag, "getChildren: " + cur.toString());
      ArrayList<View> views = new ArrayList<View>();
      if (cur instanceof ViewGroup) {
        boolean scrollable = scrollable();
        ViewGroup parent = (ViewGroup) cur;
        while (scrollable && solo.scrollUp());
        do {
          for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            Log.d(tag, "child: " + child.toString());
            views.add(child);
          }
        } while (scrollable && solo.scrollDown());
        while (scrollable && solo.scrollUp());
      }
      printViews(out, views, false);
      printEND(out);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void goBackTo(BufferedReader in, BufferedWriter out) {
    try {
      String act_name = in.readLine();
      for (Activity act : solo.getAllOpenedActivities()) {
        String opened = act.getClass().getSimpleName();
        Log.d(tag, "opened activity: " + opened);
        if (opened.equals(act_name)) {
          Log.d(tag, "matched: " + act_name);
          solo.goBackToActivity(act_name);
          break;
        }
      }
      getRootWindows(out);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  private void printViews(BufferedWriter out, ArrayList<View> views, boolean skipLayout) {
    for (View v : views) {
      if (skipLayout && v instanceof ViewGroup) continue;
      if (v.getParent() == null) {
        Log.d(tag, "null parent? " + v.toString());
      }
      String json = gson.toJson(new ADRView(v), view_ty);
      try {
        out.write(json); out.newLine();
        out.flush();
      } catch (IOException e) {
        Log.e(tag, e.toString());
      }
    }
  }

  protected void getViews(BufferedWriter out) {
    printViews(out, solo.getCurrentViews(), true);
    printEND(out);
  }

  protected void getAllViews(BufferedWriter out) {
    boolean scrollable = scrollable();
    while (scrollable && solo.scrollUp());
    do {
      printViews(out, solo.getCurrentViews(), false);
    } while (scrollable && solo.scrollDown());
    while (scrollable && solo.scrollUp());
    printEND(out);
  }

  protected void getViewAtPoint(BufferedReader in, BufferedWriter out) {
    try {
Log.e(tag, "1");
      Activity act = solo.getCurrentActivity();
Log.e(tag, "2");
      String actJson = gson.toJson(new ADRActivity(act), act_ty);
Log.e(tag, "3");

      out.write(actJson); out.newLine();
Log.e(tag, "4");

      String[] coords = in.readLine().split(",");
Log.e(tag, "5");
      int x = Integer.parseInt(coords[0].trim());
Log.e(tag, "6");
      int y = Integer.parseInt(coords[1].trim());
Log.e(tag, "7");

      for (View v : solo.getCurrentViews()) {
Log.e(tag, "8");
        final int[] xy = new int[2];
        v.getLocationInWindow(xy);
        int viewX = xy[0];
        int viewY = xy[1];

        if (x >= viewX && x <= viewX + v.getWidth()
            && y >= viewY && y <= viewY + v.getHeight()) {
Log.e(tag, "9");
          String json = gson.toJson(new ADRView(v), view_ty);
          out.write(json); out.newLine();
          out.flush();

          return;
        }
      }
    } catch (IOException e) {
Log.e(tag, "10");
	try {
Log.e(tag, "11");
	out.write("Exception thrown in getViewAtPoint"); 
	out.newLine(); 
	out.flush();
	    } catch (IOException i) {
Log.e(tag, "12");
		Log.e(tag, i.toString());
	}
      Log.e(tag, e.toString());
    }
  }

  protected void edit(BufferedReader in) {
    try { // TODO EditText may not shown on the current screen
      int target = Integer.valueOf(in.readLine());
      String input  = in.readLine();
      EditText t = solo.getEditText(target);
      solo.enterText(t, input);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void clear(BufferedReader in) {
    try { // TODO EditText may not shown on the current screen
      int target = Integer.valueOf(in.readLine());
      solo.clearEditText(target);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void click(BufferedReader in, BufferedWriter out, boolean isLong) {
    // TODO things may not shown on the current screen...
    Activity prv = solo.getCurrentActivity();
    try { 
      String target = in.readLine();
      if (isLong) {
        Log.d(tag, "clickLong: " + target);
      } else {
        Log.d(tag, "click: " + target);
      }
      if (solo.searchText(target)) {
        if (isLong)
          solo.clickLongOnText(target);
        else
          solo.clickOnText(target);
      } else if (solo.searchButton(target)) {
        if (isLong)
          Log.e(tag, "button cannot be clicked long : " + target);
        else
          solo.clickOnButton(target);
      } else {
        Log.d(tag, "not yet : click : " + target);
      }
      Activity cur = solo.getCurrentActivity();
      String json = gson.toJson(new ADRActivity(prv), act_ty);
      out.write(json); out.newLine();
      json = gson.toJson(new ADRActivity(cur), act_ty);
      out.write(json); out.newLine();
      printEND(out);
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void back(BufferedWriter out) {
    solo.goBack();
    try {
      out.write("back");
      out.newLine();
      out.flush();
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  private boolean scrollable() {
    for (View v : solo.getCurrentViews()) {
      if (v instanceof ListView) {
        Log.d(tag, "scrollable: ListView");
        return true;
      } else if (v instanceof GridView) {
        Log.d(tag, "scrollable: GribView");
        return true;
      } else if (v instanceof ScrollView) {
        Log.d(tag, "scrollable: ScrollView");
        return true;
      }
    }
    Log.d(tag, "not scrollable");
    return false;
  }

  private boolean checkScrollable(BufferedWriter out) {
    boolean scrollable = scrollable();
    if (!scrollable) {
      try {
        out.write("not scrollable");
        out.newLine();
        out.flush();
      } catch (IOException e) {
        Log.e(tag, e.toString());
      }
    }
    return scrollable;
  }

  protected void down(BufferedWriter out) {
    if (!checkScrollable(out)) return;
    try {
      if (solo.scrollDown()) {
        out.write("more");
        Log.d(tag, "down: more");
      } else {
        out.write("BOT");
        Log.d(tag, "down: bottom");
      }
      out.newLine();
      out.flush();
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void up(BufferedWriter out) {
    if (!checkScrollable(out)) return;
    try {
      if (solo.scrollUp()) {
        out.write("more");
        Log.d(tag, "up: more");
      } else {
        out.write("TOP");
        Log.d(tag, "up: top");
      }
      out.newLine();
      out.flush();
    } catch (IOException e) {
      Log.e(tag, e.toString());
    }
  }

  protected void finish_coverage() {
    // use reflection to call emma dump coverage method, to avoid
    // always statically compiling against emma jar
    String coverageFilePath = getCoverageFilePath();
    java.io.File coverageFile = new java.io.File(coverageFilePath);
    try {
      Class<?> emmaRTClass = Class.forName("com.vladium.emma.rt.RT");
      Method dumpCoverageMethod = emmaRTClass.getMethod("dumpCoverageData",
          coverageFile.getClass(), boolean.class, boolean.class);

      dumpCoverageMethod.invoke(null, coverageFile, false, false);
      // output path to generated coverage file so it can be parsed by a test harness if
      // needed
      Bundle mResults = new Bundle();
      mResults.putString("coverageFilePaht", coverageFilePath);
      // also output a more user friendly msg
      final String currentStream = mResults.getString(
          Instrumentation.REPORT_KEY_STREAMRESULT);
      mResults.putString(Instrumentation.REPORT_KEY_STREAMRESULT,
          String.format("%s\nGenerated code coverage data to %s", currentStream,
            coverageFilePath));
    } catch (ClassNotFoundException e) {
      Log.e(tag, "Is emma jar on classpath?");
    } catch (SecurityException e) {
      Log.e(tag, e.toString());
    } catch (NoSuchMethodException e) {
      Log.e(tag, e.toString());
    } catch (IllegalArgumentException e) {
      Log.e(tag, e.toString());
    } catch (IllegalAccessException e) {
      Log.e(tag, e.toString());
    } catch (InvocationTargetException e) {
      Log.e(tag, e.toString());
    }
    finish(0, new Bundle());
  }

  private String getCoverageFilePath() {
    return getTargetContext().getFilesDir().getAbsolutePath() + File.separator +
      "coverage.ec";
  }

}
