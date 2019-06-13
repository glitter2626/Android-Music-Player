package android.example.com.new_project_1;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private String[] itemAll;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = findViewById(R.id.songList);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }



    public ArrayList<File> readAudio(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File f : files)
        {
            if (f.isDirectory() && !f.isHidden())
            {
                arrayList.addAll(readAudio(f));
            }
            else
            {
                if(f.getName().endsWith(".mp3") || f.getName().endsWith("aac") || f.getName().endsWith("wav") || f.getName().endsWith("wma"))
                {
                    arrayList.add(f);
                }
            }
        }

        return arrayList;
    }



    public void displayName()
    {
        final ArrayList<File> arrayList = readAudio(Environment.getExternalStorageDirectory());

        itemAll = new String[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++)
        {
            itemAll[i] = arrayList.get(i).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_1, itemAll);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                String name = listView.getItemAtPosition(position).toString();

                intent.putExtra("song_order", position);
                intent.putExtra("song_music", arrayList );
                intent.putExtra("song_name", name);

                startActivity(intent);
            }
        });
    }
}
