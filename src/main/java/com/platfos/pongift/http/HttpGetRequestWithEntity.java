package com.platfos.pongift.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.springframework.http.HttpMethod;

import java.net.URI;

public class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
    public HttpGetRequestWithEntity(final URI uri) {
        super.setURI(uri);
    }

    @Override
    public String getMethod() {
        return HttpMethod.GET.name();
    }
}