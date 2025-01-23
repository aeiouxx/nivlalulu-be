package com.nivlalulu.nnpro.common.context;

public class AccessTokenContextHolder {
    private static final ThreadLocal<String> ACCESS_TOKEN = new ThreadLocal<>();

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN.set(accessToken);
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN.get();
    }

    public static void clear() {
        ACCESS_TOKEN.remove();
    }
}
