package photos.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import photos.R;
import photos.model.Album;
import java.util.List;

/**
 * Adapter for displaying albums in RecyclerView
 * @author Jess
 * @author Pavel
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    
    private List<Album> albums;
    private OnAlbumClickListener onAlbumClickListener;
    
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }
    
    public AlbumAdapter(List<Album> albums, OnAlbumClickListener clickListener) {
        this.albums = albums;
        this.onAlbumClickListener = clickListener;
    }
    
    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album);
    }
    
    @Override
    public int getItemCount() {
        return albums.size();
    }
    
    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView albumName;
        private TextView albumPhotoCount;
        
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.albumName);
            albumPhotoCount = itemView.findViewById(R.id.albumPhotoCount);
        }
        
        public void bind(Album album) {
            albumName.setText(album.getName());
            
            int photoCount = album.getPhotoCount();
            String countText = photoCount == 1 ? "1 photo" : photoCount + " photos";
            albumPhotoCount.setText(countText);
            
            itemView.setOnClickListener(v -> {
                if (onAlbumClickListener != null) {
                    onAlbumClickListener.onAlbumClick(album);
                }
            });
        }
    }
}