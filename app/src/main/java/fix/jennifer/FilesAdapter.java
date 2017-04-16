package fix.jennifer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by fix on 15.04.17.
 */
class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    private final LayoutInflater layoutInflater;

    private File[] files;
    private File currentFile;
    private Context context;


    FilesAdapter(final Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    void setDirectory(final File file) {
        this.currentFile = file;
        this.files = file.listFiles();
        sortFiles(this.files);
        notifyDataSetChanged();
    }

    boolean goBack() {
        File parent = currentFile.getParentFile();
        if (parent != null) {
            setDirectory(parent);
            return true;
        }
        return false;
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileViewHolder(layoutInflater.inflate(R.layout.file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        holder.bind(files[position]);
    }

    @Override
    public int getItemCount() {
        return files != null ? files.length : 0;
    }

    private static void sortFiles(final File[] files) {
        if (files == null) {
            return;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && !f2.isDirectory()) return -1;
                if (f2.isDirectory() && !f1.isDirectory()) return 1;
                return f1.getName().compareTo(f2.getName());
            }
        });
    }

    final class FileViewHolder extends RecyclerView.ViewHolder {
        private final TextView filename;

        FileViewHolder(View view) {
            super(view);
            filename = (TextView) view.findViewById(R.id.filename);
        }

        void bind(final File file) {
            if (file.isDirectory()) {
                filename.setTypeface(null, Typeface.BOLD);
                filename.setText("[" + file.getName() + "]");
            } else {
                filename.setTypeface(null, Typeface.NORMAL);
                filename.setText(file.getName());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (file.isDirectory()) {
                        FilesAdapter.this.setDirectory(file);
                    }
                    else{
                        context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                    }
                }
            });
        }
    }
}

