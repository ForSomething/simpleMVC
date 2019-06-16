package crawler.dao.bo;

import bo.annotation.Table;

@Table(table = "chapter_tree", sortColumns = "indexNum")
public class House {
    private String id;
    private String city;
    private String region;
    private double size;
    private String orientations;
    @Table(column = "room_num_desc")
    private String roomNumDesc;
    private double price;
    @Table(column = "price_unit")
    private String priceUnit;
    private String label;
    @Table(column = "publish_time")
    private String publishTime;
    @Table(column = "create_time")
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
