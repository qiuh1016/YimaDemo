package com.cetcme.yimademo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import yimamapapi.skia.M_POINT;

public class NavigationActivity extends AppCompatActivity implements SkiaDrawView.OnMapClickListener{

    private SkiaDrawView fMainView;

    private int wp = -1;
    private M_POINT myLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        getSupportActionBar().hide();

        fMainView = findViewById(R.id.skiaView);
        fMainView.setOnMapClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        myLocation = new M_POINT();
        myLocation.x = 121 * 10000000;
        myLocation.y = 32 * 10000000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SetOwnShip(myLocation, 45);
                fMainView.mYimaLib.SetCurrentScale(8878176.0f);
            }
        }, 10);
    }

    @Override
    public void onMapClicked(M_POINT m_point) {
        if (wp != -1) {
            fMainView.mYimaLib.DeleteWayPoint(wp);
            fMainView.postInvalidate();
        }
        wp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
    }

    public void ShowScale_Event(View view) {
        System.out.println(fMainView.mYimaLib.GetCurrentScale());
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
     * 定位本船位置
     * @param view
     */
    public void OwnCenterClick_Event(View view){
        if (myLocation.x == 0.0 && myLocation.y == 0.0) return;
        fMainView.mYimaLib.CenterMap(myLocation.x, myLocation.y);
        fMainView.postInvalidate();//刷新fMainView
    }

    /**
     * 设置本船
     */
    public void SetOwnShip(M_POINT m_point, float heading) {
        fMainView.mYimaLib.SetOwnShipBasicInfo("本船", "123456789", 100, 50);
        fMainView.mYimaLib.SetOwnShipCurrentInfo(m_point.x, m_point.y, heading, 50, 50, 0, 0);
        fMainView.mYimaLib.SetOwnShipShowSymbol(false, 4, true, 16, 5000000);
        fMainView.mYimaLib.CenterMap(m_point.x, m_point.y);
        fMainView.postInvalidate();

//        fMainView.mYimaLib.SetOwnShipCurrentInfo(1215000000, 320000000, 0, 50, 50, 0, 0);

//        fMainView.mYimaLib.SetOwnShipCurrentInfo(1220000000, 320000000, 0, 50, 50, 0, 0);
        Toast.makeText(this, "测试设置本船", Toast.LENGTH_SHORT).show();
    }

}
