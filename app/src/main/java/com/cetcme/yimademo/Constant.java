package com.cetcme.yimademo;

import android.os.Environment;

/**
 * Created by qiuhong on 15/05/2018.
 */

public class Constant {

    //
    public static final String YIMA_WORK_PATH = "/data/data/com.cetcme.yimademo/files/WorkDir";

    //
    public static final String ROUTE_FILE_PATH = Environment.getExternalStorageDirectory() + "/0yima_routes";


    // 危险区园面 判断半径
    public static final float NAVIGATION_TO_DANGER_DIST_LIMIT = 50f;

    // 本船距离危险水域多远报警的值
    public static final float NAVIGATION_APPROACH_DIST_LIMIT = 50f;

    // 偏航报警距离
    public  static final float NAVIGATION_OFF_ROUTE_LIMIT = 50f;

    // 导航结束距离，与终端距离小于该值则导航结束
    public static final int NAVIGATION_END_DIST = 50;
}
