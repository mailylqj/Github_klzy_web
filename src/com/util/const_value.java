
package com.util;


public class const_value
{
	public static  final String key = "klzy83203826";
	//	
	public static final int DTU1_HEAD = 0x44545531;
	public static final int DTU2_HEAD = 0x44545532;
	public static final int DTU9_HEAD = 0x44545539;
	//
	public static final int RIP0_HEAD = 0x52495030;
	public static final int RIP1_HEAD = 0x52495031;
	public static final int REG_HEAD = 0x52454730;
	public static final int REGC_HEAD = 0x52454743;
	public static final int UREG_HEAD = 0x55524547;
	public static final int HERT_HEAD = 0x48455254;
	
	public static final int HMIS_HEAD = 0x484D4953;
	public static final int MODS_HEAD = 0x4D4F4453;
	
	public static final int HMIC_HEAD = 0x484D4943;
	public static final int MODC_HEAD = 0x4D4F4443;
	
	//
	public static final int TRANS0_HEAD = 0x54524E30;
	public static final int CONNECT_HEAD = 0x434F4E54;
	public static final int RET_CONNECT_HEAD = 0x52454354;
	public static final int RET0_HEAD = 0x52455430;
	public static final int GOIN_HEAD = 0x474F494E;
	
	//
	public static final int PROT_HEAD = 0x50524F54;
	//
	public static final int DTU_TYPE = 1;
	public static final int HMI_TYPE = 2;
	

	public static final long DROP_PACKET_TIME = 30 * 1000;
	//
	public static final int FRONT_SESSION_OPEN = 1;
	public static final int FRONT_SESSION_CLOSE = 2;
	public static final int FRONT_SESSION_REG = 3;
	public static final int FRONT_SESSION_UNREG = 4;
	// method_type
	public static final int READ = 1; // 读一个Item或者一个Item的子项
	public static final int WRITE = 2; // 写一个Item或者一个Item的子项
	public static final int DELETE = 3; // 删除一个Item或者一个Item的子项

	public static final  int PORT = 55060;
	//----singapore   US East----
//	public static final String Host = "52.77.111.246";

	public static final String Host = "101.132.136.210";
	//--------local test------
//	public static final String Host = "192.168.8.101";

//	public static final String SqlAdd = "192.168.8.101";

	public static final String SqlAdd = "rm-uf641vi1s896vlis1o.mysql.rds.aliyuncs.com";


}
