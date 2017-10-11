package com.util;

import com.bean.HisAlarmDataBean_mid;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DataSort {
	private static  DataSort instance = null;

	public static DataSort getInstance() {
		if(instance == null)
			instance= new DataSort();
		return instance;
	}

	private int type;

	/**
	 *
	 * @param list
	 * @param ctype
	 * @return
	 */
//	public List<HisAlarmDataBean_mid> sort_alarm(List<HisAlarmDataBean_mid> list, int ctype) {
//		type  = ctype;
//		Collections.sort(list, new Comparator<HisAlarmDataBean_mid>() {
//			/**
//			 * @param lhs
//			 * @param rhs
//			 * @return an integer < 0 if lhs is less than rhs, 0 if they are
//			 *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
//			 */
//			@Override
//			public int compare(HisAlarmDataBean_mid lhs, HisAlarmDataBean_mid rhs) {
//				Date date1 = DateUtil.stringToDate(lhs.getStime());
//				Date date2 = DateUtil.stringToDate(rhs.getStime());
//				// 对日期字段进行升序，如果欲降序可采用after方法
//				if (type == 0) {
//					if (date1.before(date2)) {
//						return 1;
//					}
//					return -1;
//				} else {
//					if (date1.after(date2)) {
//						return 1;
//					}
//					return -1;
//				}
//			}
//		});
//		return list;
//	}



}
