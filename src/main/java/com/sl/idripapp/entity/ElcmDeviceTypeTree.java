package com.sl.idripapp.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: 李旭日
 * Date: 2020/10/27 8:51
 * FileName: ElcmDeviceTypeTree
 * Description: 设备类型树
 */
public class ElcmDeviceTypeTree {
    private int deviceTypeId;   //id
    private String deviceTypeName;      //名称
    private int parentId;       //父节点id
    private int deviceCount;    //设备数量
    private String deviceTypeUrl;    //图片
    private List<ElcmDeviceTypeTree> childs;  //孩子节点


    /**
     * 递归生成Tree结构数据
     * @param rootId 根id
     * @param treeList 树的全部数据
     * @return
     */
    public List<ElcmDeviceTypeTree> getElcmDeviceTypeTreeByRecursion(int rootId, List<ElcmDeviceTypeTree> treeList, Map<Integer, Integer> deviceCount){
//        ElcmDeviceTypeTree root = this.getNodeById(rootId,treeList);
        List<ElcmDeviceTypeTree> childs = this.getChildrenNodeById(rootId,treeList);
        for (ElcmDeviceTypeTree item : childs) {
            List<ElcmDeviceTypeTree> node = this.getElcmDeviceTypeTreeByRecursion(item.getDeviceTypeId(),treeList,deviceCount);
            if (node.size()!=0){ //不是叶子节点 设备数量累加
                item.setChilds(node);
                int count=0;
                for (ElcmDeviceTypeTree rootItem : node){
                    count+=rootItem.getDeviceCount();
                }
                item.setDeviceCount(count);
            }else{ //叶子节点 存设备数量
                int count=deviceCount.get(item.getDeviceTypeId())==null?0:deviceCount.get(item.getDeviceTypeId());
                item.setDeviceCount(count);

            }

        }
        return childs;
    }

    /**
     *根据cid获取节点对象
     * @param nodeId
     * @return
     */
    public ElcmDeviceTypeTree getNodeById(int nodeId,List<ElcmDeviceTypeTree> treeList){
        ElcmDeviceTypeTree treeNode = new ElcmDeviceTypeTree();
        for (ElcmDeviceTypeTree item : treeList) {
            if (item.getDeviceTypeId() == nodeId) {
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
    public List<ElcmDeviceTypeTree> getChildrenNodeById(int nodeId,List<ElcmDeviceTypeTree> treeList){
        List<ElcmDeviceTypeTree> childs = new ArrayList<>();
        for (ElcmDeviceTypeTree item : treeList) {
            if(item.getParentId() == nodeId){
                childs.add(item);
            }
        }
        return childs;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<ElcmDeviceTypeTree> getChilds() {
        return childs;
    }

    public void setChilds(List<ElcmDeviceTypeTree> childs) {
        this.childs = childs;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public String getDeviceTypeUrl() {
        return deviceTypeUrl;
    }

    public void setDeviceTypeUrl(String deviceTypeUrl) {
        this.deviceTypeUrl = deviceTypeUrl;
    }
}


