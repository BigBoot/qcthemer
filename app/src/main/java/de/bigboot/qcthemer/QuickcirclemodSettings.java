package de.bigboot.qcthemer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Marco Kirchner
 */
@EActivity(R.layout.activity_quickcirclemod_settings)
@OptionsMenu(R.menu.menu_quickcirclemod_settings)
public class QuickcirclemodSettings extends Activity {
    private static final int IMPORT_FILE_REQUEST_CODE = 42;

    @ViewById(R.id.viewpager)
    ViewPager viewPager;
    @ViewById(R.id.pageIndicator)
    CirclePageIndicator pagerIndicator;

    @ViewById(R.id.lblAuthorContent)
    TextView author;
    @ViewById(R.id.lblDescriptionContent)
    TextView description;
    @ViewById(R.id.lblTitle)
    TextView title;

    @ViewById(R.id.layout_root)
    RelativeLayout layoutRoot;
    @ViewById(R.id.lblEmpty)
    TextView empty;

    @ViewById(R.id.button)
    Button applyButton;
    @ViewById(R.id.applied)
    TextView applied;

    private Clock currentClock = null;

    @Bean
    protected ClockAdapter adapter;
    protected Preferences prefs;

    @AfterViews
    protected void init() {
        prefs = new Preferences(this);

        viewPager.setAdapter(adapter);

        pagerIndicator.setViewPager(viewPager);
        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}

            @Override
            public void onPageSelected(int i) {
                loadClockInfo(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        loadClockInfo(0);
    }

    private void loadClockInfo (int i) {
        if (adapter.getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            layoutRoot.setVisibility(View.GONE);
            currentClock = null;
            setApplied(false);
        } else {
            if( i < 0 || i >= adapter.getCount())
                return;

            Clock c = adapter.getClock(i);
            title.setText(c.getTitle());
            author.setText(c.getAuthor());
            description.setText(c.getDescription());
            empty.setVisibility(View.GONE);
            layoutRoot.setVisibility(View.VISIBLE);
            currentClock = c;
            setApplied(c.equals(prefs.getActiveClock()));
        }
    }

    @Click(R.id.button)
    protected void apply() {
        if(currentClock == null) {
            prefs.setActiveClock(null);
        } else {
            prefs.setActiveClock(currentClock);
            if(currentClock.getActivate() >= 0)
                executeSuCommand("echo \"<?xml version='1.0' encoding='utf-8' standalone='yes'?><map><int name=\\\"cover_index\\\" value=\\\"" + currentClock.getActivate() + "\\\" /></map>\" > /data/data/com.lge.clock/shared_prefs/quick_cover.xml");
            setApplied(true);
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.apply_theme)
                .setMessage(R.string.apply_theme_text)
                .setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        executeSuCommand("reboot now");
                    }
                })
                .setNeutralButton(R.string.apply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        executeSuCommand("service call activity 42 s16 com.android.systemui && am startservice --user 0 -n com.android.systemui/.SystemUIService");
                    }
                })
                .setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void setApplied(boolean isApplied) {
        applyButton.setVisibility(isApplied?View.INVISIBLE:View.VISIBLE);
        applied.setVisibility(isApplied?View.VISIBLE:View.GONE);
    }

    @OptionsItem(R.id.action_delete)
    protected void delete() {
        if(currentClock != null) {
            String path = getFilesDir() + "/" + currentClock.getId() + "/";
            File file = new File(path);

            if (file.exists()) {
                String deleteCmd = "rm -r " + path;
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(deleteCmd);
                } catch (IOException e) { }
            }

            adapter.deleteClock(currentClock);
            loadClockInfo(adapter.getCount()-1);
        }
    }

    @OptionsItem(R.id.action_import)
    protected void importFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        startActivityForResult(intent, IMPORT_FILE_REQUEST_CODE);
    }

    private Clock importZip(String zip) throws ImportClockException {
        try {
            ZipFile zipFile = new ZipFile(zip);

            ZipEntry clockEntry = zipFile.getEntry("clock.xml");
            if(clockEntry == null)
                throw new ImportClockException(ImportClockException.Error.NO_CLOCK_XML);

            Clock clock = Clock.fromXML(zipFile.getInputStream(clockEntry));

            String path = getFilesDir() + "/" + clock.getId() + "/";
            File clockDir = new File(path);
            clockDir.mkdirs();
            setFilePermission(clockDir, "0755");

            File clockFile = new File(path + clockEntry.getName());
            writeFile(zipFile, clockEntry, clockFile);
            setFilePermission(clockFile, "0644");

            for(String filename : clock.getFiles()) {
                ZipEntry fileEntry = zipFile.getEntry(filename);
                if(fileEntry == null)
                    throw new ImportClockException(ImportClockException.Error.MISSING_FILE);

                File f = new File(path + filename);
                writeFile(zipFile, fileEntry, f);
                setFilePermission(f, "0644");
            }

            ZipEntry previewEntry = zipFile.getEntry("preview.png");
            if(previewEntry != null) {
                File f = new File(path + previewEntry.getName());
                writeFile(zipFile, previewEntry, f);
                setFilePermission(f, "0644");
            }

            return clock;
        } catch (IOException e) {
            throw new ImportClockException(ImportClockException.Error.READ_ERROR);
        }
    }

    private void writeFile(ZipFile file, ZipEntry entry, File out) throws IOException {
        BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(out));
        BufferedInputStream in = new BufferedInputStream(file.getInputStream(entry));

        int count;
        byte[] buffer = new byte[1024];
        while ((count = in.read(buffer)) != -1)
        {
            fout.write(buffer, 0, count);
        }

        fout.close();
        in.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMPORT_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Clock clock = importZip(data.getData().getPath());
                    adapter.addClock(clock);
                    viewPager.setCurrentItem(adapter.getCount() - 1);
                    loadClockInfo(adapter.getCount() - 1);
                } catch (ImportClockException e) {
                    int msg;
                    switch (e.getError()) {
                        case NO_CLOCK_XML:
                            msg = R.string.err_no_xml;
                            break;
                        case INVALID_CLOCK_XML:
                            msg = R.string.err_invalid_xml;
                            break;
                        case READ_ERROR:
                            msg = R.string.err_read_error;
                            break;
                        case MISSING_FILE:
                            msg = R.string.err_missing_file;
                            break;

                        default:
                            msg = R.string.err_import;
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean setFilePermission(File f, String permission) {
        String command = "chmod " + permission + " " + f.getAbsolutePath();

        Runtime runtime = Runtime.getRuntime();
        Process process;
        boolean error = false;
        try {
            process = runtime.exec(command);
            try {
                String str;
                process.waitFor();
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((str = stdError.readLine()) != null) {
                    error = true;
                }
                process.getInputStream().close();
                process.getOutputStream().close();
                process.getErrorStream().close();
            } catch (InterruptedException e) {
                error = true;
            }
        } catch (IOException e1) {
            error = true;
        }
        return !error;
    }



    private void executeSuCommand(String command) {

        Process su = null;

        // get superuser
        try {

            su = Runtime.getRuntime().exec("su");

        } catch (IOException e) {

            e.printStackTrace();

        }

        // kill given package
        if (su != null ){

            try {

                DataOutputStream os = new DataOutputStream(su.getOutputStream());
                os.writeBytes(command + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                su.waitFor();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }
        }
    }
}
