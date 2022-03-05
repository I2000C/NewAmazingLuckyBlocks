package me.i2000c.newalb.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader{
    //https://www.spigotmc.org/threads/getting-actual-item-display-name.34494/
    public static String getMinecraftAssetsURL(String locale){
        return null;
    }
    
    public static void downloadFile(String url, File path){
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();

            FileOutputStream outputStream = new FileOutputStream(path);
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int readBytes = inputStream.read(buffer);
            while (readBytes > 0) {
                outputStream.write(buffer, 0, readBytes);
                readBytes = inputStream.read(buffer);
            }
            inputStream.close();
            outputStream.close();
            
            connection.disconnect();
        }catch (Exception ex) {}
    }
}
