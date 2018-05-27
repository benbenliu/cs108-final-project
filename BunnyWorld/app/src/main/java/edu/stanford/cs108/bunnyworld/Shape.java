package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class Shape implements Serializable {

    public String name;
    public HashSet<String> dependShapeName;
    private float width;
    private float height;
    private float positionX;
    private float positionY;
    private float fontSize;
    private int fontColor;
    private boolean movable;
    private boolean visible;
    private String text;
    // example imagePath: carrot
    private String imagePath;
    private boolean selected;
    private String script;
    private boolean clickable;
    private boolean active;
    private Hashtable<String, ArrayList<trigger>> triggers;
    private float minSize = 1;
    private boolean isItalic;
    private boolean isBold;
    private boolean isUnderLined;

    private Paint stokepaint = new Paint();
    private Paint greypaint = new Paint();
    Paint alphaPaint = new Paint();

    public static Shape deepCopy(Shape s) {
        String str = s.toString();
        Shape news = Shape.fromString(str);
        return news;
    }

    public class trigger{
        String targetShape;
        moveAction moveAction;
        public trigger(String targetShape, moveAction moveAction) {
            this.targetShape = targetShape;
            this.moveAction = moveAction;
        }

    }

    public Shape(String _name, float _width, float _height, float _x, float _y) {
        isItalic = false;
        isBold = false;
        isUnderLined = false;

        stokepaint.setStyle(Paint.Style.STROKE);
        stokepaint.setStrokeWidth(5.0f);
        stokepaint.setColor(Color.BLUE);
        greypaint.setStyle(Paint.Style.STROKE);
        greypaint.setStrokeWidth(15.0f);
        greypaint.setColor(Color.argb(1,120,120,120));

        name = _name;
        width = _width;
        height = _height;
        positionX = _x;
        positionY = _y;

        if (width<minSize) {width = minSize;}
        if (height<minSize) {height = minSize;}

        fontSize = 14;
        fontColor = Color.BLACK;
        movable = true;
        visible = true;
        text = "";
        imagePath = "";
        selected = false;
        script = "";
        clickable = true;
        active = true;
        isItalic=false;
        isBold=false;
        isUnderLined=false;
        triggers = new Hashtable<String, ArrayList<trigger>>();
        dependShapeName = new HashSet<String>();
        alphaPaint.setAlpha(42);

    }

    public String getName() {return name;}
    public float getWidth() {return width;}
    public float getHeight() {return height;}
    public float getPositionX() {return positionX;}
    public float getPositionY() {return positionY;}
    public float getFontSize() {return fontSize;}
    public int getFontColor() {return fontColor;}
    public boolean isMovable() {return movable;}
    public boolean isVisible() {return visible;}
    public boolean isText() {return text.length()>0;}
    public boolean isImage() {return imagePath.length()>0 && !isText();}
    public boolean isSelected() {return selected;}
    public boolean isNone() {return text.length()==0 && imagePath.length()==0;}
    public String getScript() {return script;}
    public boolean getClickable() {return clickable;}
    public boolean getActive() {return active;}
    public Hashtable<String, ArrayList<trigger>> getTriggers() {return triggers;}
    public boolean getItalic() {return isItalic;}
    public boolean getBold() {return isBold;}
    public boolean getUnderLined() {return isUnderLined;}
    public String getText() {return text;}

    public void setName(String newvalue) {name = newvalue;}
    public void setWidth(float newvalue) {
        if (newvalue<minSize) {
            width = minSize;
            return;
        }
        width = newvalue;
    }
    public void setHeight(float newvalue) {
        if (newvalue<minSize) {
            height = minSize;
            return;
        }
        height = newvalue;
    }
    public void setPositionX(float newvalue) {positionX = newvalue;}
    public void setPositionY(float newvalue) {positionY = newvalue;}
    public void setFontSize(float newvalue) {fontSize = newvalue;}
    public void setFontColor(int newvalue) {fontColor = newvalue;}
    public void setMovable(boolean newvalue) {movable = newvalue;}
    public void setVisible(boolean newvalue) {visible = newvalue;}
    public void setText(String newvalue) {text = newvalue;}
    public void setImagePath(String newvalue) {imagePath = newvalue;}
    public void setSelected(boolean newvalue) {selected = newvalue;}
    public void setScript(String newvalue) {script = newvalue;}
    public void setClickable(boolean newvalue) {clickable = newvalue;}
    public void setActive(boolean newvalue) {
        active = newvalue;
        clickable = true;
    }
    public void setItalic(boolean newvalue) {isItalic = newvalue;}
    public void setBold(boolean newvalue) {isBold = newvalue;}
    public void setUnderLined(boolean newvalue) {isUnderLined = newvalue;}
    // example path: R.raw.hooray

    // if image, return the path, if text, return the text
    public String getContent() {
        if (text.length()>0) return text;
        if (imagePath.length()>0) return imagePath;
        return null;
    }

    // show on canvas
    public void draw(Canvas canvas, Context context) {
        if ((!visible) && !mySingleton.getInstance().savedDocument.isEdit) {
            System.out.println("visible==false");
            hide(canvas, context);
            return;
        }
        if (selected) {
            if (text.length()>0) {
                Paint paint = new Paint();
                paint.setColor(fontColor);
                if (!visible) paint.setColor(Color.LTGRAY);
                paint.setTextSize(fontSize);
                Rect rect = new Rect();
                paint.getTextBounds(text, 0, text.length(), rect);

                width = rect.width();
                height = rect.height();

                if (isItalic) paint.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
                if (isBold) paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                if (isUnderLined) paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
                canvas.drawText(text, positionX, positionY+height, paint);

                RectF rf = new RectF(positionX,positionY,positionX+width,positionY+height);
                canvas.drawRect(rf, stokepaint);
                return;
            }
            if (imagePath.length()>0) {
                int id = context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName());
                BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(id);
                Bitmap bunnyBitmap = drawable.getBitmap();
                if (!visible) {
                    canvas.drawBitmap(bunnyBitmap,null,new RectF(positionX,positionY,positionX+width,positionY+height),alphaPaint);
                } else {
                    canvas.drawBitmap(bunnyBitmap,null,new RectF(positionX,positionY,positionX+width,positionY+height),null);
                }

                RectF rf = new RectF(positionX,positionY,positionX+width,positionY+height);
                canvas.drawRect(rf, stokepaint);
                if (mySingleton.getInstance().savedDocument.isEdit) {
                    canvas.drawCircle(positionX, positionY, 10, stokepaint);
                    canvas.drawCircle(positionX+width, positionY, 10, stokepaint);
                    canvas.drawCircle(positionX, positionY+height, 10, stokepaint);
                    canvas.drawCircle(positionX+width, positionY+height, 10, stokepaint);
                }
                return;
            }
            RectF rf = new RectF(positionX,positionY,positionX+width,positionY+height);
            canvas.drawRect(rf, greypaint);
            return;
        } else {
            if (text.length()>0) {
                Paint paint = new Paint();
                paint.setColor(fontColor);
                if (!visible) paint.setColor(Color.LTGRAY);
                paint.setTextSize(fontSize);
                if (isItalic) paint.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
                if (isBold) paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                if (isUnderLined) paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
                canvas.drawText(text, positionX, positionY+height, paint);
                return;
            }
            if (imagePath.length()>0) {
                int id = context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName());
                BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(id);
                Bitmap bunnyBitmap = drawable.getBitmap();
                if (!visible) {
                    canvas.drawBitmap(bunnyBitmap,null,new RectF(positionX,positionY,positionX+width,positionY+height),alphaPaint);
                } else {
                    canvas.drawBitmap(bunnyBitmap,null,new RectF(positionX,positionY,positionX+width,positionY+height),null);
                }
                return;
            }
            RectF rf = new RectF(positionX,positionY,positionX+width,positionY+height);
            canvas.drawRect(rf, greypaint);
            return;
        }
    }

    // 鼠标释放时的动作，目标被选中。不更新canvas。
    public void release() {
        setSelected(true);
    }

    // 取消选择，不更新canvas。
    public void onCancel() {
        setSelected(false);
    }

    // 隐藏自己，call draw() 之后即可重新显示（用于on drop），更新canvas。不可从外部直接call。
    private void hide(Canvas canvas, Context context) {
        System.out.println("In Shape hide");
        setMovable(false);
        setClickable(false);
        setVisible(false);
//        Paint paint = new Paint();
//        paint.setColor(fontColor);
//        paint.setTextSize(fontSize);
//        canvas.drawText("", positionX, positionY, paint);
        return;
    }

    // 整合设置x，y值的method，不更新canvas。手动更改x，y值应使用setPositionX和setPositionY。
    public void moveTo(float x, float y) {
        if (!movable && !mySingleton.getInstance().savedDocument.isEdit) return;
//        setPositionX(x-width/2);
//        setPositionY(y-height/2);
        setPositionX(x);
        setPositionY(y);
    }

    // 处理MotionEvent，不更新canvas。
    public void parseMotionEvent(MotionEvent event, float dx, float dy) {
        setSelected(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                moveTo(event.getX()-dx, event.getY()-dy);
                break;
        }
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1-x2), 2)+Math.pow((y1-y2), 2));
    }

    public int checkOnEdit(float _x, float _y, float threshold) {
        ArrayList<Float> dists = new ArrayList<Float>();
        dists.add(getDistance(_x, _y, positionX, positionY));
        dists.add(getDistance(_x, _y, positionX+width, positionY));
        dists.add(getDistance(_x, _y, positionX, positionY+height));
        dists.add(getDistance(_x, _y, positionX+width, positionY+height));
        int minIndex = dists.indexOf (Collections.min(dists));
        if (dists.get(minIndex)<=threshold) return minIndex+1;
        return 0;
    }

    // 改变shape size, mode - 1：左上，2：右上，3：左下，4：右下
    public void editShapeSize(MotionEvent event, int mode) {
        if (((!visible) && (!mySingleton.getInstance().savedDocument.isEdit))) return;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float px = event.getX();
                float py = event.getY();
                if (mode == 1) {
                    float x2 = positionX+width;
                    float y2 = positionY+height;
                    if (px<=x2) {
                        setPositionX(px);
                    } else {
                        setPositionX(x2);
                    }
                    if (py<=y2) {
                        setPositionY(py);
                    } else {
                        setPositionY(y2);
                    }
                    setWidth(Math.abs(px-x2));
                    setHeight(Math.abs(py-y2));
                } else if (mode == 2) {
                    float x2 = positionX;
                    float y2 = positionY+height;
                    if (px<=x2) {
                        setPositionX(px);
                    } else {
                        setPositionX(x2);
                    }
                    if (py<=y2) {
                        setPositionY(py);
                    } else {
                        setPositionY(y2);
                    }
                    setWidth(Math.abs(px-x2));
                    setHeight(Math.abs(py-y2));
                } else if (mode == 3) {
                    float x2 = positionX+width;
                    float y2 = positionY;
                    if (px<=x2) {
                        setPositionX(px);
                    } else {
                        setPositionX(x2);
                    }
                    if (py<=y2) {
                        setPositionY(py);
                    } else {
                        setPositionY(y2);
                    }
                    setWidth(Math.abs(px-x2));
                    setHeight(Math.abs(py-y2));
                } else if (mode == 4) {
                    float x2 = positionX;
                    float y2 = positionY;
                    if (px<=x2) {
                        setPositionX(px);
                    } else {
                        setPositionX(x2);
                    }
                    if (py<=y2) {
                        setPositionY(py);
                    } else {
                        setPositionY(y2);
                    }
                    setWidth(Math.abs(px-x2));
                    setHeight(Math.abs(py-y2));
                }
                break;
        }
    }

    // 看event
    public boolean inRange(MotionEvent event) {
        if (!(clickable && !((!visible) && !mySingleton.getInstance().savedDocument.isEdit))) return false;
        float x = event.getX();
        float y = event.getY();
        return (x>positionX && x<positionX+width && y>positionY && y<positionY+height);
    }

    public void GOTO(String pageName){
        if (!(clickable)) return;
        System.out.println("   ingoto   ");
        System.out.println(pageName);
        System.out.print("   ingoto   ");
        Page destP = mySingleton.getInstance().savedDocument.pageDictionary.get(pageName);
        if(destP == null){
            return;
        }
        System.out.println(destP.name);
        mySingleton.getInstance().currentPageView.setPageAndDraw(destP);
        mySingleton.getInstance().currentPageView.onEnter();
    }

    public void PLAY(Context context, String soundName){
        System.out.print("   inplay   ");
        int id = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
        MediaPlayer mp = MediaPlayer.create(context,id);
        mp.start();
    }

    public void HIDE(String shapeName){
        if(mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName) == null){
            return;
        }
        if(shapeName.equals(name)){
            visible=false;
            clickable=false;
            movable=false;
            active=false;
        }
        else {
            System.out.println(name);
            System.out.println(shapeName);
            mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName).clickable=false;
            mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName).visible=false;
            mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName).movable=false;
            mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName).active=false;
            System.out.println(visible);
            System.out.println(clickable);
        }
        mySingleton.getInstance().currentPageView.draw();
        System.out.println("    after reDraw   ");
    }

    public void SHOW(String shapeName){
        if(mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName) == null){
            return;
        }
        setClickable(true);
        System.out.print("   inshow   ");
        Shape destS = mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName);
        destS.setActive(true);
        destS.setVisible(true);
        mySingleton.getInstance().currentPageView.draw();
    }

    public interface moveAction{
        void move(Context context);
    }
    public moveAction createMoveAction(final String[] splitStr, final int index) {
        System.out.println("!!!!!!!!!!in createMoveAction!!!!!!!!!");
        System.out.println(splitStr);
        moveAction currMoveAction = new moveAction() {
            @Override
            public void move(Context context) {
                System.out.println("!!!"+splitStr[index]+"!!insidemove");
                if (splitStr[index].equals("goto")) {
                    System.out.println("     MoveAction     : goto   ");
                    GOTO(splitStr[index+1]);
                } else if (splitStr[index].equals("play")) {
                    System.out.print("     MoveAction     : play   ");
                    PLAY(context, splitStr[index+1]);
                } else if (splitStr[index].equals("hide")) {
                    System.out.println("     MoveAction     : hide   ");
                    HIDE(splitStr[index+1]);
                } else if (splitStr[index].equals("show")){
                    System.out.println("     MoveAction     : show   ");
                    SHOW(splitStr[index+1]);
                }
            }
        };
        return currMoveAction;
    }
    public void addTriggers(){
        System.out.print("!!!!!!!!print scripts!!!!!!!!!");
        System.out.println("the script is : "+script);
        if(script.equals("")){
            return;
        }

        System.out.print("!!!!!!!!!!1after print scripts!!!!!!!!!");
        String[] clauses = script.split("\\*");
        int count = 0;
        for(String clause: clauses){
            ArrayList<trigger> triggerPairs = new ArrayList<trigger>();
            count += 1;
            System.out.print("\n");
            System.out.println("clauseLennnnnnnnnnnnnnnnnnclause!!!!!!!!!");
            System.out.println(String.format("!!count: ", count));
            System.out.println(clause);
            System.out.print("\n");
            final String[] splitStr = clause.split("\\|");
            int clauseLen = splitStr.length;
            System.out.println("clauseLennnnnnnnnnnnnnnnnn!!!!!!!!!!");
            System.out.println(clauseLen);
            System.out.print("\n");
            String clauseName = splitStr[0];
            System.out.println("clauseLennnnnnnnnnnnnnnnnn name   ");
            System.out.println(clauseName);
            System.out.print("\n");
            moveAction currMoveAction;
            String checkOnDrop;
            trigger triggerPair;
            if(clauseLen == 3){
                System.out.print("!!!!!!!!!!!!!!!if length is equal to 4!!!!!!!!!!!!");
                currMoveAction = createMoveAction(splitStr, 1);
                System.out.print("!!!!!!!!!!!!!!!after if length is equal to 4!!!!!!!!!!!!");
                checkOnDrop = "None";
            }
            else{
                currMoveAction = createMoveAction(splitStr, 2);
                checkOnDrop = splitStr[1];
            }
            triggerPair = new trigger(checkOnDrop, currMoveAction);
            if(triggers.containsKey(clauseName.toLowerCase())){
                triggerPairs = triggers.get(clauseName.toLowerCase());
            }
            triggerPairs.add(triggerPair);
            triggers.put(clauseName.toLowerCase(), (ArrayList<trigger>) triggerPairs);
            System.out.println("triggers size");
            System.out.println(triggers.get(clauseName.toLowerCase()).size());
        }
    }
//    public boolean checkDrop(String shapeName){
//        Shape s = mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName);
//        float sX = s.getPositionX();
//        float sY = s.getPositionY();
//        if (sX<positionX-s.getWidth()) return false;
//        if (sY<positionY-s.getHeight()) return false;
//        if (sX>positionX+width) return false;
//        if (sY>positionY+height) return false;
//        return true;
//    }
    public boolean checkDrop(String shapeName){
        Shape s = mySingleton.getInstance().savedDocument.shapeDictionary.get(shapeName);
        float sX = s.getPositionX();
        float sY = s.getPositionY();

        if (sX>=positionX && sX<=positionX+width && sY>=positionY && sY<=positionY+height) return true;
        if (positionX>=sX && positionX<=sX+s.getWidth() && positionY>=sY && positionY<=sY+s.getHeight()) return true;

        if (sX+s.getWidth()>=positionX && sX+s.getWidth()<=positionX+width && sY>=positionY && sY<=positionY+height) return true;
        if (positionX+width>=sX && positionX+width<=sX+s.getWidth() && positionY>=sY && positionY<=sY+s.getHeight()) return true;

        if (sX>=positionX && sX<=positionX+width && sY+s.getHeight()>=positionY && sY+s.getHeight()<=positionY+height) return true;
        if (positionX>=sX && positionX<=sX+s.getWidth() && positionY+height>=sY && positionY+height<=sY+s.getHeight()) return true;

        if (sX+s.getWidth()>=positionX && sX+s.getWidth()<=positionX+width && sY+s.getHeight()>=positionY && sY+s.getHeight()<=positionY+height) return true;
        if (positionX+width>=sX && positionX+width<=sX+s.getWidth() && positionY+height>=sY && positionY+height<=sY+s.getHeight()) return true;
        return false;
    }
    public void doTrigger(Context context, String triggerName) {

        if (triggerName.equals("on click") || triggerName.equals("on enter")) {
            ArrayList<trigger> triggerPairs = triggers.get(triggerName);
            if(triggerPairs == null){
                return;
            }
            System.out.print("all triggereeeeeeeeeeeee size\n");
            System.out.print(triggerPairs.size());
            System.out.print("\n");
            for (trigger t : triggerPairs) {
                System.out.print(" in for loooooooooooooooop   \n");
                System.out.print(t.targetShape);
                System.out.print("    ");
                System.out.print(t.moveAction);
                System.out.print("    ");
                t.moveAction.move(context);
            }
        } else {
            System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
            for(Shape s: mySingleton.getInstance().savedDocument.currentPage.shapeList){
                System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                if(checkDrop(s.name)){
                    System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                    ArrayList<trigger> triggerPairs = s.getTriggers().get(triggerName);
                    if(triggerPairs == null){
                        return;
                    }
                    System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                    for (trigger t : triggerPairs) {
                        System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                        if(mySingleton.getInstance().savedDocument.shapeDictionary.get(t.targetShape) == null){
                            continue;
                        }
                        if(t.targetShape.equals(name))
                            System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                            t.moveAction.move(context);
                        System.out.println("siyueeeeeeeeeeeeeeeeeeeeeeeeee");
                    }
                }
            }

        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name:"+this.name);
        sb.append("~width:"+this.width);
        sb.append("~height:"+this.height);
        sb.append("~positionX:"+this.positionX);
        sb.append("~positionY:"+this.positionY);
        sb.append("~fontColor:"+this.fontColor);
        sb.append("~fontSize:"+fontSize);
        sb.append("~movable:"+movable);
        sb.append("~visable:"+visible);
        sb.append("~text:"+text);
        sb.append("~imagePath:"+imagePath);
        sb.append("~selected:"+selected);
        sb.append("~script:"+script);
        sb.append("~clickable:"+clickable);
        sb.append("~active:"+active);
        sb.append("~isItalic:"+isItalic);
        sb.append("~isBold:"+isBold);
        sb.append("~isUnderLined:"+isUnderLined);

        sb.append("~dependShape:");
        int count=0;
        for(String s : dependShapeName){
            System.out.println("count: "+count);
            sb.append(s);
            System.out.println(s);
            sb.append("^");
            count+=1;
        }
        String s = sb.toString();
        if(dependShapeName.size() > 0){
            return s.substring(0, s.length()-1);
        }
        return s;
    }

    public static Shape fromString(String s) {
        System.out.println("AAAAAA");
        String[] sa = s.split("~");
        System.out.println(s);
        String _name = sa[0].split(":")[1];
        float _width = Float.parseFloat(sa[1].split(":")[1]);
        float _height = Float.parseFloat(sa[2].split(":")[1]);
        float _x = Float.parseFloat(sa[3].split(":")[1]);
        float _y = Float.parseFloat(sa[4].split(":")[1]);

        Shape ret = new Shape(_name, _width, _height, _x, _y);
        System.out.println("BBBBB");
        ret.setFontColor(Integer.parseInt(sa[5].split(":")[1]));
        ret.setFontSize(Float.parseFloat(sa[6].split(":")[1]));
        ret.setMovable(Boolean.parseBoolean(sa[7].split(":")[1]));
        ret.setVisible(Boolean.parseBoolean(sa[8].split(":")[1]));
        System.out.println("CCCCC");
        if(sa[9].split(":").length != 1) {
            ret.setText(sa[9].split(":")[1]);
        }
        if(sa[10].split(":").length != 1) {
            ret.setImagePath(sa[10].split(":")[1]);
        }
        ret.setSelected(Boolean.parseBoolean(sa[11].split(":")[1]));
        if(sa[12].split(":").length != 1) {
            ret.setScript(sa[12].split(":")[1]);
        }
        System.out.println("DDDDD");
        ret.setClickable(Boolean.parseBoolean(sa[13].split(":")[1]));
        ret.setActive(Boolean.parseBoolean(sa[14].split(":")[1]));
        ret.setItalic(Boolean.parseBoolean(sa[15].split(":")[1]));
        ret.setBold(Boolean.parseBoolean(sa[16].split(":")[1]));
        ret.setUnderLined(Boolean.parseBoolean(sa[17].split(":")[1]));
        String [] shapeNames = sa[18].split(":");
        if(shapeNames.length  > 1){
            String [] dependNames = shapeNames[1].split("\\^");
            for(int i = 0; i<dependNames.length; i++){
                if(dependNames[i].length() > 0)
                    ret.dependShapeName.add(dependNames[i]);
            }
        }

        System.out.println("EEEEE");
        System.out.println(ret.script);
        ret.addTriggers();
        System.out.println("FFFFF");
        return ret;
    }

}
