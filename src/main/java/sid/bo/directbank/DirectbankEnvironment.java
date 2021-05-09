package sid.bo.directbank;

import sid.bo.BaseDataEntity;
import sid.bo.annotation.Persistence;

@Persistence(table = "Directbank_Environment")
public class DirectbankEnvironment extends BaseDataEntity {
    private String name;
    private String coreDomain;
    private String vueUrl;
    private String dbDomain;
    private String dbUser;
    private String dbPassword;

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
}
