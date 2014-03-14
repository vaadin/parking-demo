package com.vaadin.demo.parking.widgetset.client.ticketview;

import org.vaadin.juho.imageupload.client.EXIFOrientationNormalizer;
import org.vaadin.juho.imageupload.client.ImageLoadedEvent;
import org.vaadin.juho.imageupload.client.ImageLoadedHandler;
import org.vaadin.juho.imageupload.client.ImageTransformer;
import org.vaadin.juho.imageupload.client.ImageUpload;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.demo.parking.widgetset.client.OfflineDataService;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

public class PhotoLayout extends VerticalComponentGroupWidget {
    private final SimplePanel imagePanel;
    private String thumbnailUrl;
    private boolean imageLoaded;
    private final TicketViewModuleListener listener;

    private final VButton removeButton = new VButton();
    private final VButton takePhotoButton = new VButton();
    private final ImageUpload fileUpload = new ImageUpload();

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

        takePhotoButton.getElement().setId("uploadbutton");
        takePhotoButton.setStyleName("parkingbutton");
        takePhotoButton.addStyleName("blue");
        takePhotoButton.addStyleName("textcentered");
        takePhotoButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fileUpload.click();
            }
        });

        fileUpload.setCapture(true);

        EXIFOrientationNormalizer normalizer = new EXIFOrientationNormalizer();
        normalizer.setMaxWidth(1024);
        normalizer.setMaxHeight(1024);
        fileUpload.addImageManipulator(normalizer);
        fileUpload.addImageLoadedHandler(new ImageLoadedHandler() {
            @Override
            public void onImageLoaded(ImageLoadedEvent event) {
                setImage(event.getImageData().getDataURL());
            }
        });

        ImageTransformer thumbnailGenerator = new ImageTransformer();
        thumbnailGenerator.setImageDataSource(fileUpload);
        thumbnailGenerator.setMaxWidth(75);
        thumbnailGenerator.setMaxHeight(75);
        thumbnailGenerator.addImageLoadedHandler(new ImageLoadedHandler() {
            @Override
            public void onImageLoaded(ImageLoadedEvent event) {
                thumbnailUrl = event.getImageData().getDataURL();
            }
        });

        VCssLayout buttonsLayout = new VCssLayout();
        buttonsLayout.addStyleName("buttonslayout");
        buttonsLayout.add(fileUpload);
        buttonsLayout.add(takePhotoButton);

        removeButton.getElement().setId("removebutton");
        removeButton.setText("Remove");
        removeButton.setStyleName("parkingbutton");
        removeButton.addStyleName("blue");
        removeButton.addStyleName("textcentered");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                removeImage();
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

    private void setImage(String dataURL) {
        OfflineDataService.setCachedImage(dataURL);
        imageLoaded = true;

        imagePanel.getElement().getStyle()
                .setBackgroundImage("url(" + dataURL + ")");
        takePhotoButton.setText("Replace...");
        takePhotoButton.removeStyleName("empty");

        imagePanel.setVisible(true);
        removeButton.setVisible(true);
        listener.fieldsChanged();
        setImagePanelScale();
    }

    private void removeImage() {
        imageLoaded = false;
        thumbnailUrl = null;
        takePhotoButton.setText("Take a photo");
        takePhotoButton.addStyleName("empty");
        imagePanel.setVisible(false);
        removeButton.setVisible(false);
        listener.fieldsChanged();
        setImagePanelScale();
    }

    public void ticketUpdated(Ticket ticket) {
        if (!ticket.isImageIncluded()) {
            removeImage();
        } else {
            setImage(OfflineDataService.getCachedImage());
        }
    }

    public final void populateTicket(final Ticket ticket) {
        ticket.setImageIncluded(imageLoaded);
        ticket.setThumbnailUrl(thumbnailUrl);
    }
}
