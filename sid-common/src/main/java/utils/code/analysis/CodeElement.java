package utils.code.analysis;

public class CodeElement {
    private String stringValue;
    private String type;

    protected CodeElement(String value,String type){
        this.stringValue = value;
        this.type = type;
    }

    @Override
    public String toString(){
        return stringValue;
    }
}
