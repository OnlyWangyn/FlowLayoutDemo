package widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nancy on 15-9-15.
 */
public class FlowLayout extends ViewGroup {


    //each item of this list is also list which contains all views in one line
    private List<List<View>> mLineViewsList = new ArrayList<>();
    //item is the height of each line
    private List<Integer> mLineHeightList = new ArrayList<>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //Make this kind of ViewGroup support Margin
    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLineViewsList.clear();
        mLineHeightList.clear();

        ArrayList<View> singleLineViewList = new ArrayList<>();

        //int takes up 4 bytes,32 bits,high 16 bites means mode,low 16 bites means size
        //Mode EXACTLY,AT_MOST,UNSPECIFIED
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int maxWidth = 0;// total width of this flowlayout
        int maxHeight = 0;//total height of this flowlayout
        int singleLineWidth = 0;
        int singleLineHeight = 0;

        //calculate size of child
        for(int index = 0;index < getChildCount();index++){
            View child = getChildAt(index);
            if(child.getVisibility() != View.GONE){
                measureChild(child,widthMeasureSpec,heightMeasureSpec);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams)child.getLayoutParams();

                int childWidth = child.getMeasuredWidth() + marginLayoutParams.leftMargin
                        + marginLayoutParams.rightMargin;
                int childHeight = child.getMeasuredHeight() + marginLayoutParams.topMargin
                        +marginLayoutParams.bottomMargin;

                if((singleLineWidth + childWidth) <= sizeWidth){//still in one line
                    singleLineWidth += childWidth;
                    singleLineHeight = Math.max(singleLineHeight,childHeight);
                    singleLineViewList.add(child);

                }else{
                    //have to change to new line
                    maxWidth = Math.max(singleLineWidth,childWidth);
                    mLineHeightList.add(singleLineHeight);// currently singleLineHeight is the height of last line
                    maxHeight += singleLineHeight;
                    singleLineWidth = childWidth;// initial width value of new line
                    singleLineHeight = childHeight;// initial height value of new line

                    mLineViewsList.add(singleLineViewList);
                    singleLineViewList = new ArrayList<>();
                    singleLineViewList.add(child);

                }

                if (index == getChildCount() - 1) {
                    maxWidth =  Math.max(singleLineWidth,childWidth);
                    maxHeight += singleLineHeight;
                }
            }
        }

        mLineHeightList.add(singleLineHeight);
        mLineViewsList.add(singleLineViewList);

        if(MeasureSpec.EXACTLY != modeWidth){
            sizeWidth = maxWidth;
        }
        if(MeasureSpec.EXACTLY != modeHeight){
            sizeHeight = maxHeight;
        }
        setMeasuredDimension(sizeWidth,sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int top = 0;
        int left = 0;
        for(int i =0;i<mLineViewsList.size();i++){
            List<View> singleLineViews = mLineViewsList.get(i);
            for(View view:singleLineViews){
                MarginLayoutParams lp = (MarginLayoutParams) view
                        .getLayoutParams();
                int childLeft = left + lp.leftMargin;
                int childTop = top + lp.topMargin;
                int childRight = childLeft + view.getMeasuredWidth();
                int childBottom =  childTop + view.getMeasuredHeight();
                view.layout(childLeft,childTop,childRight,childBottom);
                left += view.getMeasuredWidth() + lp.rightMargin
                        + lp.leftMargin;
            }
            //start new line ,reset the start position in X,increase the start position in Y
            left = 0;
            int lineHeight = mLineHeightList.get(i).intValue();
            top+=lineHeight;
        }
    }
}
