package com.cetcme.yimademo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qiuhong.qhlibrary.QHTitleView.QHTitleView;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cetcme.yimademo.FileUtil.stampToDate;
import static com.cetcme.yimademo.MainActivity.FILE_PATH;

public class RouteListActivity extends Activity {

    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.qhTitleView) QHTitleView qhTitleView;

    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList = new ArrayList<>();

    public static final int ACTIVITY_RESULT_ROUTE_SHOW = 0x02;
    public static final int ACTIVITY_RESULT_ROUTE_ADD = 0x01;
    public static final int ACTIVITY_RESULT_ROUTE_NOTHING = 0x00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        ButterKnife.bind(this);

        PermissionUtil.verifyStoragePermissions(this);

        initTitleView();
        initListView();

    }

    private void initTitleView() {
        qhTitleView.setTitle("航迹列表");
        qhTitleView.setBackView(0);
        qhTitleView.setRightView(R.mipmap.title_icon_add_2x);
        qhTitleView.setBackgroundResource(R.drawable.top_select);
        qhTitleView.setClickCallback(new QHTitleView.ClickCallback() {
            @Override
            public void onBackClick() {
                //
            }

            @Override
            public void onRightClick() {
                setResult(ACTIVITY_RESULT_ROUTE_ADD);
                finish();
            }
        });
    }

    private void initListView() {
        simpleAdapter = new SimpleAdapter(this, getFilesData(), R.layout.cell_route_list,
                new String[] {"fileName", "lastModifyTime"},
                new int[] {R.id.tv_name, R.id.tv_time});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("fileName", dataList.get(i).get("fileName").toString());
                setResult(ACTIVITY_RESULT_ROUTE_SHOW, intent);
                finish();
            }
        });
    }

    public List<Map<String, Object>> getFilesData() {
        dataList.clear();

        File f = new File(FILE_PATH);
        File[] files = f.listFiles();

        if (files == null) {
            return dataList;
        }

        for (File file: files) {
            Map<String, Object> map = new Hashtable<>();
            map.put("fileName", file.getName());
            map.put("lastModifyTime", stampToDate(file.lastModified()));
            map.put("lastModifyStamp", file.lastModified());
            map.put("fileLength", (new BigDecimal(file.length() / 1024f)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB");
            dataList.add(map);
        }
        System.out.println(dataList.size());
        return dataList;
    }


    @Override
    public void onBackPressed() {
        setResult(ACTIVITY_RESULT_ROUTE_NOTHING);
        super.onBackPressed();
    }
}
