package com.springmvc;


import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
public class AsyncService {

    DeferredResult<Map<String,Object>> deferredResult;

    public DeferredResult asyncDef(){
        deferredResult  = new DeferredResult<>();
        new Thread(new async_runable()).start();
        return deferredResult;
    }

    public class async_runable implements  Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("result", 1);
                deferredResult.setResult(map);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}