package com.example.dongtianming.tpp2.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dongtianming.tpp2.R;
import com.example.dongtianming.tpp2.beans.ComingSoonMovieBean;
import com.example.dongtianming.tpp2.utils.GetImageFromInternet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dongtianming on 2018/7/29.
 */

public class ComingSoonRecyclerviewAdapter extends RecyclerView.Adapter <ComingSoonRecyclerviewAdapter.ViewHolder>{
    private Activity activity;
    private  ComingSoonMovieBean comingsoonmoivebean;
    public ComingSoonRecyclerviewAdapter(ComingSoonMovieBean comingsoonmoivebean,Activity activity) {
        this.comingsoonmoivebean=comingsoonmoivebean;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,null);
        RecyclerView.ViewHolder viewHolder=new ViewHolder(view);
        return (ViewHolder) viewHolder;
    }
 //向对应的控件中设置内容
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //设置缓存数据
        if (comingsoonmoivebean.getSubjects()==null&&comingsoonmoivebean.getCount()==0) {

        }
        else {
            holder.movie_item_gradetextview.setText(String.valueOf((double) comingsoonmoivebean.getSubjects().get(position).getRating().getAverage()));
            holder.movie_item_nametextview.setText(comingsoonmoivebean.getSubjects().get(position).getTitle());
            final String path=comingsoonmoivebean.getSubjects().get(position).getImages().getSmall();
            final String[] paths=path.split("/");
            File file=new File(activity.getCacheDir().getAbsolutePath().toString(),paths[paths.length-1]);
            if (file.exists()){
                Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                holder.movie_item_imageview.setImageBitmap(bitmap);
            }
            else {
                GetImageFromInternet.setImageView(comingsoonmoivebean.getSubjects().get(position).getImages().getSmall(), holder.movie_item_imageview, activity);
                new Thread(){
                    @Override
                    public void run() {
                        URL url= null;
                        try {
                            url = new URL(comingsoonmoivebean.getSubjects().get(position).getImages().getSmall());
                            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("GET");
                            httpURLConnection.setConnectTimeout(5000);
                            int code=httpURLConnection.getResponseCode();
                            if (code==200){
                                File file1=new File(activity.getCacheDir().getAbsolutePath().toString(),paths[paths.length-1]);
                                FileOutputStream fileOutputStream=new FileOutputStream(file1);
                                InputStream inputStream=httpURLConnection.getInputStream();
                                byte []buffer=new byte[1024];
                                int len=-1;
                                while ((len=inputStream.read(buffer))!=-1){
                                    fileOutputStream.write(buffer,0,len);
                                }
                                fileOutputStream.close();
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }.start();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (comingsoonmoivebean.getCount()==0){
            return 20;
        }
        return comingsoonmoivebean.getCount();
    }
//找到item对应的子控件
    public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView movie_item_imageview;
    TextView movie_item_gradetextview;
    TextView movie_item_nametextview;
        public ViewHolder(View itemView) {
            super(itemView);
            movie_item_imageview= (ImageView) itemView.findViewById(R.id.movie_item_imageview);
            movie_item_gradetextview = (TextView) itemView.findViewById(R.id.movie_item_gradetextview);
            movie_item_nametextview = (TextView)itemView.findViewById(R.id.movie_item_nametextview);
        }
    }
}
