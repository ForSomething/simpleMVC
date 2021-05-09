package sid.bo.directbank;

import sid.bo.BaseDataEntity;
import sid.bo.annotation.Persistence;

import java.util.HashMap;

@Persistence(table = "Directbank_Environment",columnNameRule = Persistence.ColumnNameRule.UNDERLINE_SEPARATOR)
public class DirectbankEnvironment extends BaseDataEntity {
    private String name;
    private String coreDomain;
    private String vueUrl;
    private String dbDomain;
    private String dbUser;
    private String dbPassword;
    private String dbName;

    public static DirectbankEnvironment load(String name) throws Exception {
        return selectFirst(new HashMap<String,Object>(){{
            put("name",name);
        }},DirectbankEnvironment.class);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoreDomain() {
        return coreDomain;
    }

    public void setCoreDomain(String coreDomain) {
        this.coreDomain = coreDomain;
    }

    public String getVueUrl() {
        return vueUrl;
    }

    public void setVueUrl(String vueUrl) {
        this.vueUrl = vueUrl;
    }

    public String getDbDomain() {
        return dbDomain;
    }

    public void setDbDomain(String dbDomain) {
        this.dbDomain = dbDomain;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
