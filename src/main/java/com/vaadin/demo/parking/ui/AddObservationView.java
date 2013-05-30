package com.vaadin.demo.parking.ui;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.gwt.client.theme.StyleNames;
import com.vaadin.addon.touchkit.gwt.client.vcom.DatePickerState.Resolution;
import com.vaadin.addon.touchkit.ui.DatePicker;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.model.Observation;
import com.vaadin.demo.parking.model.ObservationDB;
import com.vaadin.demo.parking.model.Location;
import com.vaadin.demo.parking.model.Species;
import com.vaadin.demo.parking.ui.LocationSelectorMap.LocationSelectedCallback;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.demo.parking.util.UploadImageScaler;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

/**
 * A view to report a new observation.
 * <p>
 * The form in the view uses "pre created fields pattern" thus the view also
 * implements FormFieldFactory that return pre created fields based on the
 * property key.
 */
@SuppressWarnings("serial")
public class AddObservationView extends NavigationView implements
        ClickListener, LocationSelectedCallback {

    private final Button cancel = new Button(null, this);
    private final Button save = new Button(null, this);

    private final Observation observation = new Observation();

    private final CssLayout layout = new CssLayout();
    private ResourceBundle tr;
    private NumberField count;
    private FieldGroup fieldGroup;
    private ComboBox species;
    private TextField observer;
    private DatePicker observationTime;
    private NumberField latitude;
    private NumberField longitude;
    private TextField place;
    private Button chooseFromMap;
    private final Validator latValidator = new DoubleRangeValidator(
            "Must be e.g. 20.22", -90.0, 90.0);
    private final Validator longValidator = new DoubleRangeValidator(
            "Must be e.g. 20.22", -180.0, 180.0);

    private Upload imageUpload;
    private ProgressIndicator progressIndicator;
    private Button cancelUpload;
    private Image image;

    /**
     * An interface for users of this class, so that they can react on if new
     * Observation was added.
     */
    interface ObservationAddedCallback {

        /**
         * Called when observation has been added.
         * 
         * @param observation
         *            the added observation
         */
        void observationAdded(Observation observation);

    }

    private ObservationAddedCallback observerAddedCallback;

    public AddObservationView(Species s, ObservationAddedCallback cb) {
        Locale locale = ParkingUI.getApp().getLocale();
        observerAddedCallback = cb;

        // set some sane default values for the observation
        observation.setCount(1);
        observation.setObservationTime(new Date());
        observation.setSpecies(s);
        observation.setObserver(ParkingUI.getApp().getUser());
        Location observationPoint = new Location();
        observationPoint.setLatitude(ParkingUI.getApp()
                .getCurrentLatitude());
        observationPoint.setLongitude(ParkingUI.getApp()
                .getCurrentLongitude());
        observationPoint.setName("Rovaniemi"); // that is where all Vaadin's are
                                               // :-)
        observation.setLocation(observationPoint);

        tr = Translations.get(locale);

        setCaption(tr.getString("New Observation"));

        // add and configure save and cancel buttons
        cancel.setCaption(tr.getString("Cancel"));
        save.setCaption(tr.getString("Save"));
        save.setStyleName(StyleNames.BUTTON_GREEN);
        setLeftComponent(cancel);
        setRightComponent(save);

        fieldGroup = new FieldGroup();
        fieldGroup.setItemDataSource(new BeanItem<Observation>(observation));
        fieldGroup.setBuffered(false);

        image = new Image("");
        image.setWidth("100%");
        progressIndicator = new ProgressIndicator();
        cancelUpload = new Button(tr.getString("Cancel"));
        UploadImageScaler uploader = new UploadImageScaler();
        imageUpload = new Upload(tr.getString("Image"), uploader);
        imageUpload.setWidth("80px");
        imageUpload.setImmediate(true);
        imageUpload.addSucceededListener(uploader);
        progressIndicator.setVisible(false);
        cancelUpload.setVisible(false);

        imageUpload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(StartedEvent event) {
                imageUpload.setVisible(false);
                cancelUpload.setVisible(true);
                progressIndicator.setVisible(true);
                progressIndicator.setValue(new Float(0.0));
                progressIndicator.setCaption("Uploading...");
            }
        });

        imageUpload.addProgressListener(new Upload.ProgressListener() {
            @Override
            public void updateProgress(long readBytes, long contentLength) {
                progressIndicator.setValue(new Float((float) readBytes
                        / contentLength));
            }
        });

        imageUpload.addFailedListener(new Upload.FailedListener() {
            @Override
            public void uploadFailed(FailedEvent event) {
                if (event.getReason() != null) {
                    Notification.show("Upload failed: "
                            + event.getReason().getMessage());
                }
            }
        });

        imageUpload.addFinishedListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(FinishedEvent event) {
                imageUpload.setVisible(true);
                progressIndicator.setVisible(false);
                cancelUpload.setVisible(false);
            }
        });

        imageUpload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(SucceededEvent event) {
                progressIndicator.setCaption("Processing image...");
            }
        });

        cancelUpload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                imageUpload.interruptUpload();
            }
        });

        uploader.setListener(new UploadImageScaler.ScalingDoneListener() {

            @Override
            public void onFileScaled(final File file) {
                file.deleteOnExit();
                observation.setImage(file.getAbsolutePath());
                image.setSource(new FileResource(file));
            }

            @Override
            public void onScalingError(String message) {
                Notification.show(message);
            }
        });

        VerticalComponentGroup details = new VerticalComponentGroup();
        details.setCaption("Observation details");

        // Configure fields
        species = new ComboBox(tr.getString("species"));
        species.setContainerDataSource(ObservationDB
                .getSpeciesContainer(locale));
        species.setWidth("100%");
        species.setFilteringMode(FilteringMode.CONTAINS);
        species.setItemCaptionPropertyId("lang");
        
        count = new NumberField(tr.getString("count"));
        count.setWidth("100%");
        observer = new TextField(tr.getString("observer"));
        observer.setWidth("100%");
        observer.setId("observer");
        observationTime = new DatePicker(tr.getString("time"));
        observationTime.setWidth("100%");
        observationTime.setResolution(Resolution.TIME);
        
        // add fields to form
        details.addComponents(species, observationTime, count,
                observer, imageUpload, progressIndicator, cancelUpload,
                image);
        
        /* Bind fields in this object by naming convention to bean */
        fieldGroup.bindMemberFields(this);


        FieldGroup obsPointFieldGroup = new FieldGroup();
        obsPointFieldGroup.setItemDataSource(new BeanItem<Location>(
                observationPoint));
        obsPointFieldGroup.setBuffered(false);

        // add observation point fields (nested bean) manually
        latitude = new NumberField();
        latitude.setInputPrompt("Lat (eg. 60.0023)");
        latitude.setWidth("50%");
        latitude.addValidator(latValidator);
        latitude.setConverter(StringToDoubleConverter.class);
        latitude.setLocale(Locale.US);
        obsPointFieldGroup.bind(latitude, "latitude");

        longitude = new NumberField();
        longitude.setWidth("50%");
        longitude.setInputPrompt("Lon (eg. 20.0023)");
        longitude.addValidator(longValidator);
        longitude.setConverter(StringToDoubleConverter.class);
        longitude.setLocale(Locale.US);
        obsPointFieldGroup.bind(longitude, "longitude");

        place = new TextField();
        place.setInputPrompt(tr.getString("name"));
        place.setNullRepresentation("");
        place.setCaption(tr.getString("name"));
        place.setWidth("100%");
        obsPointFieldGroup.bind(place, "name");

        VerticalComponentGroup location = new VerticalComponentGroup();
        location.setCaption("Observation location");
        location.addComponent(place);

        CssLayout cssLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                String style = "float:left;";
                if(c instanceof Button) {
                    style += "margin: 10px;";
                }
                return style;
            }
        };
        cssLayout.setWidth("100%");

        // Also add button to trigger location selection via map
        chooseFromMap = new Button(tr.getString("Choose from map"), this);
        cssLayout.setCaption("lon/lat");

        cssLayout.addComponent(longitude);
        cssLayout.addComponent(latitude);
        cssLayout.addComponent(chooseFromMap);
        location.addComponent(cssLayout);

        layout.addComponent(details);
        layout.addComponent(location);

        setContent(layout);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == save) {
            if (observation.getSpecies() == null) {
                Notification.show("Species must be defined");
                return;
            }

            ObservationDB.persist(observation);
            if (observerAddedCallback != null) {
                observerAddedCallback.observationAdded(observation);
            }
        } else if (event.getButton() == chooseFromMap) {
            UI.getCurrent().addWindow(new LocationSelectorMap(this));
            return;
        }
        UI.getCurrent().removeWindow((Window) getParent());
    }

    @Override
    public void locationSelected(Point point) {
        longitude.setConvertedValue(translateLongitude(point.getLon()));
        latitude.setConvertedValue(point.getLat());
    }

    private double translateLongitude(double lon) {
        int mod = 360;
        int limit = mod / 2;
        double l = lon % mod;
        if (l < -limit) {
            l = l + mod;
        } else if (l > limit) {
            l = l - mod;
        }
        return l;
    }

}
