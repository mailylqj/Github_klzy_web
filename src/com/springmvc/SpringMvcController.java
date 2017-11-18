package com.springmvc;

import com.application.ValueCenter;
import com.bean.SelectInfoBean;
import com.bean.Token;
import com.bean.UserBean;
import com.bean.ValueInfoBean;
import com.mysql.SqlCmd;
import com.pack.SendCmd;
import com.util.ErrorCode;
import com.util.Helper;
import com.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.DefaultReadFuture;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("ajax")
public class SpringMvcController {
    SendCmd send = SendCmd.getIntance();
    SqlCmd sql = SqlCmd.getInstance();
    ValueCenter valueCenter = ValueCenter.getInstance();
    private Logger log = Logger.getLogger(getClass());


    /**
     * @param result
     * @return
     */
    private Token checkToken(DeferredResult<Object> result, String token_str, String key) {
        Boolean isrun = valueCenter.getToken_map().get(token_str);
        if (isrun != null) {
            Claims claims = JwtUtil.parseJWT(token_str);
            String uuid = claims.getId();
            String time = claims.getAudience();

            if (uuid != null && time != null) {
                Token token = new Token(token_str, uuid);
                long token_time = Long.valueOf(time);
                //超时
                if ((System.currentTimeMillis() - token_time) > 1000 * 60 * 30000) {
                    Helper.getInstance().DefReturn(result, ErrorCode.LOGIN_EXPIRED, ErrorCode.LOGIN_EXPIRED_STR, token, null);
                } else {
                    if (isrun) {
                        Helper.getInstance().DefReturn(result, ErrorCode.PROCESSING, ErrorCode.PROCESSING_STR, token, null);
                    } else {
                        result.onTimeout(new SpringMvcTimeOut(token, key, result));
                        valueCenter.getToken_map().put(token_str, Boolean.TRUE);
                        valueCenter.getTime_map().put(uuid, System.currentTimeMillis());
                        return token;
                    }
                }
            } else {
                Helper.getInstance().DefReturn(result, ErrorCode.UNLOGIN, ErrorCode.UNLOGIN_STR, null, null);
            }
        } else {
            Helper.getInstance().DefReturn(result, ErrorCode.UNLOGIN, ErrorCode.UNLOGIN_STR, null, null);
        }
        return null;
    }

    private UserBean getUserInfo(DeferredResult deferredResult, Token token) {
        UserBean userbean = valueCenter.getUuid_userinfo().get(token.getUuid());
        if (userbean == null) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.UNLOGIN, ErrorCode.UNLOGIN_STR, null, null);
        }
        return userbean;
    }


    @ResponseBody
    @RequestMapping(value = "/Login", method = RequestMethod.POST)
    public DeferredResult<Object> login(@RequestBody(required = true) Map<String, Object> map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sql.user_login(map, result);
            }
        }).start();
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getDeviceList", method = RequestMethod.POST)
    public DeferredResult<Object> getDeviceList(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sql.getdevicelist(token, result);
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/readConfigFile", method = RequestMethod.POST)
    public DeferredResult<Object> readConfigFile(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String name = (String) req_map.get("filename");
                    if (name != null) {
                        sql.updateConfigInfo(token, name, 0, result);
                    } else {
                        Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/control", method = RequestMethod.POST)
    public DeferredResult<Object> control(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), "control");
        if (token != null) {
            String key = "control" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            String d = String.valueOf(req_map.get("value"));
            String uid = String.valueOf(req_map.get("uid"));
            String name = String.valueOf(req_map.get("name"));
            List<ValueInfoBean> list = sql.getconfigfile(uid, 1);

            if (d != null && uid != null && name != null && list != null) {
                for (ValueInfoBean bean : list) {
                    if (bean.getName().equals(name)) {
                        send.control(uid, "02883203826", bean.getDataType(), bean.getDecimals(), bean.getWriteadd(), Float.valueOf(d), token);
                        break;
                    }
                }
            } else {
                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
            }
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/readModData", method = RequestMethod.POST)
    public DeferredResult<Object> readModData(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), "mdata");
        if (token != null) {
            String key = "mdata" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            String uid = (String) req_map.get("uid");
            if (uid != null) {
                long startTime = (long) req_map.get("startTime");
                long endTime = (long) req_map.get("endTime");
                List<Long> list = new ArrayList<>();
                list.add(startTime);
                valueCenter.getUuid_selecttime().put(token.getUuid(), new SelectInfoBean(uid, startTime, endTime, 0, list));
                send.readModData(uid, startTime, endTime, token);
            } else
                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/ModDataPrev", method = RequestMethod.POST)
    public DeferredResult<Object> ModDataPrev(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), "mdata");
        if (token != null) {
            String key = "mdata" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            UserBean userbean = getUserInfo(result, token);
            if (userbean != null) {
                SelectInfoBean bean = valueCenter.getUuid_selecttime().get(token.getUuid());
                if (bean != null) {
                    String uid = bean.getUid();
                    List<Long> list = bean.getTime_list();
                    int size = list.size();
                    if (size >= 3) {
                        long startTime = list.get(size - 3);
                        if (startTime != bean.getStarttime())
                            startTime += 1;
                        long endTime = list.get(size - 2) + 1;
                        send.readModData(uid, startTime, endTime, token);
                    } else {
                        Helper.getInstance().DefReturn(result, ErrorCode.SELECTEND, ErrorCode.SELECTEND_STR, token, null);
                    }
                } else {
                    Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                }
            }
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/ModDataNext", method = RequestMethod.POST)
    public DeferredResult<Object> ModDataNext(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), "mdata");
        if (token != null) {
            String key = "mdata" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            UserBean userbean = getUserInfo(result, token);
            if (userbean != null) {
                SelectInfoBean bean = valueCenter.getUuid_selecttime().get(token.getUuid());
                if (bean != null) {
                    if (bean != null) {
                        String uid = bean.getUid();
                        List<Long> list = bean.getTime_list();
                        int size = list.size();
                        long startTime = list.get(size - 1) + 1;
                        long endTime = bean.getEndtime();
                        if (startTime != endTime) {
                            send.readModData(uid, startTime, endTime, token);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.SELECTEND, ErrorCode.SELECTEND_STR, token, null);
                        }
                    } else {
                        Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/readControlData", method = RequestMethod.POST)
    public DeferredResult<Object> readControlData(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), "cdata");
        if (token != null) {
            String key = "cdata" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            send.readControlData((String) req_map.get("uid"), (long) req_map.get("startTime"), (long) req_map.get("endTime"), token);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/readAlarmData", method = RequestMethod.POST)
    public DeferredResult<Object> readAlarmData(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), "adata");
        if (token != null) {
            String key = "adata" + "#" + token.getUuid();
            valueCenter.getUuid_deferredResult().put(key, result);
            send.readAlarmData((String) req_map.get("uid"), (long) req_map.get("startTime"), (long) req_map.get("endTime"), token);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userAdd", method = RequestMethod.POST)
    public DeferredResult<Object> userAdd(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        String name = (String) req_map.get("name");
                        String passowrd = (String) req_map.get("password");
                        int type = (int) req_map.get("type");
                        int level = (int) req_map.get("level");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        if (userlevel >= level && userlevel >= 4) {
                            if (name != null && passowrd != null) {
                                sql.User_Add(token, name, passowrd, type, pro_company_id, use_company_id, level, result);
                            } else {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                        }
                    } else {
                        Helper.getInstance().DefReturn(result, ErrorCode.UNLOGIN, ErrorCode.UNLOGIN_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userDel", method = RequestMethod.POST)
    public DeferredResult<Object> userDel(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        List<Integer> id_list = new ArrayList<>();
                        id_list = (ArrayList<Integer>) req_map.get("namelist");
                        if (id_list.size() > 0) {
                            id_list = (ArrayList) req_map.get("namelist");
                            sql.User_Del(token, id_list, userbean, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userCpwd", method = RequestMethod.POST)
    public DeferredResult<Object> userChangepwd(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String opwd = (String) req_map.get("original_pwd");
                        String npwd = (String) req_map.get("new_pwd");

                        if (opwd != null && npwd != null) {
                            sql.User_Cpwd(token, userbean, opwd, npwd, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/userSelectByName", method = RequestMethod.POST)
    public DeferredResult<Object> userSelectByName(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String username = (String) req_map.get("name");
                        if (username != null) {
                            sql.User_SelectByName(token, username, userbean, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userSelectByID", method = RequestMethod.POST)
    public DeferredResult<Object> userSelectByID(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int id = (int) (req_map.get("id"));
                        sql.User_SelectByID(token, id, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userSelectAll", method = RequestMethod.POST)
    public DeferredResult<Object> userSelectAll(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        sql.User_SelectAll(token, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> userUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        int user_id = (int) req_map.get("id");
                        String name = (String) req_map.get("name");
                        int type = (int) req_map.get("type");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        int level = (int) req_map.get("level");

                        if (userlevel >= level) {
                            sql.User_Update(token, user_id, name, type, pro_company_id, use_company_id, level, userbean, result);
                        } else
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceAdd", method = RequestMethod.POST)
    public DeferredResult<Object> deviceAdd(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        String device_id = (String) req_map.get("device_id");
                        int type = (int) req_map.get("type");
                        int cmd_len = (int) req_map.get("cmd_len");
                        int looptime = (int) req_map.get("looptime");
                        int use_company_id = (int) req_map.get("use_company_id");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        double longitude = (double) req_map.get("longitude");
                        double latitude = (double) req_map.get("latitude");
                        String devicename = (String) req_map.get("devicename");
                        String configfilename = (String) req_map.get("configfilename");
                        int ischeck = (int) req_map.get("ischeck");
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String creat_date = formatter.format(System.currentTimeMillis());
                        String allow_date = creat_date;
                        String mod_add = "1";
                        String data_add = "00 00";

                        String cmd_len_str = String.format("%04X", cmd_len);
                        cmd_len_str = cmd_len_str.substring(0, 2) + " " + cmd_len_str.substring(2);
                        String cmd = "01 03 00 00 " + cmd_len_str;

                        if (userlevel == 9) {
                            ischeck = 0;
                            long allow_day = (long) req_map.get("allow_date");
                            allow_date = formatter.format(allow_day);
                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        sql.Device_Add(token, device_id, type, cmd, looptime, pro_company_id, use_company_id, longitude, latitude, devicename, mod_add, data_add, configfilename, ischeck, creat_date, allow_date, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceDel", method = RequestMethod.POST)
    public DeferredResult<Object> deviceDel(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        List<Integer> list = (ArrayList) req_map.get("id_list");
                        if (list.size() > 0) {
                            sql.Device_Del(token, list, userbean, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> deviceUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        int id = (int) req_map.get("id");
                        String device_id = (String) req_map.get("device_id");
                        int type = (int) req_map.get("type");
                        int cmd_len = (int) req_map.get("cmd_len");
                        int looptime = (int) req_map.get("looptime");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        double longitude = (double) req_map.get("longitude");
                        double latitude = (double) req_map.get("latitude");
                        String devicename = (String) req_map.get("devicename");
                        String configfilename = (String) req_map.get("configfilename");

                        int ischeck = (int) req_map.get("ischeck");
                        String allow_date = "";

                        String cmd_len_str = String.format("%04X", cmd_len);
                        cmd_len_str = cmd_len_str.substring(0, 2) + " " + cmd_len_str.substring(2);
                        String cmd = "01 03 00 00 " + cmd_len_str;
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        if (userlevel == 9) {
                            long allow_day = (long) req_map.get("allow_date");
                            allow_date = formatter.format(allow_day);
                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        sql.Device_Update(token, id, device_id, type, cmd, looptime, pro_company_id, use_company_id, longitude, latitude, devicename, configfilename, ischeck, allow_date, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceSelectByID", method = RequestMethod.POST)
    public DeferredResult<Object> deviceSelectByID(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int id = (int) req_map.get("id");
                        sql.Device_SelectByID(token, id, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceSelectAll", method = RequestMethod.POST)
    public DeferredResult<Object> deviceSelectAll(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        sql.Device_SelectAll(token, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceSelect_UNAllow", method = RequestMethod.POST)
    public DeferredResult<Object> deviceSelect_UNAllow(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        if (userbean.getLevel() == 9)
                            sql.Device_Select_UNAllow(token, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result,ErrorCode.PERMISSION,ErrorCode.PERMISSION_STR,token,null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceSelectByDeviceID", method = RequestMethod.POST)
    public DeferredResult<Object> deviceSelectByDeviceID(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String device_id = (String) req_map.get("device_id");
                        sql.Device_SelectByDeviceID(token, device_id, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    /****************************************************** pro_company ********************************************************************/

    @ResponseBody
    @RequestMapping(value = "/proCompanyAdd", method = RequestMethod.POST)
    public DeferredResult<Object> proCompanyAdd(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        if (userbean.getLevel() == 9) {
                            String pro_company = (String) req_map.get("pro_company");
                            if (pro_company != null) {
                                sql.ProCompany_Add(token, pro_company, result);
                            } else {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/proCompanyUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> proCompanyUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        if (userbean.getLevel() == 9) {
                            String pro_company = (String) req_map.get("pro_company");
                            int id = (int) req_map.get("id");
                            sql.ProCompany_Update(token, id, pro_company, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/proCompanyDel", method = RequestMethod.POST)
    public DeferredResult<Object> proCompanyDel(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        if (userbean.getLevel() == 9) {
                            List<Integer> id_list = (ArrayList<Integer>) req_map.get("id_list");
                            if (id_list != null)
                                sql.ProCompany_Del(token, id_list, result);
                            else
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/proCompanySelectAll", method = RequestMethod.POST)
    public DeferredResult<Object> proCompanySelectAll(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        if (userbean.getLevel() == 9) {
                            sql.ProCompany_Select(token, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    /****************************************************** use_company ********************************************************************/

    @ResponseBody
    @RequestMapping(value = "/useCompanyAdd", method = RequestMethod.POST)
    public DeferredResult<Object> useCompanyAdd(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String use_company = (String) req_map.get("use_company");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        sql.UseCompany_Add(token, use_company, pro_company_id, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/useCompanyUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> useCompanyUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String use_company = (String) req_map.get("use_company");
                        int id = (int) req_map.get("id");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        sql.UseCompany_Update(token, id, use_company, pro_company_id, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/useCompanyDel", method = RequestMethod.POST)
    public DeferredResult<Object> useCompanyDel(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        List<Integer> id_list = (ArrayList<Integer>) req_map.get("id_list");
                        sql.UseCompany_Del(token, id_list, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/useCompanySelectByProCompanyID", method = RequestMethod.POST)
    public DeferredResult<Object> useCompanySelectByProCompanyID(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        sql.UseCompany_Select(token, pro_company_id, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/userDeviceSelect", method = RequestMethod.POST)
    public DeferredResult<Object> userDeviceSelect(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int user_id = (int) req_map.get("user_id");
                        int user_level = (int) req_map.get("level");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        if (user_level == 9) {
                        } else if (user_level == 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        sql.User_Device_Select(token, user_id, user_level, pro_company_id, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userDeviceUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> userDeviceUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int user_id = (int) req_map.get("user_id");
                        List<Integer> device_list = (ArrayList<Integer>) req_map.get("chose_list");
                        sql.User_Device_Update(token, user_id, device_list, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/deviceImeiSelect", method = RequestMethod.POST)
    public DeferredResult<Object> deviceImeiSelect(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int device_id = (int) req_map.get("device_id");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int user_level = userbean.getLevel();

                        if (user_level == 9) {
                        } else if (user_level == 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        sql.Device_Imei_Select(token, device_id, pro_company_id, user_level, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceImeiUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> deviceImeiUpdate
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int device_id = (int) req_map.get("device_id");
                        List<Integer> imei_list = (ArrayList<Integer>) req_map.get("imei_list");

                        if (imei_list != null) {
                            sql.Device_Imei_Update(token, device_id, userbean, imei_list, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/imeiAdd", method = RequestMethod.POST)
    public DeferredResult<Object> imeiAdd(@RequestBody(required = true) Map<String, Object> req_map, HttpSession
            session) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        String imei = (String) req_map.get("imei");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        String info = (String) req_map.get("info");
                        if (userlevel == 9) {
                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        sql.Imei_Add(token, imei, pro_company_id, use_company_id, info, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/imeiDel", method = RequestMethod.POST)
    public DeferredResult<Object> imeiDel(@RequestBody(required = true) Map<String, Object> req_map, HttpSession
            session) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        List<Integer> imei_list = (ArrayList<Integer>) req_map.get("imei_list");
                        if (imei_list != null) {
                            if (imei_list.size() > 0) {
                                sql.imei_Del(token, imei_list, userbean, result);
                            } else {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/imeiUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> imeiUpdate(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        int id = (int) req_map.get("id");
                        String imei = (String) req_map.get("imei");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        String info = (String) req_map.get("info");

                        if (userlevel == 9) {
                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }

                        if (imei != null && info != null) {
                            sql.imei_Update(token, id, imei, pro_company_id, use_company_id, info, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/imeiSelectByImei", method = RequestMethod.POST)
    public DeferredResult<Object> imeiSelectByImei
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String imei = (String) req_map.get("imei");
                        if (imei != null) {
                            sql.imei_SelectByImei(token, imei, userbean, result);
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                        }
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/imeiSelectByID", method = RequestMethod.POST)
    public DeferredResult<Object> imeiSelectByID
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int id = (int) req_map.get("id");
                        sql.imei_SelectByID(token, id, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/imeiSelectAll", method = RequestMethod.POST)
    public DeferredResult<Object> imeiSelectAll(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        sql.imei_SelectAll(token, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/configAdd", method = RequestMethod.POST)
    public DeferredResult<Object> configAdd
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();

                        String filename = (String) req_map.get("filename");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        int type = (int) req_map.get("type");
                        String data_name = (String) req_map.get("data_name");
                        String data_units = (String) req_map.get("data_units ");
                        int data_type = (int) req_map.get("data_type");
                        int data_decimal = (int) req_map.get("data_decimal");
                        int data_rwtype = (int) req_map.get("data_rwtype");
                        int data_level = (int) req_map.get("data_level");
                        int data_showtype = (int) req_map.get("data_showtype");
                        int data_max = (int) req_map.get("data_max");
                        int data_min = (int) req_map.get("data_min");

                        if (userlevel == 9) {
                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }
                        if (filename != null && data_name != null)
                            sql.config_Add(token, filename, pro_company_id, use_company_id, type, data_name, data_units, data_type, data_decimal, data_rwtype, data_level, data_showtype, data_max, data_min, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/configDel", method = RequestMethod.POST)
    public DeferredResult<Object> configDel
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        List<Integer> id_list = (ArrayList<Integer>) req_map.get("id_list");
                        if (id_list != null)
                            sql.config_Del(token, id_list, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/configUpdate", method = RequestMethod.POST)
    public DeferredResult<Object> configUpdate
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int userlevel = userbean.getLevel();
                        int id = (int) req_map.get("id");
                        String filename = (String) req_map.get("filename");
                        int pro_company_id = (int) req_map.get("pro_company_id");
                        int use_company_id = (int) req_map.get("use_company_id");
                        int type = (int) req_map.get("type");
                        String data_name = (String) req_map.get("data_name");
                        String data_units = (String) req_map.get("data_units ");
                        int data_type = (int) req_map.get("data_type");
                        int data_decimal = (int) req_map.get("data_decimal");
                        int data_rwtype = (int) req_map.get("data_rwtype");
                        int data_level = (int) req_map.get("data_level");
                        int data_showtype = (int) req_map.get("data_showtype");
                        int data_max = (int) req_map.get("data_max");
                        int data_min = (int) req_map.get("data_min");

                        if (userlevel == 9) {

                        } else if (userlevel >= 4) {
                            if (pro_company_id != userbean.getPro_company_id()) {
                                Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                                return;
                            }
                        } else {
                            Helper.getInstance().DefReturn(result, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
                            return;
                        }

                        if (filename != null && data_name != null)
                            sql.config_update(token, id, pro_company_id, use_company_id, filename, type, data_name, data_units, data_type, data_decimal,
                                    data_rwtype, data_level, data_showtype, data_max, data_min, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/configSelectByDataname", method = RequestMethod.POST)
    public DeferredResult<Object> configSelectByDataname
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String filename = (String) req_map.get("filename");
                        String data_name = (String) req_map.get("data_name");
                        if (filename != null && data_name != null)
                            sql.config_SelectByDataName(token, filename, data_name, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/configSelectByID", method = RequestMethod.POST)
    public DeferredResult<Object> configSelectByID
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        int id = (int) req_map.get("id");
                        sql.config_SelectByID(token, id, userbean, result);
                    }
                }
            }).start();
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/configSelectByFilename", method = RequestMethod.POST)
    public DeferredResult<Object> configSelectByFilename
            (@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);
        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        String filename = (String) req_map.get("filename");
                        if (filename != null)
                            sql.config_SelectByFilename(token, filename, userbean, result);
                        else
                            Helper.getInstance().DefReturn(result, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/configShowtypeSelect", method = RequestMethod.POST)
    public DeferredResult<Object> configShowtypeSelect(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserBean userbean = getUserInfo(result, token);
                    if (userbean != null) {
                        sql.config_Showtype_Select(token, result);
                    }
                }
            }).start();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getLevel", method = RequestMethod.POST)
    public DeferredResult<Object> getLevel(@RequestBody(required = true) Map<String, Object> req_map) {
        DeferredResult<Object> result = new DeferredResult<Object>((long) 20000);

        Token token = checkToken(result, (String) req_map.get("token"), null);
        if (token != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sql.sqlGetLevel(result,token);
                }
            }).start();
        }
        return result;
    }
}
