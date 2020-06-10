package com.fiberhome.ms.auth.init.h2util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.ms.common.util.Util;
import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 本地缓存h2
 *
 * @author ww
 * @create 2020-06-08 11:05
 **/
@Component
@Log4j2
public class AuthLocalDataH2Helper {

    private final AuthDataH2DbHelper authDataH2DbHelper;

    @Autowired
    public AuthLocalDataH2Helper(AuthDataH2DbHelper authDataH2DbHelper) {
        this.authDataH2DbHelper = authDataH2DbHelper;
    }

    public void init() {
        this.authDataH2DbHelper.update("CREATE TABLE IF NOT EXISTS AUTH_LOCAL_CACHE (ID BIGINT NOT NULL, DATA VARCHAR(255) NOT NULL, PRIMARY KEY(ID) )", new Object[0]);
        log.info("authLocalCacheH2 log table finish (H2 DATABASE)");
    }


    public void saveAuthDataLocalCache(JSONObject jsonObject) {
        String sql = "INSERT INTO AUTH_LOCAL_CACHE (ID, DATA) VALUES(?,?)";
        try {
            this.authDataH2DbHelper.queryRunner().update(sql, jsonObject.getString("dataId"),
                jsonObject.toJSONString());
        } catch (Exception e) {
            log.error("insert auth_data local cache error:" + e.getMessage());
        }
        finally {

        }
    }

    public JSONObject getAuthDataById(String dataId) throws SQLException {
        String sql = "SELECT * FROM AUTH_LOCAL_CACHE WHERE ID = ?";
        return this.authDataH2DbHelper.queryRunner().query(sql, (rs) -> {
            JSONObject jsonObject;
            if (Util.isEmpty(rs)) {
                return null;
            } else {
                jsonObject = JSON.parseObject(rs.toString());
            }

            return jsonObject;
        }, new Object[]{dataId});
    }


    public List<JSONObject> getAuthDataByIds(String... dataIds) throws SQLException {
        Preconditions.checkNotNull(dataIds, "数据id为null时无需查询");
        StringBuilder sql = new StringBuilder("SELECT * FROM AUTH_LOCAL_CACHE WHERE ID in (");
        for (String dataId : dataIds) {
            sql.append("?,");
        }
        String sql1 = StringUtils.removeEnd(sql.toString(), ",") + ")";
        return this.authDataH2DbHelper.queryRunner().query(sql1, (rs) -> {
                ArrayList<JSONObject> list = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
                while(rs.next()) {
                    Object data = rs.getObject("data");
                    if (Util.isNotEmpty(data)) {
                        jsonObject = JSON.parseObject(data.toString());
                    }

                    list.add(jsonObject);
                }
                return list;
            }, dataIds);
    }


    public void deleteAuthCache(String dataId) throws SQLException {
        String sql = "DELETE FROM AUTH_LOCAL_CACHE WHERE ID = ? ";
        this.authDataH2DbHelper.queryRunner().update(sql, new Object[]{dataId});
    }
}
