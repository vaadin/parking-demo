package com.vaadin.demo.parking.widgetset.client.ticketview;

import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.vaadin.client.BrowserInfo;

/**
 * This class contains some image processing utility methods not related to
 * Vaadin or TouchKit.
 */
public class ImageUtil {

    public static void getImageData(final FileUploadExt fileUpload,
            final ImageDataCallback imageDataCallback) {

        final Object[] imageData = new Object[2];

        getImageFilereaderData(fileUpload.getFiles().getItem(0),
                new ImageDataCallback() {
                    @Override
                    public void dataReceived(final String dataUrl,
                            final int orientation) {
                        imageData[1] = orientation;
                        if (BrowserInfo.get().isAndroid()) {
                            // For android devices, just scale the image data
                            // and send it back
                            scaleImage(dataUrl, 1024, orientation,
                                    imageDataCallback);
                        } else if (imageData[0] != null) {
                            callbackImageData(imageDataCallback,
                                    (String) imageData[0],
                                    (Integer) imageData[1]);
                        } else if (!Canvas.isSupported()) {
                            // Canvas not supported, we'll use the one received
                            // from filereader
                            callbackImageData(imageDataCallback, dataUrl,
                                    (Integer) imageData[1]);
                        }
                    }

                });

        if (Canvas.isSupported() && !BrowserInfo.get().isAndroid()) {
            final RootPanel rootPanel = RootPanel.get();
            final Image image = new Image();
            Style style = image.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setVisibility(Visibility.HIDDEN);
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(final LoadEvent event) {
                    imageData[0] = image.getUrl();
                    rootPanel.remove(image);
                    if (imageData[1] != null) {
                        callbackImageData(imageDataCallback,
                                (String) imageData[0], (Integer) imageData[1]);
                    }
                }
            });
            renderToImage(image.getElement(), fileUpload.getElement());
            rootPanel.add(image);
        }
    }

    private static void callbackImageData(
            final ImageDataCallback imageDataCallback, final String dataUrl,
            final int orientation) {
        if (Canvas.isSupported()) {
            rotateImage(dataUrl, 1024, orientation, imageDataCallback);
        } else {
            imageDataCallback.dataReceived(dataUrl, orientation);
        }
    }

    private static void rotateImage(final String imageUrl, final int maxScale,
            final int orientation, final ImageDataCallback imageDataCallback) {
        final RootPanel rootPanel = RootPanel.get();
        // Rotate the image to reset orientation fix
        final Image image = new Image(imageUrl);
        Style style = image.getElement().getStyle();
        style.setPosition(Position.ABSOLUTE);
        style.setVisibility(Visibility.HIDDEN);

        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(final LoadEvent event) {
                imageDataCallback.dataReceived(image.getUrl(), 1);
                rootPanel.remove(image);
            }
        });

        scaleAndRotateImage(image.getElement(), maxScale, orientation);
        rootPanel.add(image);
    }

    public static void scaleImage(final String imageUrl, final int width,
            final int orientation, final ImageDataCallback callback) {
        final Canvas canvas = Canvas.createIfSupported();
        if (BrowserInfo.get().isIOS() && canvas != null) {
            rotateImage(imageUrl, width, orientation, callback);
        } else if (canvas != null) {
            final RootPanel rootPanel = RootPanel.get();
            final Image image = new Image();
            Style style = image.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setVisibility(Visibility.HIDDEN);

            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(final LoadEvent event) {
                    ImageElement imageElement = ImageElement.as(image
                            .getElement());
                    int sw = imageElement.getPropertyInt("naturalWidth");
                    int sh = imageElement.getPropertyInt("naturalHeight");
                    double aspectRatio = Math.min(sh, sw)
                            / (double) Math.max(sh, sw);

                    int height = (int) (width * aspectRatio);

                    canvas.setCoordinateSpaceWidth(width);
                    canvas.setCoordinateSpaceHeight(height);
                    canvas.getContext2d().drawImage(imageElement, 0, 0, width,
                            height);
                    String scaledData = canvas.toDataUrl("image/jpeg");
                    rootPanel.remove(image);
                    callback.dataReceived(scaledData, orientation);
                }
            });
            image.setUrl(imageUrl);

            rootPanel.add(image);
        }
    }

    private static void getImageFilereaderData(final File imageFile,
            final ImageDataCallback imageDataCallback) {
        final Object[] imageData = new Object[2];

        final FileReader dataURLReader = new FileReader();
        dataURLReader.addLoadEndHandler(new LoadEndHandler() {
            @Override
            public void onLoadEnd(final LoadEndEvent event) {
                String dataUrl = dataURLReader.getStringResult();
                imageData[0] = dataUrl;
                if (imageData[1] != null) {
                    imageDataCallback.dataReceived(dataUrl,
                            (Integer) imageData[1]);
                }
            }
        });
        dataURLReader.readAsDataURL(imageFile);

        final FileReader binaryReader = new FileReader();
        binaryReader.addLoadEndHandler(new LoadEndHandler() {
            @Override
            public void onLoadEnd(final LoadEndEvent event) {
                if (binaryReader.getError() == null) {
                    String binary = binaryReader.getStringResult();
                    imageData[1] = getImageOrientation(binary);
                } else {
                    imageData[1] = 1;
                }

                if (imageData[0] != null) {
                    imageDataCallback.dataReceived((String) imageData[0],
                            (Integer) imageData[1]);
                }
            }
        });
        binaryReader.readAsBinaryString(imageFile);

    }

    public interface ImageDataCallback {
        void dataReceived(String dataUrl, int orientation);
    }

    private static native void renderToImage(final Element img,
            final Element fileInput)
    /*-{
        var file = fileInput.files[0];
        var mpImg = new $wnd.MegaPixImage(file);
        mpImg.render(img, { maxWidth: 1024, maxHeight: 1024 });
    }-*/;

    private static native void scaleAndRotateImage(final Element img,
            final int maxScale, final int orientation)
    /*-{
        var mpImg = new $wnd.MegaPixImage(img);
        mpImg.render(img, { maxWidth: maxScale, maxHeight: maxScale, orientation: orientation });
    }-*/;

    private static native int getImageOrientation(final String binary)
    /*-{
    var binaryFile = new $wnd.BinaryFile(binary);
    var exif = $wnd.EXIF.readFromBinaryFile(binaryFile);
    var orientation = exif.Orientation;
    if (!orientation){
        orientation = 1;
    }
    return orientation;
    }-*/;
}
