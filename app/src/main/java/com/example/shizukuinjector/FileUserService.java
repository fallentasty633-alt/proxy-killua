package com.example.shizukuinjector;

import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUserService extends IFileService.Stub {

    private static final String TAG = "FileUserService";

    public FileUserService() {
        Log.i(TAG, "FileUserService started");
    }

    @Override
    public boolean writeToFile(String path, String content) throws RemoteException {
        Log.i(TAG, "Attempting to write to: " + path);
        try {
            File file = new File(path);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directories: " + parentDir.getAbsolutePath());
                    // We'll continue anyway, maybe the directory exists but mkdirs failed
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                fos.flush();
                Log.i(TAG, "Successfully wrote to " + path);
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file", e);
            return false;
        }
    }

    @Override
    public void destroy() throws RemoteException {
        Log.i(TAG, "FileUserService destroyed");
        System.exit(0);
    }
}
