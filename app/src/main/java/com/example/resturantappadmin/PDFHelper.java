package com.example.resturantappadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class PDFHelper {

    private File mFolder;
    private File mFile;
    private Context mContext;

    public PDFHelper(File folder, Context context) {

        this.mContext = context;
        this.mFolder = folder;

        if(!mFolder.exists())
            mFolder.mkdirs();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveImageToPDF(View title, Bitmap bitmap, String filename) {

        mFile = new File(mFolder, filename + ".jpg");
        if (!mFile.exists()) {
            int height = title.getHeight() + bitmap.getHeight();
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();


            try {
                mFile.createNewFile();
                OutputStream out = new FileOutputStream(mFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
               
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public  Bitmap getBitmapFromView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view = getNextView(view);
            Log.d(TAG, "New view id: " + view.getId());
        }
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap;
        if (view instanceof ScrollView) {
            returnedBitmap = Bitmap.createBitmap(((ViewGroup) view).getChildAt(0).getWidth(), ((ViewGroup) view).getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
        } else if (view instanceof RecyclerView) {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        }

        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    /**
     * If the base view is not visible, then it has no width or height.
     * This causes a problem when we are creating a PDF based on its size.
     * This method gets the next visible View.
     *
     * @param view The invisible view
     * @return The next visible view after the given View, or the original view if there's no more
     * visible views.
     */
    private  View getNextView(View view) {
        if (view.getParent() != null && (view.getParent() instanceof ViewGroup)) {
            ViewGroup group = (ViewGroup) view.getParent();
            View child;
            boolean getNext = false;
            //Iterate through all views from parent
            for (int i = 0; i < group.getChildCount(); i++) {
                child = group.getChildAt(i);
                if (getNext) {
                    //Make sure the view is visible, else iterate again until we find a visible view
                    if (child.getVisibility() == View.VISIBLE) {
                        Log.d(TAG, String.format(Locale.ENGLISH, "CHILD: %s : %s", child.getClass().getSimpleName(), child.getId()));
                        view = child;
                    }
                }
                //Iterate until we find out current view,
                // then we want to get the NEXT view
                if (child.getId() == view.getId()) {
                    getNext = true;
                }
            }
        }
        return view;
    }

}