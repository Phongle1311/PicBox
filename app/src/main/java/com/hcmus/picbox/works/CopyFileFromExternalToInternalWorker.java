package com.hcmus.picbox.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created on 2/1/2023 by Phong Le
 */
public class CopyFileFromExternalToInternalWorker extends Worker {

    public static final String KEY_INPUT_PATH = "key_input_path";
    public static final String KEY_OUTPUT_DIR = "key_output_path";


    public CopyFileFromExternalToInternalWorker(@NonNull Context context,
                                                @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String inputPath = getInputData().getString(KEY_INPUT_PATH);
        String outputDir = getInputData().getString(KEY_OUTPUT_DIR);

        if (inputPath == null || inputPath.equals("")) return Result.failure();
        if (outputDir == null || outputDir.equals("")) return Result.failure();

        InputStream in;
        OutputStream out;
        try {
            //create output directory if it doesn't exist
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(new File(new File(outputDir), new File(inputPath).getName()));

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            return Result.failure();
        }

        return Result.success();
    }
}
