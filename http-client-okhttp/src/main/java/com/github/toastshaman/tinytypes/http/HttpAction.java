package com.github.toastshaman.tinytypes.http;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public interface HttpAction<R> {

    Request toRequest(HttpUrl.Builder builder);

    R fromResponse(Response response) throws Exception;
}
