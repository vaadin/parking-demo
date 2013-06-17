package com.vaadin.demo.parking.widgetset.client.ticketview;

import org.vectomatic.file.File;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.FileUploadExt;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VUpload;
import com.vaadin.demo.parking.widgetset.client.OfflineDataService;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

public class PhotoLayout extends VerticalComponentGroupWidget {
    private final SimplePanel imagePanel;
    private String imageLocalUrl;
    private int imageLocalOrientation;
    private String thumbnailUrl;
    private final TicketViewModuleListener listener;

    private final VButton removeButton = new VButton();
    private final VUpload takePhotoButton = new VUpload() {
        @Override
        public void submit() {
            // VUpload submit uses application connection so it needs to
            // be overridden to avoid npe.
        };
    };

    private void setImagePanelScale() {
        Widget parent = imagePanel.getParent();
        int width = parent.getOffsetWidth();
        imagePanel.getElement().getStyle().setFontSize(width, Unit.PX);
    }

    public PhotoLayout(final TicketViewModuleListener listener) {
        this.listener = listener;

        VCssLayout innerLayout = new VCssLayout();
        innerLayout.addStyleName("photoinnerlayout");

        imagePanel = new SimplePanel();
        imagePanel.addStyleName("imagepanel");
        innerLayout.add(imagePanel);

        takePhotoButton.setImmediate(true);
        takePhotoButton.submitButton.setStyleName("parkingbutton");
        takePhotoButton.submitButton.addStyleName("blue");
        takePhotoButton.submitButton.addStyleName("textcentered");

        takePhotoButton.fu.getElement().setId("takephotobutton");
        takePhotoButton.fu.getElement().setAttribute("capture", "camera");
        takePhotoButton.fu.getElement().setAttribute("accept", "image/*");

        VCssLayout buttonsLayout = new VCssLayout();
        buttonsLayout.addStyleName("buttonslayout");

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                final FileUploadExt fileUpload = new ParkingFileUpload(
                        takePhotoButton);
                fileUpload.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(final ChangeEvent event) {
                        imageReceived(fileUpload.getFiles().getItem(0));
                    }
                });
            }
        });

        buttonsLayout.add(takePhotoButton);

        removeButton.setText("Remove");
        removeButton.setStyleName("parkingbutton");
        removeButton.addStyleName("blue");
        removeButton.addStyleName("textcentered");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                setImageData(null, 0);
            }
        });
        buttonsLayout.add(removeButton);

        innerLayout.add(buttonsLayout);

        add(innerLayout);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                setImagePanelScale();
            }
        });
    }

    private void imageReceived(final File imageFile) {
        final Object[] imageData = new Object[2];

        final FileReader orientationReader = new FileReader();
        orientationReader.addLoadEndHandler(new LoadEndHandler() {
            @Override
            public void onLoadEnd(final LoadEndEvent event) {
                if (orientationReader.getError() == null) {
                    String binary = orientationReader.getStringResult();
                    imageData[1] = getImageOrientation(binary);
                } else {
                    imageData[1] = 1;
                }

                if (imageData[0] != null && imageData[1] != null) {
                    setImageData((String) imageData[0], (Integer) imageData[1]);
                }
            }
        });
        orientationReader.readAsBinaryString(imageFile);

        final FileReader dataURLReader = new FileReader();
        dataURLReader.addLoadEndHandler(new LoadEndHandler() {
            @Override
            public void onLoadEnd(final LoadEndEvent event) {
                if (dataURLReader.getError() == null) {
                    imageData[0] = dataURLReader.getStringResult();
                }

                if (imageData[0] != null && imageData[1] != null) {
                    setImageData((String) imageData[0], (Integer) imageData[1]);
                }
            }
        });
        dataURLReader.readAsDataURL(imageFile);
    }

    public static native int getImageOrientation(final String binary) /*-{
                                                                      var binaryFile = new $wnd.BinaryFile(binary);
                                                                      var exif = $wnd.EXIF.readFromBinaryFile(binaryFile);
                                                                      var orientation = exif.Orientation;
                                                                      if (!orientation){
                                                                          orientation = 1;
                                                                      }
                                                                      return orientation;
                                                                      }-*/;

    private void setImageData(final String dataUrl, final int orientation) {
        if (dataUrl == null) {
            OfflineDataService.setCachedImage(null);
            setImageOrientation(orientation, false);
        } else {
            scaleImage(dataUrl, 1024 * 768, true, new Callback() {
                @Override
                public void imageScaled(final String imageData) {
                    OfflineDataService.setCachedImage(imageData);
                    setImageOrientation(orientation, false);
                }
            });
        }
    }

    public interface Callback {
        void imageScaled(String imageData);
    }

    private void scaleImage(final String imageUrl, final int maxArea,
            final boolean callbackNonNull, final Callback callback) {
        final Canvas canvas = Canvas.createIfSupported();
        if (canvas != null) {
            final Image image = new Image();
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(final LoadEvent event) {

                    ImageElement imageElement = ImageElement.as(image
                            .getElement());
                    int[] scaledSize = getScaledSize(imageElement, maxArea);

                    canvas.setCoordinateSpaceWidth(scaledSize[0]);
                    canvas.setCoordinateSpaceHeight(scaledSize[1]);
                    canvas.getContext2d().drawImage(imageElement, 0, 0,
                            scaledSize[0], scaledSize[1]);

                    String scaledData = canvas.toDataUrl("image/jpeg");
                    remove(image);
                    callback.imageScaled(scaledData);
                }
            });
            image.setUrl(imageUrl);
            Style style = image.getElement().getStyle();
            style.setPosition(Position.ABSOLUTE);
            style.setVisibility(Visibility.HIDDEN);
            add(image);
        } else {
            callback.imageScaled(callbackNonNull ? imageUrl : null);
        }
    }

    private static int[] getScaledSize(final ImageElement imageElement,
            final int maxArea) {
        int width = imageElement.getWidth();
        int height = imageElement.getHeight();

        double area = width * height;

        if (area > maxArea) {
            double multiplier = Math.sqrt(maxArea / area);
            width = new Double(multiplier * width).intValue();
            height = new Double(multiplier * height).intValue();
        }

        return new int[] { width, height };
    }

    private void setImageOrientation(final int oritentation,
            final boolean initialize) {
        imageLocalOrientation = oritentation;
        boolean empty = imageLocalOrientation == 0;
        if (empty) {
            if (!initialize) {
                OfflineDataService.setCachedImage(null);
            }
            takePhotoButton.submitButton.setText("Take a photo");
            takePhotoButton.addStyleName("empty");
        } else {
            String dataUrl = OfflineDataService.getCachedImage();
            imagePanel.getElement().getStyle()
                    .setBackgroundImage("url(" + dataUrl + ")");
            for (int i = 1; i < 9; i++) {
                imagePanel.removeStyleName("orientation" + i);
            }
            imagePanel.addStyleName("orientation" + oritentation);

            takePhotoButton.submitButton.setText("Replace...");
            takePhotoButton.removeStyleName("empty");

            scaleImage(dataUrl, 75 * 75, false, new Callback() {
                @Override
                public void imageScaled(final String imageData) {
                    thumbnailUrl = imageData;
                }
            });
        }
        imagePanel.setVisible(!empty);
        removeButton.setVisible(!empty);
        listener.fieldsChanged();
        setImagePanelScale();
    }

    public final void populateTicket(final Ticket ticket) {
        ticket.setImageUrl(imageLocalUrl);

        ticket.setImageOrientation(imageLocalOrientation);

        ticket.setThumbnailUrl(thumbnailUrl);
    }

    public final void ticketUpdated(final Ticket ticket,
            final boolean initialize) {
        setImageOrientation(ticket.getImageOrientation(), initialize);

    }

    public class ParkingFileUpload extends FileUploadExt {
        public ParkingFileUpload(final VUpload upload) {
            super(upload.fu.getElement(), false);
            onAttach();
            try {
                RootPanel.detachOnWindowClose(this);
            } catch (java.lang.AssertionError e) {
                // Occurs in dev mode, ignore
            }
        }
    }

}
