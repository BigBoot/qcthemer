package de.bigboot.qcthemer;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.File;


/**
 * Created by Marco Kirchner
 */
@EFragment(R.layout.fragment_clock)
public class ClockFragment extends Fragment {
   @ViewById(R.id.imageView)
   ImageView imageView;

    @FragmentArg
    String previewImage;

    @AfterViews
    protected void init () {
        File imgFile = new File(previewImage);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }
}
