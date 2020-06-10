package com.fiberhome.ms.auth.init.h2util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.h2.Driver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 鉴权h2本地缓存设置
 *
 * @author ww
 * @create 2020-06-08 11:08
 **/
@Component
@Log4j2
public class AuthDataH2DbHelper implements DisposableBean {

    private final HikariDataSource hikariDataSource;
    private final QueryRunner queryRunner;

    /**
     *     数据库连接URL，通过使用TCP/IP的服务器模式（远程连接）
     */
    private static final String JDBC_URL = "jdbc:h2:mem:test";
    /**
     *     连接数据库时使用的用户名
     */
    private static final String USER = "auth";
    /**
     *     连接数据库时使用的密码
     */
    private static final String PASSWORD = "123";

    @Autowired
    public AuthDataH2DbHelper() {
        HikariConfig hikariConfig = new HikariConfig();
        //连接H2数据库时使用的驱动类，org.h2.Driver这个类是由H2数据库自己提供的，在H2数据库的jar包中可以找到
        hikariConfig.setDriverClassName(Driver.class.getName());
        hikariConfig.setJdbcUrl(JDBC_URL);
        log.debug("Init H2 DATABASE at {}", JDBC_URL);
        hikariConfig.setUsername(USER);
        log.debug("Init H2 USER at {}", USER);
        hikariConfig.setPassword(PASSWORD);
        log.debug("Init H2 PASSWORD at {}", PASSWORD);
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setIdleTimeout(10000);
        hikariConfig.setPoolName("auth_h2_pool");
        this.hikariDataSource = new HikariDataSource(hikariConfig);
        this.queryRunner = new QueryRunner(this.hikariDataSource);

        log.info("Init H2 DATABASE finished.");
    }

    public QueryRunner queryRunner() {
        return this.queryRunner;
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    public int update(String sql, Object... params) {
        try {
            return this.queryRunner.update(sql, params);
        } catch (SQLException var4) {
            log.error("update error", var4);
            return 0;
        }
    }

    public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) {
        try {
            return this.queryRunner.query(sql, rsh, params);
        } catch (SQLException var5) {
            log.error("query error", var5);
            return null;
        }
    }

    public <T> T query(String sql, ScalarHandler<T> scalarHandler, Object... params) {
        try {
            return this.queryRunner.query(sql, scalarHandler, params);
        } catch (SQLException var5) {
            log.error("query error", var5);
            return null;
        }
    }

    @Override
    public void destroy() throws Exception {
        this.hikariDataSource.close();
        log.info("log hikariDataSource close.");
    }
}
