package com.util;

public class ErrorCode {

    //成功
    public static final int SUCCESS = 0;
    public static final String SUCCESS_STR= "成功";
    //密码错误
    public static final int PWD_ERROR = -1;
    public static final String PWD_ERROR_STR="密码错误";
    //未登录
    public static final int UNLOGIN = -2;
    public static final String UNLOGIN_STR="未登录";
    //返回错误
    public static final int NO_USER = -3;
    public static final String NO_USER_STR="用户不存在";
    //异常
    public static final int EXCEPTION = -4;
    public static final String EXCEPTION_STR="发生异常";
    //权限不足
    public static final int PERMISSION = -5;
    public static final String PERMISSION_STR="权限不足";
    // 已到最后或最前
    public static final int SELECTEND = -6;
    public static final String SELECTEND_STR="已到最后或最前";
    // 请求错误
    public static final int REQUEST_ERROR = -7;
    public static final String REQUEST_ERROR_STR="请求参数错误";
    // websocket未连接
    public static final int WEBSOCKET_UNCONN = -8;
    public static final String WEBSOCKET_UNCONN_STR="websocket未连接";
    // 发送失败
    public static final int SEND_ERROR = -9;
    public static final String SEND_ERROR_STR="发送失败";
    // sql出错
    public static final int SQL_EXECUTE_ERROR = -10;
    public static final String SQL_EXECUTE_ERROR_STR="命令有误";
    // 无数据返回
    public static final int NO_DATA =-11;
    public static final String NO_DATA_STR="无数据返回";
    // 当前数据在其他地方有使用不能删除
    public static final int CAN_NOT_DEL =-12;
    public static final String CAN_NOT_DEL_STR="当前数据在其他地方有使用不能删除";
    // 重复添加
    public static  final  int REPEAT_ADD = -13;
    public static final String REPEAT_ADD_STR="重复添加";
    // 失败
    public static  final  int FAILURE = -14;
    public static final String FAILURE_STR="失败";
    //登录过期
    public static  final  int  LOGIN_EXPIRED = -14;
    public static final String LOGIN_EXPIRED_STR="登录已过期";

    //请求超时
    public static  final  int  TIME_OUT = -15;
    public static final String TIME_OUT_STR="请求超时";

    //有请求未处理完
    public static  final  int  PROCESSING = -16;
    public static final String PROCESSING_STR ="已有请求在处理中";
}
