package com.listener;

import com.mina.BalanceMinaClient;
import com.mina.RealMinaClient;
import com.mysql.JDBC_Pool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        BalanceMinaClient.getInstance().start();
        JDBC_Pool.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //RealMinaClient.getInstance().closeConnection();
    }
}
