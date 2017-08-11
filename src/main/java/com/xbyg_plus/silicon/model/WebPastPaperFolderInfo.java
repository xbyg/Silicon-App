package com.xbyg_plus.silicon.model;

import java.util.Map;

public class WebPastPaperFolderInfo extends WebResourceInfo {
    public static final WebPastPaperFolderInfo rootFolder = new WebPastPaperFolderInfo("root", "", "", null);
    //openfolder(imageName,folderName,id)[/it-school//php/resdb/panel1content.php]   ->   showpath(imagename,foldername,id,sortcol,sortorder)[/it-school//php/resdb/panel2content.php]
    //another showpath function defined in [/it-school//php/resdb/panel2title.php] maybe is wrong
    private Map<String, String> requestDataMap;  //'namepath':imageName,    'filepath':folderName,    'id':id         e.g. 'namepath':'fl6' ,  'filepath':'/Chemistry'  ,  'id'':'912'

    private String parentAbsolutePath;

    public WebPastPaperFolderInfo(String name, String date, String parentAbsolutePath, Map<String, String> requestDataMap) {
        this.name = name;
        this.date = date;
        this.parentAbsolutePath = parentAbsolutePath;
        this.requestDataMap = requestDataMap;
    }

    public Map<String, String> getRequestDataMap() {
        return this.requestDataMap;
    }

    public String getParentAbsolutePath() {
        return this.parentAbsolutePath;
    }

    public String getAbsolutePath() {
        return this.parentAbsolutePath + "/" + this.name;
    }
}
