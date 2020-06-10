package com.fiberhome.ms.auth.init.cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 测试H2 dataBase
 *
 * @author ww
 * @create 2020-06-08 10:31
 **/
public class H2ConnTest1 {

    //数据库连接URL，通过使用TCP/IP的服务器模式（远程连接）
    private static final String JDBC_URL = "jdbc:h2:mem:test";
    //连接数据库时使用的用户名
    private static final String USER = "sa";
    //连接数据库时使用的密码
    private static final String PASSWORD = "123";
    //连接H2数据库时使用的驱动类，org.h2.Driver这个类是由H2数据库自己提供的，在H2数据库的jar包中可以找到
    private static final String DRIVER_CLASS = "org.h2.Driver";


    public static void testH2() throws ClassNotFoundException, SQLException {
        // 加载H2数据库驱动
        Class.forName(DRIVER_CLASS);
        // 根据连接URL，用户名，密码获取数据库连接
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

        //如果存在USER_INFO表就先删除USER_INFO表
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TEST (ID BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(32) NOT NULL, sex VARCHAR(32) NOT NULL, PRIMARY KEY(ID) )");

        //创建TEST表
        // stmt.execute("DROP IF EXISTS TABLE TEST");
        //新增
        stmt.executeUpdate("INSERT INTO TEST VALUES(3,'大日如来','男')");
        stmt.executeUpdate("INSERT INTO TEST VALUES(4,'青龙','男')");

//        //删除
//        stmt.executeUpdate("DELETE FROM TEST WHERE name='大日如来'");
//        //修改
//        stmt.executeUpdate("UPDATE TEST SET name='孤傲苍狼' WHERE name='青龙'");
        //查询
        ResultSet rs = stmt.executeQuery("SELECT * FROM TEST");
        //遍历结果集
        while (rs.next()) {
            System.out.println(rs.getString("id") + "," + rs.getString("name") + "," + rs.getString("sex"));
        }
        //释放资源
        stmt.close();
        //关闭连接
        conn.close();
    }
}
