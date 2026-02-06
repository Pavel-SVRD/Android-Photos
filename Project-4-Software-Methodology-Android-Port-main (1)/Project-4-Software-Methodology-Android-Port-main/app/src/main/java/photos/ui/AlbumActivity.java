package photos.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import photos.R;
import photos.model.Album;
import photos.model.DataManager;
import photos.model.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AlbumActivity extends AppCompatActivity {

    private TextView albumNameLabel;
    private ListView photoListView;
    private Album currentAlbum;
    private Photo selectedPhoto;

    private ArrayAdapter<Photo> adapter;

    // Android file chooser launcher
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumNameLabel = findViewById(R.id.albumNameLabel);
        photoListView = findViewById(R.id.photoListView);

        // Retrieve album name from intent
        String albumName = getIntent().getStringExtra("albumName");
        currentAlbum = DataManager.getInstance().getAlbumByName(albumName);
        
        if (currentAlbum == null) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        albumNameLabel.setText(currentAlbum.getName());

        setupImagePicker();
        setupListView();
        setupButtons();
    }

    private void setupListView() {
        adapter = new ArrayAdapter<Photo>(this, R.layout.item_photo, currentAlbum.getPhotos()) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
                }

                Photo photo = getItem(position);

                ImageView thumb = view.findViewById(R.id.photoThumb);
                TextView caption = view.findViewById(R.id.photoCaption);

                // Use URI for the photo path
                thumb.setImageURI(Uri.parse("file://" + photo.getFilePath()));
                caption.setText(photo.getCaption().isEmpty() ?
                        photo.getFileName() : photo.getCaption());

                return view;
            }
        };

        photoListView.setAdapter(adapter);

        photoListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPhoto = currentAlbum.getPhotos().get(position);
            Toast.makeText(this, "Selected: " + selectedPhoto.getFileName(), Toast.LENGTH_SHORT).show();
        });

        photoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            openPhotoDisplay(position);
            return true;
        });
    }

    private void setupButtons() {
        findViewById(R.id.addPhotoBtn).setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        findViewById(R.id.removePhotoBtn).setOnClickListener(v -> removePhoto());
        findViewById(R.id.captionPhotoBtn).setOnClickListener(v -> captionPhoto());
        findViewById(R.id.displayPhotoBtn).setOnClickListener(v -> {
            if (selectedPhoto != null) {
                int index = currentAlbum.getPhotos().indexOf(selectedPhoto);
                openPhotoDisplay(index);
            } else {
                Toast.makeText(this, "Please select a photo first", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        addPhoto(uri);
                    }
                });
    }

    private void addPhoto(Uri uri) {
        try {
            // Get filename
            String filename = getFileName(uri);
            
            // Copy to internal storage
            File dest = new File(getFilesDir(), filename);
            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(dest);
            
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();

            // Create photo object
            Photo photo = new Photo(dest.getAbsolutePath());
            if (currentAlbum.addPhoto(photo)) {
                adapter.notifyDataSetChanged();
                DataManager.getInstance().saveAll();
                Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Photo already exists in album", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to add photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String name = "photo_" + System.currentTimeMillis() + ".jpg";
        try {
            var cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    name = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            // Use default name
        }
        return name;
    }

    private void removePhoto() {
        if (selectedPhoto == null) {
            alert("Error", "Select a photo to remove.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Remove Photo")
                .setMessage("Are you sure you want to remove this photo?")
                .setPositiveButton("Remove", (d, w) -> {
                    currentAlbum.removePhoto(selectedPhoto);
                    selectedPhoto = null;
                    adapter.notifyDataSetChanged();
                    DataManager.getInstance().saveAll();
                    Toast.makeText(this, "Photo removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void captionPhoto() {
        if (selectedPhoto == null) {
            alert("Error", "Select a photo to caption.");
            return;
        }

        final EditText input = new EditText(this);
        input.setText(selectedPhoto.getCaption());
        input.setHint("Enter caption");

        new AlertDialog.Builder(this)
                .setTitle("Set Caption")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    selectedPhoto.setCaption(input.getText().toString());
                    adapter.notifyDataSetChanged();
                    DataManager.getInstance().saveAll();
                    Toast.makeText(this, "Caption saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openPhotoDisplay(int photoIndex) {
        Intent intent = new Intent(this, PhotoDisplayActivity.class);
        intent.putExtra("albumName", currentAlbum.getName());
        intent.putExtra("photoIndex", photoIndex);
        startActivity(intent);
    }

    private void alert(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}