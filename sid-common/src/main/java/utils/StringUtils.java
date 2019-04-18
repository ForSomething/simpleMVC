package utils;

import org.apache.commons.beanutils.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StringUtils {
    static public String emptyString = "";

    static public byte[] GetByteArray(String str,String charsetName) throws UnsupportedEncodingException {
        if(str != null){
            return str.getBytes(charsetName);
        }
        return null;
    }

    static public boolean isNullOrEmpty(String str){
        return str == null || str.equals("");
    }

    static public boolean isNullOrWihtespace(String str){
        return str == null || str.trim().equals("");
    }

    static public <T> T getValueByTemplate(String srcString,String templateString,Class disObjectClass) throws Exception {
        if(isNullOrWihtespace(templateString)){
            return null;
        }
        T disObject = (T)disObjectClass.newInstance();
        HashMap<String,Object> valueMap = new HashMap<>();
        SyntaxTree syntaxTree = new SyntaxTree(templateString);
        BeanUtils.populate(disObject,valueMap);
        return disObject;
    }
}

class SyntaxTree{
    private static SyntaxTree rulesSyntaxTree;

    TreeNode root;

    static {
        rulesSyntaxTree = new SyntaxTree();
        rulesSyntaxTree.root = new TreeNode();
        rulesSyntaxTree.root.value = '#';
        rulesSyntaxTree.root.children = new LinkedList<>();
        rulesSyntaxTree.root.children.add(null);
    }

    private SyntaxTree(){}

    public SyntaxTree(String statement){
        ParsedStatement parsedStatement = new ParsedStatement(statement);
        root = new TreeNode();
        parsedStatement.nextChar();
        root.value = parsedStatement.currentChar();
        addChildren(root,parsedStatement);
    }

    private void addChildren(TreeNode node,ParsedStatement parsedStatement){

    }

    private class ParsedStatement{
        private String statement;
        private int point;

        public ParsedStatement(String statement){
            point = -1;
            this.statement = statement;
        }

        public boolean nextChar(){
            return ++point == statement.length();
        }

        public char currentChar(){
            return statement.charAt(point);
        }
    }
}

class TreeNode{
    char value;
    List<TreeNode> children;

    public boolean hasChildren(){
        return children == null || children.size() == 0;
    }
}
