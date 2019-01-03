package com.fislike.spring.boot.schedule.client.config;

import com.fislike.spring.boot.schedule.client.enums.MatchUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName ScheduleClientProperties
 * @Description
 * @date 2018年12月28日 11:14
 **/
@ConfigurationProperties("schedule.client")
public class ScheduleClientProperties {

    private int serverPort;

    private String serverHost;

    /** e.g 127.0.0.1:8080,127.0.0.1:8081*/
    private String serverAddress;

    /** scaner basePackage muilt package use ',' split */
    private String basePackage;

    private MatchUnit matchUnit = MatchUnit.SECOND;

    /** time between heartbeat packets to the server*/
    private long healthBeat = 60l;

    /** disconnection reconnection time interval*/
    private long retryInterval = 60l;

    public int getServerPort() {
        return serverPort;
    }

    public ScheduleClientProperties setServerPort(int serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public ScheduleClientProperties setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public ScheduleClientProperties setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public MatchUnit getMatchUnit() {
        return matchUnit;
    }

    public ScheduleClientProperties setMatchUnit(MatchUnit matchUnit) {
        this.matchUnit = matchUnit;
        return this;
    }

    public long getHealthBeat() {
        return healthBeat;
    }

    public ScheduleClientProperties setHealthBeat(long healthBeat) {
        this.healthBeat = healthBeat;
        return this;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public ScheduleClientProperties setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    public String getServerHost() {
        return serverHost;
    }

    public ScheduleClientProperties setServerHost(String serverHost) {
        this.serverHost = serverHost;
        return this;
    }
}
