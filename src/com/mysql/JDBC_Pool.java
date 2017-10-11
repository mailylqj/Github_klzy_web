package com.mysql;

import com.util.const_value;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JDBC_Pool {
	private static JDBC_Pool instance = null;
	private DataSource dataSource = null;

	public static JDBC_Pool getInstance() {
		if (instance == null) {
			instance = new JDBC_Pool();
			instance.init();
		}
		return instance;
	}

	/**
	 * 初始化 数据池
	 */
	public void init() {
		PoolProperties pool = new PoolProperties(); // 池管理对象实例化
		//pool.setUrl("jdbc:mysql://"+"101.132.233.218"+":3306/db_klzy?characterEncoding=utf8&autoReconnect=true"); // 设置url

		pool.setUrl("jdbc:mysql://rm-uf641vi1s896vlis1o.mysql.rds.aliyuncs.com:3306/db_klzy?characterEncoding=utf8&autoReconnect=true"); // 设置url

		pool.setDriverClassName("com.mysql.jdbc.Driver");// 设置驱动
		pool.setUsername("root");// 设置用户名
		//pool.setPassword("klzy");// 设置密码
		pool.setPassword("Guo10526243210");
		pool.setJmxEnabled(true);// 设置java管理扩展是否可用 是否支持JMX

		//将testOnBorrow设置为false，而将“testWhileIdle”设置为true，再设置好testBetweenEvictionRunsMillis值（小于8小时）。那些被mysql关闭的连接就可以别清除出去，避免“8小时问题”。

		pool.setTestWhileIdle(true);// 设置空闲是否可用 空闲对象回收器开启状态
		pool.setTestOnBorrow(false);// 取回连接对链接有效性进行检查
		// 在borrow一个池实例时，是否提前进行池操作；如果为true，则得到的池实例均是可用的；
		pool.setTestOnReturn(false);// 测试连接是否有返回
		pool.setValidationQuery("SELECT 1");// 验证连接有效性的方式，这步不能省 测试连接语句
		pool.setValidationInterval(30000);// 验证间隔时间
		// timeBetweenEvictionRunsMillis 和 minEvictableIdleTimeMillis，
		// 他们两个配合，可以持续更新连接池中的连接对象，当timeBetweenEvictionRunsMillis 大于0时，
		// 每过timeBetweenEvictionRunsMillis时间，就会启动一个线程，校验连接池中闲置时间超过minEvictableIdleTimeMillis的连接对象。
		pool.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
		pool.setMinEvictableIdleTimeMillis(1000 * 60 * 60);

		pool.setInitialSize(10);// 初始化连接池时,创建连接个数
		pool.setMaxActive(1000);// 连接池最大并发容量
		pool.setMaxWait(10000);// 超时等待时间以毫秒为单位 无连接等待时间
		pool.setRemoveAbandonedTimeout(60);// 超时时间(以秒数为单位)
		pool.setMaxIdle(100);// 最大空闲连接数
		pool.setMinIdle(10);// 最小空闲连接数
		pool.setLogAbandoned(false); // 是否在自动回收超时连接的时候打印连接的超时错误
		pool.setRemoveAbandoned(true);// 是否自动回收超时连接
		pool.setFairQueue(true);

		// 設置jdbc拦截器
		// pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
		// + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

		dataSource = new DataSource();// TOMCAT 的数据源
		dataSource.setPoolProperties(pool); // 池与数据源绑定
		syncGetConnection();
	}

	/**
	 * 异步获取连接
	 * 
	 * @return
	 */
	public Connection syncGetConnection() {
		Connection conn = null;
		Future<Connection> future;
		try {
			while (dataSource == null) {
				Thread.sleep(100);
			}
			future = dataSource.getConnectionAsync();
			while (!future.isDone()) {
				Thread.sleep(100); // simulate work
			}
			conn = future.get(); // should return instantly

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupted();
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 释放连接
	 * 
	 * @param con
	 */
	public void freeConnection(Connection con) {
		try {
			if (null != con) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (null != con) {
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				con = null;
			}
		}
	}

	/**
	 * 释放Statement资源
	 * 
	 * @param statement
	 */
	public void freeStatement(Statement statement) {
		try {
			if (null != statement) {
				statement.close();
				statement = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (null != statement) {
				try {
					statement.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				statement = null;
			}
		}
	}

	/**
	 * 释放查询结果
	 * 
	 * @param
	 */
	public void freeResultSet(ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				rs = null;
			}
		}
	}

	/**
	 * 关闭hsqldb连接
	 * 
	 * @param con
	 * @throws SQLException
	 */
	/*
	 * public void freeHsqldb(Connection con) { try{ // hsqldb 需要关闭数据库
	 * if(drive.contains("hsqldb")){ Statement st = con.createStatement();
	 * st.executeUpdate("  SHUTDOWN  "); st.close(); st = null; }
	 * }catch(SQLException e){ e.printStackTrace(); } }
	 */

	/***
	 * 设置sql语句中的问号
	 * 
	 * @param ps
	 * @param param
	 * @throws SQLException
	 */
	private void setParam(PreparedStatement ps, List<Object> param) throws SQLException {
		int seq = 1;
		// 如果为null,则表示没有参数
		if (param != null) {
			for (Object elem : param) {
				// String 类型
				if (elem instanceof String) {
					ps.setString(seq, (String) elem);
					// Long 类型
				} else if (elem instanceof Long) {
					ps.setLong(seq, (Long) elem);
					// Double 类型
				} else if (elem instanceof Double) {
					ps.setDouble(seq, (Double) elem);
					// sql 日期类型
				} else if (elem instanceof Float) {
					ps.setFloat(seq, (Float) elem);
					// Integer 整形
				} else if (elem instanceof Integer) {
					ps.setInt(seq, (Integer) elem);
					// Timestamp 类型
				} else if (elem instanceof Timestamp) {
					ps.setTimestamp(seq, (Timestamp) elem);
				} else if (elem instanceof Date) {
					ps.setDate(seq, (Date) elem);
					// util 日期类型
				} else if (elem instanceof java.util.Date) {
					java.util.Date tmp = (java.util.Date) elem;
					ps.setDate(seq, new Date(tmp.getTime()));
					// Float 浮点类型
				} else {
					ps.setString(seq, (String) elem);
				}
				seq++;
			}
		}
	}

	/**
	 * 根据结果集中的数据类型 转换成相应String 类型
	 * 
	 * @throws SQLException
	 */
	private String parseResultSet(ResultSet rs, ResultSetMetaData rsmd, int type, int i) throws SQLException {
		String resStr = "";
		switch (type) {
		case Types.VARCHAR:
			resStr = rs.getString(i + 1);
			break;
		case Types.NUMERIC:
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			int scale = rsmd.getScale(i + 1);
			if (scale == 0) {
				resStr = nf.format(rs.getLong(i + 1));
			} else {
				nf.setMinimumFractionDigits(2);
				resStr = nf.format(rs.getDouble(i + 1));
			}
			break;
		case Types.CHAR:
			resStr = rs.getString(i + 1);
			break;
		case Types.FLOAT:
			resStr = String.valueOf(rs.getFloat(i + 1));
			break;
		case Types.DOUBLE:
			resStr = String.valueOf(rs.getDouble(i + 1));
			break;
		case Types.DATE:
			if (rs.getDate(i + 1) != null)
				resStr = DateUtil.getStringDate(rs.getDate(i + 1));
			break;
		case Types.TIMESTAMP:
			resStr = DateUtil.getStringTimestamp(rs.getTimestamp(i + 1));
			break;
		default:
			resStr = rs.getString(i + 1);
		}

		return resStr;
	}

	/**
	 * 返回数组格式 查询使用
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public List<String[]> queryForArray(String sql, List<Object> param) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		List<String[]> list = new ArrayList<String[]>();
		try {
			// 获取一个连接
			conn = this.syncGetConnection();
			// 执行预备语句
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			// 查询操作
			rs = ps.executeQuery();
			// 获取结果集列信息
			rsmd = rs.getMetaData();
			// 获取总列数
			rsmd = rs.getMetaData();
			int columCount = rsmd.getColumnCount();
			while (rs.next()) {
				String[] resStr = new String[columCount];
				for (int i = 0; i < columCount; i++) {
					int type = rsmd.getColumnType(i + 1);
					resStr[i] = parseResultSet(rs, rsmd, type, i);
				}
				list.add(resStr);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 释放资源
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}

		return list;
	}

	/**
	 * 仅有一个数据返回时 直接返回list
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public List<String> queryForList(String sql, List<Object> param) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		List<String> list = new ArrayList<String>();
		try {
			// 获取一个连接
			conn = this.syncGetConnection();
			// 执行预备语句
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			// 查询操作
			rs = ps.executeQuery();
			// 获取结果集列信息
			rsmd = rs.getMetaData();
			// 获取总列数
			rsmd = rs.getMetaData();
			int columCount = rsmd.getColumnCount();
			while (rs.next()) {
				int type = rsmd.getColumnType(1);
				list.add(parseResultSet(rs, rsmd, type, 0));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 释放资源
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}

		return list;
	}

	/**
	 * 修改，删除，添加使用
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public int execute(String sql, List<Object> param) {
		// 处理结果
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			// 获取一个连接
			conn = this.syncGetConnection();
			// 执行预备语句
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			// 执行操作
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接 释放资源
			this.freeStatement(ps);
			this.freeConnection(conn);
		}
		return result;
	}

	/**
	 * 用于事务控制，修改，删除，添加 使用
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public int execute(Connection conn, String sql, List<Object> param) {
		// 处理结果
		int result = 0;
		PreparedStatement ps = null;
		try {
			// 执行预备语句
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			// 执行操作
			result = ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.freeStatement(ps);
		}
		return result;
	}

	/**
	 * 查询表中的记录数，返回数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public int count(String sql, List<Object> param) {
		// 处理结果
		int result = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 获取一个连接
			conn = this.syncGetConnection();
			// 执行预备语句
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			// 查询操作
			rs = ps.executeQuery();
			rs.next();
			result = rs.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}

		return result;
	}

	/**
	 * 仅1条数据 返回 查询 返回 map 格式
	 * 
	 * @param sql
	 *            例如：select * from tb where name=? and pwd=?
	 * @param
	 *
	 * @return 和sql中的问号一一对应
	 */
	public Map<String, String> queryForOnceMap(String sql, List<Object> param) {

		Map<String, String> map = new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.syncGetConnection();
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int j = 0; j < cols; j++) {
					String colName = rsmd.getColumnLabel(j + 1);
					int type = rsmd.getColumnType(j + 1);
					String val = parseResultSet(rs, rsmd, type, j);
					map.put(colName.toLowerCase(), val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}
		return map;
	}

	/**
	 * 查询 返回 map 格式
	 * 
	 * @param sql
	 *            例如：select * from tb where name=? and pwd=?
	 * @param
	 *
	 * @return和sql中的问号一一对应
	 */
	public List<Map<String, String>> queryForMap(String sql, List<Object> param) {

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.syncGetConnection();
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int j = 0; j < cols; j++) {
					String colName = rsmd.getColumnLabel(j + 1);
					int type = rsmd.getColumnType(j + 1);
					String val = parseResultSet(rs, rsmd, type, j);
					map.put(colName.toLowerCase(), val);
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}
		return list;
	}

	/**
	 * 根据 sql 返回对象，sql 中查询的字段需要和bean中的一致，bean中属性必须为string类型
	 * 
	 * @param sql
	 *            eg:select id,name from tb_demo where name=? and pass =?
	 * @param param
	 *            参数列表，需要和sql 中的问号一一对应
	 * @param cls
	 *            保存数据库字段的javabean
	 * @return
	 */
	public <T> List<T> queryListObject(String sql, List<Object> param, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.syncGetConnection();
			ps = conn.prepareStatement(sql);
			// 设置sql 语句中的参数
			setParam(ps, param);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			// 获取所有的方法
			Method[] methods = cls.getDeclaredMethods();
			Map<String, Method> meth = new HashMap<String, Method>();
			for (int k = 0; k < methods.length; k++) {
				// 获取方法名 转换成小写
				String methodName = methods[k].getName().toLowerCase();
				// 以set开头的方法 存入map中
				if (methodName.startsWith("set")) {
					meth.put(methodName, methods[k]);
				}
			}
			while (rs.next()) {
				// 构造bean对象，
				T obj = cls.newInstance();
				for (int j = 0; j < cols; j++) {
					String colName = "set" + rsmd.getColumnLabel(j + 1);
					Method m_tem = meth.get(colName.toLowerCase());
					if (m_tem != null) {
						int type = rsmd.getColumnType(j + 1);
						String val = parseResultSet(rs, rsmd, type, j);
						if (val == null) {
							val = "";
						}
						m_tem.invoke(obj, val);
					}
				}
				list.add(obj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.freeResultSet(rs);
			this.freeStatement(ps);
			this.freeConnection(conn);
		}

		return list;
	}

	/**
	 * 根据 sql 返回单个对象，sql 中查询的字段需要和bean中的一致，不一致 需要使用 as 增加别名 id as userid
	 * 
	 * @param sql
	 *            eg:select id,name from tb_demo where name=? and pass =?
	 * @param param
	 *            参数列表，需要和sql 中的问号一一对应
	 * @param clazz
	 *            保存数据库字段的javabean
	 * @return
	 */
	public <T> T queryObject(String sql, List<Object> param, Class<T> clazz) {
		T obj = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			conn = this.syncGetConnection();
			pstmt = conn.prepareStatement(sql);
			this.setParam(pstmt, param);
			rs = pstmt.executeQuery();
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			Method[] methods = clazz.getDeclaredMethods();
			Map<String, Method> meth = new HashMap<String, Method>();
			for (int k = 0; k < methods.length; k++) {
				String methodName = methods[k].getName().toLowerCase();
				if (methodName.startsWith("set")) {
					meth.put(methodName, methods[k]);
				}
			}
			if (rs.next()) {
				obj = clazz.newInstance();
				for (int j = 0; j < cols; j++) {
					String colName = "set" + rsmd.getColumnLabel(j + 1);
					Method m_tem = meth.get(colName.toLowerCase());
					if (m_tem != null) {
						int type = rsmd.getColumnType(j + 1);
						String val = parseResultSet(rs, rsmd, type, j);
						if (val != null) {
							m_tem.invoke(obj, val);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.freeResultSet(rs);
			this.freeStatement(pstmt);
			this.freeConnection(conn);
		}

		return obj;
	}
}
