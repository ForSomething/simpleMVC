package sid.utils.cache;

public interface Cache {
    String get(String key);

    boolean set(String key,String value);

    boolean set(String key,String value,int validSecond);
}
