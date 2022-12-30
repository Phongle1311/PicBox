package com.hcmus.picbox.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.picbox.R;
import com.hcmus.picbox.interfaces.IPickerMediaAdapterCallback;
import com.hcmus.picbox.models.AbstractModel;
import com.hcmus.picbox.models.AlbumModel;
import com.hcmus.picbox.models.DateModel;
import com.hcmus.picbox.models.MediaModel;

import java.util.stream.IntStream;

/**
 * Create on 30/12/2022 by Phong Le
 * <br/> This class is use for picker media file activity
 */
public class PickerMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final float SCALE_X = 0.7f;
    private static final float SCALE_Y = 0.7f;
    private final Context context;
    private final AlbumModel album;
    private final SparseBooleanArray selectedIndex;
    private final IPickerMediaAdapterCallback callback;
    private int selectedCount;

    public PickerMediaAdapter(Context context, AlbumModel album, IPickerMediaAdapterCallback callback) {
        this.context = context;
        this.album = album;
        this.callback = callback;
        this.selectedCount = 0;
        selectedIndex = new SparseBooleanArray();
    }

    public SparseBooleanArray getSelectedIndex() {
        return selectedIndex;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case AbstractModel.TYPE_DATE: {
                View view = inflater.inflate(R.layout.date_layout, parent, false);
                return new DateViewHolder(view);
            }
            case AbstractModel.TYPE_PHOTO: {
                View view = inflater.inflate(R.layout.photo_card_layout, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_VIDEO: {
                View view = inflater.inflate(R.layout.video_card_layout, parent, false);
                return new MediaViewHolder(view);
            }
            case AbstractModel.TYPE_GIF: {
                View view = inflater.inflate(R.layout.gif_card_layout, parent, false);
                return new MediaViewHolder(view);
            }
            default:
                throw new IllegalStateException("unsupported type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case AbstractModel.TYPE_DATE: {
                DateModel date = (DateModel) album.getModelList().get(position);
                DateViewHolder viewHolder = (DateViewHolder) holder;
                viewHolder.txt_date.setText(date.getStringLastModifiedTime());
                break;
            }

            case AbstractModel.TYPE_GIF:
            case AbstractModel.TYPE_PHOTO:
            case AbstractModel.TYPE_VIDEO: {
                MediaModel model = (MediaModel) album.getModelList().get(position);
                MediaViewHolder viewHolder = (MediaViewHolder) holder;

                viewHolder.rbSelect.setVisibility(View.VISIBLE);

                if (selectedIndex.get(position)) {
                    viewHolder.imageView.setScaleX(SCALE_X);
                    viewHolder.imageView.setScaleY(SCALE_Y);
                    viewHolder.rbSelect.setChecked(true);
                } else {
                    viewHolder.imageView.setScaleX(1f);
                    viewHolder.imageView.setScaleY(1f);
                    viewHolder.rbSelect.setChecked(false);
                }

                // Load image by glide library
                Glide.with(context)
                        .load(model.getFile())
                        .placeholder(R.drawable.placeholder_color)
                        .error(R.drawable.placeholder_color) // TODO: replace by other drawable
                        .into(viewHolder.imageView);

                // Set onClick Listener to display media
                viewHolder.itemView.setOnClickListener(view -> {
                    if (selectedIndex.get(position)) {
                        selectedIndex.put(position, false);
                        viewHolder.imageView.setScaleX(1f);
                        viewHolder.imageView.setScaleY(1f);
                        viewHolder.rbSelect.setChecked(false);
                        selectedCount--;
                    } else {
                        selectedIndex.put(position, true);
                        viewHolder.imageView.setScaleX(SCALE_X);
                        viewHolder.imageView.setScaleY(SCALE_Y);
                        viewHolder.rbSelect.setChecked(true);
                        selectedCount++;
                    }
                    callback.onUpdateHeader(selectedCount);
                });
                break;
            }

            default:
                throw new IllegalStateException("Unsupported type");
        }
    }

    @Override
    public int getItemCount() {
        return album == null ? 0 : album.getModelList().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || album == null || position >= album.getModelList().size()) {
            throw new IllegalStateException("The position is invalid");
        }
        return album.getModelList().get(position).getType();
    }

    public void selectAll() {
        IntStream.range(0, album.getMediaList().size()).forEach(i -> selectedIndex.put(i, true));
        notifyItemRangeChanged(0, album.getModelList().size());
        selectedCount = album.getMediaList().size();
        callback.onUpdateHeader(selectedCount);
    }

    public void deselectAll() {
        IntStream.range(0, album.getMediaList().size()).forEach(i -> selectedIndex.put(i, false));
        notifyItemRangeChanged(0, album.getModelList().size());
        selectedCount = 0;
        callback.onUpdateHeader(selectedCount);
    }
}
