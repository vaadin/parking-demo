package com.vaadin.demo.parking.widgetset.client.ticketview;

import org.vectomatic.file.FileUploadExt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VUpload;
import com.vaadin.demo.parking.widgetset.client.OfflineDataService;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;
import com.vaadin.demo.parking.widgetset.client.ticketview.ImageUtil.ImageDataCallback;

public class PhotoLayout extends VerticalComponentGroupWidget {
    private final SimplePanel imagePanel;
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
                        ImageUtil.getImageData(fileUpload,
                                new ImageDataCallback() {
                                    @Override
                                    public void dataReceived(
                                            final String dataUrl,
                                            final int orientation) {
                                        OfflineDataService
                                                .setCachedImage(dataUrl);
                                        setImageOrientation(orientation, false);
                                    }
                                });
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
                setImageOrientation(0, false);
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

            if (Canvas.isSupported()) {
                ImageUtil.scaleAndRotateImage(dataUrl, 75, 1,
                        new ImageDataCallback() {
                            @Override
                            public void dataReceived(final String dataUrl,
                                    final int orientation) {
                                thumbnailUrl = dataUrl;
                            }
                        });
            } else {
                thumbnailUrl = null;
            }

        }
        imagePanel.setVisible(!empty);
        removeButton.setVisible(!empty);
        listener.fieldsChanged();
        setImagePanelScale();
    }

    public final void populateTicket(final Ticket ticket) {
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
