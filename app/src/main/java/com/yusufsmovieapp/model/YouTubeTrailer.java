package com.yusufsmovieapp.model;

import java.io.Serializable;

public class YouTubeTrailer implements Serializable {

    private String name;
    private String source;

    public YouTubeTrailer(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }
}
