package com.mysql;

import com.application.ValueCenter;
import com.bean.DeviceBean;
import com.bean.UserBean;
import com.bean.ValueInfoBean;
import com.util.ErrorCode;
import org.springframework.web.context.request.async.DeferredResult;

import java.sql.Date;
import java.util.*;


public class SqlCmd {

    private static SqlCmd instance = null;

    public static SqlCmd getInstance() {
        if (instance == null) {
            instance = new SqlCmd();
        }
        return instance;
    }

    ValueCenter valueCenter = ValueCenter.getInstance();


    /**
     * 登录
     *
     * @param username
     * @param password
     * @param sessionid
     */
    public synchronized void user_login(String username, String password, String sessionid) {
        String key = "login" + "#" + sessionid;
        String sql = "select * from tb_user where username=?";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(username);
        Map<String, String> sql_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);

        if (password.equals(sql_map.get("password"))) {

            UserBean user = valueCenter.getSession_userinfo().get(sessionid);
            if (user == null) {
                user = new UserBean();
                valueCenter.getSession_userinfo().put(sessionid, user);
            }
            user.setUsername(sql_map.get("username"));
            //user.setPassword(sql_map.get("password"));
            user.setPro_company(sql_map.get("pro_company"));
            user.setUse_company(sql_map.get("use_company"));
            user.setImei(sql_map.get("imei"));
            user.setType(Integer.valueOf(sql_map.get("type")));
            user.setLevel(Integer.valueOf(sql_map.get("level")));

            DeferredResult<UserBean> result = (DeferredResult<UserBean>) valueCenter.getSession_deferredResult_map().get(key);
            result.setResult(user);
        } else {
            DeferredResult<Map<String, Integer>> result = (DeferredResult<Map<String, Integer>>) valueCenter.getSession_deferredResult_map().get(key);
            Map<String, Integer> map = new LinkedHashMap<>();
            map.put("result", ErrorCode.PWD_ERROR);
            result.setResult(map);
        }
    }

    /**
     * 获取列表
     *
     * @param sessionid
     */
    public synchronized void getdevicelist(String sessionid) {
        UserBean user = valueCenter.getSession_userinfo().get(sessionid);
        if (user == null) {
            Map<String, Integer> map = new HashMap<>();
            map.put("result", ErrorCode.UNLOGIN);
            ((DeferredResult<Map<String, Integer>>) valueCenter.getSession_deferredResult_map().get("dlist" + "#" + sessionid)).setResult(map);
        } else {
            String username = user.getUsername();
            String sql = "select * from tb_device where device_id = any(select device_id from tb_user_device where username=?)";
            ArrayList<Object> param = new ArrayList<Object>();
            param.add(username);
            List<Map<String, String>> sql_map = JDBC_Pool.getInstance().queryForMap(sql, param);

            List<String> online_device = valueCenter.getAll_OnlineDevice();
            List<DeviceBean> return_list = new ArrayList<>();
            List<String> reg_list = new ArrayList<>();
            if (valueCenter.getSs_map().get(sessionid) != null) {
                reg_list = valueCenter.getWebsoket_map().get(valueCenter.getSs_map().get(sessionid));
            }

            for (Map<String, String> map : sql_map) {
                String uid = map.get("device_id");
                DeviceBean bean = new DeviceBean();
                bean.setDeviceNO(uid);
                bean.setDeviceName(map.get("device_name"));
                bean.setDeviceType(Integer.valueOf(map.get("type")));
                if (online_device.contains(uid))
                    bean.setOnLine(true);
                else
                    bean.setOnLine(false);
                if (reg_list.contains(uid))
                    bean.setReg(true);
                else
                    bean.setReg(false);
                bean.setSave(false);
                bean.setLongitude(Double.valueOf(map.get("longitude")));
                bean.setLatitude(Double.valueOf(map.get("latitude")));
                bean.setFileName(map.get("configfile_name"));
                return_list.add(bean);
            }
            DeferredResult<List<DeviceBean>> result = (DeferredResult<List<DeviceBean>>) valueCenter.getSession_deferredResult_map().get("dlist" + "#" + sessionid);
            result.setResult(return_list);
        }
    }


    /**
     * 获取配置文件
     *
     * @param name
     * @param
     */
    public synchronized List<ValueInfoBean> getconfigfile(String name, int type) {
        if (type == 0) {
            List<ValueInfoBean> list = valueCenter.getConfig_map().get(name);
            if (list != null) {
                return list;
            } else {
                List<Map<String, String>> res_list = sqlConfigFileByName(name);
                list = build_ValueInfoBean(res_list);
                valueCenter.getConfig_map().put(name, list);
                return list;
            }
        } else if (type == 1) {
            String filename = sqlGetFileNameByUid(name);
            if (filename == null)
                return null;
            List<ValueInfoBean> list = valueCenter.getConfig_map().get(filename);
            if (list != null) {
                return list;
            } else {
                List<Map<String, String>> res_list = sqlConfigFileByName(filename);
                list = build_ValueInfoBean(res_list);
                valueCenter.getConfig_map().put(name, list);
                return list;
            }
        }
        return null;
    }

// 获取对应名
//    private Map<Integer, String> getShowTypeMap() {
//        Map<Integer, String> showtype_map = valueCenter.getShowtype_map();
//        if (showtype_map == null || showtype_map.size() == 0) {
//            showtype_map = sqlShowTypeMap();
//            valueCenter.setShowtype_map(showtype_map);
//        }
//        return showtype_map;
//    }

    //数据查询
    private Map<Integer, String> sqlShowTypeMap() {
        Map<Integer, String> showtype_map = new HashMap<>();
        String sql = "select * from tb_showtype";
        ArrayList<Object> param = new ArrayList<>();
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        for (Map<String, String> map : list) {
            showtype_map.put(Integer.valueOf(map.get("id")), map.get("name"));
        }
        return showtype_map;
    }

    /**
     * 强制更新 配置文件数据
     *
     * @param name
     * @param type
     * @return
     */
    public synchronized List<ValueInfoBean> updateConfigInfo(String name, int type) {

        if (type == 0) {
            List<Map<String, String>> res_list = sqlConfigFileByName(name);
            List<ValueInfoBean> list = build_ValueInfoBean(res_list);
            valueCenter.getConfig_map().put(name, list);
            return list;
        } else if (type == 1) {
            List<Map<String, String>> res_list = sqlConfigFileByUID(name);
            List<ValueInfoBean> list = build_ValueInfoBean(res_list);
            valueCenter.getConfig_map().put(name, list);
            return list;
        }
        return null;
    }

    /**
     * autoward
     *
     * @param res_list
     * @return
     */
    private List<ValueInfoBean> build_ValueInfoBean(List<Map<String, String>> res_list) {

        int writeadd = 1;
        List<ValueInfoBean> config_list = new ArrayList<>();
        for (Map<String, String> map : res_list) {
            ValueInfoBean bean = new ValueInfoBean(
                    Integer.valueOf(map.get("type")),
                    map.get("data_name"),
                    0,
                    map.get("data_units"),
                    Integer.valueOf(map.get("data_type")),
                    Integer.valueOf(map.get("data_decimal")),
                    Integer.valueOf(map.get("data_rwtype")),
                    Integer.valueOf(map.get("data_level")),
                    map.get("data_showname"),
                    Integer.valueOf(map.get("data_max")),
                    Integer.valueOf(map.get("data_min")),
                    writeadd);

            config_list.add(bean);

            if (Integer.valueOf(map.get("data_rwtype")) > 0) {
                int dataType = Integer.valueOf(map.get("data_type"));
                if (dataType == 0 || dataType == 1 || dataType == 2)
                    writeadd = writeadd + 1;
                else if (dataType == 3 || dataType == 4 || dataType == 5)
                    writeadd = writeadd + 2;
                else if (dataType == 6)
                    writeadd = writeadd + 4;
            }
        }
        return config_list;
    }

    /**
     * @param filename
     * @return
     */
    public List<Map<String, String>> sqlConfigFileByName(String filename) {
        if (filename.indexOf(".txt") > 0)
            filename = filename.substring(0, filename.indexOf(".txt"));
        List<Map<String, String>> res_list = new ArrayList<>();
        String sql = "SELECT " +
                "tb_config.type," +
                "tb_config.data_name," +
                "tb_config.data_units," +
                "tb_config.data_type," +
                "tb_config.data_decimal," +
                "tb_config.data_rwtype," +
                "tb_config.data_level," +
                "tb_config.data_max," +
                "tb_config.data_min," +
                "tb_showtype.data_showname " +
                "from tb_config,tb_showtype " +
                "where (tb_config.data_showtype=tb_showtype.id)and(tb_config.filename=?) " +
                "order by tb_config.data_index;";
        ArrayList<Object> param = new ArrayList<>();
        param.add(filename);
        res_list = JDBC_Pool.getInstance().queryForMap(sql, param);
        return res_list;
    }

    /**
     * 配置文件
     */
    public List<Map<String, String>> sqlConfigFileByUID(String uid) {
        List<Map<String, String>> res_list = new ArrayList<>();
        String sql = "SELECT " +
                "tb_config.type," +
                "tb_config.data_name," +
                "tb_config.data_units," +
                "tb_config.data_type," +
                "tb_config.data_decimal," +
                "tb_config.data_rwtype," +
                "tb_config.data_level," +
                "tb_config.data_max," +
                "tb_config.data_min," +
                "tb_showtype.data_showname" +
                "from tb_config,tb_showtype" +
                "where (tb_config.data_showtype=tb_showtype.id) and (tb_config.filename= (select configfile_name from tb_device where device_id =? ))" +
                "order by tb_config.data_index;";
        ArrayList<Object> param = new ArrayList<>();
        param.add(uid);
        res_list = JDBC_Pool.getInstance().queryForMap(sql, param);
        return res_list;
    }

    /**
     * 获取文件名
     *
     * @param uid
     * @return
     */
    public String sqlGetFileNameByUid(String uid) {
        List<String> res_list = new ArrayList<>();
        String sql = "select configfile_name from tb_device where device_id =?";
        ArrayList<Object> param = new ArrayList<>();
        param.add(uid);
        res_list = JDBC_Pool.getInstance().queryForList(sql, param);
        if (res_list.size() > 0)
            return res_list.get(0);
        else
            return null;
    }

    /***********************************************************************
     * user 的增删改查
     *
     * @param username
     * @param password
     * @param type
     * @param pro_company
     * @param use_company
     * @param level
     * @return
     */
    public int User_Add(String username, String password, int type, String pro_company, String use_company, int level,
                        String imei) {
        int result = 0;
        String sql = "insert into tb_user (username, password, type, pro_company, use_company, level,imei) values(?, ?, ?, ?, ?, ?, ?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(username);
        param.add(password);
        param.add(type);
        param.add(pro_company);
        param.add(use_company);
        param.add(level);
        param.add(imei);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int User_Del(List<String> username_list) {
        int result = 0;
        boolean frist = false;
        String sql = "delete from tb_user where username in (";
        ArrayList<Object> list = new ArrayList<Object>();
        for (String param : username_list) {
            if (!frist) {
                sql = sql + "?";
                frist = true;
            } else
                sql = sql + ",?";
            list.add(param);
        }
        sql += ");";
        result = JDBC_Pool.getInstance().execute(sql, list);
        return result;
    }

    public int User_Cpwd(String username, String opwd, String npwd) {
        int result = 0;
        String sql = "update tb_user set password=? where username=? and passwoed = ? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(npwd);
        param.add(username);
        param.add(opwd);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public void User_SelectByName(String username, String id) {
        String key  = "usn"+"#"+id;
        UserBean user = valueCenter.getSession_userinfo().get(id);
        if (user.getLevel() >= 2) {
            String pro_company = user.getPro_company();
            String sql = "select * from tb_user where username=? and pro_company = ?";
            ArrayList<Object> param = new ArrayList<Object>();
            param.add(username);
            param.add(pro_company);
            Map<String, String> sql_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);

            UserBean userBean  =  new UserBean(sql_map.get("username"),"",Integer.valueOf(sql_map.get("type")),
                    sql_map.get("pro_company"),sql_map.get("use_company"),Integer.valueOf(sql_map.get("level")),sql_map.get("imei"));

            DeferredResult<UserBean> result = (DeferredResult<UserBean>) valueCenter.getSession_deferredResult_map().get("");
            result.setResult(userBean);
        } else {
            DeferredResult<Map<String, Integer>> result = (DeferredResult<Map<String, Integer>>) valueCenter.getSession_deferredResult_map().get(key);
            Map<String, Integer> map = new LinkedHashMap<>();
            map.put("result", ErrorCode.PERMISSION);
            result.setResult(map);
        }
    }

    public void User_SelectAll( String id) {
        String key  = "usa"+"#"+id;
        UserBean user = valueCenter.getSession_userinfo().get(id);
        if (user.getLevel() >= 2) {
            String pro_company = user.getPro_company();
            String sql = "select * from tb_user where  pro_company = ?";
            ArrayList<Object> param = new ArrayList<Object>();
            param.add(pro_company);
            List<Map<String, String>> sql_map = JDBC_Pool.getInstance().queryForMap(sql, param);
            List<UserBean> user_list  = new ArrayList<>();
            for (Map<String, String> map : sql_map) {
                UserBean userBean = new UserBean(map.get("username"), "", Integer.valueOf(map.get("type")),
                        map.get("pro_company"), map.get("use_company"), Integer.valueOf(map.get("level")), map.get("imei"));

                user_list.add(userBean);
            }
            DeferredResult<List<UserBean>> result = (DeferredResult<List<UserBean>>) valueCenter.getSession_deferredResult_map().get("");
            result.setResult(user_list);
        } else {
            DeferredResult<Map<String, Integer>> result = (DeferredResult<Map<String, Integer>>) valueCenter.getSession_deferredResult_map().get(key);
            Map<String, Integer> map = new LinkedHashMap<>();
            map.put("result", ErrorCode.PERMISSION);
            result.setResult(map);
        }
    }

    public int User_Update(String username, String password, int type, String pro_company, String use_company, int level, String imei) {
        int result = 0;
        String sql = "update tb_user set password=? ,type=?,pro_company=?,use_company=?,level=?,imei=? where username=? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(username);
        param.add(password);
        param.add(type);
        param.add(pro_company);
        param.add(use_company);
        param.add(level);
        param.add(imei);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;

    }

    /********************************************************************
     * pro_company 的增删改查
     *
     * @param pro_company
     * @param use_company
     * @return
     */
    public int addProCompany(String pro_company, String use_company) {
        int result = 0;
        String sql = "insert into tb_pro_company (uuid, pro_company, use_company) values(?, ?, ?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        param.add(uuid);
        param.add(pro_company);
        param.add(use_company);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int delProCompany(List<String> uuid_list) {
        int result = 0;
        String sql = "delete from tb_pro_company where uuid in ( ";
        ArrayList<Object> list = new ArrayList<Object>();
        for (String param : uuid_list) {
            sql = sql + "?,";
            list.add(param);
        }
        sql += ");";
        result = JDBC_Pool.getInstance().execute(sql, list);
        return result;
    }

    public int updateProCompany(String uuid, String pro_company, String use_company) {
        int result = 0;
        String sql = "update tb_pro_company set pro_company=?, use_company =? where uuid=? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(pro_company);
        param.add(use_company);
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public List<Map<String, String>> selectProCompany(String pro_company) {
        List<Map<String, String>> list = null;
        String sql = "select * from tb_pro_company where pro_company=?";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(pro_company);
        list = JDBC_Pool.getInstance().queryForMap(sql, param);
        return list;
    }

//	public List<Map<String, String>> selectProCompanyByProtobuf(List<select_company_param> cmd_list) {
//		List<Map<String, String>> list = null;
//		String sql = "select * from tb_pro_company where";
//		ArrayList<Object> paramlist = new ArrayList<Object>();
//
//		int count = cmd_list.size();
//		for (int i = 0; i < count; i++) {
//			select_company_param param = cmd_list.get(i);
//			String cmdname = param.getCmdname();
//			String cmdparam = param.getCmdparam();
//
//			if (i == 0) {
//				sql = sql + " " + cmdname + "=?";
//				paramlist.add(cmdparam);
//			} else {
//				sql = sql + ",and " + cmdname + "=?";
//				paramlist.add(cmdparam);
//			}
//		}
//		list = JDBC_Pool.getInstance().queryForMap(sql, paramlist);
//		return list;
//	}

    /********************************************************************
     * device 的增删改查
     *
     */
    public int addDevice(String device_id, int type, String cmd, int looptime, String pro_company, String use_company,
                         double longitude, double latitude, String devicename, String mod_add, String data_add,
                         String configfile_name, int ischeck, Date creat_time) {
        int result = 0;
        String sql = "insert into tb_device (uuid, device_id, type,cmd,loop_time,pro_company,"
                + "use_company,longitude,latitude,device_name,mod_add,data_add,configfile_name,ischeck,creat_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        param.add(uuid);
        param.add(device_id);
        param.add(type);
        param.add(cmd);
        param.add(looptime);
        param.add(pro_company);
        param.add(use_company);
        param.add(longitude);
        param.add(latitude);
        param.add(devicename);
        param.add(mod_add);
        param.add(data_add);
        param.add(configfile_name);
        param.add(ischeck);
        param.add(creat_time);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int delDevice(List<String> uuid_list) {
        int result = 0;
        String sql = "delete from tb_device where uuid in ( ";
        ArrayList<Object> list = new ArrayList<Object>();
        for (String param : uuid_list) {
            sql = sql + "?,";
            list.add(param);
        }
        sql += ");";
        result = JDBC_Pool.getInstance().execute(sql, list);
        return result;
    }

    public int updateDevice(String uuid, String device_id, int type, String cmd, int looptime, String pro_company,
                            String use_company, double longitude, double latitude, String devicename, String mod_add, String data_add,
                            String configfile_name, int ischeck, Date creat_time) {
        int result = 0;
        String sql = "update tb_device set device_id=?, type =?,cmd =?,loop_time=?,pro_company=?"
                + "use_company=?,longitude=?,latitude=?,device_name=?,mod_add=?,data_add=?,configfile_name=? "
                + "ischeck =?,creat_time = ? where uuid=? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(device_id);
        param.add(type);
        param.add(cmd);
        param.add(looptime);
        param.add(pro_company);
        param.add(use_company);
        param.add(longitude);
        param.add(latitude);
        param.add(devicename);
        param.add(mod_add);
        param.add(data_add);
        param.add(configfile_name);
        param.add(ischeck);
        param.add(creat_time);
        param.add(configfile_name);
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public Map<String, String> selectDevice_get_devicename(String device_id) {
        Map<String, String> map = null;
        String sql = "select device_name from tb_device where device_id=? and ischeck = 0";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(device_id);
        map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        return map;
    }

    public Map<String, String> selectDevice(String device_id) {
        Map<String, String> map = null;
        String sql = "select * from tb_device where device_id=? and ischeck = 0";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(device_id);
        map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        return map;
    }

    public List<Map<String, String>> selecetAllDeviceDTU() {
        List<Map<String, String>> list;
        String sql = "select * from tb_device where ischeck = 0 and type = 0";
        ArrayList<Object> param = new ArrayList<Object>();
        list = JDBC_Pool.getInstance().queryForMap(sql, param);
        return list;
    }

    public List<Map<String, String>> selecetAllDeviceHmi() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ;
        String sql = "select * from tb_device where ischeck = 0 and type = 1";
        ArrayList<Object> param = new ArrayList<Object>();
        list = JDBC_Pool.getInstance().queryForMap(sql, param);
        return list;
    }

//	public List<Map<String, String>> selectDeviceByProtobuf(List<select_device_param> cmd_list) {
//		List<Map<String, String>> list = null;
//		String sql = "select * from tb_device where";
//		ArrayList<Object> paramlist = new ArrayList<Object>();
//
//		int count = cmd_list.size();
//		for (int i = 0; i < count; i++) {
//			select_device_param param = cmd_list.get(i);
//			String cmdname = param.getCmdtype();
//			String cmdparam = param.getCmdparam();
//
//			if (i == 0) {
//				sql = sql + " " + cmdname + "=?";
//				paramlist.add(cmdparam);
//			} else {
//				sql = sql + ",and " + cmdname + "=?";
//				paramlist.add(cmdparam);
//			}
//		}
//		list = JDBC_Pool.getInstance().queryForMap(sql, paramlist);
//		return list;
//	}


    /********************************************************************
     * imei 的增删改查
     *
     */
    public int addImei(String device_id, String imei) {
        int result = 0;
        String sql = "insert into tb_imei (uuid, device_id,imei) values(?,?,?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        param.add(uuid);
        param.add(device_id);
        param.add(imei);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int delImei(String uuid) {
        int result = 0;
        String sql = "delete from tb_device where uuid =? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int updateImei(String uuid, String device_id, String imei) {
        int result = 0;
        String sql = "update tb_device set device_id=?, imei =? where uuid=? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(device_id);
        param.add(imei);
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public List<String> selectImei(String device_id) {
        List<String> list = null;
        String sql = "select imei from tb_imei where device_id=?";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(device_id);
        list = JDBC_Pool.getInstance().queryForList(sql, param);
        return list;
    }

    /********************************************************************
     * user_device 的增删改查
     *
     */
    public int addUserDevice(String username, String device_id) {
        int result = 0;
        String sql = "insert into tb_user_device (uuid, username,device_id) values(?,?,?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        param.add(uuid);
        param.add(username);
        param.add(device_id);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int delUserDevice(String uuid) {
        int result = 0;
        String sql = "delete from tb_user_device where uuid =? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public int updateUserDevice(String uuid, String username, String device_id) {
        int result = 0;
        String sql = "update tb_user_device set username=?,device_id=? where uuid=? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(username);
        param.add(device_id);
        param.add(uuid);
        result = JDBC_Pool.getInstance().execute(sql, param);
        return result;
    }

    public List<String> selectUserDevice(String username) {
        List<String> list = null;
        String sql = "select device_id from tb_user_device where username=?";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(username);
        list = JDBC_Pool.getInstance().queryForList(sql, param);
        return list;
    }


}
