package com.veyndan.redditclient.api.model;

import java.util.ArrayList;
import java.util.List;

public class CaptchaJson {
    public List<Object> errors = new ArrayList<>();
    public CaptchaData data;
}
