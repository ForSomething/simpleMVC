package sid.bo;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import sid.bo.annotation.Persistence;
import sid.utils.CommonStringUtils;
import sid.utils.db.DBUtils;

import java.lang.reflect.Field;
import java.util.*;

public class BaseDataEntity {
    public void insert() throws Exception{
        Class entityClass = this.getClass();
        String tableName;
        if(!entityClass.isAnnotationPresent(Persistence.class) || StringUtils.isEmpty((tableName = ((Persistence)entityClass.getAnnotation(Persistence.class)).table()))){
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
            if(!field.isAnnotationPresent(Persistence.class) || StringUtils.isEmpty((columnName = field.getAnnotation(Persistence.class).column()))){
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
        DBUtils.executeBySqlTemplate(String.format("insert into %s (%s) values (%s)",tableName,fieldsBuilder.toString(),valuesBuilder.toString()),parameters,false);
    }

    public void delete(){

    }

    public void update(){

    }

    protected static <T extends BaseDataEntity> T selectFirst(Object cond, Class<T> entityClass) throws Exception {
        List<T> resultList = select(cond,entityClass);
        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
    }

    protected static <T extends BaseDataEntity> List<T> select(Object cond, Class<T> entityClass) throws Exception{
        String tableName = entityClass.getSimpleName();
        String sortColumns = "";
        Persistence.ColumnNameRule columnNameRule = Persistence.ColumnNameRule.EQUAL;
        if(entityClass.isAnnotationPresent(Persistence.class)){
            //如果类被Table注解标注
            Persistence persistenceAnnotation = entityClass.getAnnotation(Persistence.class);
            columnNameRule = persistenceAnnotation.columnNameRule();
            if(!StringUtils.isEmpty(persistenceAnnotation.table())){
                //注解指明了表名，则用注解中的表名
                tableName = persistenceAnnotation.table();
            }
            //排序列
            sortColumns = persistenceAnnotation.sortColumns() == null ? "" : persistenceAnnotation.sortColumns().trim();
        }
        StringBuilder sqlBilder = new StringBuilder("select * from ").append(tableName).append(" where 1=1 ");
        String fieldName;
        Field[] fields = entityClass.getDeclaredFields();
        Map<String,String> c2fDic = new HashMap<>(fields.length);
        for(Field field : fields){
            StringBuffer columnName = new StringBuffer();
            field.setAccessible(true);
            fieldName = field.getName();
            Optional.ofNullable(field.getAnnotation(Persistence.class)).ifPresent(e->columnName.append(CommonStringUtils.toString(e.column())));
            if(columnName.length() == 0){
                //如果不能从注解中获取到列名，则用变量名来确定列名
                switch (columnNameRule){
                    case UNDERLINE_SEPARATOR:
                        //找出所有的大写字母，在前面加上下划线，所有的小写字母转成大写
                        StringBuilder columnNameBuilder = new StringBuilder();
                        for(char e : fieldName.toCharArray()){
                            //第一个字母如果是大写的，就不加下划线了
                            if(Character.isUpperCase(e)){
                                if(columnNameBuilder.length() != 0){
                                    columnNameBuilder.append("_");
                                }
                                columnNameBuilder.append(e);
                            }else{
                                columnNameBuilder.append(Character.toUpperCase(e));
                            }
                        }
                        columnName.append(columnNameBuilder.toString());
                        break;
                    default:
                        columnName.append(field.getName());
                }
            }
            c2fDic.put(columnName.toString(),fieldName);
            sqlBilder.append(String.format(" #{%s:and %s = ?} ",fieldName,columnName));
        }
        if(!StringUtils.isEmpty(sortColumns)){
            sqlBilder.append(" order by ").append(sortColumns);//拼接上排序字符串
        }
        List<Map<String,Object>> resultList = DBUtils.executeQuerySql(sqlBilder.toString(),cond);
        List<T> resultBeanList = new ArrayList<>(resultList.size());
        for (Map<String, Object> one : resultList) {
            Map<String, Object> nameTransferedMap = new HashMap<>(one.size());
            one.forEach((k,v)->nameTransferedMap.put(Optional.ofNullable(c2fDic.get(k)).orElseThrow(()->new RuntimeException(String.format("列名[%s]无法匹配",k))),v));
            T resultBean = entityClass.newInstance();
            BeanUtils.populate(resultBean, nameTransferedMap);
            resultBeanList.add(resultBean);
        }
        return resultBeanList;
    }
}
