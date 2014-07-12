package com.addz.wallbasefun.app;

/**
 * Created by sunilnt on 31/05/14.
 */
public class Wallpaper {

    public String thumbURL;
    public String URL;

    public Wallpaper(String thumbURL, String URL) {
        this.thumbURL = thumbURL;
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Wallpaper URL: " + URL + ", Thumbnail URL: " + thumbURL;
    }
}
