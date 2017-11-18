package com.springmvc;

import com.application.ValueCenter;
import com.bean.Token;
import com.util.ErrorCode;
import com.util.Helper;
import org.springframework.web.context.request.async.DeferredResult;

public class SpringMvcTimeOut implements Runnable {

    private DeferredResult<Object> deferredResult;
    private String uuid;
    private String key;
    private Token token;

    public SpringMvcTimeOut(Token token, String key, DeferredResult<Object> deferredResult) {
        this.deferredResult = deferredResult;
        this.uuid = token.getUuid();
        this.key = key;
        this.token  = token;
    }

    @Override
    public void run() {
        Helper.getInstance().DefReturn(deferredResult, ErrorCode.TIME_OUT, ErrorCode.TIME_OUT_STR,token ,null);
        if (key != null)
            ValueCenter.getInstance().getUuid_deferredResult().remove(key + "#" + uuid);
    }
}