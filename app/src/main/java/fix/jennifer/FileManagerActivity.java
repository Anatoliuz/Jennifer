package fix.jennifer;

/**
 * Created by fix on 15.04.17.
 */
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.*;


import fix.jennifer.config.HelperFactory;
import fix.jennifer.dbexecutor.executorCreateUser;

public class FileManagerActivity extends AppCompatActivity {

    private String userSpacePath;
    private FilesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_file_manager);

        adapter = new FilesAdapter(this);

        int folderId =  HelperFactory.getHelper().getUserId();
        String intstr;
        intstr = Integer.toString(folderId);
        String folder_main = intstr;

        File f = new File(getFilesDir().getAbsolutePath(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
        userSpacePath = getFilesDir().getAbsolutePath()+"/"+folder_main;
        adapter.setDirectory(new File(userSpacePath) );



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.files);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileManagerActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!adapter.goBack()) {
            super.onBackPressed();
        }
    }


    public String getUserSpacePath(){
        return getUserSpacePath();
    }



}