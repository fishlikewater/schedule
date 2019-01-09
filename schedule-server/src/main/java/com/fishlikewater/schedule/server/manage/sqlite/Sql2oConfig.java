package com.fishlikewater.schedule.server.manage.sqlite;

import com.fishlikewater.schedule.common.entity.TaskDetail;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Sql2oConfig
 * @Description
 * @date 2019年01月09日 11:30
 **/
@Slf4j
public class Sql2oConfig {

    public static Sql2o sql2o;

    public static Sql2o open(String url, String user, String password) {
        sql2o = new Sql2o(url, user, password);
        log.info("⬢ sqlite initializing");
        init();
        return sql2o;
    }

    public static void init(){
        Connection connection = sql2o.open();
        String sql = "select count(*)  from sqlite_master where type='table' and name = 'task_list'";
        Query query = connection.createQuery(sql);
        Integer integer = query.executeScalar(Integer.class);
        if(integer == 0){
            String createSql = "create table task_list\n" +
                    "(\n" +
                    "  appName      varchar,\n" +
                    "  serialNumber int,\n" +
                    "  corn         varchar,\n" +
                    "  descriple    varchar,\n" +
                    "  actionAdress varchar,\n" +
                    "  isUse        tinyint\n" +
                    ")";
            connection.createQuery(createSql).executeUpdate();
        }
        connection.close();
    }

    /**
     * 获取所有已存储的数据
     * @return
     */
    public static List<TaskDetail> getTaskWithDB(){
        Connection connection = sql2o.open();
        List<TaskDetail> list = connection.createQuery("select * from task_list")
                .addColumnMapping("descriple" ,"desc")
                .executeAndFetch(TaskDetail.class);
        connection.close();
        return list;
    }

    /**
     * 获取已存储的数据
     * @param appName
     * @return
     */
    public static List<TaskDetail> getTaskWithDB(String appName){
        Connection connection = sql2o.open();
        List<TaskDetail> list = connection.createQuery("select * from task_list where appName=:appName")
                .addParameter("appName", appName)
                .addColumnMapping("descriple" ,"desc")
                .executeAndFetch(TaskDetail.class);
        connection.close();
        return list;
    }

    /**
     * 重置存储数据
     * @param list
     */
    public static void resetTaskWithDB(List<TaskDetail> list, String appName){
        Connection connection = sql2o.open();
        String delSql = "delete from task_list where appName=:appName";
        Query del = connection.createQuery(delSql);
        del.addParameter("appName", appName);
        del.executeUpdate();
        String sql = "insert into task_list(appName, serialNumber, corn, descriple, actionAdress, isUse) values(:appName, :serialNumber, :corn, :descriple, :actionAdress, :isUse)";
        Query query = connection.createQuery(sql);
        for (TaskDetail taskDetail : list) {
            query.addParameter("isUse", taskDetail.isUse()?1:0);
            query.addParameter("descriple", taskDetail.getDesc());
            query.bind(taskDetail).addToBatch();
        }
        query.executeBatch();
        connection.close();
    }

    /**
     * 更新某个task
     * @param taskDetail
     */
    public static void updateTask(TaskDetail taskDetail){
        Connection connection = sql2o.open();
        String upSql = "update task_list set corn=:corn, actionAdress=:actionAdress, isUse=:isUse where appName=:appName and serialNumber=:serialNumber";
        Query query = connection.createQuery(upSql);
        query.addParameter("isUse", taskDetail.isUse()?1:0);
        query.bind(taskDetail).executeUpdate();
        connection.close();
    }
}
