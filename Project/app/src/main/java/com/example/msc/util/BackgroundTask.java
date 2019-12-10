package com.example.msc.util;

import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.RequiresApi;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class BackgroundTask <T> extends AsyncTask<Void, Void, T> {
    private final Lambder <T> lambder;
    private final LinkedList<WeakReference<View>> weakReferences = new LinkedList<>();

    public BackgroundTask(final Lambder <T> lambder, final View... views) {
        super();

        this.lambder = lambder;

        for(final View view : views)
            weakReferences.add(new WeakReference<>(view));
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPreExecute() {
        super.onPreExecute();
        weakReferences.forEach(weakReference -> {
            final View view = weakReference.get();

            if (view instanceof ProgressBar) {
                ((ProgressBar) view).setIndeterminate(true);
                view.setVisibility(View.VISIBLE);
            } else
                view.setEnabled(false);
        });
    }

    @Override
    protected T doInBackground(Void... voids) {
        return lambder.run();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        weakReferences.forEach(weakReference -> {
            final View view = weakReference.get();

            if (view instanceof ProgressBar)
                view.setVisibility(View.GONE);
            else
                view.setEnabled(true);
        });
    }
}
