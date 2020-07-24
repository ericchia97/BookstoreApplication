package com.example.bookstoreapplication.Helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeButton extends ItemTouchHelper.SimpleCallback {

    int ButtonWidth;
    private RecyclerView recyclerView;
    private List<DeleteButton> ButtonList;
    private GestureDetector gestureDetector;
    private int SwipePosition = -1;
    private float SwipeThreshold = 0.5f;
    private Map<Integer, List<DeleteButton>> buttonActivation;
    private Queue<Integer> remover;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for(DeleteButton button : ButtonList){
                if(button.onClick(e.getX(),e.getY())){
                    break;}
            }
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(SwipePosition < 0){
                return false;}

            Point point = new Point((int)event.getRawX(), (int)event.getRawY());

            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(SwipePosition);
            View swipeItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipeItem.getGlobalVisibleRect(rect);

            if(event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_MOVE){
                if(rect.top < point.y && rect.bottom > point.y){
                    gestureDetector.onTouchEvent(event);
                }else{
                    remover.add(SwipePosition);
                    SwipePosition = -1;
                    RecoverSwipedItem();
                }
            }
            return false;
        }
    };

    private synchronized void RecoverSwipedItem() {
        while(!remover.isEmpty()){
            int pos = remover.poll();
            if(pos >-1){
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    public SwipeButton(Context c, RecyclerView recyclerView, int ButtonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.ButtonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(c, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonActivation = new HashMap<>();
        this.ButtonWidth = ButtonWidth;

        remover = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                if(contains(o)){
                    return false;
                }else{
                    return super.add(o);
                }
            }
        };
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public class DeleteButton {
        private String text;
        private int imageResId, textSize, color, pos;
        private RectF clickRegion;
        private deleteOnClickListener listener;
        private Context c;
        private Resources resources;

        public DeleteButton(Context c, String text, int imageResId, int textSize, int color, deleteOnClickListener listener) {
            this.text = text;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.color = color;
            this.listener = listener;
            this.c = c;
            resources = c.getResources();
        }

        public boolean onClick(float x, float y){
            if(clickRegion != null && clickRegion.contains(x,y)) {
                listener.onClick(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas canvas, RectF rectF, int pos){
            Paint p = new Paint();
            p.setColor(color);
            canvas.drawRect(rectF, p);
            p.setColor(Color.WHITE);
            p.setTextSize(textSize);

            Rect r = new Rect();
            float cWidth = rectF.height();
            float cHeight = rectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x=0, y=0;

            if(imageResId == 0){
                x = cWidth/2f - r.width()/2f - r.left;
                y = cHeight/2f - r.height()/2f - r.bottom;
                canvas.drawText(text, rectF.left+x, rectF.top+y, p);
            }else{

                Drawable d = ContextCompat.getDrawable(c,imageResId);
                Bitmap bitmap = drawableToBitmap(d);
                float bw = bitmap.getWidth()/2;
                float bh = bitmap.getHeight()/2;
                canvas.drawBitmap(bitmap, (rectF.left+rectF.right)/2-bw, (rectF.top+rectF.bottom)/2-bh, p);
            }

            clickRegion = rectF;
            this.pos = pos;
        }
    }

    private Bitmap drawableToBitmap(Drawable d){
        if(d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if(SwipePosition != pos){
            remover.add(SwipePosition);
        }
        SwipePosition = pos;
        if(buttonActivation.containsKey(SwipePosition)){
            ButtonList = buttonActivation.get(SwipePosition);
        }else{
            ButtonList.clear();
        }
        buttonActivation.clear();
        SwipeThreshold = 0.5f * ButtonList.size() * ButtonWidth;
        RecoverSwipedItem();
    }

    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return SwipeThreshold;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if(pos < 0){
            SwipePosition = pos;
            return;
        }
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if(dX < 0){
                List<DeleteButton> buffer = new ArrayList<>();
                if(!buttonActivation.containsKey(pos)){
                    instantiateDeleteButton(viewHolder, buffer);
                    buttonActivation.put(pos, buffer);
                }else{
                    buffer = buttonActivation.get(pos);
                }
                translationX = dX * buffer.size() * ButtonWidth / itemView.getWidth();
                drawButton(c, itemView, buffer, pos, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState,isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<DeleteButton> buffer, int pos, float translationX) {
        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / buffer.size();
        for(DeleteButton button : buffer){
            float left = right - dButtonWidth;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public abstract void instantiateDeleteButton(RecyclerView.ViewHolder viewHolder, List<DeleteButton> buffer);
}
