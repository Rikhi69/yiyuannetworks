package com.yiyuan.ai.retrofit.api;



import com.yiyuan.ai.retrofit.response.VersionResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by wangyu on 2019/4/24.
 */

public interface VersionApi {
    @GET("/yy-face/version")
    Observable<VersionResponse> getVersion();
}
