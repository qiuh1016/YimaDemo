package com.cetcme.yimademo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import yimamapapi.skia.M_POINT;
import yimamapapi.skia.ShipOffRoute;

public class NavigationActivity extends AppCompatActivity implements SkiaDrawView.OnMapClickListener{

    @BindView(R.id.skiaView) SkiaDrawView fMainView;
    @BindView(R.id.btn_navigation) Button btn_navigation;
    @BindView(R.id.ly_status) LinearLayout ly_status;

    private int endWp = -1;
    private int routeID = -1;
    private M_POINT myLocation;

    private boolean inNavigating = false;
    private Toast toast;

    private static final int MESSAGE_TYPE_FLASH_BACK_COLOR = 0x01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_navigation);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        setStatusBackColor();

        fMainView.setOnMapClickListener(this);
        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inNavigating) {
                    toast.setText("导航结束");
                    toast.show();
                    btn_navigation.setText("开始导航");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    if (myLocation.x == 0 && myLocation.y == 0) {
                        toast.setText("未获取自身定位");
                        toast.show();
                        return;
                    }

                    if (endWp == -1) {
                        toast.setText("请设置导航终点");
                        toast.show();
                        return;
                    }

                    int startWp = fMainView.mYimaLib.AddWayPoint(myLocation.x, myLocation.y, "1", 20, "1");
                    int[] wpids = new int[]{startWp, endWp};
                    routeID = fMainView.mYimaLib.AddRoute("导航航线", wpids, 2, true);
                    fMainView.postInvalidate();

                    toast.setText("开始导航");
                    toast.show();

                    btn_navigation.setText("结束导航");

                }
                inNavigating = !inNavigating;
                SetOwnShip(myLocation, 45f, inNavigating);
            }
        });
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
                SetOwnShip(myLocation, 45f, inNavigating);
                fMainView.mYimaLib.SetCurrentScale(8878176.0f);
            }
        }, 10);
    }

    @Override
    public void onMapClicked(M_POINT m_point) {
        if (!inNavigating) {
            if (endWp != -1) {
                fMainView.mYimaLib.DeleteWayPoint(endWp);
                fMainView.postInvalidate();
            }
            endWp = fMainView.mYimaLib.AddWayPoint(m_point.x, m_point.y, "1", 20, "1");
        }

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
     * 返回
     * @param view
     */
    public void Back_Event(View view){
        finish();
    }

    /**
     * 设置本船
     */
    public void SetOwnShip(M_POINT m_point, float heading, boolean rotateScreen) {
        fMainView.mYimaLib.SetOwnShipBasicInfo("本船", "123456789", 100, 50);
        fMainView.mYimaLib.SetOwnShipCurrentInfo(m_point.x, m_point.y, heading, 50, 50, 0, 0);
        fMainView.mYimaLib.SetOwnShipShowSymbol(false, 4, true, 16, 5000000);
        fMainView.mYimaLib.CenterMap(m_point.x, m_point.y);
        fMainView.mYimaLib.RotateMapByScrnCenter(rotateScreen ? 0 - heading : 0);
        fMainView.postInvalidate();
    }

    /**
     * 航行监控，获取位置后调用此方法，无信息则返回null
     * @param m_point
     * @param heading
     * @param routeID
     * @return
     */
    private String safetyControl(M_POINT m_point, float heading, int routeID) {
        String msg = null;
        boolean approachDanger = fMainView.mYimaLib.IsShipApproachingIsolatedDanger(m_point.x, m_point.y, Constant.NAVIGATION_TO_DANGER_DIST_LIMIT);
        boolean crossingSafety = fMainView.mYimaLib.IsShipCrossingSafetyContour(m_point.x, m_point.y, heading, Constant.NAVIGATION_APPROACH_DIST_LIMIT);
        ShipOffRoute offRoute = fMainView.mYimaLib.isShipOffRoute(m_point.x, m_point.y, routeID, Constant.NAVIGATION_OFF_ROUTE_LIMIT);

        if (approachDanger) {
            msg = "即将进入危险区，距离" + Constant.NAVIGATION_TO_DANGER_DIST_LIMIT + "米";
        } else if (crossingSafety) {
            msg = "已进入危险水域，水深" + Constant.NAVIGATION_APPROACH_DIST_LIMIT + "米";
        } else if (offRoute.bOffRoute) {
            msg = "已偏航" + Constant.NAVIGATION_OFF_ROUTE_LIMIT + "米";
        }

        return msg;
    }

    boolean isBackWrite = true;
    boolean isDanger = true;
    private void setStatusBackColor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isDanger) {
                    try {
                        Message msg = new Message();
                        msg.what = MESSAGE_TYPE_FLASH_BACK_COLOR;
                        handler.sendMessage(msg);
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        // 处理子线程给我们发送的消息。
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_TYPE_FLASH_BACK_COLOR){
                ly_status.setBackgroundColor(isBackWrite ? getResources().getColor(R.color.navigation_status_danger) : getResources().getColor(R.color.navigation_status_normal));
                isBackWrite = !isBackWrite;
            }
        }
    };

    @Override
    protected void onDestroy() {
        isDanger = false;
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationEvent event) {
        LocationBean locationBean = event.getLocationBean();
        myLocation.x = locationBean.getLongitude();
        myLocation.y = locationBean.getLatitude();

        // 根据每次gps信息更新位置
        SetOwnShip(myLocation, locationBean.getHeading(), inNavigating);

        // 判断是否有危险
        String msg = safetyControl(myLocation, locationBean.getHeading(), 1);
        if (msg != null) {
            // 如果有危险
            if (!isDanger) {
                isDanger = true;
                setStatusBackColor();
            }
        } else {
            isDanger = false;
        }

        if (getNavigationEndDistance(myLocation, 1) < Constant.NAVIGATION_END_DIST) {
            toast.setText("导航结束");
            toast.show();
            btn_navigation.setText("开始导航");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
            inNavigating = false;
        }

    }

    private double getNavigationEndDistance(M_POINT m_point, int routeID) {
        int[] wpids = fMainView.mYimaLib.GetRouteWayPointsID(routeID);
        int lastwp = wpids[wpids.length - 1];
        M_POINT wpCoor = fMainView.mYimaLib.getWayPointCoor(lastwp);
        return fMainView.mYimaLib.GetDistBetwTwoPoint(wpCoor.x, wpCoor.y, m_point.x, m_point.y);
    }
}
