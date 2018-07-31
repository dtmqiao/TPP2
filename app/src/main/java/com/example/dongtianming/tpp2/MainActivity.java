package com.example.dongtianming.tpp2;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dongtianming.tpp2.adapters.ComingSoonRecyclerviewAdapter;
import com.example.dongtianming.tpp2.adapters.HotRecyclerviewAdapter;
import com.example.dongtianming.tpp2.adapters.MyViewPagerAdapter;
import com.example.dongtianming.tpp2.beans.ComingSoonMovieBean;
import com.example.dongtianming.tpp2.beans.HotMoiveBean;
import com.example.dongtianming.tpp2.beans.ViewPagerAltBean;
import com.example.dongtianming.tpp2.utils.ComingSoonMovieParse;
import com.example.dongtianming.tpp2.utils.HotMovieParse;
import com.example.dongtianming.tpp2.utils.HttpGetJsonData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    //////////////////////////////////////////////////////////ViewPager的数据
    private List<ViewPagerAltBean> viewPagerAltBeanList;
    private ViewPager activity_main_viewpager;
    private LinearLayout activity_main_llpoints;
    private List<ImageView> imageViewList;;
    private int viewPagerLastIndex;
    private boolean isRunning=false;
    ////////////////////////////////////////////////////////////ComingSoonRecycler的数据
    private RecyclerView activity_main_recycylerview;
    private ComingSoonMovieBean comingSoonMovieBean;
    private Handler recyclerViewHandler;
    private TextView activity_main_totaltextvie;
    //////////////////////////////////////////////////////////////hotRecyler数据
    private RecyclerView activity_main_recycylerview_hot;
    private Handler hotRecyclerViewHandler;
    private HotMoiveBean hotMoiveBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPagerView();
        initViewPagerData();
        initViewPagerAdapter();
        initComingSoonRecyclerView();
        initComingSoonRecyclerData();
        initHotRecyclerView();
        initHotRecyclerData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭子线程
        isRunning=false;
    }
////////////////////////////////////////////////////////////
    private void initViewPagerAdapter(){
        activity_main_viewpager.setAdapter(new MyViewPagerAdapter(imageViewList));
        int pos=Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2%imageViewList.size());
        activity_main_viewpager.setCurrentItem(pos);
        new Thread(){
            @Override
            public void run() {
                isRunning=true;
                while (isRunning){
                    try {
                        Thread.sleep(4000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity_main_viewpager.setCurrentItem(activity_main_viewpager.getCurrentItem()+1);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    private void initViewPagerView(){
        activity_main_viewpager= (ViewPager) findViewById(R.id.activity_main_viewpager);
        activity_main_llpoints= (LinearLayout) findViewById(R.id.activity_main_llpoints);
        activity_main_viewpager.setOnPageChangeListener(this);
    }
    private void initViewPagerData(){
        viewPagerAltBeanList = new ArrayList<>();
        imageViewList = new ArrayList<>();
        ViewPagerAltBean viewPagerAltBean=null;
        ImageView imageView=null;
        View point=null;
        LinearLayout.LayoutParams params=null;
        int []imageResId = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h};
        for (int i=0;i<8;i++){
            viewPagerAltBean=new ViewPagerAltBean();
            viewPagerAltBean.setImageId(imageResId[i]);
            viewPagerAltBeanList.add(viewPagerAltBean);
        }
        for (int j=0;j<viewPagerAltBeanList.size();j++){
            imageView=new ImageView(this);
            imageView.setBackgroundResource(viewPagerAltBeanList.get(j).getImageId());
            imageViewList.add(imageView);
            point=new View(this);
            point.setBackgroundResource(R.drawable.selector_bg_point);
            params = new LinearLayout.LayoutParams(15,15);
            params.setMargins(5,5,5,5);
            point.setEnabled(false);
            activity_main_llpoints.addView(point,params);
        }
    }
    //////////////////////////////////////////////////////////////////////////////
    public void initComingSoonRecyclerView(){
        activity_main_recycylerview= (RecyclerView) findViewById(R.id.activity_main_recycylerview);
        activity_main_totaltextvie= (TextView) findViewById(R.id.activity_main_totaltextview);
    }
    public void initComingSoonRecyclerData(){
        recyclerViewHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                StringBuilder stringBuilder=(StringBuilder) msg.obj;
                if (stringBuilder==null){
                    initComingSoonRecyclerAdapter();
                }
                else {
                    comingSoonMovieBean = ComingSoonMovieParse.getComingSoonMovieBean(stringBuilder);
                    activity_main_totaltextvie.setText("全部"+comingSoonMovieBean.getCount()+"部");
                    initComingSoonRecyclerAdapter();
                }
            }
        };
            HttpGetJsonData.getStringBuilder(recyclerViewHandler, "http://api.douban.com/v2/movie/in_theaters?");
    }
    public void initComingSoonRecyclerAdapter(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        activity_main_recycylerview.setLayoutManager(linearLayoutManager);
        ComingSoonRecyclerviewAdapter comingSoonRecyclerviewAdapter=null;
        if (comingSoonMovieBean==null){
            comingSoonRecyclerviewAdapter=new ComingSoonRecyclerviewAdapter(new ComingSoonMovieBean(),this);
        }
        else {
            comingSoonRecyclerviewAdapter = new ComingSoonRecyclerviewAdapter(comingSoonMovieBean, this);
        }
        activity_main_recycylerview.setAdapter(comingSoonRecyclerviewAdapter);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void initHotRecyclerView(){
        activity_main_recycylerview_hot= (RecyclerView) findViewById(R.id.activity_main_hotrecycylerview);
    }
    public void initHotRecyclerData(){
        hotRecyclerViewHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                StringBuilder stringBuilder= (StringBuilder) msg.obj;
                if (stringBuilder!=null) {
                     hotMoiveBean = HotMovieParse.getHotMoiveBean(stringBuilder);
                    initHotRecyclerAdapter();
                }
                else {
                    initHotRecyclerAdapter();
                }
            }
        };
        HttpGetJsonData.getStringBuilder(hotRecyclerViewHandler,"http://api.douban.com/v2/movie/coming_soon?");
    }
    public void initHotRecyclerAdapter(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        activity_main_recycylerview_hot.setLayoutManager(linearLayoutManager);
        HotRecyclerviewAdapter hotRecyclerviewAdapter=null;
        if (hotMoiveBean==null){
            hotRecyclerviewAdapter=new HotRecyclerviewAdapter(this,new HotMoiveBean());
        }
        else {
        hotRecyclerviewAdapter = new HotRecyclerviewAdapter(this,hotMoiveBean);
        }
        activity_main_recycylerview_hot.setAdapter(hotRecyclerviewAdapter);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//写ViewPager的监听事件
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        activity_main_llpoints.getChildAt(position%imageViewList.size()).setEnabled(true);
        activity_main_llpoints.getChildAt(viewPagerLastIndex).setEnabled(false);
        viewPagerLastIndex = position%imageViewList.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
