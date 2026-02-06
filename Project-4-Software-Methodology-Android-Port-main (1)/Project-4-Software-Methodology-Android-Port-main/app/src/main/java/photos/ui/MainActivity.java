package photos.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import photos.R;
import photos.model.Album;
import photos.model.DataManager;
import java.util.List;

/**
 * Main activity displaying all albums
 * @author Jess
 * @author Pavel
 */
public class MainActivity extends AppCompatActivity {
    
    private RecyclerView albumsRecyclerView;
    private AlbumAdapter albumAdapter;
    private DataManager dataManager;
    private TextView emptyText;
    private Button createAlbumButton;
    private Button deleteAlbumButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize DataManager
        dataManager = DataManager.getInstance();
        
        // Initialize views
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        createAlbumButton = findViewById(R.id.createAlbumButton);
        deleteAlbumButton = findViewById(R.id.deleteAlbumButton);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup buttons
        createAlbumButton.setOnClickListener(v -> showCreateAlbumDialog());
        deleteAlbumButton.setOnClickListener(v -> deleteSelectedAlbum());
        
        // Update UI
        updateUI();
    }
    
    private void setupRecyclerView() {
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumAdapter = new AlbumAdapter(
            dataManager.getAlbums(),
            this::onAlbumClick
        );
        albumsRecyclerView.setAdapter(albumAdapter);
    }
    
    private void onAlbumClick(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("albumName", album.getName());
        startActivity(intent);
    }
    
    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Album");
        
        final EditText input = new EditText(this);
        input.setHint("Album name");
        builder.setView(input);
        
        builder.setPositiveButton("Create", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (albumName.isEmpty()) {
                Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Album newAlbum = new Album(albumName);
            dataManager.addAlbum(newAlbum);
            albumAdapter.notifyDataSetChanged();
            updateUI();
            Toast.makeText(this, "Album created", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    private void deleteSelectedAlbum() {
        Toast.makeText(this, "Long press an album to select it first", Toast.LENGTH_SHORT).show();
    }
    
    private void updateUI() {
        List<Album> albums = dataManager.getAlbums();
        if (albums.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            albumsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            albumsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        albumAdapter.notifyDataSetChanged();
        updateUI();
    }
}