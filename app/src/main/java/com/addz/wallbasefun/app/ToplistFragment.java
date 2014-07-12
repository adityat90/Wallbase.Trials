package com.addz.wallbasefun.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunilnt on 31/05/14.
 */
public class ToplistFragment extends Fragment {

    /**
     * Bundle argument: index name
     */
    static String FRAGMENT_INDEX = "FRAGMENT_INDEX";

    public static ToplistFragment newInstance(int index) {
        ToplistFragment f = new ToplistFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_INDEX, index);
        f.setArguments(args);
        return f;
    }

    /**
     * Wallpaper list stored here
     */
    private List<Wallpaper> wallpaperList;

    /**
     * Main image imageview
     */
    private ImageView mainImageView;

    /**
     * fullscreen state maintenance
     */
    private boolean isFullscreen = false;

    private GridView wallpaperGridView;
    private WallpaperListBaseAdapter wallpaperListBaseAdapter;

    int pageNumber = 0;
    int pageSize = 32;

    boolean isDownloading = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_toplist, container, false);

        isFullscreen = false;
        wallpaperList = new ArrayList<Wallpaper>();

        mainImageView = (ImageView) rootView.findViewById(R.id.mainImageView);
        mainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullscreen();
            }
        });

        wallpaperListBaseAdapter = new WallpaperListBaseAdapter(getActivity(), wallpaperList, mainImageView);

        wallpaperGridView = (GridView) rootView.findViewById(R.id.wallpaperGridView);
        wallpaperGridView.setAdapter(wallpaperListBaseAdapter);

        float scalefactor = getResources().getDisplayMetrics().density * 100;
        int number = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int columns = (int) ((float) number / (float) scalefactor);
        wallpaperGridView.setNumColumns(columns);

        new DownloadWallpaperList(getActivity()).execute("http://wallbase.cc/toplist/index/" + pageNumber * pageSize + "?section=wallpapers&q=&res_opt=eqeq&res=0x0&thpp=" + pageSize + "&purity=100&board=21&aspect=0.00&ts=3d");

        wallpaperGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mainImageView.setAdjustViewBounds(false);
                new WallpaperDownload().execute(wallpaperList.get(position).URL);
            }
        });

        wallpaperGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//                try {
//                    if (0 == firstVisibleItem || firstVisibleItem == 1 || firstVisibleItem == 2) {
//                        view.getChildAt(firstVisibleItem).setPadding(0, mainImageView.getHeight(), 0, 0);
//                    } else {
//                        view.getChildAt(firstVisibleItem).setPadding(0, 0, 0, 0);
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }

                if(firstVisibleItem + visibleItemCount >= totalItemCount){
                    if(!isDownloading)
                    {
                        mainImageView.setAdjustViewBounds(false);
                        Log.e("Will donwload new", "Will download new");
                        new DownloadWallpaperList(getActivity()).execute("http://wallbase.cc/toplist/index/" + pageNumber * pageSize + "?section=wallpapers&q=&res_opt=eqeq&res=0x0&thpp=" + pageSize + "&purity=100&board=21&aspect=0.00&ts=3d");
                    }
                }
            }
        });

        return rootView;
    }

    private void toggleFullscreen()
    {
        if(isFullscreen)
        {
            isFullscreen = !isFullscreen;
            View decorView = getActivity().getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.hide();
        }
        else
        {
            isFullscreen = !isFullscreen;
            View decorView = getActivity().getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)activity).onSectionAttached(getArguments().getInt(FRAGMENT_INDEX));
    }


    /**
     * Wallpaper Page List Downloader AsyncTask
     */
    public class DownloadWallpaperList extends AsyncTask<String, Void, Document> {

        private Activity activity;

        DownloadWallpaperList(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(Document document) {
            for (Element element : document.select("#thumbs .thumbnail")) {
                wallpaperList.add(new Wallpaper(element.select(".wrapper a:last-child").get(element.select(".wrapper a:last-child").size() - 1).select("img").attr("data-original"), element.select(".wrapper a:last-child").get(element.select(".wrapper a:last-child").size() - 1).attr("href")));
            }

            pageNumber += 1;

            wallpaperListBaseAdapter.notifyDataSetChanged();

            if(wallpaperList.size() > 0 && pageNumber == 1) {
                new WallpaperDownload().execute(wallpaperList.get(0).URL);
            }
            mainImageView.setAdjustViewBounds(false);
            isDownloading = false;
        }

        @Override
        protected Document doInBackground(String... urls) {
            isDownloading = true;
            Document document = new Document("");
            try {
                document = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }
    }

    /**
     * Wallpaper Download
     */
    public class WallpaperDownload extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPostExecute(String path) {
            mainImageView.setAdjustViewBounds(true);
            Picasso.with(getActivity()).load(path).into(mainImageView);
            Log.e("Loaded image", "Reached Here main imageview");
        }

        @Override
        protected String doInBackground(String... urls) {
            Document document = new Document("");
            try {
                document = Jsoup.connect(urls[0]).get();
                return document.select(".content img").attr("src").toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }
}