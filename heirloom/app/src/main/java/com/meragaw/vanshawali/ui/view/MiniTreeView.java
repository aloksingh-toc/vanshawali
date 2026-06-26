package com.meragaw.vanshawali.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.meragaw.vanshawali.R;
import com.meragaw.vanshawali.model.FamilyMember;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Small static (non-interactive) thumbnail of the family tree, used as a preview
 * card on the Home screen. Scales its layout to fit whatever bounds it's given.
 */
public class MiniTreeView extends View {

    private static final float DOT_RADIUS_DP = 6f;

    private List<FamilyMember> members = new ArrayList<>();
    private final TreeMap<Integer, List<FamilyMember>> rows = new TreeMap<>();
    private final java.util.Map<String, PointF> positions = new java.util.HashMap<>();

    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float dotRadiusPx;

    public MiniTreeView(Context context) {
        this(context, null);
    }

    public MiniTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dotRadiusPx = DOT_RADIUS_DP * context.getResources().getDisplayMetrics().density;

        dotPaint.setColor(ContextCompat.getColor(context, R.color.heirloom_primary));
        dotPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(ContextCompat.getColor(context, R.color.heirloom_border_accent));
        linePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.tree_connector_mini_height) / 9f);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setTreeData(List<FamilyMember> treeMembers) {
        this.members = treeMembers != null ? treeMembers : new ArrayList<>();
        computeLayout();
        invalidate();
    }

    private void computeLayout() {
        rows.clear();
        positions.clear();

        for (FamilyMember member : members) {
            rows.computeIfAbsent(member.generation, g -> new ArrayList<>()).add(member);
        }
        if (rows.isEmpty() || getWidth() == 0 || getHeight() == 0) return;

        int rowCount = rows.size();
        float rowHeight = getHeight() / (float) (rowCount + 1);
        int rowIndex = 0;

        for (Map.Entry<Integer, List<FamilyMember>> entry : rows.entrySet()) {
            List<FamilyMember> rowMembers = entry.getValue();
            float colWidth = getWidth() / (float) (rowMembers.size() + 1);
            float y = rowHeight * (rowIndex + 1);

            for (int i = 0; i < rowMembers.size(); i++) {
                float x = colWidth * (i + 1);
                positions.put(rowMembers.get(i).id, new PointF(x, y));
            }
            rowIndex++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        computeLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (FamilyMember member : members) {
            if (member.parentId == null) continue;
            PointF from = positions.get(member.parentId);
            PointF to = positions.get(member.id);
            if (from == null || to == null) continue;
            canvas.drawLine(from.x, from.y, to.x, to.y, linePaint);
        }

        for (PointF point : positions.values()) {
            canvas.drawCircle(point.x, point.y, dotRadiusPx, dotPaint);
        }
    }
}
