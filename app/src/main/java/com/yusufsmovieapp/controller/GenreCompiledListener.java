package com.yusufsmovieapp.controller;

import com.yusufsmovieapp.model.Genre;

import java.util.List;

public interface GenreCompiledListener {

    void onCompiled(List<Genre> genres);

    void onError(String errorMessage);

}
