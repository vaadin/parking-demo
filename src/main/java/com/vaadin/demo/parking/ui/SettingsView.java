package com.vaadin.demo.parking.ui;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;

import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.addon.touchkit.ui.Switch;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.demo.parking.ParkingUI;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * A settings view for the applicaiton.
 * <p>
 * As the purpose of this application is to demo Vaadin TouchKit, we are also
 * using this as a landing page for users. Some settings don't actually do
 * anything, but just demonstrate TouchKit specific components.
 */
public class SettingsView extends NavigationView {

    @Override
    public void attach() {
        super.attach();
        buildView();
    }

    private void buildView() {
        ResourceBundle tr = Translations.get(getLocale());
        setCaption(tr.getString("Settings"));

        CssLayout content = new CssLayout();

        VerticalComponentGroup componentGroup = new VerticalComponentGroup();
        componentGroup.addStyleName("about");
        componentGroup.setCaption("About Vornitologist:");

        try {
            Label about = new Label(IOUtils.toString(getClass().getResource(
                    "/intro.html")), ContentMode.HTML);
            componentGroup.addComponent(about);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        content.addComponent(componentGroup);

        componentGroup = new VerticalComponentGroup();
        componentGroup.setCaption("Settings");
        Label label = new Label(
                "<p>Only the language setting works. The other settings are just to demonstrate widgets.</p>");
        label.setContentMode(ContentMode.HTML);
        componentGroup.addComponent(label);

        TextField username = new TextField("Username");
        username.setId("username");
        username.setWidth("100%");
        username.setValue(ParkingUI.getApp().getUser());
        username.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() != null) {
                    ParkingUI.getApp().setUser(
                            event.getProperty().getValue().toString());
                }
            }
        });
        componentGroup.addComponent(username);

        EmailField email = new EmailField("Email");
        email.setWidth("100%");
        email.setValue("ornithologist@example.com");
        componentGroup.addComponent(email);

        NumberField age = new NumberField("Age");
        age.setWidth("100%");
        componentGroup.addComponent(age);
        Switch switch1 = new Switch("Use my location");
        switch1.setValue(true);
        componentGroup.addComponent(switch1);
        switch1 = new Switch("Alerts");
        componentGroup.addComponent(switch1);

        Button button = new Button("Go offline");
        button.setWidth("100%");
        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                ((ParkingUI) UI.getCurrent()).goOffline();
            }
        });
        componentGroup.addComponent(button);

        content.addComponent(componentGroup);

        componentGroup = new VerticalComponentGroup("Language");
        OptionGroup languageSelect = new OptionGroup();
        Locale[] availableLocales = Translations.getAvailableLocales();
        Locale curlocale = ParkingUI.getApp().getLocale();
        for (Locale locale : availableLocales) {
            languageSelect.addItem(locale);
            languageSelect.setItemCaption(locale,
                    locale.getDisplayLanguage(locale));
            if (locale.getLanguage().equals(curlocale.getLanguage())) {
                languageSelect.setValue(locale);
            }
        }

        languageSelect
                .addValueChangeListener(new Property.ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        /*
                         * Language has changed. Set it to the application and
                         * then rebuild the whole UI by assigning new
                         * MainTabsheet to window.
                         */
                        ParkingUI.getApp().setLocale(
                                (Locale) event.getProperty().getValue());
                        UI.getCurrent().setContent(new MainTabsheet());
                    }
                });
        languageSelect.setImmediate(true);
        componentGroup.addComponent(languageSelect);
        content.addComponent(componentGroup);

        setContent(content);

    }

}
