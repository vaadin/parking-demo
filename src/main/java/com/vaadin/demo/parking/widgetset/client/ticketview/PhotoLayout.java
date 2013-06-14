package com.vaadin.demo.parking.widgetset.client.ticketview;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.touchkit.gwt.client.ui.VerticalComponentGroupWidget;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.VCssLayout;
import com.vaadin.client.ui.VUpload;
import com.vaadin.demo.parking.widgetset.client.model.Ticket;

public class PhotoLayout extends VerticalComponentGroupWidget {
    private final SimplePanel imagePanel;
    private String imageLocalUrl;
    private int imageLocalOrientation;
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

    public PhotoLayout(TicketViewModuleListener listener) {
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
                Timer timer = new Timer() {

                    @Override
                    public void run() {
                        bindFileInput(takePhotoButton.getElement(),
                                PhotoLayout.this);
                    }
                };
                timer.schedule(1000);
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
                setImageSrc(null, 1);
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

    private native void bindFileInput(final Element e, final PhotoLayout widget) /*-{
                                                                                      e.onchange = function(event){
                                                                                      if(event.target.files.length == 1 && 
                                                                                      event.target.files[0].type.indexOf("image/") == 0) {
                                                                                      var file = event.target.files[0];
                                                                                      var src = URL.createObjectURL(file);
                                                                                      var reader = new FileReader();
                                                                                      reader.onload = function(event) {
                                                                                      var binary = event.target.result;
                                                                                      var binaryFile = new $wnd.BinaryFile(binary);
                                                                                      var exif = $wnd.EXIF.readFromBinaryFile(binaryFile);
                                                                                      var orientation = exif.Orientation;
                                                                                      if (!orientation){
                                                                                          orientation = 1;
                                                                                      }
                                                                                      widget.@com.vaadin.demo.parking.widgetset.client.ticketview.PhotoLayout::setImageSrc(Ljava/lang/String;I)(src,orientation);
                                                                                      }
                                                                                      reader.readAsBinaryString(file);
                                                                                      
                                                                                      }
                                                                                      }
                                                                                      
                                                                                      if(!("url" in window) && ("webkitURL" in window)) {
                                                                                      window.URL = window.webkitURL;   
                                                                                      }
                                                                                      }-*/;

    private void setImageSrc(final String src, final int oritentation) {
        boolean empty = src == null;
        imageLocalUrl = src;
        imageLocalOrientation = oritentation;
        if (!empty) {
            imagePanel.getElement().getStyle()
                    .setBackgroundImage("url(" + src + ")");
            for (int i = 1; i < 9; i++) {
                imagePanel.removeStyleName("orientation" + i);
            }
            imagePanel.addStyleName("orientation" + oritentation);
        }
        imagePanel.setVisible(!empty);
        removeButton.setVisible(!empty);
        takePhotoButton.submitButton.setText(empty ? "Take a photo"
                : "Replace...");
        if (empty) {
            takePhotoButton.addStyleName("empty");
        } else {
            takePhotoButton.removeStyleName("empty");
        }
        listener.fieldsChanged();
        setImagePanelScale();
    }

    public final void populateTicket(final Ticket ticket) {
        ticket.setImageUrl(imageLocalUrl);

        ticket.setImageOrientation(imageLocalOrientation);
    }

    public final void ticketUpdated(final Ticket ticket) {
        setImageSrc(ticket.getImageUrl(), ticket.getImageOrientation());

    }

}
