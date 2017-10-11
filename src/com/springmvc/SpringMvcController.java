package com.springmvc;

import com.application.ValueCenter;
import com.bean.DeviceBean;
import com.bean.ValueInfoBean;
import com.mysql.SqlCmd;
import com.pack.SendCmd;
import com.util.ErrorCode;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("ajax")
public class SpringMvcController {
    SendCmd send = SendCmd.getIntance();
    SqlCmd sql = SqlCmd.getInstance();
    ValueCenter valueCenter = ValueCenter.getInstance();
    private Logger log = Logger.getLogger(getClass());


    @ResponseBody
    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    public DeferredResult<Object> login(@RequestBody(required = true) Map<String, Object> map, HttpSession session) {

        String key = "login" + "#" + session.getId();
        DeferredResult<Object> result = new DeferredResult<>();
        valueCenter.getSession_deferredResult_map().put(key, result);
        sql.user_login((String) map.get("username"), (String) map.get("password"), session.getId());
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/getDeviceList", method = RequestMethod.POST)
    public DeferredResult<Object> getDeviceList(HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "dlist" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            sql.getdevicelist(session.getId());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/regDevice", method = RequestMethod.POST)
    public DeferredResult<Object> regDevice(@RequestBody(required = true) Map<String, Object> map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String uid = (String) map.get("uid");

//            Map<String, DeviceBean> device_map = valueCenter.getSession_device().get(session.getId());
//            if (device_map == null) {
//                DeviceBean bean = device_map.get(uid);
//                if (bean!= null)
//                    bean.setReg(true);
//            }

            WebSocketSession webSocketSession = valueCenter.getSs_map().get(session.getId());
            if (webSocketSession != null) {
                List<String> list = valueCenter.getWebsoket_map().get(webSocketSession);
                if (list != null) {
                    if (!list.contains(uid)) {
                        list.add(uid);
                    }
                    Map<String, Object> res_map = new HashMap<>();
                    res_map.put("result", ErrorCode.SUCCESS);
                    result.setResult(res_map);
                } else {
                    Map<String, Object> res_map = new HashMap<>();
                    res_map.put("result", ErrorCode.WEBSOCKET_UNCONN);
                    result.setResult(res_map);
                }
            } else {
                Map<String, Object> res_map = new HashMap<>();
                res_map.put("result", ErrorCode.WEBSOCKET_UNCONN);
                result.setResult(res_map);
            }
        }

        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/unregDevice", method = RequestMethod.POST)
    public DeferredResult<Object> unregDevice(@RequestBody(required = true) Map<String, Object> map, HttpSession session) {

        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String uid = (String) map.get("uid");

//            Map<String, DeviceBean> device_map = valueCenter.getSession_device().get(session.getId());
//            if (device_map == null) {
//                DeviceBean bean = device_map.get(uid);
//                if (bean!= null)
//                    bean.setReg(false);
//            }

            WebSocketSession webSocketSession = valueCenter.getSs_map().get(session.getId());
            if (webSocketSession != null) {
                List<String> list = valueCenter.getWebsoket_map().get(webSocketSession);
                if (list != null) {
                    list.remove(uid);
                    Map<String, Object> res_map = new HashMap<>();
                    res_map.put("result", ErrorCode.SUCCESS);
                    result.setResult(res_map);
                } else {
                    Map<String, Object> res_map = new HashMap<>();
                    res_map.put("result", ErrorCode.WEBSOCKET_UNCONN);
                    result.setResult(res_map);
                }
            } else {
                Map<String, Object> res_map = new HashMap<>();
                res_map.put("result", ErrorCode.WEBSOCKET_UNCONN);
                result.setResult(res_map);
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/readConfigFile", method = RequestMethod.POST)
    public DeferredResult<Object> readConfigFile(@RequestBody(required = true) Map<String, Object> req_map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "config" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            List<ValueInfoBean> list  = sql.updateConfigInfo(String.valueOf(req_map.get("filename")), 0);

            DeferredResult<List<ValueInfoBean>> res = (DeferredResult<List<ValueInfoBean>>) valueCenter.getSession_deferredResult_map().get("config" + "#" + session.getId());
            result.setResult(list);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/control", method = RequestMethod.POST)
    public DeferredResult<Object> control(@RequestBody(required = true) Map<String, Object> req_map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "control" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            String d = String.valueOf(req_map.get("value"));
            String uid  = String.valueOf(req_map.get("uid"));
            String name = String.valueOf(req_map.get("name"));
            List<ValueInfoBean> list  = sql.getconfigfile(uid,1);

            for(ValueInfoBean bean:list){
                if (bean.getName().equals(name)){
                    send.control(uid, "02883203826", bean.getDataType(),bean.getDecimals(), bean.getWriteadd(), Float.valueOf(d), session.getId());
                    break;
                }
            }
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/readModData", method = RequestMethod.POST)
    public DeferredResult<Object> readModData(@RequestBody(required = true) Map<String, Object> req_map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "mdata" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            send.readModData((String) req_map.get("uid"), (long) req_map.get("startTime"), (long) req_map.get("endTime"), key);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/ModDataPrev", method = RequestMethod.POST)
    public DeferredResult<Object> ModDataPrev(HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "mdata" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            send.readModDataPrev(key);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/ModDataNext", method = RequestMethod.POST)
    public DeferredResult<Object> ModDataNext(HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "mdata" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            send.readModDataNext(key);
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/readControlData", method = RequestMethod.POST)
    public DeferredResult<Object> readControlData(@RequestBody(required = true) Map<String, Object> req_map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "cdata" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            send.readControlData((String) req_map.get("uid"), (long) req_map.get("startTime"), (long) req_map.get("endTime"), session.getId());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/readAlarmData", method = RequestMethod.POST)
    public DeferredResult<Object> readAlarmData(@RequestBody(required = true) Map<String, Object> req_map, HttpSession session) {
        DeferredResult<Object> result = new DeferredResult<>();
        if (isLogin(result, session)) {
            String key = "adata" + "#" + session.getId();
            valueCenter.getSession_deferredResult_map().put(key, result);
            send.readAlarmData((String) req_map.get("uid"), (long) req_map.get("startTime"), (long) req_map.get("endTime"), session.getId());
        }
        return result;
    }

    private Boolean isLogin(DeferredResult<Object> result, HttpSession session) {
        if (valueCenter.getSession_userinfo().get(session.getId()) == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("result", ErrorCode.UNLOGIN);
            result.setResult(map);
            return false;
        } else {
            valueCenter.getSession_timeout().put(session.getId(),System.currentTimeMillis());
            return true;
        }
    }
}