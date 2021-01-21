package com.sl.idripapp.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/10/27 8:51
 * FileName: ElcmDeviceTypeTree
 * Description: 类型树
 */
public class AccountActivityTypeTree {
    private int Id;   //id
    private String typeName;      //名称
    private int parentId;       //父节点id
    private int level;       //等级
    private String levelName;       //等级名称
    private List<AccountActivityTypeTree> childs;  //孩子节点
    String [] levalList={"一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五","十六","十七","十八","十九","二十"};


    /**
     * 递归生成Tree结构数据
     * @param rootId 根id
     * @param treeList 树的全部数据
     * @return
     */
    public List<AccountActivityTypeTree> getAccountActivityTreeByRecursion(int rootId, List<AccountActivityTypeTree> treeList,int level) {
//        ElcmDeviceTypeTree root = this.getNodeById(rootId,treeList);
        List<AccountActivityTypeTree> childs = this.getChildrenNodeById(rootId, treeList);
        for (AccountActivityTypeTree item : childs) {
            item.setLevelName(levalList[level]+"级分类");
            item.setLevel(level+1);
            List<AccountActivityTypeTree> node = this.getAccountActivityTreeByRecursion(item.getId(), treeList,level+1);
            if (node.size() != 0) { //不是叶子节点 设备数量累加
                item.setChilds(node);
        }
        }
        return childs;
    }

    /**
     *根据cid获取节点对象
     * @param nodeId
     * @return
     */
    public AccountActivityTypeTree getNodeById(int nodeId, List<AccountActivityTypeTree> treeList){
        AccountActivityTypeTree treeNode = new AccountActivityTypeTree();
        for (AccountActivityTypeTree item : treeList) {
            if (item.getId() == nodeId) {
                treeNode = item;
                break;
            }
        }
        return treeNode;
    }


    /**
     *查询cid下的所有子节点
     * @param nodeId
     * @return
     */
    public List<AccountActivityTypeTree> getChildrenNodeById(int nodeId, List<AccountActivityTypeTree> treeList){
        List<AccountActivityTypeTree> childs = new ArrayList<>();
        for (AccountActivityTypeTree item : treeList) {
            if(item.getParentId() == nodeId){
                childs.add(item);
            }
        }
        return childs;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<AccountActivityTypeTree> getChilds() {
        return childs;
    }

    public void setChilds(List<AccountActivityTypeTree> childs) {
        this.childs = childs;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}


