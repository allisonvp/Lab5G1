package pe.pucp.tel306.firebox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FilesActivity extends AppCompatActivity {
    // Make sure to use the FloatingActionButton
    // for all the FABs
    FloatingActionButton mAddSto, mAddFile, mAddPrivate;

    // These are taken to make visible and invisible along
    // with FABs
    TextView addFileActionText, addPrivateActionText;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        setTitle("Profile");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            TextView textViewBienvenido = findViewById(R.id.textViewBienvenido);
            textViewBienvenido.setText("Bienvenido " + displayName);
        }
        setFloatingButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.files_menu, menu);
        return true;
    }

    public void logout(MenuItem menuitem) {
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(FilesActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    public void setFloatingButton() {
        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddSto = findViewById(R.id.add_storage);
        // FAB button
        mAddFile = findViewById(R.id.add_file);
        mAddPrivate = findViewById(R.id.add_private);

        // Also register the action name text, of all the FABs.
        addFileActionText = findViewById(R.id.add_private_action_text);
        addPrivateActionText = findViewById(R.id.add_file_action_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        mAddFile.setVisibility(View.GONE);
        mAddPrivate.setVisibility(View.GONE);
        addFileActionText.setVisibility(View.GONE);
        addPrivateActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddSto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddFile.show();
                            mAddPrivate.show();
                            addFileActionText.setVisibility(View.VISIBLE);
                            addPrivateActionText.setVisibility(View.VISIBLE);

                            isAllFabsVisible = true;
                        } else {

                            mAddFile.hide();
                            mAddPrivate.hide();
                            addFileActionText.setVisibility(View.GONE);
                            addPrivateActionText.setVisibility(View.GONE);

                            isAllFabsVisible = false;
                        }
                    }
                });

        mAddFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectFile();
                    }
                });


        mAddPrivate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectFile();
                    }
                });
    }

    UploadTask task = null;
    ProgressBar progressBar = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            subirArchivo(data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 2) {
                selectFile();
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void subirArchivo(Uri uri) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
            String uid = currentUser.getUid();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            task = storageReference.child(uid + "/" + getFileName(uri))
                    .putFile(uri);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FilesActivity.this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show();
                    Log.d("debugeo", "subida exitosa");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("debugeo", "error en la subida");
                    e.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();

                    int progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);

                    Log.d("debugeo", "progreso: " + progreso + "%");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(progreso, true);
                    } else {
                        progressBar.setProgress(progreso);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (task != null) {
            progressBar.setVisibility(View.GONE);
            task.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (task != null && task.isInProgress()) {
            progressBar.setVisibility(View.GONE);
            task.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (task != null && task.isPaused()) {
            progressBar.setVisibility(View.VISIBLE);
            task.resume();
        }
    }

    public void selectFile() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("*/*").addCategory(Intent.CATEGORY_OPENABLE);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            Log.d("debugeo", "ABIERTO");
            startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo"), 1);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }
    }
}