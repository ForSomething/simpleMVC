package crawlerfj.crawlercase.dataentity;

import annotation.Table;

import java.util.HashMap;
import java.util.List;

@Table(table = "chapter_tree")
public class Chapter extends BaseDataEntity {
    String id;
    String parentID;
    String chapterName;
    String chapterContent;
    String remark;
    Integer indexNum;

    public Chapter(String id,String parentID,String chapterName,String chapterContent,String remark){
        this.id = id;
        this.parentID = parentID;
        this.chapterName = chapterName;
        this.chapterContent = chapterContent;
        this.remark = remark;
        // TODO 这里少了一个序号
    }

    public Chapter(){

    }

    public static Chapter load(String id) throws Exception {
        Chapter cond = new Chapter();
        cond.setParentID(id);
        return selectFirst(cond,cond.getClass());
    }

    public static List<Chapter> load(Object cond) throws Exception {
        return select(cond,Chapter.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(Integer indexNum) {
        this.indexNum = indexNum;
    }
}
