package com.vaadin.demo.parking.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public final class UploadImageScaler implements 
    Upload.SucceededListener, 
    Upload.Receiver {

    /**
     * If there is a listener it must claim ownership
     * of the returned File.
     */
    public interface ScalingDoneListener {
        void onFileScaled(File file);
        void onScalingError(String message);
    }

    private static final int IMAGE_MAX_DIMENSION = 300;
    private File uploadTempFile = null;
    private ScalingDoneListener listener = null;
    
    public UploadImageScaler() {        
    }
    
    public void setListener(ScalingDoneListener listener) {
        this.listener = listener;
    }
    
    public void removeListener() {
        listener = null;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            uploadTempFile = File.createTempFile("UPLOAD_TEMP_", ".jpg");
            return new FileOutputStream(uploadTempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        //When no listener, no really point of
        //converting the file.
        if(listener == null) {
            return;
        }
        
        try {
            BufferedImage unscaledImage = ImageIO.read(uploadTempFile);
            if(unscaledImage == null) {
                listener.onScalingError("Uploaded file cannot be read");                
                return;
            }
            
            final int width = IMAGE_MAX_DIMENSION;
            final int height = (int) (
                width / (
                    (double) unscaledImage.getWidth() / 
                    (double) unscaledImage.getHeight()
                )
            );
            
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = scaledImage.createGraphics();                    
            g.drawImage(
                unscaledImage.getScaledInstance(
                    width, 
                    height, 
                    Image.SCALE_FAST
                ), 
                0, 
                0, 
                width, 
                height, 
                null
            );
            g.dispose();
            
            OutputStream out = new FileOutputStream(uploadTempFile);
            ImageIO.write(scaledImage, "jpg", out);
            out.close();

            listener.onFileScaled(uploadTempFile);
            
        } catch (IOException e) {
            listener.onScalingError("Exception: "+e.getMessage());
        }
    }
}
