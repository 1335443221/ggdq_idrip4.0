package com.sl.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Component
//@PropertySource(value = "classpath:redis.properties",ignoreResourceNotFound = false)
@ConfigurationProperties(prefix = "redis")
public class RedisConfig extends RedisConfigBase {
    public static String host;
    public static int port;
    public static int database;
    public static int database0;
    public static int database6;
    public static String password;
    public static int timeout;
    public static int poolMaxTotal;
    public static int poolMaxIdle;
    public static int poolMinIdle;
    public static int poolMaxWait;

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getDatabase() {
        return database;
    }

    @Override
    public void setDatabase(int database) {
        this.database = database;
    }

    public int getDatabase0() {
        return database0;
    }

    public void setDatabase0(int database0) {
        this.database0 = database0;
    }

    public int getDatabase6() {
        return database6;
    }

    public void setDatabase6(int database6) {
        this.database6 = database6;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    @Override
    public void setPoolMaxTotal(int poolMaxTotal) {
        this.poolMaxTotal = poolMaxTotal;
    }

    @Override
    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    @Override
    public void setPoolMaxIdle(int poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public int getPoolMinIdle() {
        return poolMinIdle;
    }

    public void setPoolMinIdle(int poolMinIdle) {
        this.poolMinIdle = poolMinIdle;
    }

    @Override
    public int getPoolMaxWait() {
        return poolMaxWait;
    }

    @Override
    public void setPoolMaxWait(int poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }

    @Override
    public String toString() {
        return "RedisConfig [host=" + host + ", port=" + port + ", database=" + database + ", password=" + password
                + ", timeout=" + timeout + ", poolMaxTotal=" + poolMaxTotal + ", poolMaxIdle=" + poolMaxIdle
                + ", poolMaxWait=" + poolMaxWait + "]";
    }

}
