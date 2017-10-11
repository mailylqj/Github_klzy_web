package com.ideal.logic;

import com.google.protobuf.ByteString;
import com.ideal.logic.alarm_data.read_alarm_req;
import com.ideal.logic.control_data.control_req;
import com.ideal.logic.control_data.read_control_req;
import com.ideal.logic.file_data.update_file_request;
import com.ideal.logic.lastest_data.reg_lastest_data;
import com.ideal.logic.lastest_data.unreg_lastest_data;
import com.ideal.logic.lastest_data.update_lastest_data_request;
import com.ideal.logic.login_data.change_pwd_request;
import com.ideal.logic.login_data.login_req;
import com.ideal.logic.modbus_data.read_data_req;
import com.ideal.logic.offline_info.device_info_request;
import com.ideal.logic.offline_info.update_offline_status_request;
import com.ideal.logic.online_info.update_online_status_request;
import com.ideal.logic.server_lastestdata.reg_server_lastestdata;
import com.ideal.logic.server_lastestdata.unreg_server_lastestdata;
import com.ideal.logic.server_online.reg_server_online;
import com.ideal.logic.server_online.unreg_server_online;

public class logic_req {

	private static logic_req instance = null;

	public logic_req() {
	}

	public static logic_req GetInstance() {
		if (instance == null) {
			instance = new logic_req();
		}
		return instance;
	}

	/**
	 *
	 * @param user_name
	 * @param password
	 * @return
	 */
	public login_req userLoginClient(String user_name, String password) {
		login_req.Builder login_req_build = login_req.newBuilder();
		login_req_build.setUserName(user_name);
		login_req_build.setUserPassword(password);
		login_req login_req = login_req_build.build();
		return login_req;
	}

	/**
	 *
	 * @param uidByteString
	 * @param imei
	 * @param modbus_data
	 * @return
	 */
	public control_req commandControlClient(ByteString uidByteString, String imei, byte[] modbus_data,String key) {

		ByteString modaddByteString = ByteString.copyFrom(modbus_data);
		control_req.Builder control_req_build = control_req.newBuilder();
		control_req_build.setUid(uidByteString);
		control_req_build.setImei(imei);
		control_req_build.setModbusProc(modaddByteString);
		control_req_build.setSessionid(key);
		control_req control_req = control_req_build.build();
		return control_req;
	}

	/**
	 *
	 * @param uidByteString
	 * @param modaddByteString
	 * @param dataaddByteString
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public read_data_req modbusReadClient(ByteString uidByteString, ByteString modaddByteString, ByteString dataaddByteString, long start_time, long end_time,String key) {

		read_data_req.Builder wData_req_build = read_data_req.newBuilder();
		wData_req_build.setUid(uidByteString);
		wData_req_build.setModAddress(modaddByteString);
		wData_req_build.setDataAddress(dataaddByteString);
		wData_req_build.setStartTime(start_time);
		wData_req_build.setEndTime(end_time);
		wData_req_build.setSessionid(key);
		read_data_req wData_req = wData_req_build.build();
		return wData_req;
	}

	/**
	 *
	 * @param user_name
	 * @return
	 */
	public update_online_status_request updateOnlineStatusClient(String user_name) {
		update_online_status_request.Builder online_status_build = update_online_status_request.newBuilder();
		online_status_build.setUserName(user_name);
		update_online_status_request online_status = online_status_build.build();
		return online_status;
	}

	/**
	 *
	 * @param uidByteString
	 * @param modaddByteString
	 * @param dataaddByteString
	 * @return
	 */
	public update_lastest_data_request updateLastestDataClient(ByteString uidByteString, ByteString modaddByteString, ByteString dataaddByteString) {

		update_lastest_data_request.Builder lastest_data_build = update_lastest_data_request.newBuilder();
		lastest_data_build.setUid(uidByteString);
		lastest_data_build.setModAddress(modaddByteString);
		lastest_data_build.setDataAddress(dataaddByteString);
		update_lastest_data_request lastest_data = lastest_data_build.build();
		return lastest_data;
	}

	/**
	 *
	 * @param uidByteString
	 * @param modaddByteString
	 * @param dataaddByteString
	 * @return
	 */
	public reg_lastest_data regRefreshLastestDataClient(ByteString uidByteString, ByteString modaddByteString, ByteString dataaddByteString) {

		reg_lastest_data.Builder reg_lastest_data_build = reg_lastest_data.newBuilder();
		reg_lastest_data_build.setUid(uidByteString);
		reg_lastest_data_build.setModAddress(modaddByteString);
		reg_lastest_data_build.setDataAddress(dataaddByteString);
		reg_lastest_data reg_lastest_data = reg_lastest_data_build.build();
		return reg_lastest_data;

	}

	/**
	 *
	 * @param uidByteString
	 * @param modaddByteString
	 * @param dataaddByteString
	 * @return
	 */
	public unreg_lastest_data unregRefreshLastestDataClient(ByteString uidByteString, ByteString modaddByteString, ByteString dataaddByteString) {

		unreg_lastest_data.Builder unreg_lastest_data_build = unreg_lastest_data.newBuilder();
		unreg_lastest_data_build.setUid(uidByteString);
		unreg_lastest_data_build.setModAddress(modaddByteString);
		unreg_lastest_data_build.setDataAddress(dataaddByteString);
		unreg_lastest_data unreg_Lastest_data = unreg_lastest_data_build.build();
		return unreg_Lastest_data;
	}

	/**
	 * 
	 * @param file_name
	 * @return
	 */
	public update_file_request updateFile(String file_name) {

		update_file_request.Builder update_file_request_build = update_file_request.newBuilder();
		update_file_request_build.addFileName(file_name);
		update_file_request update_file_request = update_file_request_build.build();
		return update_file_request;
	}

	/**
	 * 
	 * @param username
	 * @param old_pwd
	 * @param new_pwd
	 * @return
	 */
	public change_pwd_request changePwd(String username, String old_pwd, String new_pwd) {
		change_pwd_request.Builder change_pwd_request_build = change_pwd_request.newBuilder();
		change_pwd_request_build.setUserName(username);
		change_pwd_request_build.setOldPwd(old_pwd);
		change_pwd_request_build.setNewPwd(new_pwd);		;
		change_pwd_request change_pwd_request = change_pwd_request_build.build();
		return change_pwd_request;
	}

	/**
	 * 
	 * @param uidByteString
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public read_control_req readControl(ByteString uidByteString, long start_time, long end_time,String key) {
		read_control_req.Builder read_control_req_build = read_control_req.newBuilder();
		read_control_req_build.setUid(uidByteString);
		read_control_req_build.setStartTime(start_time);
		read_control_req_build.setEndTime(end_time);
		read_control_req_build.setSessionid(key);
		read_control_req read_control_req = read_control_req_build.build();
		return read_control_req;
	}

	/**
	 * 
	 * @param uidByteString
	 * @param start_time
	 * @param end_time
	 * @return
	 */
	public read_alarm_req readAlarm(ByteString uidByteString, long start_time, long end_time,String key) {
		read_alarm_req.Builder read_alarm_req_build = read_alarm_req.newBuilder();
		read_alarm_req_build.setUid(uidByteString);
		read_alarm_req_build.setStartTime(start_time);
		read_alarm_req_build.setEndTime(end_time);
		read_alarm_req_build.setSessionid(key);
		read_alarm_req read_control_req = read_alarm_req_build.build();
		return read_control_req;
	}

	/**
	 *
	 * @param username
	 * @return
	 */
	public update_offline_status_request readoffline (String username, int index){
		update_offline_status_request.Builder builder = update_offline_status_request.newBuilder();
		builder.setUserName(username);
		builder.setSelectindex(index);
		update_offline_status_request req = builder.build();
		return req;
	}

	/**
	 *
	 * @param username
	 * @param uid
	 * @return
	 */
	public device_info_request read_deviceinfo(String username, String uid){
		device_info_request.Builder builder  = device_info_request.newBuilder();
		builder.setUserName(username);
		builder.setUid(uid);
		device_info_request req = builder.build();
		return req;
	}

	public reg_server_lastestdata reg_server_lastestdata(String uuid,String company){
		reg_server_lastestdata.Builder builder  = reg_server_lastestdata.newBuilder();
		builder.setUuid(uuid);
		builder.setCompany(company);
		return builder.build();
	}
	public reg_server_lastestdata reg_server_lastestdata(String uuid){
		reg_server_lastestdata.Builder builder  = reg_server_lastestdata.newBuilder();
		builder.setUuid(uuid);
		return builder.build();
	}
	public unreg_server_lastestdata unreg_server_lastestdata(String uuid){
		unreg_server_lastestdata.Builder builder  = unreg_server_lastestdata.newBuilder();
		builder.setUuid(uuid);
		return builder.build();
	}

	public reg_server_online reg_server_online(String uuid, String company){
		reg_server_online.Builder builder  = reg_server_online.newBuilder();
		builder.setUuid(uuid);
		builder.setCompany(company);
		return builder.build();
	}
	public reg_server_online reg_server_online(String uuid){
		reg_server_online.Builder builder  = reg_server_online.newBuilder();
		builder.setUuid(uuid);
		return builder.build();
	}
	public unreg_server_online unreg_server_online(){
		unreg_server_online.Builder builder  = unreg_server_online.newBuilder();
		return builder.build();
	}
}
