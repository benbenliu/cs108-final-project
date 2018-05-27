package edu.stanford.cs108.bunnyworld;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by vinyao on 17/11/19.
 */

public class PageView extends View {
    private Paint textPaint;
    private Context context;
    private Document d;
    private float px, py;
    private float downx, downy;
    private boolean downlock;
    private boolean editlock;

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);
        try{
            d = ((EditMode) context).d;
        } catch (Exception ignored){
            d = ((PlayMode) context).d;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("on ondraw: "+d.currentPage.name);
        for(Shape s : d.currentPage.shapeList){
            if(d.shapeDictionary.contains(s)){
                System.out.println("Same");
            } else{
                System.out.println("not Same");
            }
        }
        if(d.currentPage != null){
            System.out.println("In PageView onDraw");
            d.currentPage.draw(canvas, context);
        } else{
            canvas.drawText("Please add or select a page to start", 10, 25, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        d.saved = false;
        if(d.currentPage == null){ // nothing will happen if no page is selected
            return true;
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (editlock) {
                    px = event.getX();
                    py = event.getY();

                    boolean isSelectingShape = false;
                    Shape selectedShape = null;
                    for (Shape s : d.currentPage.shapeList) {
                        if (s.isSelected()) {
                            isSelectingShape = true;
                            selectedShape = s;
                            break;
                        }
                    }
                    if (isSelectingShape) {
                        int mode = selectedShape.checkOnEdit(px, py, Float.MAX_VALUE);
                        if (mode!=0) {
                            selectedShape.editShapeSize(event, mode);
                            invalidate();
                            return true;
                        }
                    }
                }
            case MotionEvent.ACTION_DOWN:
                if (editlock || downlock) break;
                px = event.getX();
                py = event.getY();

                if (d.isEdit && !downlock) {
                    boolean isSelectingShape = false;
                    Shape selectedShape = null;
                    for (Shape s : d.currentPage.shapeList) {
                        if (s.isSelected()) {
                            isSelectingShape = true;
                            selectedShape = s;
                            break;
                        }
                    }
                    if (isSelectingShape) {
                        int mode = selectedShape.checkOnEdit(px, py, 25);
                        if (mode!=0) {
                            editlock = true;
                            return true;
                        }
                    }
                }

                Shape newSelected = null;
                for(int i = d.currentPage.shapeList.size() - 1; i >= 0; i-- ){
                    Shape s = d.currentPage.shapeList.get(i);
                    if(s.inRange(event)){
                        newSelected = s;
                        s.setSelected(true);
                        if (!downlock) {
                            downx = px-s.getPositionX();
                            downy = py-s.getPositionY();
                            downlock=true;
                        }
                        break;
                    }
                }
                if(newSelected != d.currentPage.currentShape){
                    if (d.currentPage.currentShape != null) {
                        d.currentPage.currentShape.onCancel();
                    }
                    d.currentPage.currentShape = newSelected;
                }

                if (mySingleton.getInstance().savedDocument.isEdit) {
                    Button shapeBox = (Button)((EditMode) context).findViewById(R.id.editShapeBox);
                    Button delShapeButton = (Button)((EditMode) context).findViewById(R.id.delShape);
                    // show button/ del button
                    if (d.currentPage.currentShape == null){
                        shapeBox.setVisibility(View.VISIBLE);
                        delShapeButton.setVisibility(View.GONE);
                        shapeBox.invalidate();
                        delShapeButton.invalidate();
                    }

                    if (d.currentPage.currentShape != null){
                        shapeBox.setVisibility(View.GONE);
                        delShapeButton.setVisibility(View.VISIBLE);
                        shapeBox.invalidate();
                        delShapeButton.invalidate();
                    }
                }



                break;
            case MotionEvent.ACTION_UP:
                downlock = false;
                editlock = false;
                if(d.currentPage.currentShape != null
                        && d.currentPage.currentShape.getActive()
                        && !d.isEdit){
                    if(Math.abs(event.getX()-px) < 1e-10
                            && Math.abs(event.getY()-py) < 1e-10){
                        System.out.print("onclickkkkkkkkkkkkkkkkkkkk");
                        d.currentPage.currentShape.doTrigger(context, "on click");
                    } else {
                        System.out.print("ondroppppppppppppppppppppp");
                        d.currentPage.currentShape.doTrigger(context, "on drop");
                    }
                }
                break;
        }
        if(d.currentPage.currentShape != null){
            d.currentPage.currentShape.parseMotionEvent(event, downx, downy);
        }
        System.out.println("invalidate");
        invalidate();

        return true;
    }

    public void setPageAndDraw(Page p){
        d.currentPage = p;
        System.out.println("insetpageanddraw: "+p.name);
        System.out.println("BOWEN: In setpageanddraw");
        try{
            System.out.println("BOWEN: In try");
            if(!mySingleton.getInstance().savedDocument.isEdit) {
                ((PlayMode) getContext()).updateTitle();
            }
            //set background
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), d.currentPage.backgroundImageId);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
            if(d.currentPage.isBackgroundTiled){
                bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            }
            setBackground(bitmapDrawable);
//            setBackgroundResource(d.currentPage.backgroundImageId);

            if (mySingleton.getInstance().savedDocument.isEdit) {
                System.out.println("BOWEN: In isEdit");
                System.out.println("BOWEN: " + mySingleton.getInstance().savedDocument.isEdit);

                Button shapeBox = (Button)((EditMode) context).findViewById(R.id.editShapeBox);
                Button delShapeButton = (Button)((EditMode) context).findViewById(R.id.delShape);
                // show button/ del button
                if (d.currentPage.currentShape == null){
                    System.out.println("BOWEN: In NULL");
                    shapeBox.setVisibility(View.VISIBLE);
                    delShapeButton.setVisibility(View.GONE);
                    shapeBox.invalidate();
                    delShapeButton.invalidate();
                }

                if (d.currentPage.currentShape != null){
                    System.out.println("BOWEN: In not  NULL");
                    shapeBox.setVisibility(View.GONE);
                    delShapeButton.setVisibility(View.VISIBLE);
                    shapeBox.invalidate();
                    delShapeButton.invalidate();
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }

        invalidate();
    }

    public void setPage(Page p){
        d.currentPage = p;
    }

    public void draw(){
        try{
            ((PlayMode) getContext()).updateTitle();
        }catch(Exception e){
            System.out.println(e);
        }
        invalidate();
    }

    public void onEnter(){
        if(d.currentPage != null){
            for(Shape s : d.currentPage.shapeList){
                if(s.getActive()){
                    System.out.println("&&&&&&&&&&&&&&&&&&onEnter:   ");
                    s.doTrigger(context, "on enter");
                }
            }
        }
    }

}
