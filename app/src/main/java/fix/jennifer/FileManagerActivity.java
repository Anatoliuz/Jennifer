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
    private FilesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_file_manager);
         generateNoteOnSD(getApplicationContext(), "sss", "sss");

        adapter = new FilesAdapter(this);
       //  String user_path = getFilesDir().getAbsolutePath();
    // Log.d("APP PATYH", user_path);
        //  String user_path = (String)Environment.getRootDirectory();
//
//        String filename = "myfile";
//        String string = "Hello world!";
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(string.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        adapter.setDirectory(new File(getFilesDir().getAbsolutePath()));

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
        //HelperFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (!adapter.goBack()) {
          //  TestDb();

            super.onBackPressed();
        }
    }
    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            String user_path = getFilesDir().getAbsolutePath();

            File root = new File(user_path, "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}