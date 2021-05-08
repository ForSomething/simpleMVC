package sid.utils.db;


import org.apache.commons.beanutils.BeanUtils;
import sid.utils.CommonStringUtils;
import sid.utils.miscellaneous.CommonLogger;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class DBUtils {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
//    static final String DB_URL = "jdbc:mysql://localhost:3306/smgdy?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&useServerPrepStmts=true";
//    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/local-sit?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&useServerPrepStmts=true";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/local-cpall?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&useServerPrepStmts=true";

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

    public static void executeBySqlTemplate(String sqlTemplate, List<Object> parameters) throws Exception {
        executeBySqlTemplate(sqlTemplate,parameters,false);
    }

    public static void executeBySqlTemplate(String sqlTemplate, Object param, boolean autoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = getStatement(sqlTemplate,param,autoCommit);
            preparedStatement.execute();
        }finally {
            if(autoCommit){
                close(preparedStatement);
            }
        }
    }

    public static List<Map<String,Object>> executeQuerySql(String sqlTemplate, Object param) throws Exception {
        ResultSet resultSet = null;
        List<Map<String,Object>> resultList = new LinkedList<>();
        List<String> columnLables = null;
        PreparedStatement statement = null;
        try{
            statement = getStatement(sqlTemplate,param,false);
            resultSet = statement.executeQuery();
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
            close(statement);
        }
        return resultList;
    }

    private static PreparedStatement getStatement(String sqlTemplate,Object param,boolean autoCommit) throws Exception {
        CommonLogger.info("sql模板为：\n" + sqlTemplate);
        Map paramMap;
        if(param instanceof Map){
            paramMap = (Map)param;
        }else{
            paramMap = BeanUtils.describe(param);
        }
        //匹配被#{}框柱的内容，这些视为参数占位符
        String placeholdersRegex = "#\\{.+?}";
        String[] paramPlaceholders = CommonStringUtils.getAllMatchs(sqlTemplate,placeholdersRegex);
        List<String> paramList = new ArrayList<>(paramPlaceholders.length);
        for(int index = 0;index < paramPlaceholders.length;index++){
            String[] parts = paramPlaceholders[index].replaceAll("#\\{|}","").split(":");
            Object paramObj = paramMap.get(parts[0].trim());
            //参数为null的，直接不拼接这部分sql
            if(paramObj == null){
                sqlTemplate = sqlTemplate.replaceFirst(placeholdersRegex,"");
            }else{
                sqlTemplate = sqlTemplate.replaceFirst(placeholdersRegex,parts[1]);
                if(parts[1].contains("?")){
                    //如果有?占位符的，就添加参数
                    String paramStr = CommonStringUtils.toString(paramObj);// TODO 这里要进行处理，不能一味的tostring
                    paramList.add(paramStr);
                }
            }
        }
        CommonLogger.info("要执行的sql为：\n" + sqlTemplate);
        CommonLogger.info("参数为：\n" + paramList);
        Connection connection = getConnection(autoCommit);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate);
        for(int index = 0; index < paramList.size();index++){
            preparedStatement.setObject(index +1,paramList.get(index));
        }
        return preparedStatement;
    }

    private static synchronized Connection getConnection(boolean autoCommit) throws Exception {
        // TODO 无限制的获取连接可能会出现之前的连接被以外关闭的问题，这里限制连接数看看还会出现这种问题吗
        // TODO 现在发现不是连接数的问题，是县城获取到了被代码关闭的连接，这个可能是线程池的问题
        Connection conn = null;
        try{
//            if(connectionCount == 20){
//                DBUtils.class.wait();
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
            }else if (conn.isClosed()){
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
            }
            return conn;
        }finally {
            if(conn != null){
                conn.setClientInfo("commitType",autoCommit ? "autoCommit" : "cmdCommit");
                if(conn.getClientInfo("connectionID") == null){
                    //如果id为空，说明是新连接
                    connectionSerialNumber++;
                    connectionCount++;
                    conn.setClientInfo("connectionID",String.valueOf(connectionSerialNumber));
                }
            }
        }
    }

    private static synchronized void close(Statement statement) throws SQLException {
        if(statement != null){
            if(statement.getResultSet() != null){
                statement.getResultSet().close();
            }
            statement.getConnection().close();
            statement.close();
        }
        connectionCount--;

//        DBUtils.class.notify();
    }

//    @EventListen(event= {Events.ON_THREAD_COMPLETA,Events.ON_THREAD_ERROR})
//    private static void rollBackTransactions(Events event){
//        Connection conn = localConnection.get();
//        try{
//            if(conn == null){
//                return;
//            }
//            switch (event){
//                case ON_THREAD_COMPLETA:conn.commit();break;
//                case ON_THREAD_ERROR:conn.rollback();break;
//            }
//            closeConnection(conn);
//            //添加对线程池的支持
//            localConnection.set(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
