package com.meragaw.vanshawali.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.model.FamilyMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Pannable, pinch-zoomable canvas that draws the family tree: dot-grid background,
 * generation rows of avatar circles, parent→child connectors, spouse hearts, and
 * generation pill labels. Tapping a node reports back via {@link OnMemberTappedListener}.
 */
public class FamilyTreeView extends View {

    public interface OnMemberTappedListener {
        void onMemberTapped(String memberId);
    }

    private static final float ZOOM_MIN = 0.5f;
    private static final float ZOOM_MAX = 3.0f;

    private static final float DOT_SPACING_DP = 22f;
    private static final float DOT_RADIUS_DP = 1.1f;
    private static final float AVATAR_RADIUS_DP = 27f;
    private static final float NODE_H_SPACING_DP = 90f;
    private static final float NODE_V_SPACING_DP = 130f;
    private static final float RING_STROKE_DP = 2.5f;
    private static final float RING_PADDING_DP = 4f;

    private List<FamilyMember> members = new ArrayList<>();
    private final Map<String, FamilyMember> byId = new HashMap<>();
    private final Map<String, PointF> positions = new HashMap<>();
    private final TreeMap<Integer, List<FamilyMember>> rows = new TreeMap<>();

    private float scaleFactor = 1f;
    private float translateX = 0f;
    private float translateY = 0f;

    private OnMemberTappedListener tappedListener;

    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint avatarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nameTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint initialTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint heartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pillTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleGestureDetector;

    private final float avatarRadiusPx;
    private final float dotSpacingPx;
    private final float dotRadiusPx;
    private final float nodeHSpacingPx;
    private final float nodeVSpacingPx;
    private final float ringStrokePx;
    private final float ringPaddingPx;

    public FamilyTreeView(Context context) {
        this(context, null);
    }

    public FamilyTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        float density = context.getResources().getDisplayMetrics().density;
        avatarRadiusPx = AVATAR_RADIUS_DP * density;
        dotSpacingPx = DOT_SPACING_DP * density;
        dotRadiusPx = DOT_RADIUS_DP * density;
        nodeHSpacingPx = NODE_H_SPACING_DP * density;
        nodeVSpacingPx = NODE_V_SPACING_DP * density;
        ringStrokePx = RING_STROKE_DP * density;
        ringPaddingPx = RING_PADDING_DP * density;

        dotPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_tree_dot));
        dotPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(ContextCompat.getColor(context, R.color.heirloom_border_accent));
        linePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.tree_connector_width));
        linePaint.setStyle(Paint.Style.STROKE);

        ringPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_primary));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ringStrokePx);

        nameTextPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_on_surface));
        nameTextPaint.setTextAlign(Paint.Align.CENTER);
        nameTextPaint.setFakeBoldText(true);
        nameTextPaint.setTextSize(11f * density);

        initialTextPaint.setColor(ContextCompat.getColor(context, R.color.white));
        initialTextPaint.setTextAlign(Paint.Align.CENTER);
        initialTextPaint.setFakeBoldText(true);
        initialTextPaint.setTextSize(18f * density);

        heartPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_love_heart));
        heartPaint.setStyle(Paint.Style.FILL);

        pillPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_surface));
        pillPaint.setStyle(Paint.Style.FILL);

        pillTextPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_on_surface_subtle));
        pillTextPaint.setTextAlign(Paint.Align.LEFT);
        pillTextPaint.setFakeBoldText(true);
        pillTextPaint.setTextSize(10f * density);
        pillTextPaint.setLetterSpacing(0.05f);

        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setOnMemberTappedListener(OnMemberTappedListener listener) {
        this.tappedListener = listener;
    }

    public void setTreeData(List<FamilyMember> treeMembers) {
        this.members = treeMembers != null ? treeMembers : new ArrayList<>();
        computeLayout();
        invalidate();
    }

    public float getScale() {
        return scaleFactor;
    }

    public void setScale(float scale, boolean animate) {
        float target = Math.max(ZOOM_MIN, Math.min(scale, ZOOM_MAX));
        if (animate) {
            ValueAnimator animator = ValueAnimator.ofFloat(scaleFactor, target);
            animator.addUpdateListener(a -> {
                scaleFactor = (float) a.getAnimatedValue();
                invalidate();
            });
            animator.setDuration(200);
            animator.start();
        } else {
            scaleFactor = target;
            invalidate();
        }
    }

    private void computeLayout() {
        byId.clear();
        positions.clear();
        rows.clear();

        for (FamilyMember member : members) {
            byId.put(member.id, member);
            rows.computeIfAbsent(member.generation, g -> new ArrayList<>()).add(member);
        }

        for (Map.Entry<Integer, List<FamilyMember>> entry : rows.entrySet()) {
            List<FamilyMember> rowMembers = orderWithSpouses(entry.getValue());
            float rowWidth = (rowMembers.size() - 1) * nodeHSpacingPx;
            float startX = -rowWidth / 2f;
            float y = entry.getKey() * nodeVSpacingPx;

            for (int i = 0; i < rowMembers.size(); i++) {
                float x = startX + i * nodeHSpacingPx;
                positions.put(rowMembers.get(i).id, new PointF(x, y));
            }
        }
    }

    /** Reorders a generation row so spouses end up as adjacent neighbors. */
    private List<FamilyMember> orderWithSpouses(List<FamilyMember> rowMembers) {
        List<FamilyMember> ordered = new ArrayList<>();
        List<FamilyMember> remaining = new ArrayList<>(rowMembers);

        while (!remaining.isEmpty()) {
            FamilyMember member = remaining.remove(0);
            ordered.add(member);
            if (member.spouseId != null) {
                for (int i = 0; i < remaining.size(); i++) {
                    if (member.spouseId.equals(remaining.get(i).id)) {
                        ordered.add(remaining.remove(i));
                        break;
                    }
                }
            }
        }
        return ordered;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDotGrid(canvas);

        canvas.save();
        canvas.translate(getWidth() / 2f + translateX, getHeight() / 2f + translateY);
        canvas.scale(scaleFactor, scaleFactor);

        drawConnectors(canvas);
        drawSpouseHearts(canvas);
        drawNodes(canvas);

        canvas.restore();

        drawGenerationPills(canvas);
    }

    private void drawDotGrid(Canvas canvas) {
        for (float x = 0; x < getWidth(); x += dotSpacingPx) {
            for (float y = 0; y < getHeight(); y += dotSpacingPx) {
                canvas.drawCircle(x, y, dotRadiusPx, dotPaint);
            }
        }
    }

    private void drawConnectors(Canvas canvas) {
        for (FamilyMember member : members) {
            if (member.parentId == null) continue;
            PointF from = positions.get(member.parentId);
            PointF to = positions.get(member.id);
            if (from == null || to == null) continue;

            float midY = (from.y + to.y) / 2f;
            Path path = new Path();
            path.moveTo(from.x, from.y + avatarRadiusPx);
            path.lineTo(from.x, midY);
            path.lineTo(to.x, midY);
            path.lineTo(to.x, to.y - avatarRadiusPx);
            canvas.drawPath(path, linePaint);
        }
    }

    private void drawSpouseHearts(Canvas canvas) {
        for (FamilyMember member : members) {
            if (member.spouseId == null || member.id.compareTo(member.spouseId) > 0) continue;
            PointF a = positions.get(member.id);
            PointF b = positions.get(member.spouseId);
            if (a == null || b == null) continue;

            float midX = (a.x + b.x) / 2f;
            float midY = (a.y + b.y) / 2f;
            drawHeart(canvas, midX, midY, avatarRadiusPx * 0.32f);
        }
    }

    private void drawHeart(Canvas canvas, float cx, float cy, float size) {
        Path path = new Path();
        path.moveTo(cx, cy + size * 0.7f);
        path.cubicTo(cx - size * 1.4f, cy - size * 0.5f, cx - size * 0.4f, cy - size * 1.4f, cx, cy - size * 0.4f);
        path.cubicTo(cx + size * 0.4f, cy - size * 1.4f, cx + size * 1.4f, cy - size * 0.5f, cx, cy + size * 0.7f);
        path.close();
        canvas.drawPath(path, heartPaint);
    }

    private void drawNodes(Canvas canvas) {
        for (FamilyMember member : members) {
            PointF point = positions.get(member.id);
            if (point == null) continue;

            avatarPaint.setColor(ContextCompat.getColor(getContext(), member.getAvatarColorRes()));
            canvas.drawCircle(point.x, point.y, avatarRadiusPx, avatarPaint);

            if (member.isCurrentUser) {
                canvas.drawCircle(point.x, point.y, avatarRadiusPx + ringPaddingPx, ringPaint);
            }

            float textY = point.y - ((initialTextPaint.descent() + initialTextPaint.ascent()) / 2f);
            canvas.drawText(member.getInitial(), point.x, textY, initialTextPaint);

            String name = member.firstName != null ? member.firstName : "";
            canvas.drawText(name, point.x, point.y + avatarRadiusPx + nameTextPaint.getTextSize() + 6, nameTextPaint);
        }
    }

    private void drawGenerationPills(Canvas canvas) {
        int[] labelRes = {
            R.string.tree_label_grandparents,
            R.string.tree_label_children,
            R.string.tree_label_grandchildren,
            R.string.tree_label_great_grandchildren
        };

        float top = 12f * getResources().getDisplayMetrics().density;
        float left = 12f * getResources().getDisplayMetrics().density;
        float y = top;

        for (Integer generation : rows.keySet()) {
            String label = (generation >= 1 && generation <= labelRes.length)
                ? getContext().getString(labelRes[generation - 1])
                : "GEN " + generation;

            float textWidth = pillTextPaint.measureText(label);
            float pad = 8f * getResources().getDisplayMetrics().density;
            RectF rect = new RectF(left, y, left + textWidth + pad * 2, y + pillTextPaint.getTextSize() + pad);
            canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, pillPaint);
            canvas.drawText(label, rect.left + pad, rect.bottom - pad * 0.9f, pillTextPaint);

            y += rect.height() + (8f * getResources().getDisplayMetrics().density);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private FamilyMember findMemberAt(float screenX, float screenY) {
        float contentX = (screenX - getWidth() / 2f - translateX) / scaleFactor;
        float contentY = (screenY - getHeight() / 2f - translateY) / scaleFactor;

        for (FamilyMember member : members) {
            PointF point = positions.get(member.id);
            if (point == null) continue;
            float dx = contentX - point.x;
            float dy = contentY - point.y;
            if (Math.sqrt(dx * dx + dy * dy) <= avatarRadiusPx) {
                return member;
            }
        }
        return null;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            translateX -= distanceX;
            translateY -= distanceY;
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            FamilyMember tapped = findMemberAt(e.getX(), e.getY());
            if (tapped != null && tappedListener != null) {
                tappedListener.onMemberTapped(tapped.id);
            }
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(ZOOM_MIN, Math.min(scaleFactor, ZOOM_MAX));
            invalidate();
            return true;
        }
    }
}
