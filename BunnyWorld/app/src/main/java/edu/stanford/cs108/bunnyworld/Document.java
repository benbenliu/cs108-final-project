package edu.stanford.cs108.bunnyworld;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;

/**
 * Created by vinyao on 17/11/19.
 */

public class Document {
    public Hashtable<String, Shape> shapeDictionary;
    public Hashtable<String, Page> pageDictionary;
    public ArrayList<Shape> possessionList;
    public Page currentPage;
    public boolean isEdit;
    public Shape copiedShape;
    public int copyNum;
    public boolean saved;
//    static Gson readerAndWriter = new Gson();

//
//    public Document(PageView pageView){
//        shapeDictionary = new Hashtable<String, Shape>();
//        pageDictionary = new Hashtable<String, Page>();
//        myPageView = pageView;
//    }

    public Document(){
        shapeDictionary = new Hashtable<String, Shape>();
        possessionList = new ArrayList<Shape>();
        pageDictionary = new Hashtable<String, Page>();
        Page page1 = new Page("page1");
        pageDictionary.put("page1", page1);
        currentPage = page1;
        saved=true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        assert currentPage != null;

        sb.append(currentPage.toString());
        sb.append("`");

        for(String s : shapeDictionary.keySet()) {
            sb.append(s+"->");
            sb.append(shapeDictionary.get(s).toString()+";");
        }

        if(shapeDictionary.size() > 0) {
            sb.deleteCharAt(sb.lastIndexOf(";"));
        }
        sb.append("`");

        for(Shape s: possessionList){
            sb.append(s.toString());
            sb.append(";");
        }
        if(possessionList.size() > 0) {
            sb.deleteCharAt(sb.lastIndexOf(";"));
        }
        sb.append("`");

        for(String s : pageDictionary.keySet()) {
            sb.append(s+"->");
            sb.append(pageDictionary.get(s).toString()+";");
        }
        if(pageDictionary.size() > 0) {
            sb.deleteCharAt(sb.lastIndexOf(";"));
        }

        return sb.toString();
    }

    public static Document fromString(String s) {
//        System.out.println("111111111");
        Document ret = new Document();
        String [] sa = s.split("`");
//        ret.currentPage = Page.fromString(sa[0]);
//        System.out.println("222222222222");
        if(sa.length == 1) {
            return ret;
        }
        if(sa[1].length() > 0) {
            String [] shapes = sa[1].split(";");
            for(int i = 0; i < shapes.length; i++) {
                String [] key_val = shapes[i].split("->");
                ret.shapeDictionary.put(key_val[0], Shape.fromString(key_val[1]));
            }
        }
//        System.out.println("333333333333");
        if(sa[2].length() > 0) {
            String [] shapes = sa[2].split(";");
            for(int i = 0; i < shapes.length; i++) {
                String shapeName=shapes[i].split("~")[0].split(":")[1];
                ret.possessionList.add(ret.shapeDictionary.get(shapeName));
//                ret.possessionList.add(Shape.fromString(shapes[i]));
            }
        }
//        System.out.println("44444444444");
        if(sa.length > 3) {
            String [] pages = sa[3].split(";");
            for(int i = 0; i < pages.length; i++) {
                String [] key_val = pages[i].split("->");
                // make sure the shapes stored in shape list and in page and in shape dict are the same reference
                Page tempPage = Page.fromString(key_val[1]);
                ArrayList<Shape> tempList = new ArrayList<>();
                for(Shape listShape: tempPage.shapeList){
                    tempList.add(ret.shapeDictionary.get(listShape.name));
                }
                tempPage.shapeList = tempList;
                if(tempPage.currentShape!= null && ret.shapeDictionary.containsKey(tempPage.currentShape.name)){
                    tempPage.currentShape = ret.shapeDictionary.get(tempPage.currentShape.name);
                }
//                if(tempPage.name.equals(ret.currentPage.name)){
//                    ret.currentPage = tempPage;
//                }
                ret.pageDictionary.put(key_val[0], tempPage);
            }
        }
        //current page should always be page1
        ret.currentPage = ret.pageDictionary.get("page1");
//        System.out.println("5555555555555");
        return ret;
    }

    public boolean addPage(String pageName) throws Exception {
        pageName = pageName.toLowerCase();
        if(pageDictionary.containsKey(pageName)){
            throw new Exception("Page name already exists!");
        }
        Page p = new Page(pageName);
        pageDictionary.put(pageName, p);
        currentPage = p;
        return true;
    }

    public void deletePage(String pageName) throws Exception {
        pageName = pageName.toLowerCase();
        if(pageName.toLowerCase().equals("page1")){
            throw new Exception("Page1 shall never be deleted or renamed :)");
        }
        if(pageDictionary.containsKey(pageName)){
            if(currentPage == pageDictionary.get(pageName)){ // if deleted, reset currentPage to page1
                currentPage = pageDictionary.get("page1");
            }
            for(Shape s:pageDictionary.get(pageName).shapeList){
                deleteShape_help(s);
            }
            pageDictionary.remove(pageName);

        }
    }
    public void deleteShape_help(Shape shape) throws Exception {
        mySingleton.getInstance().savedDocument.saved = false;
        checkDependentShape(shape);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee after check shape");
        deleteShape(shape);
        //to be implemented by Siyue
    }
    public void checkDependentShape(Shape s){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in checkDependentShape");
        HashSet<String> dependShapeNames = s.dependShapeName;
        System.out.println(s.name);
        System.out.println(dependShapeNames);
        String shapeName = s.name;
        for(String name: dependShapeNames){
            System.out.println("depend shape name: ");
            System.out.println(name);
            System.out.println(mySingleton.getInstance().savedDocument.shapeDictionary.keySet().toString());
            if(shapeDictionary.get(name) == null){
                continue;
            }
            Shape dependShape = shapeDictionary.get(name);
            System.out.println(dependShape);
            String scripts = dependShape.getScript();
            dependShape.dependShapeName.remove(shapeName);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in after get the final scripts");
            String new_scripts = deleteShapeScript(shapeName, scripts);
            System.out.println(String.format("new_sciprts {}", new_scripts));
            shapeDictionary.get(name).setScript(new_scripts);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    public String deleteShapeScript(String name, String script){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript");
        System.out.println(script);
        String[] clauses = script.split("\\*");
        String finalScripts = "";
        for(String clause: clauses){
            String[] splitStr = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript for loop");
            System.out.println(splitStr[1]);
            System.out.println(splitStr[2]);
            if(splitStr[0].equals("on drop")){
                if(!(splitStr[1].equals(name)) && !(splitStr[2].equals("hide") && splitStr[3].equals(name))
                        && !(splitStr[2].equals("show") && splitStr[3].equals(name))){
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }

            else{
                if(!(splitStr[1].equals("hide") && splitStr[2].equals(name))
                        && !(splitStr[1].equals("show") && splitStr[2].equals(name))){
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }
        }
        System.out.println(finalScripts);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return finalScripts;
    }

    public void renamePage(String originName, String newName) throws Exception {
        originName = originName.toLowerCase();
        newName = newName.toLowerCase();
        if(originName.equals("page1")){
            throw new Exception("Page1 shall never be deleted or renamed :)");
        }
        if(!pageDictionary.containsKey(originName)){
            throw new Exception("Origin page name doesn't exist!");
        }
        if(pageDictionary.containsKey(newName)){
            throw new Exception("New page name already exists!");
        }
        Page p = pageDictionary.get(originName);
        System.out.println("reannnnnnnnnnnnnnnnnnnnnn");
        checkDependentShape_renamepage(p, originName, newName);
        //p.dependShapeName = new HashSet<String>();
        System.out.println("reannnnnnnnnnnnnnnnnnnnnn");
        pageDictionary.remove(originName);
        pageDictionary.put(newName, p);



        //should also change the page name itself:
        pageDictionary.get(newName).name=newName;
    }

    public void checkDependentShape_renamepage(Page p, String oldNmae, String newName){
        System.out.println("reannnnnnnnnnnnnnnnnnnnnn in check rename");
        HashSet<String> dependShapeNames = p.dependShapeName;
        //String shapeName = s.name;
        for(String name: dependShapeNames){
            System.out.println("depend shape name: ");
            System.out.println(name);
            Shape dependShape = mySingleton.getInstance().savedDocument.shapeDictionary.get(name);
            if(dependShape == null){
                continue;
            }
            String scripts = dependShape.getScript();
            //dependShape.dependShapeName.remove(shapeName);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in after get the final scripts");
            String new_scripts = changeShapeScript(oldNmae, newName, scripts);
            System.out.println(String.format("new_sciprts {}", new_scripts));
            mySingleton.getInstance().savedDocument.shapeDictionary.get(name).setScript(new_scripts);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    public String changeShapeScript(String oldName, String newName, String script){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript");
        System.out.println(script);
        String[] clauses = script.split("\\*");
        String finalScripts = "";
        for(String clause: clauses) {
            String[] splitStr = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript for loop");
            System.out.println(splitStr[1]);
            System.out.println(splitStr[2]);
            script = "";
            if (splitStr[0].equals("on drop")) {
                if (splitStr[1].equals(oldName))
                    script += "on drop|" + newName + "|";
                else
                    script += "on drop|" + splitStr[1] + '|';
                if (splitStr[2].equals("goto") && splitStr[3].equals(oldName))
                    script += "goto|" + newName;
                else
                    script += splitStr[2] + "|" + splitStr[3];
                finalScripts += script;
                finalScripts += "*";
            }
            else{
                System.out.println("reannnnnnnnnnnnnnnnnnnnnn onclick");
                if(splitStr[1].equals("goto") && splitStr[2].equals(oldName)){
                    script += splitStr[0] + "|goto|" + newName;
                    finalScripts += script;
                    finalScripts += "*";
                    System.out.println("reannnnnnnnnnn");
                    System.out.println(script);
                }
                else {
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }
        }
        System.out.println(finalScripts);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return finalScripts;
    }



    public boolean addShape(Shape s) throws Exception {
        if(currentPage == null){
            throw new Exception("Please add or select a page first");
        }

        if(s.name.equals("")){// find the first unused name
            int i = 0;
            String newName = "shape" + (i + shapeDictionary.size());
            while(shapeDictionary.containsKey(newName)){
                i += 1;
                newName = "shape" + (i + shapeDictionary.size());
            }
            s.name = newName;
        }

        String shapeName = s.name;
        if(shapeDictionary.containsKey(shapeName)) {
            throw new Exception("Shape name already exists!");
        }
        shapeDictionary.put(shapeName, s);
        currentPage.shapeList.add(s);
        return true;
    }

    public void deleteShape(Shape s) throws Exception {
        if(currentPage == null){
            throw new Exception("Please add or select a page first");
        }
        String shapeName = s.name;
        shapeDictionary.get(shapeName).setActive(false);
        shapeDictionary.remove(shapeName);
        currentPage.shapeList.remove(s);

    }

    public void renameShape(Shape s, String newName) throws Exception {
        if(currentPage == null){
            throw new Exception("Please add or select a page first");
        }

        if(shapeDictionary.containsKey(newName)){
            throw new Exception("New shape name already exists!");
        }
        String originName = s.name;
        String oldScript = s.getScript();
        checkDependentShape_renameshape(s, originName, newName);
        changedepend(originName, newName, oldScript);
        shapeDictionary.remove(originName);
        shapeDictionary.put(newName, s);
        s.name = newName;
    }
    public void checkDependentShape_renameshape(Shape s, String oldNmae, String newName){
        System.out.println("reannnnnnnnnnnnnnnnnnnnnn in check rename");
        HashSet<String> dependShapeNames = s.dependShapeName;
        String shapeName = s.name;
        for(String name: dependShapeNames){
            System.out.println("depend shape name: ");
            System.out.println(name);
            if(name.equals(shapeName)){
                s.dependShapeName.remove(shapeName);
                s.dependShapeName.add(newName);
            }
            Shape dependShape = mySingleton.getInstance().savedDocument.shapeDictionary.get(name);
            if(dependShape == null){
                continue;
            }
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee before get scripts");
            String scripts = dependShape.getScript();
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in after get the final scripts");
            String new_scripts = changeShapeScript_renameshape(oldNmae, newName, scripts);
            System.out.println("new_sciprts "+new_scripts);
            mySingleton.getInstance().savedDocument.shapeDictionary.get(name).setScript(new_scripts);
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
    }
    public void changedepend(String oldName, String newName, String script){
        String[] clauses = script.split("\\*");
        for(String clause: clauses) {
            String[] scriptContent = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee remoVeand add name");
//            System.out.println(scriptContent[1]);
//            System.out.println(scriptContent[2]);
            if (scriptContent.length == 3) {
                System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee remoVeand add name");
                if (scriptContent[1].equals("goto")) {
                    if(scriptContent[2].equals(oldName)){
                        mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[2]).dependShapeName.remove(oldName);
                        mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[2]).dependShapeName.add(newName);
                    }
                }
                if (scriptContent[1].equals("hide") || scriptContent[1].equals("show")) {
                    if(scriptContent[2].equals(oldName)){
                        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee remoVeand add name");
                        mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[2]).dependShapeName.remove(oldName);
                        mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[2]).dependShapeName.add(newName);
                    }

                }

            }
            if (scriptContent.length == 4) {
                mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[1]).dependShapeName.remove(oldName);
                mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[1]).dependShapeName.add(newName);
                if (scriptContent[2].equals("goto")) {
                    if(scriptContent[3].equals(oldName)) {
                        mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[3]).dependShapeName.remove(oldName);
                        mySingleton.getInstance().savedDocument.pageDictionary.get(scriptContent[3]).dependShapeName.add(newName);
                    }
                }
                if (scriptContent[2].equals("hide")|| scriptContent[2].equals("show")) {
                    if(scriptContent[2].equals(oldName)) {
                        mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[3]).dependShapeName.remove(oldName);
                        mySingleton.getInstance().savedDocument.shapeDictionary.get(scriptContent[3]).dependShapeName.add(newName);
                    }
                }
            }
        }
    }
    public String changeShapeScript_renameshape(String oldName, String newName, String script){
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript");
        System.out.println(script);
        String[] clauses = script.split("\\*");
        String finalScripts = "";
        for(String clause: clauses) {
            String[] splitStr = clause.split("\\|");
            System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee in deleteShapeScript for loop");
            System.out.println(splitStr[1]);
            System.out.println(splitStr[2]);
            script = "";
            if (splitStr[0].equals("on drop")) {
                if(splitStr[1].equals(oldName)){
                    script += "on drop|" + newName + "|";
                }
                else
                    script += "on drop|" + splitStr[1] + '|';
                if (splitStr[2].equals("hide") && splitStr[3].equals(oldName) || splitStr[2].equals("show") && splitStr[3].equals(oldName))
                    script += splitStr[2] + "|" + newName;
                else
                    script += splitStr[2] + "|" + splitStr[3];
                finalScripts += script;
                finalScripts += "*";
            }
            else{
                System.out.println("reannnnnnnnnnnnnnnnnnnnnn onclick");
                if(splitStr[1].equals("hide") && splitStr[2].equals(oldName) || splitStr[1].equals("show") && splitStr[2].equals(oldName)){
                    script += splitStr[0] + "|"+ splitStr[1] + "|" + newName;
                    finalScripts += script;
                    finalScripts += "*";
                    System.out.println("reannnnnnnnnnn");
                    System.out.println(script);
                }
                else {
                    finalScripts += clause;
                    finalScripts += "*";
                }
            }
        }
        System.out.println(finalScripts);
        System.out.println("Siyueeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        return finalScripts;
    }
}
