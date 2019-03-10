package toolroom.dbutil;

import annotation.EventListen;
import common.constvaslue.Events;
import toolroom.FileUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class MysqlUtils {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/smgdy?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&useServerPrepStmts=true";

    static final String USER = "root";
    static final String PASS = "123456";

    private static ThreadLocal<Connection> localConnection = new ThreadLocal<>();

    private static int connectionCount = 0;

    private static int connectionSerialNumber = 10000;

    private Set<Integer> connectSet = new HashSet<>();

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ExecuteBySqlTemplate(String sqlTemplate,List<Object> parameters,boolean autoCommit) throws Exception {
        Connection connection = getConnection(autoCommit);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate);
        try{
            for(int index = 0; index < parameters.size();index++){
                Object value = parameters.get(index);
                preparedStatement.setObject(index + 1,value.toString());// TODO 这里要进行处理，不能一味的tostring
            }
            preparedStatement.execute();
        }finally {
            preparedStatement.close();
            if(autoCommit){
                closeConnection(connection);
            }
        }
    }

    public static List<Map<String,Object>> ExecuteQuerySql(String sql,List<Object> parameters) throws Exception {
        Connection connection = getConnection(true);
        ResultSet resultSet = null;
        List<Map<String,Object>> resultList = new LinkedList<>();
        List<String> columnLables = null;
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        if(parameters != null && parameters.size() > 0){
//            for(int index = 0;index < parameters.size();index++){
//                preparedStatement.setString(index+1,parameters.get(index).toString());
//            }
//        }
        Statement statement = connection.createStatement();
        try{
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                if(columnLables == null){
                    columnLables = new LinkedList<>();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for(int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
                        columnLables.add(metaData.getColumnName(columnIndex));
                    }
                }
                Map<String,Object> rowMap = new HashMap<>();
                resultList.add(rowMap);
                for(String columnLable : columnLables){
                    rowMap.put(columnLable,resultSet.getString(columnLable));
                }
            }
        }finally {
//            preparedStatement.close();
            resultSet.close();
            closeConnection(connection);
        }
        return resultList;
    }

    private static synchronized Connection getConnection(boolean autoCommit) throws Exception {
        // TODO 无限制的获取连接可能会出现之前的连接被以外关闭的问题，这里限制连接数看看还会出现这种问题吗
        // TODO 现在发现不是连接数的问题，是县城获取到了被代码关闭的连接，这个可能是线程池的问题
        Connection conn = null;
        try{
//            if(connectionCount == 20){
//                MysqlUtils.class.wait();
//            }
            if(autoCommit){
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                return conn;
            }
            conn = localConnection.get();
            if(conn == null){
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
                conn.setAutoCommit(false);
                localConnection.set(conn);
            }
            return conn;
        }finally {
            if(conn.getClientInfo("connectionID") == null){
                //如果id为空，说明是新连接
                connectionSerialNumber++;
                connectionCount++;
                conn.setClientInfo("connectionID",String.valueOf(connectionSerialNumber));
            }
            conn.setClientInfo("commitType",autoCommit ? "autoCommit" : "cmdCommit");
            System.out.println("one connection has been got,id is " + conn.getClientInfo("connectionID") +
                    " and it's " + conn.getClientInfo("commitType") +
                    " ,current connection count is " + connectionCount);
        }
    }

    private static synchronized void closeConnection(Connection connection) throws SQLException {
        connection.close();
        connectionCount--;

        System.out.println("current connection count lose 1 ," + "this one id is " + connection.getClientInfo("connectionID") +
                " and it's " + connection.getClientInfo("commitType") + " now is " + connectionCount);
//        MysqlUtils.class.notify();
    }

    @EventListen(event= {Events.ON_THREAD_COMPLETA,Events.ON_THREAD_ERROR})
    private static void rollBackTransactions(Events event){
        Connection conn = localConnection.get();
        try{
            if(conn == null){
                return;
            }
            switch (event){
                case ON_THREAD_COMPLETA:conn.commit();break;
                case ON_THREAD_ERROR:conn.rollback();break;
            }
            closeConnection(conn);
            //添加对线程池的支持
            localConnection.set(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
