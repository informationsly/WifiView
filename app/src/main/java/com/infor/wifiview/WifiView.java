package com.infor.wifiview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class WifiView extends View{
	private float VIEW_SIZE;
	private float BORDER_WIDTH;
	private float POINT_SIZE;
	private Paint paint;
	private float centerX,centerY;
	
	private int red = Color.rgb(255, 93, 93);
	private int gray = Color.GRAY;
	private boolean isRed[] = {true,true,true};
	private int count = 0;
	private Handler handler;
	private Runnable runnable;
	private boolean animIsStart = false;
	private float scale = 0;
	private Path mPath;
	
	public WifiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		/**
		 * 当view的height为wrap_content时，VIEW_SIZE,BORDER_WIDTH,POINT_SIZE使用默认值绘图，
		 * 当view的height为具体值或match_parent时,VIEW_SIZE,BORDER_WIDTH,POINT_SIZE的值根据height得到
		 */
		Resources resources = context.getResources();
		VIEW_SIZE = resources.getDimension(R.dimen.wifiview_view_size);
		BORDER_WIDTH = resources.getDimension(R.dimen.wifiview_border_width);
		POINT_SIZE = resources.getDimensionPixelSize(R.dimen.wifiview_point_size);
		scale = getResources().getDisplayMetrics().density;
		initPaint();
	}

	private void initPaint(){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPath = new Path();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		centerX = getWidth()/2;
		centerY = getHeight()/2;
		
		paint.setStyle(Style.FILL);
		paint.setColor(Color.argb(127, 255, 255, 255));
		canvas.drawCircle(centerX, centerY, VIEW_SIZE+BORDER_WIDTH, paint);
		
		paint.setColor(getResources().getColor(android.R.color.white));
		canvas.drawCircle(centerX, centerY, VIEW_SIZE, paint);
		
		//画点
		paint.setColor(red);
		float incre = VIEW_SIZE*3/5;
		float pointX = centerX;
		float pointY = centerY+incre;
		canvas.drawCircle(pointX, pointY, POINT_SIZE, paint);
		
		//使用贝塞尔曲线画弧线
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(BORDER_WIDTH);
        
        float aveY = (VIEW_SIZE + incre)/3;
        float aveX = (float)Math.sqrt(Math.pow(VIEW_SIZE, 2)+Math.pow(incre, 2))/4;
        
        //第一条弧线
        if(isRed[0]){
        	paint.setColor(red);
        }else{
        	paint.setColor(gray);
        }
        mPath.moveTo(pointX-aveX, pointY-aveX);
        float firstBezPoint = pointY - aveY;
        mPath.quadTo(pointX, firstBezPoint, pointX+aveX, pointY-aveX); 
        canvas.drawPath(mPath, paint);
        mPath.reset();
        
        //第二条弧线
		if (isRed[1]) {
			paint.setColor(red);
		} else {
			paint.setColor(gray);
		}
        mPath.moveTo(pointX-2*aveX, pointY-2*aveX);
        float secBezPoint = firstBezPoint - aveY;
        mPath.quadTo(pointX, secBezPoint, pointX+2*aveX, pointY-2*aveX);
        canvas.drawPath(mPath, paint);
        mPath.reset();//不调用reset,paint.setColor(red)没有效果
        
        //第三条弧线
        if (isRed[2]) {
			paint.setColor(red);
		} else {
			paint.setColor(gray);
		}
        float thirBezPoint = secBezPoint - aveY;
        mPath.moveTo(pointX-3*aveX, pointY-3*aveX);
        mPath.quadTo(pointX, thirBezPoint, pointX+3*aveX, pointY-3*aveX);
        canvas.drawPath(mPath, paint);
        mPath.reset();
	}
	
	/**
	 * 宽度和高度一样，宽度取高度的值
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float width,height;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
        if(heightMode == MeasureSpec.EXACTLY){
        	height = heightSize;
        	VIEW_SIZE = height/2-height/20;
        	POINT_SIZE = (float)(VIEW_SIZE/7.5);
        	BORDER_WIDTH = (float)(VIEW_SIZE/10);
        }else{
        	// Measure the text
        	height = 2*(VIEW_SIZE + BORDER_WIDTH);
            if (heightMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
            	height = Math.min(height, heightSize);
            }
        }
        
        width = height;
		setMeasuredDimension((int)width, (int)height);
	}
	
	private void setRectColor(){
		switch ((count++) %3) {
		case 0:
			isRed[0] = true;
			isRed[1] = false;
			isRed[2] = false;
			break;
		case 1:
			isRed[0] = false;
			isRed[1] = true;
			isRed[2] = false;
			break;
		case 2:
			isRed[0] = false;
			isRed[1] = false;
			isRed[2] = true;
			break;
		default:
			if(count >=2){
				count = 0;
			}
		}
		invalidate();
	}
	
	public void startAnimation(){
		if(handler == null){
			handler = new Handler();
			runnable = new Runnable() {
				
				@Override
				public void run() {
					setRectColor();
					handler.postDelayed(this, 500);
				}
			};
			handler.post(runnable);
			animIsStart = true;
		}
	}
	
	public void stopAnimation(){
		if(handler != null){
			handler.removeCallbacks(runnable);
			animIsStart = false;
			handler = null;
		}
		for(int i=0;i<isRed.length;i++){
			isRed[i] = true;
		}
		count = 0;
		setClickable(true);
		invalidate();
	}
	
	public void setLevel(int level){
		for(int i=0;i<isRed.length;i++){
			if(i<level){
				isRed[i] = true;
			}else{
				isRed[i] = false;
			}
		}
		invalidate();
	}
	
	public boolean animIsStart(){
		return animIsStart;
	}
}
