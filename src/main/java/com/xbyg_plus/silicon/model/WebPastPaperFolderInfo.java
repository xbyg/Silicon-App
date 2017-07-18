package com.xbyg_plus.silicon.model;

import java.util.Map;

public class WebPastPaperFolderInfo extends WebResourceInfo {
    //openfolder(imageName,folderName,id)[/it-school//php/resdb/panel1content.php]   ->   showpath(imagename,foldername,id,sortcol,sortorder)[/it-school//php/resdb/panel2content.php]
    //another showpath function defined in [/it-school//php/resdb/panel2title.php] maybe is wrong
    private Map<String, String> requestDataMap;  //'namepath':imageName,    'filepath':folderName,    'id':id         e.g. 'namepath':'fl6' ,  'filepath':'/Chemistry'  ,  'id'':'912'

    private WebPastPaperFolderInfo parentFolder;

    public WebPastPaperFolderInfo(String name, String date, WebPastPaperFolderInfo parentFolder, Map<String, String> requestDataMap) {
        this.name = name;
        this.date = date;
        this.requestDataMap = requestDataMap;
        this.parentFolder = parentFolder;
    }

    public Map<String, String> getRequestDataMap(){
        return this.requestDataMap;
    }

    public WebPastPaperFolderInfo getParentFolder(){
        return this.parentFolder;
    }
}
