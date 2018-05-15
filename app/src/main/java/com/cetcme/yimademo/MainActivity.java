package com.cetcme.yimademo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import yimamapapi.skia.M_POINT;
import yimamapapi.skia.PointArrayInfo;
import yimamapapi.skia.YimaLib;

public class MainActivity extends AppCompatActivity implements SkiaDrawView.OnMapClickListener {


    private SkiaDrawView fMainView;
    private Toast toast;

    /**
     * 加载库文件（只需调用一次）
     */
    static {
        YimaLib.LoadLib();
    }

    int firstWp = -1;
    int secondWp = -1;
    int routeID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyYimaFile();

        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        fMainView = findViewById(R.id.skiaView);

        fMainView.setOnMapClickListener(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    /**
     * 文件复制: 把assets目录下的workDir目录拷贝到data/data/包名/files目录下。（只需调用一次，用户也可以自己实现）
     */
    private void copyYimaFile() {
        String strFile = getApplicationContext().getFilesDir().getAbsolutePath();
        long startTime = System.currentTimeMillis();
        YimaLib.CopyWorkDir(getApplicationContext(), strFile);
        long endTime = System.currentTimeMillis(); //获取结束时间
        Toast.makeText(MainActivity.this, "文件拷贝" + String.valueOf(endTime - startTime), Toast.LENGTH_SHORT).show();
    }

    /**
     * 放大地图
     * @param view
     */
    public void ZoomInClick_Event(View view) {
        System.out.println("放大");
        fMainView.mYimaLib.SetCurrentScale(fMainView.mYimaLib.GetCurrentScale() / 2);
        fMainView.postInvalidate();//刷新fMainView
    }

    /**
     * 缩小地图
     * @param view
     */
    public void ZoomOutClick_Event(View view) {
        System.out.println("缩小");
        fMainView.mYimaLib.SetCurrentScale(fMainView.mYimaLib.GetCurrentScale() * 2);
        fMainView.postInvalidate();//刷新fMainView
    }

    /**
     * 取消上一个路径点
     * @param view
     */
    public void PointCancel_Event(View view) {
        if (routeID != -1) {
            int count = fMainView.mYimaLib.GetRouteWayPointsCount(routeID);
            int[] ids = fMainView.mYimaLib.GetRouteWayPointsID(routeID);

            if (count == 0) {
                return;
            }
            fMainView.mYimaLib.DeleteRouteWayPoint(routeID, count - 1, 1);
            fMainView.mYimaLib.DeleteWayPoint(ids[ids.length - 1]);
            fMainView.postInvalidate();

//            fMainView.mYimaLib.DeleteRouteWayPoint(routeID, count - 1, 1);
//            fMainView.mYimaLib.DeleteWayPoint(ids[ids.length - 1]);
//            fMainView.postInvalidate();
//
//            if (count == 1) {
//                firstWp = -1;
//                routeID = -1;
//            } else if (count == 2) {
//                secondWp = -1;
//            }
        }
    }

    /**
     * 保存路径到文件
     * @param view
     */
    public void RouteSave_Event(View view) {
        if (routeID == -1) {
            toast.setText("没有路径点");
            toast.show();
            return;
        }

        PermissionUtil.verifyStoragePermissions(this);

        File filePath = new File(Constant.ROUTE_FILE_PATH);
        if(!filePath.exists()) {
            filePath.mkdir();
        }

        long timestamp = new Date().getTime();
        boolean saveOk = fMainView.mYimaLib.SaveRoutesToFile(Constant.ROUTE_FILE_PATH + "/" + timestamp);

        if (saveOk) {
            toast.setText("保存成功: " + timestamp);
            toast.show();
        }
    }

    /**
     * 打开路径清单activity
     * @param view
     */
    public void RouteList_Event(View view) {
        startActivityForResult(new Intent( this, RouteListActivity.class), 0);
    }

    /**
     * 打开导航activity
     * @param view
     */
    public void Navigation_Event(View view) {
        startActivity(new Intent( this, NavigationActivity.class));
    }


    /**
     * 清屏
     * @param view
     */
    public void ClearRoute_Event(View view) {
        clearRoute();
    }
    /**
     * 将物理坐标变成整形
     * @param location
     * @return
     */
    private int getLocationInt(double location) {
        return (int) (location * 10000000);
    }

    /**
     * 获取屏幕参数
     */
    public void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        System.out.println("屏幕宽度（像素）：" + width);
        System.out.println("屏幕高度（像素）：" + height);
        System.out.println("屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        System.out.println("屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        System.out.println("屏幕宽度（dp）：" + screenWidth);
        System.out.println("屏幕高度（dp）：" + screenHeight);
    }

    /**
     * 设置路径
     * @param m_point
     */
    @Override
    public void onMapClicked(M_POINT m_point) {
        double x = m_point.x / 10000000f;
        double y = m_point.y / 10000000f;

        toast.setText(String .format("x: %.3f, y: %.3f", x, y));
        toast.show();

        if (routeID == -1) {
            routeID = fMainView.mYimaLib.AddRoute("航线", new int[]{}, 0, true);
            fMainView.mYimaLib.SetPointSelectJudgeDist(30, 15);
        }
        int wp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
        int wpCount = fMainView.mYimaLib.GetRouteWayPointsCount(routeID);
        fMainView.mYimaLib.AddRouteWayPoint(routeID, wpCount, new int[] {wp}, 1);

//        if (firstWp == -1) {
//            firstWp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
//        } else if (secondWp == -1) {
//            secondWp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
//            int[] wpids = {firstWp, secondWp};
//            routeID = fMainView.mYimaLib.AddRoute("航线", wpids, 2, true);
//            //int[] points = fMainView.mYimaLib.GetRouteWayPointsID(routeID);
//            //boolean bDelete = YimaLib.DeleteRoute(routeID);
//            fMainView.mYimaLib.SetPointSelectJudgeDist(30, 15);
//        } else {
//            int wp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
//            int wpCount = fMainView.mYimaLib.GetRouteWayPointsCount(routeID);
//            fMainView.mYimaLib.AddRouteWayPoint(routeID, wpCount, new int[] {wp}, 1);
//        }
    }

    /**
     * 接收RouterListActivity的返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RouteListActivity.ACTIVITY_RESULT_ROUTE_SHOW:
                clearRoute();
                String fileName = data.getStringExtra("fileName");
                Log.i(TAG, "load file: start");

                fMainView.mYimaLib.AddRoutesFromFile(Constant.ROUTE_FILE_PATH + "/" + fileName);
                int routeCount = fMainView.mYimaLib.GetRoutesCount();
                routeID = fMainView.mYimaLib.GetRouteIDFromPos(routeCount - 1);

                Log.i(TAG, "GetRoutesCount: " + fMainView.mYimaLib.GetRoutesCount());
                Log.i(TAG, "routeID: " + routeID);

                Log.i(TAG, "load file: end");

                Log.i(TAG, "==========================");
                break;
            case RouteListActivity.ACTIVITY_RESULT_ROUTE_ADD:
                clearRoute();
                break;
            case RouteListActivity.ACTIVITY_RESULT_ROUTE_NOTHING:
                break;
        }
    }

    private String TAG = "debug-qh";
    private void clearRoute() {
        Log.i(TAG, "==========================");

        Log.i(TAG, "clearRoute: start");
        // 如果有路径，则清除
        if (routeID != -1) {
            Log.i(TAG, "clearRoute: " + routeID);

            int[] ids = fMainView.mYimaLib.GetRouteWayPointsID(routeID);
            fMainView.mYimaLib.DeleteRouteWayPoint(routeID, 0, ids.length); // 必须在调用DeleteWayPoint之前
            for (int id : ids) {
                fMainView.mYimaLib.DeleteWayPoint(id);
            }
//            fMainView.mYimaLib.DeleteRoute(routeID);
//            routeID = -1;
//            firstWp = -1;
//            secondWp = -1;
        }

//        // 如果只有一个路径点，也清除
//        if (firstWp != -1) {
//            fMainView.mYimaLib.DeleteWayPoint(firstWp);
//            firstWp = -1;
//        }

        fMainView.postInvalidate();
        Log.i(TAG, "clearRoute: end");
    }
}
