package pe.pucp.tel306.firebox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    public void setFloatingButton(){
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

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mAddSto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            // when isAllFabsVisible becomes
                            // true make all the action name
                            // texts and FABs VISIBLE.
                            mAddFile.show();
                            mAddPrivate.show();
                            addFileActionText.setVisibility(View.VISIBLE);
                            addPrivateActionText.setVisibility(View.VISIBLE);

                            // make the boolean variable true as
                            // we have set the sub FABs
                            // visibility to GONE
                            isAllFabsVisible = true;
                        } else {

                            // when isAllFabsVisible becomes
                            // true make all the action name
                            // texts and FABs GONE.
                            mAddFile.hide();
                            mAddPrivate.hide();
                            addFileActionText.setVisibility(View.GONE);
                            addPrivateActionText.setVisibility(View.GONE);

                            // make the boolean variable false
                            // as we have set the sub FABs
                            // visibility to GONE
                            isAllFabsVisible = false;
                        }
                    }
                });

        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mAddFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(FilesActivity.this, "Archivo clickeado", Toast.LENGTH_SHORT).show();
                    }
                });

        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mAddPrivate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(FilesActivity.this, "Archivo privado clickeado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}