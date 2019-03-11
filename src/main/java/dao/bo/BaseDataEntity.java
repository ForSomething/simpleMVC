package dao.bo;

import annotation.Table;
import org.apache.commons.beanutils.BeanUtils;
import toolroom.StringUtils;
import toolroom.dbutil.MysqlUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaseDataEntity {
    private boolean autoCommit = false;

    public BaseDataEntity setAutoCommit(boolean autoCommit){
        this.autoCommit = autoCommit;
        return this;
    }

    public void insert() throws Exception{
        Class entityClass = this.getClass();
        String tableName;
        if(!entityClass.isAnnotationPresent(Table.class) || StringUtils.IsNullOrWihtespace((tableName = ((Table)entityClass.getAnnotation(Table.class)).table()))){
            //如果类未被Table注解标注，或者注解的table值是空字符串，则用类名作为表名,否则用table值作为表名
            tableName = entityClass.getSimpleName();
        }
        StringBuilder fieldsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        List<Object> parameters = new LinkedList<>();
        Field[] fields = entityClass.getDeclaredFields();
        String columnName;
        String fieldName;
        Object value;
        for(int index = 0; index < fields.length;index++){
            Field field = fields[index];
            field.setAccessible(true);
            if(!field.isAnnotationPresent(Table.class) || StringUtils.IsNullOrWihtespace((columnName = field.getAnnotation(Table.class).column()))){
                //如果变量未被Table注解标注，或者注解的column值是空字符串，则用变量名作为列名,否则用column值作为列名
                columnName = field.getName();
            }
            //调用getter获取变量的值
            try{
                fieldName = field.getName();
                fieldName = fieldName.substring(0,0).toUpperCase() + fieldName.substring(1,fieldName.length() - 1);
                value = entityClass.getDeclaredMethod("get" + fieldName).invoke(this);
            }catch (Exception e){
                try{
                    value = field.get(this);
                }catch (Exception e2){
                    value = null;
                }
            }
            parameters.add(value);
            if(index > 0){
                fieldsBuilder.append(",");
                valuesBuilder.append(",");
            }
            fieldsBuilder.append(columnName);
            valuesBuilder.append("?");
        }
        MysqlUtils.ExecuteBySqlTemplate(String.format("insert into %s (%s) values (%s)",tableName,fieldsBuilder.toString(),valuesBuilder.toString()),parameters,autoCommit);
    }

    public void delete(){

    }

    public void update(){

    }

    protected static <T extends BaseDataEntity> T selectFirst(Object cond,Class<T> entityClass) throws Exception {
        List<T> resultList = select(cond,entityClass);
        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
    }

    protected static <T extends BaseDataEntity> List<T> select(Object cond,Class<T> entityClass) throws Exception{
        Map condMap;
        List<Object> parameters = new LinkedList<>();
        String tableName = entityClass.getSimpleName();
        String sortColumns = StringUtils.emptyString;
        if(entityClass.isAnnotationPresent(Table.class)){
            //如果类被Table注解标注
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            if(!StringUtils.IsNullOrWihtespace(tableAnnotation.table())){
                //注解指明了表名，则用注解中的表名
                tableName = tableAnnotation.table();
            }
            //排序列
            sortColumns = tableAnnotation.sortColumns() == null ? "" : tableAnnotation.sortColumns().trim();
        }
        StringBuilder sqlBilder = new StringBuilder("select * from ").append(tableName);
        if(cond != null){
            if(cond instanceof BaseDataEntity){
                condMap = BeanUtils.describe(cond);
            }else if(cond instanceof Map){
                condMap = (Map)cond;
            }else{
                throw new Exception("不支持的条件对象类型");
            }
            int condIndex = 0;
            String columnName;
            String fieldName;
            Object value;
            Field[] fields = entityClass.getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                fieldName = field.getName();
                if(!field.isAnnotationPresent(Table.class) || StringUtils.IsNullOrWihtespace((columnName = field.getAnnotation(Table.class).column()))){
                    //如果变量未被Table注解标注，或者注解的column值是空字符串，则用变量名作为列名,否则用column值作为列名
                    columnName = field.getName();
                }
                if((value = condMap.get(fieldName)) != null){
                    parameters.add(value);
                    if(condIndex != 0){
                        sqlBilder.append(" and ");
                    }else{
                        sqlBilder.append(" where ");
                    }
//                    sqlBilder.append(columnName).append(" = ? ");
                    sqlBilder.append(columnName).append(" = '").append(value.toString()).append("' ");
                    condIndex++;
                }
            }
            if(!StringUtils.IsNullOrWihtespace(sortColumns)){
                sqlBilder.append(" order by ").append(sortColumns);//拼接上排序字符串
            }
        }
        List<Map<String,Object>> resultList = MysqlUtils.ExecuteQuerySql(sqlBilder.toString(),parameters);
        List<T> resultBeanList = new LinkedList<>();
        if(resultList.size() > 0){
            for(int resultIndex = 0;resultIndex < resultList.size();resultIndex++){
                T resultBean = entityClass.newInstance();
                BeanUtils.populate(resultBean,resultList.get(resultIndex));
                resultBeanList.add(resultBean);
            }
        }
        return resultBeanList;
    }
}
