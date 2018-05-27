package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by vinyao on 17/11/19.
 */

public class Page {
    public ArrayList<Shape> shapeList;
    public Shape currentShape;
    public boolean isEdit;
    public String name;
    //add this guy to make background image change
    public int backgroundImageId;
    public boolean isBackgroundTiled;
    public HashSet<String> dependShapeName;

    public Page(String pageName){
        shapeList = new ArrayList<Shape>();
        name = pageName;
        backgroundImageId=R.drawable.white;
        dependShapeName = new HashSet<String>();
        isBackgroundTiled = false;
    }

    // to be edited by Bowen
    public void draw(Canvas canvas, Context context) {
        System.out.println("In Page Draw");
        for(Shape s : shapeList){
            s.draw(canvas, context);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(currentShape != null) {
            sb.append("currentShape:"+currentShape.name);
        } else {
            sb.append("currentShape:");
        }
        sb.append("@");
        sb.append("isEdit:"+isEdit+"@");
        sb.append(name+"@");
        sb.append(String.valueOf(backgroundImageId)+"@");
        sb.append(String.valueOf(isBackgroundTiled)+"@");

        for(String str : dependShapeName){
            sb.append(str);
            sb.append("%");
        }
        if(dependShapeName.size() > 0){
            sb.deleteCharAt(sb.lastIndexOf("%"));
        }
        sb.append("@");

        for(Shape s : shapeList) {
            sb.append(s.toString());
            sb.append("%");
        }

        String ret = sb.toString();
        if(shapeList.size()>0) {
            return ret.substring(0, ret.length()-1);
        }
        return ret;
    }

    public static Page fromString(String s) {
        System.out.println("6666666");
        String [] sa = s.split("@");
        Page p = new Page(sa[2]);
        System.out.println(sa[2]);
        System.out.println("777777");
        String currentShapeName=null;
        if(sa[0].split(":").length > 1) {
            currentShapeName = sa[0].split(":")[1];
        }
        System.out.println("8888888");
        p.isEdit = Boolean.parseBoolean(sa[1].split(":")[1]);
        System.out.println("9999999");

        p.backgroundImageId=Integer.parseInt(sa[3]);
        p.isBackgroundTiled=Boolean.parseBoolean(sa[4]);
        System.out.println("read dependShape");
        if(sa[5].length() > 0){
            String[] shapeNames = sa[5].split("%");
            for(int i=0; i<shapeNames.length; i++){
                p.dependShapeName.add(shapeNames[i]);
            }
        }
        System.out.println("hahahahaha");
        if(sa.length > 6) {
            String[] shapes = sa[6].split("%");
            for(int i=0; i<shapes.length; i++) {
                p.shapeList.add(Shape.fromString(shapes[i]));

                if(p.shapeList.get(i).name.equals(currentShapeName)){
                    p.currentShape = p.shapeList.get(i);
                }
            }
        }

        return p;
    }
}