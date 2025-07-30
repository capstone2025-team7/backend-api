package com.capstone2025.team7.backend.utils;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriCreator {
    public static URI createUri(String defaultUrl, long resourceId) {
        return UriComponentsBuilder
                .newInstance()
                .path(defaultUrl + "/{resource-id}")
                .buildAndExpand(resourceId)
                .toUri();
    }

    public static URI createUri(String defaultUrl, String uuid) {
        return UriComponentsBuilder
                .newInstance()
                .path(defaultUrl + "/{uuid}")
                .buildAndExpand(uuid)
                .toUri();
    }
}
