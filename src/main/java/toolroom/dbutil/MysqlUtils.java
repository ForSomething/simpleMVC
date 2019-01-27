package toolroom.dbutil;

import java.sql.*;
import java.util.*;

public class MysqlUtils {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/smgdy?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false&useServerPrepStmts=true";

    static final String USER = "root";
    static final String PASS = "123456";


    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ExecuteBatchBySqlTemplate(String sqlTemplate,List<List<Object>> parametersList) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
        PreparedStatement preparedStatement = connection.prepareStatement(sqlTemplate);
        try{
            for(List<Object> parameters : parametersList ){
                preparedStatement.clearParameters();
                for(int index = 0; index < parameters.size();index++){
                    Object value = parameters.get(index);
                    preparedStatement.setString(index + 1,value == null ? null :value.toString());
                }
                preparedStatement.execute();
            }
        }finally {
            connection.close();
            preparedStatement.close();
        }
    }

    public static void ExecuteBySqlTemplate(String sqlTemplate,List<Object> parameters) throws SQLException {
        ArrayList<List<Object>> parametersList = new ArrayList<>();
        parametersList.add(parameters);
        ExecuteBatchBySqlTemplate(sqlTemplate,parametersList);
    }

    public static List<Map<String,Object>> ExecuteQuerySql(String sql,List<Object> parameters) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
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
            connection.close();
//            preparedStatement.close();
            resultSet.close();
        }
        return resultList;
    }

    public static void test() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement("insert into testtable (numCol) values (0)");
        preparedStatement.execute();
        connection.commit();
        preparedStatement.execute("insert into testtable (numCol) values (2)");
        connection.commit();
    }
}
