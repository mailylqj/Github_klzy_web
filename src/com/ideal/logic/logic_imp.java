package com.ideal.logic;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.ideal.logic.alarm_data.read_alarm_data_response;
import com.ideal.logic.alarm_data.read_alarm_req;
import com.ideal.logic.control_data.control_req;
import com.ideal.logic.control_data.control_response;
import com.ideal.logic.control_data.read_control_data_response;
import com.ideal.logic.control_data.read_control_req;
import com.ideal.logic.file_data.update_file_request;
import com.ideal.logic.file_data.update_file_result;
import com.ideal.logic.hmi_data.hmi_control_req;
import com.ideal.logic.hmi_data.hmi_control_response;
import com.ideal.logic.lastest_data.*;
import com.ideal.logic.login_data.change_pwd_request;
import com.ideal.logic.login_data.change_pwd_result;
import com.ideal.logic.login_data.login_req;
import com.ideal.logic.login_data.login_response;
import com.ideal.logic.modbus_data.modbus_req;
import com.ideal.logic.modbus_data.modbus_response;
import com.ideal.logic.modbus_data.read_data_req;
import com.ideal.logic.modbus_data.read_data_response;
import com.ideal.logic.online_info.update_online_status_request;
import com.ideal.logic.online_info.update_online_status_result;


public class logic_imp implements logic.logic_server.Interface {
	

	public logic_imp() {
	}

	@Override
	public void modbusControl(RpcController controller, modbus_req request, RpcCallback<modbus_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modbusRead(RpcController controller, read_data_req request, RpcCallback<read_data_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userLogin(RpcController controller, login_req request, RpcCallback<login_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hmiControl(RpcController controller, hmi_control_req request, RpcCallback<hmi_control_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commandControl(RpcController controller, control_req request, RpcCallback<control_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOnlineStatus(RpcController controller, update_online_status_request request, RpcCallback<update_online_status_result> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLastestData(RpcController controller, update_lastest_data_request request, RpcCallback<update_lastest_data_result> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void regRefreshLastestData(RpcController controller, reg_lastest_data request, RpcCallback<reg_lastest_data_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregRefreshLastestData(RpcController controller, unreg_lastest_data request, RpcCallback<unreg_lastest_data_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFile(RpcController controller, update_file_request request, RpcCallback<update_file_result> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePassword(RpcController controller, change_pwd_request request, RpcCallback<change_pwd_result> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controlDataRead(RpcController controller, read_control_req request, RpcCallback<read_control_data_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alarmDataRead(RpcController controller, read_alarm_req request, RpcCallback<read_alarm_data_response> done) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void offlineRead(RpcController controller, offline_info.update_offline_status_request request, RpcCallback<offline_info.update_offline_status_result> done) {

	}

	@Override
	public void deviceRead(RpcController controller, offline_info.device_info_request request, RpcCallback<offline_info.device_info_response> done) {

	}

	@Override
	public void regServerLastestdata(RpcController controller, server_lastestdata.reg_server_lastestdata request, RpcCallback<server_lastestdata.server_lastestdata_response> done) {

	}

	@Override
	public void regServerOnline(RpcController controller, server_online.reg_server_online request, RpcCallback<server_online.server_online_response> done) {

	}

	@Override
	public void unregServerLastestdata(RpcController controller, server_lastestdata.unreg_server_lastestdata request, RpcCallback<server_lastestdata.unreg_lastestdata_response> done) {

	}

	@Override
	public void unregServerOnline(RpcController controller, server_online.unreg_server_online request, RpcCallback<server_online.unreg_online_response> done) {

	}

}
