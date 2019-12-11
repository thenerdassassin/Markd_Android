package com.schmidthappens.markd.AdapterClasses;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schmidthappens.markd.R;
import com.schmidthappens.markd.customer_menu_activities.PaintingActivity;
import com.schmidthappens.markd.data_objects.PaintSurface;
import com.schmidthappens.markd.utilities.StringUtilities;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PaintingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditHomeRecyclerAdapter";

    private final String EXTERIOR_TITLE = "Exterior Surfaces";
    private final String INTERIOR_TITLE = "Interior Surfaces";
    private PaintingActivity context;
    private final List<PaintSurface> exteriorPaintSurfaces;
    private final List<PaintSurface> interiorPaintSurfaces;
    private final int EXTERIOR_TITLE_INDEX = 0;
    private final int INTERIOR_TITLE_INDEX;
    private final int itemCount;

    public PaintingRecyclerViewAdapter(
            final PaintingActivity context,
            @NonNull final List<PaintSurface> exteriorPaintSurfaces,
            @NonNull final List<PaintSurface> interiorPaintSurfaces) {
        this.context = context;

        this.exteriorPaintSurfaces = exteriorPaintSurfaces;
        if (exteriorPaintSurfaces.size() == 0) {
            INTERIOR_TITLE_INDEX = 2;
        } else {
            INTERIOR_TITLE_INDEX = 1 + exteriorPaintSurfaces.size(); // 1+2 = 3
        }

        this.interiorPaintSurfaces = interiorPaintSurfaces;
        if (interiorPaintSurfaces.size() == 0) {
            itemCount = 1 + INTERIOR_TITLE_INDEX;
        } else {
            itemCount = 1 + INTERIOR_TITLE_INDEX + interiorPaintSurfaces.size(); //1+3+5 = 9
        }
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.view_holder_title_button:
                return new TitleButtonViewHolder(view);
            case R.layout.list_row_paint:
                return new PaintSurfaceViewHolder(view);
            default:
                throw new IllegalArgumentException(String.format("No view type for %d", viewType));
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof TitleButtonViewHolder) {
            if(position == INTERIOR_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(INTERIOR_TITLE);
            } else if (position == EXTERIOR_TITLE_INDEX) {
                ((TitleButtonViewHolder) viewHolder).bindData(EXTERIOR_TITLE);
            }
        } else if (viewHolder instanceof PaintSurfaceViewHolder) {
            if(position < INTERIOR_TITLE_INDEX) {
                if (exteriorPaintSurfaces.size() == 0) {
                    ((PaintSurfaceViewHolder) viewHolder).bindData();
                } else {
                    int index = position - 1;
                    ((PaintSurfaceViewHolder) viewHolder)
                            .bindData(exteriorPaintSurfaces.get(index), true, index);
                }
            } else if (position > INTERIOR_TITLE_INDEX) {
                if (interiorPaintSurfaces.size() == 0) {
                    ((PaintSurfaceViewHolder) viewHolder).bindData();
                } else {
                    // Position - 2 Title Rows - ExteriorPaintSurface Rows
                    int index = position - 2 - exteriorPaintSurfaces.size();
                    if (exteriorPaintSurfaces.size() == 0) {
                        index -= 1; //Minus one for the "Add paint" row
                    }
                    ((PaintSurfaceViewHolder) viewHolder)
                            .bindData(interiorPaintSurfaces.get(index), false, index);
                }
            }
        } else {
            throw new IllegalArgumentException(String.format("No view holder for %d", position));
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
    @Override
    public int getItemViewType(int position) {
        //Title Views
        if (position == EXTERIOR_TITLE_INDEX || position == INTERIOR_TITLE_INDEX) {
            return R.layout.view_holder_title_button;
        }
        return R.layout.list_row_paint;
    }

    class TitleButtonViewHolder extends RecyclerView.ViewHolder {
        private ImageView button;
        private TextView textView;

        TitleButtonViewHolder(final View v) {
            super(v);
            textView = v.findViewById(R.id.title);
            button = v.findViewById(R.id.button);
        }

        void bindData(final String title) {
            textView.setText(title);
            button.setImageResource(R.drawable.add_button_round_black);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.addPaintSurface(title.equals(EXTERIOR_TITLE));
                }
            });
        }

    }
    class PaintSurfaceViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView paintLocationView;
        final TextView paintBrandView;
        final TextView paintColorView;
        final TextView paintDate;

        PaintSurface paintSurface;
        boolean isExteriorPaintSurface;
        int paintSurfaceIndex;

        PaintSurfaceViewHolder(final View view) {
            super(view);
            this.view = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.editPaintSurface(paintSurface, isExteriorPaintSurface, paintSurfaceIndex);
                }
            });
            paintLocationView = view.findViewById(R.id.paint_location);
            paintBrandView = view.findViewById(R.id.paint_brand);
            paintColorView = view.findViewById(R.id.paint_color);
            paintDate = view.findViewById(R.id.paint_date);
        }
        void bindData() {
            view.setClickable(false);
            paintLocationView.setText(R.string.add_paint);
        }
        void bindData(
                @NonNull final PaintSurface paintSurface,
                boolean isExteriorPaintSurface,
                int paintSurfaceIndex) {
            this.paintSurface = paintSurface;
            this.isExteriorPaintSurface = isExteriorPaintSurface;
            this.paintSurfaceIndex = paintSurfaceIndex;
            if (StringUtilities.isNullOrEmpty(paintSurface.getLocation())) {
                paintLocationView.setText("---");
            } else {
                paintLocationView.setText(paintSurface.getLocation());
            }
            if (StringUtilities.isNullOrEmpty(paintSurface.getBrand())) {
                paintBrandView.setText("---");
            } else {
                paintBrandView.setText(paintSurface.getBrand());
            }
            paintColorView.setText(paintSurface.getColor());
            paintDate.setText(paintSurface.getDateString());
        }
    }
}
