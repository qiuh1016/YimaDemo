package com.cetcme.yimademo;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import yimamapapi.skia.M_POINT;
import yimamapapi.skia.OtherShipBaicInfo;
import yimamapapi.skia.OtherVesselCurrentInfo;
import yimamapapi.skia.YimaLib;

public class SkiaDrawView extends View {
    public YimaLib mYimaLib;
    public Bitmap fSkiaBitmap;
    private int mLastX, mLastY;
    private int mCurrX, mCurrY;

    private int mFirstX, mFirstY; // 用于计算涂是否移动

    private int mLastX0, mLastY0, mLastX1, mLastY1;
    private int mCurrX0, mCurrY0, mCurrX1, mCurrY1;

    private Context mContext;

    public boolean bNormalDragMapMode; //是否使用移动贴图模式
    private boolean bDragingMap;//是否真在进行拽图
    private int dragStartPointX, dragStartPointY;//拽动的起始位置
    private int dragMapOffsetPointX, dragMapOffsetPointY; //移动拽图的X/Y偏移量

    private double pinchScaleFactor;//MotionEvent.ACTION_DOWN;比例尺变化因子
    private int pasteWidth, pasteHeight; //pinch时贴图宽度和高度
    private M_POINT scrnCenterPointGeo;

    public SkiaDrawView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        mYimaLib = new YimaLib();
        mYimaLib.Create();
        mYimaLib.Init(Constant.YIMA_WORK_PATH);//初始化，传入WorkDir初始化目录地址
        mContext = ctx;
        bNormalDragMapMode = false;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        fSkiaBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        String strTtfFilePath =  "/data/data/com.cetcme.yimademo/files/WorkDir/DroidSansFallback.ttf";
        mYimaLib.RefreshDrawer(fSkiaBitmap, strTtfFilePath);//刷新绘制器，需要传入字体文件地址，用户可以自己修改为别的字体
        mYimaLib.OverViewLibMap(0);//概览第一幅图
//        mYimaLib.SetDisplayCategory(3);
//        mYimaLib.SetIfShowSoundingAndMinMaxSound(true, 0, 20);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        mYimaLib.ViewDraw(fSkiaBitmap, null, null);
        //YimaLib.ViewDraw(fSkiaBitmap, "com/example/viewdraw/viewdrawinndkskiausestaticlib/SkiaDrawView", "AfterDraw");//绘制海图到fSkiaBitmap
        canvas.drawBitmap(fSkiaBitmap, 0, 0, null);//

//        Paint paint = new Paint();
//        paint.setARGB(255,255, 0, 0);
//        canvas.drawRect(0, 0, 500, 800, paint);

        Log.i("SkiaDrawView.onDraw", "onDraw end.");
    }

    private String tag = "SkiaDrawView";

    private boolean dragingMap = false;
    @Override
    //手势滑动
    public boolean onTouchEvent(MotionEvent event) {

        mCurrX = mLastX;
        mCurrY = mLastY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.i(tag, "down");
                mLastX = (int) event.getX();
                mLastY = (int) event.getY();

                if (mFirstX == 0) mFirstX = mLastX;
                if (mFirstY == 0) mFirstY = mLastY;

                if(bNormalDragMapMode) {
                    bDragingMap = true;//拽动起始
                    dragStartPointX = mLastX;
                    dragStartPointY = mLastY;
                    pasteWidth = fSkiaBitmap.getWidth();
                    pasteHeight = fSkiaBitmap.getHeight();
                    pinchScaleFactor = 1;
                    scrnCenterPointGeo = mYimaLib.getGeoPoFromScrnPo(fSkiaBitmap.getWidth() / 2, fSkiaBitmap.getHeight() / 2);
                }
                //ShowShipInfo(mLastX, mLastY, 200000);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(tag, "move");
                dragingMap = true;

                mLastX =  (int) event.getX();
                mLastY =  (int) event.getY();
                int pointCount = event.getPointerCount();

                if(pointCount >= 2)//pinch-->随手指放大缩小
                {
                    Log.i("Pinch", "into");
                    if(bNormalDragMapMode) {
                        bDragingMap = false;
                        dragMapOffsetPointX = dragMapOffsetPointY = 0;
                    }
                    int painterIndex0 = event.findPointerIndex(0);
                    int painterIndex1 = event.findPointerIndex(1);
                    if(painterIndex0 == -1 || painterIndex1 == -1){//三个手指缩放时，painterIndex1有时会获取到-1，导致崩溃
                        break;
                    }
                    Log.i("Pinch", "painterIndex0:" + String.valueOf(painterIndex0) + ",painterIndex1:" + String.valueOf(painterIndex1));
                    if((mLastX0 == 0) || (mLastY0 == 0) || ( mLastX1 == 0) || (  mLastY1 == 0) )
                    {
                        mLastX0 = (int)event.getX(painterIndex0);
                        mLastY0 = (int)event.getY(painterIndex0);
                        mLastX1 = (int)event.getX(painterIndex1);
                        mLastY1 = (int)event.getY(painterIndex1);
                        invalidate();
                        break;
                    }
                    mCurrX0 = (int)event.getX(painterIndex0);
                    mCurrY0 = (int)event.getY(painterIndex0);
                    mCurrX1 = (int)event.getX(painterIndex1);
                    mCurrY1 = (int)event.getY(painterIndex1);
                    double d1 = Math.sqrt(Math.pow(mLastX0 - mLastX1, 2) + Math.pow(mLastY0 - mLastY1, 2));
                    double d2 = Math.sqrt(Math.pow(mCurrX0 - mCurrX1, 2) + Math.pow(mCurrY0 - mCurrY1, 2));
                    double currentScaleFactor = d2 / d1;
                    if(currentScaleFactor == 1.0)
                        break;
//                    Log.i("Pinch", "currentScaleFactor:" + String.valueOf(currentScaleFactor) +
//                            "; Curr:(" + String.valueOf(mCurrX0) + "," + String.valueOf(mCurrY0)  +"),("+  String.valueOf(mCurrX1) + "," + String.valueOf(mCurrY1)  + ").");
//                    Point centerScrnPo = new Point(fSkiaBitmap.getWidth() / 2, fSkiaBitmap.getHeight() / 2);
//                    yimamapapi.skia.yimaclass.M_POINT centerGeoPo = new yimamapapi.skia.yimaclass.M_POINT();
//                    centerGeoPo = yimamapapi.skia.YimaLib.getGeoPoFromScrnPo(centerScrnPo.x, centerScrnPo.y);//由屏幕坐标获取地理坐标
//                    Point mouseScrnPo = new Point((mCurrX0 + mCurrX1) / 2, (mCurrY0 + mCurrY1) / 2);
//                    yimamapapi.skia.yimaclass.M_POINT  mouseGeoPo = new yimamapapi.skia.yimaclass.M_POINT();
//                    mouseGeoPo = yimamapapi.skia.YimaLib.getGeoPoFromScrnPo(mouseScrnPo.x, mouseScrnPo.y);
//                    Point newCenterGeoPo = new Point((int)(mouseGeoPo.x - (mouseGeoPo.x - centerGeoPo.x) / currentScaleFactor), (int)(mouseGeoPo.y - (mouseGeoPo.y - centerGeoPo.y) / currentScaleFactor));               i
                    if(bNormalDragMapMode) {
                        pasteWidth = (int) (pasteWidth * currentScaleFactor);
                        pasteHeight = (int) (pasteHeight * currentScaleFactor);
                        pinchScaleFactor = pinchScaleFactor * currentScaleFactor;
                        int dstOffsetX = (fSkiaBitmap.getWidth() - pasteWidth) / 2;
                        int dstOffsetY = (fSkiaBitmap.getHeight() - pasteHeight) / 2;
                        Log.i("Pinch", "pasteWidth:" + String.valueOf(pasteWidth) + ",pasteHeight:" + String.valueOf(pasteHeight) + ",dstOffsetX:" + String.valueOf(dstOffsetX) + ",dstOffsetY:" + String.valueOf(dstOffsetY));
                        mYimaLib.DrawScaledMap(dstOffsetX, dstOffsetY, pasteWidth, pasteHeight);
                    }
                    else {
                        mYimaLib.SetCurrentScale(mYimaLib.GetCurrentScale() / (float) currentScaleFactor);//设置比例尺
                    }
                    mLastX0 = (int)event.getX(painterIndex0);
                    mLastY0 = (int)event.getY(painterIndex0);
                    mLastX1 = (int)event.getX(painterIndex1);
                    mLastY1 = (int)event.getY(painterIndex1);
                    invalidate();
                    break;
                }

                // 判断是否为拖动
                if (mLastY - mFirstY == 0 && mLastX - mFirstX == 0) {
                    dragingMap = false;
                }

                int iDragX = mLastX - mCurrX;
                int iDragY = mLastY - mCurrY;
                if ((iDragX == 0) && (iDragY == 0)) {
                    break;
                }
                if (bNormalDragMapMode && bDragingMap) {
                    dragMapOffsetPointX = iDragX;//mLastX - dragStartPointX;
                    dragMapOffsetPointY = iDragY;//mLastY - dragStartPointY;//curMouseScrnPo - dragStartPoint;
                    mYimaLib.PasteToScrn(dragMapOffsetPointX, dragMapOffsetPointY);
                } else  mYimaLib.SetMapMoreOffset(iDragX, iDragY);//移动设置偏移

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                Log.i(tag, "up");
                mLastX0 = mLastY0 = mLastX1 = mLastY1 = mFirstX = mFirstY = 0;

                // 不是拖动模式
                if (!dragingMap) {
                    if (onMapClickListener != null) {
                        onMapClickListener.onMapClicked(mYimaLib.getGeoPoFromScrnPo(mCurrX, mCurrY));
                    }
                }
                dragingMap = false;

                if(bNormalDragMapMode){//留白模式
                    if (bDragingMap)//留白拖动结束
                        mYimaLib.SetMapMoreOffset((int)event.getX() - dragStartPointX, (int)event.getY() - dragStartPointY);
                    else{//pinch拖动结束
                        mYimaLib.CenterMap(scrnCenterPointGeo.x, scrnCenterPointGeo.y);
                        mYimaLib.SetCurrentScale( mYimaLib.GetCurrentScale() / (float) pinchScaleFactor);
                    }
                    bDragingMap = false;//拽动结束
                }
                invalidate();

                break;
            default:
                break;
        }
        return true;
    }

    //显示本船信息
    public void ShowShipInfo(int scrnX, int scrnY, int scale){
        if(mYimaLib.GetCurrentScale() > scale) return;
        int retOtherVesselId = mYimaLib.SelectOtherVesselByScrnPoint(scrnX, scrnY);
        if(retOtherVesselId != -1) {
            int shipPos = mYimaLib.GetOtherVesselPosOfID(retOtherVesselId);
            OtherShipBaicInfo otherShipBaicInfo = mYimaLib.getOtherVesselBasicInfo(shipPos);
            OtherVesselCurrentInfo otherVesselCurrentInfo = mYimaLib.getOtherVesselCurrentInfo(shipPos);
            String strInfo = new String();
            strInfo = "船名:"+otherShipBaicInfo.strShipName + "\n" + "MMSI:"+ otherShipBaicInfo.itrMmsi + "\n"
                    + "经度："+ String.valueOf(otherVesselCurrentInfo.currentPoint.x) + "\n"  + "纬度："+ String.valueOf(otherVesselCurrentInfo.currentPoint.y) + "\n"
                    + "航速："+ otherVesselCurrentInfo.fSpeedOverGround + "\n"  + "航向："+ otherVesselCurrentInfo.fCourseOverGround +"\n";
            new AlertDialog.Builder(mContext)
                    .setTitle("船舶")
                    .setMessage(strInfo)
                    .show();
        }
    }
    public OnMapClickListener onMapClickListener;

    public void setOnMapClickListener(OnMapClickListener onMapClickListener){
        this.onMapClickListener = onMapClickListener;
    }

    public interface OnMapClickListener {
        void onMapClicked(M_POINT m_point);
    }

}

