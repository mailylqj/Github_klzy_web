package com.mysql;

import com.application.ValueCenter;
import com.bean.*;
import com.util.ByteServer;
import com.util.ErrorCode;
import com.util.Helper;
import org.springframework.web.context.request.async.DeferredResult;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     */
    public void user_login(Map<String, Object> req_map, DeferredResult<Object> deferredResult) {

        String username = (String) req_map.get("username");
        String password = (String) req_map.get("password");
        if (username != null && password != null) {
            String sql = "SELECT tb_user.id,tb_user.username, tb_user.password,tb_user.level,tb_user.pro_company_id,tb_user.use_company_id,tb_pro_company.pro_company,tb_use_company.use_company,tb_level.level_name" +
                    " from tb_user,tb_pro_company,tb_use_company,tb_level  " +
                    "where (tb_pro_company.id = tb_user.pro_company_id) and (tb_use_company.id = tb_user.use_company_id)  and (tb_level.id = tb_user.level) and (tb_user.username = ?)";

            ArrayList<Object> param = new ArrayList<Object>();
            param.add(username);
            Map<String, String> sql_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);

            String pwd = sql_map.get("password");
            if (pwd != null) {
                if (password.equals(pwd)) {
                   // String uuid =  UUID.randomUUID().toString().replace("-", "");
                    String uuid = "13e2bd55b1a94633adc20349ddeceb9e";
                    ShowUserBean show_user = new ShowUserBean(Integer.valueOf(sql_map.get("id")), sql_map.get("username"), null, sql_map.get("pro_company"), sql_map.get("use_company"), sql_map.get("level_name"), Integer.valueOf(sql_map.get("level")));
                    UserBean user = new UserBean(Integer.valueOf(sql_map.get("id")), sql_map.get("username"), null, sql_map.get("pro_company"), sql_map.get("use_company"), sql_map.get("level_name"), Integer.valueOf(sql_map.get("pro_company_id")), Integer.valueOf(sql_map.get("use_company_id")), Integer.valueOf(sql_map.get("level")));
                    valueCenter.getUuid_userinfo().put(uuid, user);
                    Token token = new Token(null, uuid);
                    Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, show_user);
                } else {
                    Helper.getInstance().DefReturn(deferredResult, ErrorCode.PWD_ERROR, ErrorCode.PWD_ERROR_STR, null, null);
                }
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_USER, ErrorCode.NO_USER_STR, null, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, null, null);
        }
    }

    /**
     * 获取列表
     */
    public void getdevicelist(Token token, DeferredResult<Object> deferredResult) {

        String uuid = token.getUuid();
        UserBean user = valueCenter.getUuid_userinfo().get(uuid);
        if (user != null) {
            int user_id = user.getId();
            String sql = "select id, device_id,device_name,type,longitude,latitude,configfile_name from tb_device where id = any(select device_id from tb_user_device where user_id=?)";

            ArrayList<Object> param = new ArrayList<Object>();
            param.add(user_id);
            List<Map<String, String>> sql_map = JDBC_Pool.getInstance().queryForMap(sql, param);

            List<String> online_device = valueCenter.getAll_OnlineDevice();
            List<DeviceBean> list = new ArrayList<>();
            List<String> reg_list = new ArrayList<>();

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
                list.add(bean);
            }
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, list);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.UNLOGIN, ErrorCode.UNLOGIN_STR, token, null);
        }
    }


    /**
     * 获取配置文件
     *
     * @param name
     * @param
     */
    public List<ValueInfoBean> getconfigfile(String name, int type) {
        if (type == 0) {
            List<ValueInfoBean> list = valueCenter.getUuid_config().get(name);
            if (list != null) {
                return list;
            } else {
                List<Map<String, String>> res_list = sqlConfigFileByName(name);
                list = build_ValueInfoBean(res_list);
                valueCenter.getUuid_config().put(name, list);
                return list;
            }
        } else if (type == 1) {
            String filename = sqlGetFileNameByUid(name);
            if (filename == null)
                return null;
            List<ValueInfoBean> list = valueCenter.getUuid_config().get(filename);
            if (list != null) {
                return list;
            } else {
                List<Map<String, String>> res_list = sqlConfigFileByName(filename);
                list = build_ValueInfoBean(res_list);
                valueCenter.getUuid_config().put(filename, list);
                return list;
            }
        }
        return null;
    }


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
     * @param
     * @param type
     * @return
     */
    public void updateConfigInfo(Token token, String name, int type, DeferredResult<Object> deferredResult) {
        String uuid = token.getUuid();
        List<ValueInfoBean> list = null;
        if (type == 0) {
            List<Map<String, String>> res_list = sqlConfigFileByName(name);
            list = build_ValueInfoBean(res_list);
            valueCenter.getUuid_config().put(name, list);
        } else if (type == 1) {
            List<Map<String, String>> res_list = sqlConfigFileByUID(name);
            list = build_ValueInfoBean(res_list);
            valueCenter.getUuid_config().put(name, list);
        }
        Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, list);
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

    /**************************************************************************************************************************
     * pro_company 的增删改查
     * 仅 开发组 操作
     */
    public void ProCompany_Add(Token token, String pro_company, DeferredResult<Object> deferredResult) {

        String sql = "insert into tb_pro_company ( pro_company) values( ?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(pro_company);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void ProCompany_Del(Token token, List<Integer> id_list, DeferredResult<Object> deferredResult) {
        boolean frist = false;
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "delete from tb_pro_company where id in ( ";

        for (int id : id_list) {
            if (!frist) {
                sql = sql + "?";
                frist = true;
            } else
                sql = sql + ",?";
            param.add(id);
        }
        sql += ");";
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void ProCompany_Update(Token token, int id, String pro_company, DeferredResult<Object> deferredResult) {
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "update tb_pro_company set pro_company = ? where id = ?";
        param.add(pro_company);
        param.add(id);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void ProCompany_Select(Token token, DeferredResult<Object> deferredResult) {
        List<ProCompanyBean> return_list = new ArrayList<>();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "select * from tb_pro_company ";
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);

        if (list != null) {
            for (Map<String, String> map : list) {
                int id = Integer.valueOf(map.get("id"));
                String pro_company = map.get("pro_company");
                ProCompanyBean bean = new ProCompanyBean(id, pro_company);
                return_list.add(bean);
            }
            if (return_list.size() > 0) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, return_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    /**************************************************************************************************************************
     * use_company 的增删改查
     *
     */
    public void UseCompany_Add(Token token, String use_company, int pro_company_id, DeferredResult<Object> deferredResult) {
        String sql = "insert into tb_use_company (use_company,pro_company_id) values(?,?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(use_company);
        param.add(pro_company_id);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void UseCompany_Del(Token token, List<Integer> id_list, DeferredResult<Object> deferredResult) {
        boolean frist = false;
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "delete from tb_use_company where id in ( ";
        for (int id : id_list) {
            if (!frist) {
                sql = sql + "?";
                frist = true;
            } else
                sql = sql + ",?";
            param.add(id);
        }
        sql += ");";
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }


    public void UseCompany_Update(Token token, int id, String use_company, int pro_company_id, DeferredResult<Object> deferredResult) {
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "update tb_use_company set use_company = ? , pro_company_id = ?  where id = ?";
        param.add(use_company);
        param.add(pro_company_id);
        param.add(id);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void UseCompany_Select(Token token, int pro_company_id, DeferredResult<Object> deferredResult) {
        List<UseCompanyBean> return_list = new ArrayList<>();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "select * from tb_use_company where pro_company_id = ? ";
        param.add(pro_company_id);
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);

        if (list != null) {
            for (Map<String, String> map : list) {
                int id = Integer.valueOf(map.get("id"));
                String use_company = map.get("use_company");
                UseCompanyBean bean = new UseCompanyBean(id, use_company, pro_company_id);
                return_list.add(bean);
            }
            if (return_list.size() > 0) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, return_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    /************************************************************************************************************************************
     * user 的增删改查
     *
     * @param username
     * @param password
     * @param level
     * @return
     ***********************************************************************************************************************************/
    public void User_Add(Token token, String username, String password, int type, int pro_company_id, int use_company_id, int level, DeferredResult<Object> deferredResult) {
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "insert into tb_user (username, password, type,level,pro_company_id,use_company_id ) values(?, ?, ?, ?, ?,?) ";
        param.clear();
        param.add(username);
        param.add(password);
        param.add(type);
        param.add(level);
        param.add(pro_company_id);
        param.add(use_company_id);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void User_Del(Token token, List<Integer> userid_list, UserBean userbean, DeferredResult<Object> deferredResult) {
        int result = 0;
        boolean frist = false;
        String sql = "";
        ArrayList<Object> list = new ArrayList<Object>();
        int userlevel = userbean.getLevel();

        if (userlevel == 9) {
            sql = "delete from tb_user where id in (";
        } else if (userlevel == 4) {
            int pro_company_id = userbean.getPro_company_id();
            sql = "delete from tb_user where level< 4 and pro_company_id =? and id in (";
            list.add(pro_company_id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        for (int user_id : userid_list) {
            if (!frist) {
                sql = sql + "?";
                frist = true;
            } else
                sql = sql + ",?";
            list.add(user_id);
        }
        sql += ");";
        result = JDBC_Pool.getInstance().execute(sql, list);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void User_Cpwd(Token token, UserBean user, String opwd, String npwd, DeferredResult<Object> deferredResult) {
        int result = 0;
        int user_id = user.getId();
        String sql = "update tb_user set password=? where id=? and password = ? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(npwd);
        param.add(user_id);
        param.add(opwd);
        result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void User_SelectByName(Token token, String username, UserBean user, DeferredResult<Object> deferredResult) {
        int userlevel = user.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_user.id,tb_user.username, tb_user.password,tb_user.level,tb_pro_company.pro_company,tb_use_company.use_company,tb_level.level_name" +
                " from tb_user,tb_pro_company,tb_use_company,tb_level  " +
                " where (tb_pro_company.id = tb_user.pro_company_id) and (tb_use_company.id = tb_user.use_company_id)  and (tb_level.id = tb_user.level) and (tb_user.username = ?)";

        if (userlevel == 9) {
            param.add(username);
        } else if (userlevel >= 4) {
            int pro_company_id = user.getPro_company_id();
            sql += " and (tb_user.pro_company_id = ?)";
            param.add(username);
            param.add(pro_company_id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        Map<String, String> sql_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (sql_map != null) {
            if (sql_map.size() > 0) {
                ShowUserBean show_user = new ShowUserBean(Integer.valueOf(sql_map.get("id")), sql_map.get("username"), null, sql_map.get("pro_company"), sql_map.get("use_company"), sql_map.get("level_name"), Integer.valueOf(sql_map.get("level")));
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, show_user);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }
    }

    public void User_SelectByID(Token token, int id, UserBean user, DeferredResult<Object> deferredResult) {
        int userlevel = user.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_user.id,tb_user.username, tb_user.password,tb_user.level,tb_pro_company.pro_company,tb_use_company.use_company,tb_level.level_name" +
                " from tb_user,tb_pro_company,tb_use_company,tb_level  " +
                " where (tb_pro_company.id = tb_user.pro_company_id) and (tb_use_company.id = tb_user.use_company_id)  and (tb_level.id = tb_user.level) and (tb_user.id = ?)";

        if (userlevel == 9) {
            param.add(id);
        } else if (userlevel >= 4) {
            String pro_company = user.getPro_company();
            sql += " and (pro_company = ?)";
            param.add(id);
            param.add(pro_company);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        Map<String, String> sql_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (sql_map != null) {
            if (sql_map.size() > 0) {
                ShowUserBean show_user = new ShowUserBean(Integer.valueOf(sql_map.get("id")), sql_map.get("username"), null, sql_map.get("pro_company"), sql_map.get("use_company"), sql_map.get("level_name"), Integer.valueOf(sql_map.get("level")));
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, show_user);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }
    }

    public void User_SelectAll(Token token, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String pro_company = userbean.getPro_company();
        String use_company = userbean.getUse_company();

        String sql = " SELECT tb_user.id,tb_user.username, tb_user.password,tb_user.level,tb_pro_company.pro_company,tb_use_company.use_company,tb_level.level_name" +
                " from tb_user,tb_pro_company,tb_use_company,tb_level  " +
                " where (tb_pro_company.id = tb_user.pro_company_id) and (tb_use_company.id = tb_user.use_company_id)  and (tb_level.id = tb_user.level) ";

        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
        } else if (userlevel >= 4) {
            sql = "and (pro_company = ?)";
            param.add(pro_company);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        List<Map<String, String>> sql_list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (sql_list != null) {
            if (sql_list.size() > 0) {
                List<ShowUserBean> user_list = new ArrayList<>();
                for (Map<String, String> sql_map : sql_list) {
                    ShowUserBean show_user = new ShowUserBean(Integer.valueOf(sql_map.get("id")), sql_map.get("username"), null, sql_map.get("pro_company"), sql_map.get("use_company"), sql_map.get("level_name"), Integer.valueOf(sql_map.get("level")));
                    user_list.add(show_user);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, user_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }
    }

    public void User_Update(Token token, int user_id, String username, int type, int pro_company_id, int use_company_id, int level, UserBean userbean, DeferredResult<Object> deferredResult) {
        int result = 0;
        String sql = "";
        int userlevel = userbean.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
            sql = "update tb_user set  username = ? ,type=?,pro_company_id=?,use_company_id=?,level=? where id=? ";
            param.add(username);
            param.add(type);
            param.add(pro_company_id);
            param.add(use_company_id);
            param.add(level);
            param.add(user_id);
        } else if (userlevel >= 4) {
            sql = "update tb_user set username = ? , type=?,use_company=?,level=? where id =? and pro_company = ?";
            param.add(username);
            param.add(type);
            param.add(use_company_id);
            param.add(level);
            param.add(user_id);
            param.add(pro_company_id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    /****************************************************************************************************************************************
     * device 的增删改查
     *
     *
     *
     ***************************************************************************************************************************************/
    public void Device_Add(Token token, String device_id, int type, String cmd, int looptime, int pro_company_id, int use_company_id,
                           double longitude, double latitude, String devicename, String mod_add, String data_add,
                           String configfile_name, int ischeck, String creat_date, String allow_date, DeferredResult<Object> deferredResult) {

        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "insert into tb_device (device_id, type,cmd,loop_time,pro_company_id,use_company_id,longitude," +
                "latitude,device_name,mod_add,data_add,configfile_name,ischeck,creat_date,allow_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        param.add(device_id);
        param.add(type);
        param.add(cmd);
        param.add(looptime);
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(longitude);
        param.add(latitude);
        param.add(devicename);
        param.add(mod_add);
        param.add(data_add);
        param.add(configfile_name);
        param.add(ischeck);
        param.add(creat_date);
        param.add(allow_date);
        int result = JDBC_Pool.getInstance().execute(sql, param);

        if (result > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("ChangeNum", result);
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
        } else if (result == 0) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.FAILURE, ErrorCode.FAILURE_STR, token, null);
        } else if (result == -13)
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.REPEAT_ADD, ErrorCode.REPEAT_ADD_STR, token, null);
        else
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
    }

    public void Device_Del(Token token, List<Integer> deviceid_list, UserBean userbean, DeferredResult<Object> deferredResult) {
        int result = 0;
        boolean frist = false;
        String sql = "";
        ArrayList<Object> list = new ArrayList<Object>();
        int userlevel = userbean.getLevel();

        if (userlevel == 9) {
            sql = "delete from tb_device where id in ( ";
        } else if (userlevel >= 4) {
            sql = "delete from tb_device where pro_company_id = ? and id in ( ";
            list.add(userbean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        for (int id : deviceid_list) {
            if (!frist) {
                sql = sql + "?";
                frist = true;
            } else
                sql = sql + ",?";
            list.add(id);
        }
        sql += ");";

        result = JDBC_Pool.getInstance().execute(sql, list);
        if (result > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("ChangeNum", result);
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
        } else if (result == 0) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.FAILURE, ErrorCode.FAILURE_STR, token, null);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public void Device_Update(Token token, int id, String device_id, int type, String cmd, int looptime, int pro_company_id,
                              int use_company_id, double longitude, double latitude, String devicename,
                              String configfile_name, int ischeck, String allow_time, UserBean userbean, DeferredResult<Object> deferredResult) {

        ArrayList<Object> param = new ArrayList<Object>();
        String sql = "update tb_device set device_id = ?,  type =?,cmd =?,loop_time=?,pro_company_id=?,"
                + "use_company_id=?,longitude=?,latitude=?,device_name=?,configfile_name=? ,"
                + "ischeck =?,allow_date = ? where id=? ";
        param.add(device_id);
        param.add(type);
        param.add(cmd);
        param.add(looptime);
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(longitude);
        param.add(latitude);
        param.add(devicename);
        param.add(configfile_name);
        param.add(ischeck);
        param.add(allow_time);
        param.add(id);

        int result = JDBC_Pool.getInstance().execute(sql, param);
        if (result > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("ChangeNum", result);
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
        } else if (result == 0) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.FAILURE, ErrorCode.FAILURE_STR, token, null);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    public void Device_SelectByID(Token token, int id, UserBean userBean, DeferredResult<Object> deferredResult) {
        int userlevel = userBean.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " from tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) and (tb_device.id = ?)";

        if (userlevel == 9) {
            param.add(id);
        } else if (userlevel >= 4) {
            String pro_company = userBean.getPro_company();
            sql += " and (tb_device.pro_company_id  = ?) ";
            param.add(id);
            param.add(pro_company);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        Map<String, String> map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (map != null) {
            NormalDeviceSelect device = creatDevice(map, userlevel);
            if (device != null) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, device);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }
    }

    /**
     * @param userBean
     */
    public void Device_SelectByDeviceID(Token token, String device_id, UserBean userBean, DeferredResult<Object> deferredResult) {

        int userlevel = userBean.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " from tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) and (tb_device.device_id = ?)";

        if (userlevel == 9) {
            param.add(device_id);
        } else if (userlevel >= 4) {
            sql += " and (tb_device.pro_company_id  = ?) ";
            param.add(device_id);
            param.add(userBean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        Map<String, String> map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (map != null) {
            NormalDeviceSelect device = creatDevice(map, userlevel);
            if (device != null) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, device);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }
    }


    /**
     * @param userBean
     * @param deferredResult
     */
    public void Device_Select_UNAllow(Token token, UserBean userBean, DeferredResult<Object> deferredResult) {
        List<NormalDeviceSelect> device_list = new ArrayList<>();
        int userlevel = userBean.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " from tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) and (tb_device.ischeck != 0) ";

        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            for (Map<String, String> map : list) {
                NormalDeviceSelect device = creatDevice(map, userlevel);
                if (device != null) {
                    device_list.add(device);
                }
            }
            if (device_list.size() > 0) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, device_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    public void Device_SelectAll(Token token, UserBean userBean, DeferredResult<Object> deferredResult) {
        List<NormalDeviceSelect> device_list = new ArrayList<>();
        int userlevel = userBean.getLevel();
        ArrayList<Object> param = new ArrayList<Object>();
        String sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " from tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) ";


        if (userlevel == 9) {
        } else if (userlevel >= 4) {
            sql = " and (tb_device.pro_company_id  = ?) ";
            param.add(userBean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            for (Map<String, String> map : list) {
                NormalDeviceSelect device = creatDevice(map, userlevel);
                if (device != null) {
                    device_list.add(device);
                }
            }
            if (device_list.size() > 0) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, device_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    private NormalDeviceSelect creatDevice(Map<String, String> map, int level) {
        if (map.size() == 0)
            return null;
        int id = Integer.valueOf(map.get("id"));
        String device_id = map.get("device_id");
        int type = Integer.valueOf(map.get("type"));
        String cmd = map.get("cmd");
        int loop_time = Integer.valueOf(map.get("loop_time"));
        String pro_company = map.get("pro_company");
        String use_company = map.get("use_company");
        double longitude = Double.valueOf(map.get("longitude"));
        double latitude = Double.valueOf(map.get("latitude"));
        String device_name = map.get("device_name");
        String configfile_name = map.get("configfile_name");
        int ischeck = Integer.valueOf(map.get("ischeck"));
        String creat_date = map.get("creat_date");
        String allow_date = map.get("allow_date");

        String ss = cmd.substring(cmd.length() - 5, cmd.length());
        ss = ss.substring(0, 2) + ss.substring(3, ss.length());

        byte[] b = ByteServer.getInstance().hexString2Bytes(ss);
        ByteBuffer buffer = ByteBuffer.wrap(b);
        int cmd_len = buffer.getChar();
        //int cmd_len = Integer.valueOf(String.format("%d",ss ));
        long creat_time = 0, allow_time = 0;
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
            calendar.setTimeZone(destTimeZone);            //
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(destTimeZone);
            if (creat_date != null) {
                calendar.setTime(formatter.parse(creat_date));
                creat_time = calendar.getTimeInMillis();
            }
            if (allow_date != null) {
                calendar.setTime(formatter.parse(allow_date));
                allow_time = calendar.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (level == 9) {
            NormalDeviceSelect normaldevice = new NormalDeviceSelect(id, device_id, type, cmd_len, loop_time, pro_company, use_company, longitude, latitude, device_name, configfile_name, ischeck, creat_time, allow_time);
            return normaldevice;
        } else if (level >= 3) {
            NormalDeviceSelect normaldevice = new NormalDeviceSelect(id, device_id, type, cmd_len, loop_time, pro_company, use_company, longitude, latitude, device_name, configfile_name, creat_time);
            return normaldevice;
        }
        return null;
    }

    /********************************************************************
     * imei 的增删改查
     *
     */
    public void Imei_Add(Token token, String imei, int pro_company_id, int use_company_id, String info, DeferredResult<Object> deferredResult) {
        String sql = "insert into tb_imei (imei,pro_company_id,use_company_id,info )values(?,?,?,?) ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(imei);
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(info);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void imei_Del(Token token, List<Integer> imeilist, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        boolean isfrist = false;
        String sql = "";
        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
            sql = "delete from tb_imei where id  in ( ";
        } else if (userlevel >= 4) {
            sql = "delete from tb_imei where pro_company  = ? and id in (";
            param.add(userbean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        for (int id : imeilist) {
            if (!isfrist) {
                sql += "?";
                isfrist = true;
            } else {
                sql += ",?";
            }
            param.add(id);
        }
        sql += ")";
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void imei_Update(Token token, int id, String imei, int pro_company_id, int use_company_id, String info, DeferredResult<Object> deferredResult) {
        String sql = "update tb_imei set imei = ?, pro_company_id =?, use_company_id  = ? ,info=? where  id =? ";
        ArrayList<Object> param = new ArrayList<Object>();
        param.add(imei);
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(info);
        param.add(id);
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void imei_SelectByImei(Token token, String imei, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String sql = "";
        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
            sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.imei  = ?";
            param.add(imei);
        } else if (userlevel >= 4) {
            sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.pro_company_id  = ? and tb_imei.imei  = ?";
            param.add(userbean.getPro_company_id());
            param.add(imei);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            if (list.size() > 0) {
                List<Imei> imei_list  = new ArrayList<>();
                for(Map<String,String> map : list) {
                    Imei nimei = new Imei(Integer.valueOf(map.get("id")), map.get("imei"), map.get("pro_company_id"), map.get("use_company"), map.get("info"));
                    if (nimei != null)
                        imei_list.add(nimei);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, imei_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    public void imei_SelectByID(Token token, int id, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String sql = "";
        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
           sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.id  = ?";
            param.add(id);
        } else if (userlevel >= 4) {
            sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.pro_company_id  = ? and tb_imei.id  = ?";
            param.add(userbean.getPro_company_id());
            param.add(id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            if (list.size() > 0) {
                List<Imei> imei_list  = new ArrayList<>();
                for(Map<String,String> map : list) {
                    Imei nimei = new Imei(Integer.valueOf(map.get("id")), map.get("imei"), map.get("pro_company_id"), map.get("use_company"), map.get("info"));
                    if (nimei != null)
                        imei_list.add(nimei);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, imei_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public void imei_SelectAll(Token token, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String sql = "";
        ArrayList<Object> param = new ArrayList<Object>();

        if (userlevel == 9) {
            sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id)";
        } else if (userlevel >= 3) {
            sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                    "FROM tb_imei,tb_pro_company,tb_use_company " +
                    "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.pro_company_id  = ?";
            param.add(userbean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            if (list.size() >= 0) {
                List<Imei> imei_list  = new ArrayList<>();
                for(Map<String,String> map : list) {
                    Imei nimei = new Imei(Integer.valueOf(map.get("id")), map.get("imei"), map.get("pro_company_id"), map.get("use_company"), map.get("info"));
                    if (nimei != null)
                        imei_list.add(nimei);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, imei_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public void Device_Imei_Update(Token token, int device_id, UserBean userBean, List<Integer> curchose_list, DeferredResult<Object> deferredResult) {
        String uuid = token.getUuid();
        List<Integer> save_choselist = valueCenter.getUuid_device_imei().get(uuid);
        List<Integer> add_list = new ArrayList<>();
        List<Integer> sub_list = new ArrayList<>();
        int result01 = 0, result02 = 0;
        boolean isfrist = false;

        if (save_choselist != null) {
            for (int id : curchose_list) {
                if (!save_choselist.contains(id)) {
                    add_list.add(id);
                }
            }

            for (int id : save_choselist) {
                if (!curchose_list.contains(id)) {
                    sub_list.add(id);
                }
            }

            String sql = "";
            List<Object> param = new ArrayList<>();

            Connection conn = JDBC_Pool.getInstance().syncGetConnection();
            try {
                conn.setAutoCommit(false);
                if (add_list.size() > 0) {
                    sql = "insert into tb_device_imei (device_id,imei_id)values ";
                    param.clear();
                    for (int id : add_list) {
                        if (!isfrist) {
                            sql += "(?,?)";
                            isfrist = true;
                        } else {
                            sql += ",(?,?)";
                        }
                        param.add(device_id);
                        param.add(id);
                    }
                    result01 = JDBC_Pool.getInstance().execute(conn, sql, param);
                }
                if (sub_list.size() > 0) {
                    sql = "delete from tb_device_imei where device_id = ? and imei_id in (";
                    isfrist = false;
                    param.clear();
                    param.add(device_id);

                    for (int  id : sub_list) {
                        if (!isfrist) {
                            sql = sql + "?";
                            isfrist = true;
                        } else
                            sql = sql + ",?";
                        param.add(id);
                    }
                    sql += ");";
                    result02 = JDBC_Pool.getInstance().execute(conn, sql, param);
                }
                conn.commit();
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            if (result02 >= 0 && result01 >= 0) {
                Map<String, Integer> map = new HashMap<>();
                map.put("ChangeNum", result01+result02);
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
                valueCenter.getUuid_user_device_chose().remove(uuid);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.REQUEST_ERROR, ErrorCode.REQUEST_ERROR_STR, token, null);
        }
    }

    public void Device_Imei_Select(Token token, int device_id, int pro_company_id,int userlevel,  DeferredResult<Object> deferredResult) {
        String uuid = token.getUuid();

        List<Imei> chose_list = new ArrayList<>();
        List<Imei> unchose_list = new ArrayList<>();
        List<Integer> save_list = new ArrayList<>();
        String sql = "";
        ArrayList<Object> param = new ArrayList<Object>();

        sql = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
              "FROM tb_imei,tb_pro_company,tb_use_company " +
              "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.id in (SELECT imei_id FROM tb_device_imei WHERE device_id = ?)";
        param.add(device_id);
        List<Map<String,String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null){
            for(Map<String,String> map:list){
                Imei imei = new Imei(Integer.valueOf(map.get("id")),map.get("imei"),map.get("pro_company_id"),map.get("use_company"),map.get("info")) ;
                save_list.add(Integer.valueOf(map.get("id")));
                if (imei != null)
                    chose_list.add(imei);
            }
        }

        sql  = "SELECT tb_imei.id, tb_imei.imei,tb_imei.info, tb_pro_company.pro_company,tb_use_company.use_company " +
                "FROM tb_imei,tb_pro_company,tb_use_company " +
                "WHERE (tb_imei.pro_company_id = tb_pro_company.id) and (tb_imei.use_company_id  = tb_use_company.id) and tb_imei.id  not in (SELECT imei_id FROM tb_device_imei WHERE device_id = ?)";

        if (userlevel == 9) {
            param.clear();
            param.add(device_id);
        } else if (userlevel >= 4) {
            sql = " and tb_imei.pro_company_id = ? ";
            param.clear();
            param.add(device_id);
            param.add(pro_company_id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null){
            for(Map<String,String> map:list){
                Imei imei = new Imei(Integer.valueOf(map.get("id")),map.get("imei"),map.get("pro_company_id"),map.get("use_company"),map.get("info")) ;
                if (imei !=null)
                    unchose_list.add(imei);
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("chose", chose_list);
        map.put("unchose", unchose_list);
        Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);

        valueCenter.getUuid_device_imei().put(uuid, save_list);
    }


    /********************************************************************
     * user_device 的增删改查
     *
     */
    public void User_Device_Select(Token token, int user_id, int user_level, int pro_company_id, DeferredResult<Object> deferredResult) {
        String uuid = token.getUuid();
        List<NormalDeviceSelect> chose_list = new ArrayList<>();
        List<NormalDeviceSelect> unchose_list = new ArrayList<>();
        List<Integer> save_chose_list = new ArrayList<>();

        String sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " FROM tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) and tb_device.id in ( select device_id from tb_user_device where user_id =?)";

        ArrayList<Object> param = new ArrayList<Object>();
        param.add(user_id);
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            for (Map<String, String> map : list) {
                save_chose_list.add(Integer.valueOf(map.get("id")));
                NormalDeviceSelect device = creatDevice(map, user_level);
                if (device != null) {
                    chose_list.add(device);
                }
            }
        }

        sql = " SELECT tb_device.id,tb_device.device_id,tb_device.type,tb_device.cmd,tb_device.loop_time,tb_device.longitude," +
                " tb_device.latitude,tb_device.device_name,tb_device.mod_add,tb_device.data_add,tb_device.configfile_name," +
                " tb_device.ischeck,tb_device.creat_date,tb_device.allow_date, tb_pro_company.pro_company,tb_use_company.use_company " +
                " FROM tb_device,tb_pro_company,tb_use_company" +
                " WHERE (tb_device.pro_company_id = tb_pro_company.id) and (tb_device.use_company_id = tb_use_company.id) and tb_device.id not in ( select device_id from tb_user_device where user_id =?)";

        if (user_level == 9) {
            param.clear();
            param.add(user_id);
        } else if (user_level >= 4) {
            sql += " and tb_device.pro_company_id = ? ";
            param.clear();
            param.add(user_id);
            param.add(pro_company_id);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null) {
            for (Map<String, String> map : list) {
                NormalDeviceSelect device = creatDevice(map, user_level);
                if (device != null) {
                    unchose_list.add(device);
                }
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("chose", chose_list);
        map.put("unchose", unchose_list);
        valueCenter.getUuid_user_device_chose().put(uuid, save_chose_list);
        Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
    }


    public void User_Device_Update(Token token, int user_id, List<Integer> cur_choselist, DeferredResult<Object> deferredResult) {
        String uuid = token.getUuid();
        String sql = "";
        boolean isfrist = false;
        int result01 = 0, result02 = 0;
        ArrayList<Object> param = new ArrayList<Object>();
        List<Integer> add_list = new ArrayList<>();
        List<Integer> sub_list = new ArrayList<>();

        List<Integer> save_choselist = valueCenter.getUuid_user_device_chose().get(uuid);
        if (save_choselist != null) {
            for (int id : cur_choselist) {
                if (!save_choselist.contains(id)) {
                    add_list.add(id);
                }
            }

            for (int id : save_choselist) {
                if (!cur_choselist.contains(id)) {
                    sub_list.add(id);
                }
            }

            Connection conn = JDBC_Pool.getInstance().syncGetConnection();
            try {
                conn.setAutoCommit(false);
                if (add_list.size() > 0) {
                    sql = "insert into tb_user_device (user_id,device_id) values ";
                    param.clear();
                    for (int id : add_list) {
                        if (!isfrist) {
                            sql += "(?,?)";
                            isfrist = true;
                        } else {
                            sql += ",(?,?)";
                        }
                        param.add(user_id);
                        param.add(id);
                    }
                    result01 = JDBC_Pool.getInstance().execute(conn, sql, param);
                }

                if (sub_list.size() > 0) {
                    sql = "delete from tb_user_device where user_id = ? and device_id in (";
                    isfrist = false;
                    param.clear();
                    param.add(user_id);

                    for (int id : sub_list) {
                        if (!isfrist) {
                            sql = sql + "?";
                            isfrist = true;
                        } else
                            sql = sql + ",?";
                        param.add(id);
                    }
                    sql += ");";
                    result02 = JDBC_Pool.getInstance().execute(conn, sql, param);
                }
                conn.commit();
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                    Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            if (result01 >=0 && result02>=0) {
                Map<String, Integer> map = new HashMap<>();
                map.put("ChangeNum", result01+result02);
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
                valueCenter.getUuid_user_device_chose().remove(uuid);
            }else{
                Helper.getInstance().DefReturn(deferredResult,ErrorCode.SQL_EXECUTE_ERROR,ErrorCode.SQL_EXECUTE_ERROR_STR,token,null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.EXCEPTION, ErrorCode.EXCEPTION_STR, token, null);
        }

    }


    /**************************** config配置
     * @param filename
     * @param type
     * @param data_name
     * @param data_units
     * @param data_type
     * @param data_decimal
     * @param data_rwtype
     * @param data_level
     * @param data_showtype
     * @param data_max
     * @param data_min
     * @param userbean
     * @param ****************************************/
    public void config_Add(Token token, String filename, int pro_company_id, int use_company_id, int type, String data_name,
                           String data_units, int data_type, int data_decimal, int data_rwtype, int data_level,
                           int data_showtype, int data_max, int data_min, UserBean userbean, DeferredResult<Object> deferredResult) {

        String sql = "";
        List<Object> param = new ArrayList<>();

        sql = "select max(data_index) from tb_config where filename = ?";
        param.add(filename);
        Map<String, String> ret_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);

        int data_index = 0;
        if (ret_map.get("data_index") != null)
            data_index = Integer.valueOf(ret_map.get("data_index")) + 1;
        else
            data_index = 1;

        sql = "insert into tb_config values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        param.clear();
        param.add(0);
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(filename);
        param.add(type);
        param.add(data_name);
        param.add(data_units);
        param.add(data_type);
        param.add(data_decimal);
        param.add(data_rwtype);
        param.add(data_level);
        param.add(data_showtype);
        param.add(data_max);
        param.add(data_min);
        param.add(data_index);
        int result = JDBC_Pool.getInstance().execute(sql, param);

        if (result >= 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("ChangeNum", result);
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
        } else if (result == -13) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.REPEAT_ADD, ErrorCode.REPEAT_ADD_STR, token, null);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    /**
     * @param userbean
     * @param deferredResult
     */
    public void config_Del(Token token, List<Integer> id_list, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String pro_company = userbean.getPro_company();
        String use_company = userbean.getUse_company();
        String sql = "";
        boolean isfrist = false;
        List<Object> param = new ArrayList<>();

        if (userlevel == 9) {
            sql = "delete from tb_config where id  in ( ";
        } else if (userlevel >= 4) {
            sql = "delete from tb_config where pro_company  = ? and id in (";
            param.add(pro_company);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }

        for (int id : id_list) {
            if (!isfrist) {
                sql += "?";
                isfrist = true;
            } else {
                sql += ",?";
            }
            param.add(id);
        }
        sql += ")";
        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }

    public void config_update(Token token, int id, int pro_company_id, int use_company_id, String filename, int type, String data_name,
                              String data_units, int data_type, int data_decimal, int data_rwtype, int data_level,
                              int data_showtype, int data_max, int data_min, UserBean userbean, DeferredResult<Object> deferredResult) {

        List<Object> param = new ArrayList<>();
        String sql = "update tb_config set pro_company_id = ? ,use_company_id = ? ,filename= ? ,type = ?, data_name=?,data_units=?," +
                "data_type=?,data_decimal=? ,data_rwtype = ? ,data_level = ? ,data_showtype = ?,data_max=? ,data_min=? where id  = ?";
        param.add(pro_company_id);
        param.add(use_company_id);
        param.add(filename);
        param.add(type);
        param.add(data_name);
        param.add(data_units);
        param.add(data_type);
        param.add(data_decimal);
        param.add(data_rwtype);
        param.add(data_level);
        param.add(data_showtype);
        param.add(data_max);
        param.add(data_min);
        param.add(id);

        int result = JDBC_Pool.getInstance().execute(sql, param);
        sqlReturnForADU(result, deferredResult, token);
    }


    public void config_SelectByDataName(Token token, String filename, String data_name, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String sql = "";
        List<Object> param = new ArrayList<>();
        if (userlevel == 9) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company" +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.filename  = ? and tb_config.data_name = ? order by tb_config.data_index";
            param.add(filename);
            param.add(data_name);
        } else if (userlevel >= 4) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company " +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.filename  = ? and tb_config.data_name = ? and tb_config.and pro_company_id  = ? order by tb_config.data_index";
            param.add(filename);
            param.add(data_name);
            param.add(userbean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        Map<String, String> ret_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (ret_map != null) {
            if (ret_map.size() > 0) {
                int id = Integer.valueOf(ret_map.get("id"));
                String ret_filename = ret_map.get("filename");
                String pro_company = ret_map.get("pro_company");
                String use_company = ret_map.get("use_company");
                int type = Integer.valueOf(ret_map.get("type"));
                String ret_data_name = ret_map.get("data_name");
                String data_units = ret_map.get("data_units ");
                int data_type = Integer.valueOf(ret_map.get("data_type"));
                int data_decimal = Integer.valueOf(ret_map.get("data_decimal"));
                int data_rwtype = Integer.valueOf(ret_map.get("data_rwtype"));
                int data_level = Integer.valueOf(ret_map.get("data_level"));
                String data_showname = ret_map.get("data_showname");
                int data_max = Integer.valueOf(ret_map.get("data_max"));
                int data_min = Integer.valueOf(ret_map.get("data_min"));

                ConfigBean bean = new ConfigBean(id, pro_company, use_company, ret_filename, type, ret_data_name,
                        data_units, data_type, data_decimal, data_rwtype, data_level, data_showname, data_max, data_min);

                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, bean);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public void config_SelectByID(Token token, int sid, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String sql = "";
        List<Object> param = new ArrayList<>();
        if (userlevel == 9) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company" +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.id = ? order by tb_config.data_index";
            param.add(sid);
        } else if (userlevel >= 4) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company " +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.id = ? and tb_config.and pro_company_id  = ? order by tb_config.data_index";
            param.add(sid);
            param.add(userbean.getPro_company_id());
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        Map<String, String> ret_map = JDBC_Pool.getInstance().queryForOnceMap(sql, param);
        if (ret_map != null) {
            if (ret_map.size() > 0) {
                int id = Integer.valueOf(ret_map.get("id"));
                String ret_filename = ret_map.get("filename");
                String pro_company = ret_map.get("pro_company");
                String use_company = ret_map.get("use_company");
                int type = Integer.valueOf(ret_map.get("type"));
                String ret_data_name = ret_map.get("data_name");
                String data_units = ret_map.get("data_units ");
                int data_type = Integer.valueOf(ret_map.get("data_type"));
                int data_decimal = Integer.valueOf(ret_map.get("data_decimal"));
                int data_rwtype = Integer.valueOf(ret_map.get("data_rwtype"));
                int data_level = Integer.valueOf(ret_map.get("data_level"));
                String data_showname = ret_map.get("data_showname");
                int data_max = Integer.valueOf(ret_map.get("data_max"));
                int data_min = Integer.valueOf(ret_map.get("data_min"));

                ConfigBean bean = new ConfigBean(id, pro_company, use_company, ret_filename, type, ret_data_name,
                        data_units, data_type, data_decimal, data_rwtype, data_level, data_showname, data_max, data_min);

                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, bean);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }


    public void config_SelectByFilename(Token token, String filename, UserBean userbean, DeferredResult<Object> deferredResult) {
        int userlevel = userbean.getLevel();
        String user_pro_company = userbean.getPro_company();
        String user_use_company = userbean.getUse_company();
        String sql = "";
        List<Object> param = new ArrayList<>();
        if (userlevel == 9) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company" +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.filename  = ?  order by tb_config.data_index";
            param.add(filename);
        } else if (userlevel >= 4) {
            sql = " SELECT tb_config.id,tb_pro_company.pro_company,tb_use_company.use_company,tb_config.filename, tb_config.type,tb_config.data_name,tb_config.data_units,tb_config.data_type,tb_config.data_decimal,tb_config.data_rwtype,tb_config.data_level,tb_config.data_max,tb_config.data_min,tb_showtype.data_showname " +
                    " from tb_config,tb_showtype ,tb_pro_company,tb_use_company " +
                    " where (tb_config.pro_company_id  = tb_pro_company.id) and (tb_config.use_company_id = tb_use_company.id) and (tb_config.data_showtype=tb_showtype.id) and tb_config.filename  = ? and tb_config.and pro_company_id  = ? order by tb_config.data_index";
            param.add(filename);
            param.add(user_pro_company);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.PERMISSION, ErrorCode.PERMISSION_STR, token, null);
            return;
        }
        List<ConfigBean> list = new ArrayList<>();
        List<Map<String, String>> ret_list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (ret_list != null) {
            if (ret_list.size() > 0) {
                for (Map<String, String> ret_map : ret_list) {
                    int id = Integer.valueOf(ret_map.get("id"));
                    String ret_filename = ret_map.get("filename");
                    String pro_company = ret_map.get("pro_company");
                    String use_company = ret_map.get("use_company");
                    int type = Integer.valueOf(ret_map.get("type"));
                    String ret_data_name = ret_map.get("data_name");
                    String data_units = ret_map.get("data_units ");
                    int data_type = Integer.valueOf(ret_map.get("data_type"));
                    int data_decimal = Integer.valueOf(ret_map.get("data_decimal"));
                    int data_rwtype = Integer.valueOf(ret_map.get("data_rwtype"));
                    int data_level = Integer.valueOf(ret_map.get("data_level"));
                    String data_showname = ret_map.get("data_showname");
                    int data_max = Integer.valueOf(ret_map.get("data_max"));
                    int data_min = Integer.valueOf(ret_map.get("data_min"));
                    ConfigBean bean = new ConfigBean(id, pro_company, use_company, ret_filename, type, ret_data_name,
                            data_units, data_type, data_decimal, data_rwtype, data_level, data_showname, data_max, data_min);

                    if (bean != null)
                        list.add(bean);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public void config_Showtype_Select(Token token, DeferredResult<Object> deferredResult) {
        String sql = "select * from tb_showtype";
        List<Object> param = new ArrayList<>();
        List<Map<String, String>> map = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (map != null) {
            if (map.size() > 0) {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }

    public  void sqlGetLevel(DeferredResult deferredResult,Token token){
        String sql = "select * from tb_level";
        List<Object> param = new ArrayList<>();
        List<Map<String, String>> list = JDBC_Pool.getInstance().queryForMap(sql, param);
        if (list != null){
            if (list.size() > 0) {
                List<Level> level_list  = new ArrayList<>();
                for (Map<String,String> map : list){
                    Level level = new Level(Integer.valueOf(map.get("id")),map.get("level_name"));
                    if (level!= null)
                        level_list.add(level);
                }
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, level_list);
            } else {
                Helper.getInstance().DefReturn(deferredResult, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
        }else{
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }

    }

    /**
     * 增 删 改 的统一返回
     *
     * @param result
     * @param deferredResult
     * @param token
     */
    private void sqlReturnForADU(int result, DeferredResult deferredResult, Token token) {
        if (result > 0) {
            Map<String, Integer> map = new HashMap<>();
            map.put("ChangeNum", result);
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, map);
        } else if (result == 0) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.FAILURE, ErrorCode.FAILURE_STR, token, null);
        } else if (result == -13) {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.REPEAT_ADD, ErrorCode.REPEAT_ADD_STR, token, null);
        } else {
            Helper.getInstance().DefReturn(deferredResult, ErrorCode.SQL_EXECUTE_ERROR, ErrorCode.SQL_EXECUTE_ERROR_STR, token, null);
        }
    }



}
