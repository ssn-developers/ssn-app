package in.edu.ssn.ssnapp.message_utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import in.edu.ssn.ssnapp.R;


public class SwipeController extends ItemTouchHelper.Callback {

    private Context mContext;
    private ISwipeControllerActions mSwipeControllerActions;

    private Drawable mReplyIcon;
    private Drawable mReplyIconBackground;

    private RecyclerView.ViewHolder mCurrentViewHolder;
    private View mView;

    private float mDx = 0f;

    private float mReplyButtonProgress = 0f;
    private long mLastReplyButtonAnimationTime = 0;

    private boolean mSwipeBack = false;
    private boolean mIsVibrating = false;
    private boolean mStartTracking = false;

    private int mBackgroundColor = 0x20606060;

    private int mReplyBackgroundOffset = 18;

    private int mReplyIconXOffset = 12;
    private int mReplyIconYOffset = 11;

    public SwipeController(Context context, ISwipeControllerActions swipeControllerActions) {
        mContext = context;
        mSwipeControllerActions = swipeControllerActions;

        mReplyIcon = mContext.getResources().getDrawable(R.drawable.chat_ic_reply);
        mReplyIconBackground = mContext.getResources().getDrawable(R.drawable.chat_ic_round_shape);
    }


    public SwipeController(Context context, ISwipeControllerActions swipeControllerActions, int replyIcon, int replyIconBackground, int backgroundColor) {
        mContext = context;
        mSwipeControllerActions = swipeControllerActions;

        mReplyIcon = mContext.getResources().getDrawable(replyIcon);
        mReplyIconBackground = mContext.getResources().getDrawable(replyIconBackground);
        mBackgroundColor = backgroundColor;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        mView = viewHolder.itemView;
        return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (mSwipeBack) {
            mSwipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder);
        }
        if (mView.getTranslationX() < convertToDp(130) || dX < mDx) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            mDx = dX;
            mStartTracking = true;
        }
        mCurrentViewHolder = viewHolder;
        drawReplyButton(c);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSwipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (mSwipeBack) {
                    if (Math.abs(mView.getTranslationX()) >= convertToDp(100)) {
                        mSwipeControllerActions.onSwipePerformed(viewHolder.getAdapterPosition());
                    }
                }
                return false;
            }
        });
    }

    private int convertToDp(int pixels) {
        return Utils.getDP((float) pixels, mContext);
    }


    private void drawReplyButton(Canvas canvas) {
        if (mCurrentViewHolder == null) {
            return;
        }

        float translationX = mView.getTranslationX();
        long newTime = System.currentTimeMillis();
        long dt = Math.min(17, newTime - mLastReplyButtonAnimationTime);
        mLastReplyButtonAnimationTime = newTime;
        boolean showing = false;
        if (translationX >= convertToDp(30)) {
            showing = true;
        }
        if (showing) {
            if (mReplyButtonProgress < 1.0f) {
                mReplyButtonProgress += dt / 180.0f;
                if (mReplyButtonProgress > 1.0f) {
                    mReplyButtonProgress = 1.0f;
                } else {
                    mView.invalidate();
                }
            }
        } else if (translationX <= 0.0f) {
            mReplyButtonProgress = 0f;
            mStartTracking = false;
            mIsVibrating = false;
        } else {
            if (mReplyButtonProgress > 0.0f) {
                mReplyButtonProgress -= dt / 180.0f;
                if (mReplyButtonProgress < 0.1f) {
                    mReplyButtonProgress = 0f;
                }
            }
            mView.invalidate();
        }
        int alpha;
        float scale;
        if (showing) {
            if (mReplyButtonProgress <= 0.8f) {
                scale = 1.2f * (mReplyButtonProgress / 0.8f);
            } else {
                scale = 1.2f - 0.2f * ((mReplyButtonProgress - 0.8f) / 0.2f);
            }
            alpha = Math.min(255, 255 * ((int) (mReplyButtonProgress / 0.8f)));
        } else {
            scale = mReplyButtonProgress;
            alpha = Math.min(255, 255 * (int) mReplyButtonProgress);
        }
        mReplyIconBackground.setAlpha(alpha);
        mReplyIcon.setAlpha(alpha);
        if (mStartTracking) {
            if (!mIsVibrating && mView.getTranslationX() >= convertToDp(100)) {
                mView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
            mIsVibrating = true;
        }

        int x;
        float y;
        if (mView.getTranslationX() > convertToDp(130)) {
            x = convertToDp(130) / 2;
        } else {
            x = (int) mView.getTranslationX() / 2;
        }

        y = mView.getTop() + ((float) mView.getMeasuredHeight() / 2);
        mReplyIconBackground.setColorFilter(mBackgroundColor, PorterDuff.Mode.MULTIPLY);

        mReplyIconBackground.setBounds(new Rect(
                (int) (x - convertToDp(mReplyBackgroundOffset) * scale),
                (int) (y - convertToDp(mReplyBackgroundOffset) * scale),
                (int) (x + convertToDp(mReplyBackgroundOffset) * scale),
                (int) (y + convertToDp(mReplyBackgroundOffset) * scale)
        ));
        mReplyIconBackground.draw(canvas);

        mReplyIcon.setBounds(new Rect(
                (int) (x - convertToDp(mReplyIconXOffset) * scale),
                (int) (y - convertToDp(mReplyIconYOffset) * scale),
                (int) (x + convertToDp(mReplyIconXOffset) * scale),
                (int) (y + convertToDp(mReplyIconYOffset) * scale)
        ));
        mReplyIcon.draw(canvas);

        mReplyIconBackground.setAlpha(255);
        mReplyIcon.setAlpha(255);
    }

}


