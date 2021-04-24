package sid.bo.common;


import sid.bo.annotation.Persistence;

@Persistence(table = "chapter_tree", sortColumns = "indexNum")
public class House {
    private String id;
    private String city;
    private String region;
    private double size;
    private String orientations;
    private String roomNumDesc;
    private double price;
    private String priceUnit;
    private String label;
    private String publishTime;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getOrientations() {
        return orientations;
    }

    public void setOrientations(String orientations) {
        this.orientations = orientations;
    }

    public String getRoomNumDesc() {
        return roomNumDesc;
    }

    public void setRoomNumDesc(String roomNumDesc) {
        this.roomNumDesc = roomNumDesc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
