package com.vaadin.demo.parking.ui;

import java.util.ResourceBundle;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.demo.parking.model.ClassificationGroup;
import com.vaadin.demo.parking.model.ClassificationItem;
import com.vaadin.demo.parking.util.Translations;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;

/**
 * This is a navigation view to display taxonomic group. Children as listed with
 * NavigationButtons making navigation to deeper levels smooth. The component
 * can group two taxonomic levels into one view if requested.
 */
@SuppressWarnings("serial")
public class ClassificationGroupView extends NavigationView {

    private final ClassificationGroup group;
    private ComponentContainer layout = new VerticalComponentGroup();
    private final boolean groupSecondLevel;

    /**
     * Creates a new instance of ClassificationGroupView
     * 
     * @param group
     *            the group to be shown in the view
     * @param groupSecondLevel
     *            true if grouping of next level is requested.
     */
    public ClassificationGroupView(ClassificationGroup group,
            boolean groupSecondLevel) {
        this.group = group;
        this.groupSecondLevel = groupSecondLevel;
    }

    public ClassificationGroupView(ClassificationGroup group) {
        this(group, false);
    }

    /**
     * We use lazy initialized views in this app. With the pattern we can save
     * some memory in some cases and also access the application instance when
     * building the views.
     * 
     * @see com.vaadin.ui.AbstractComponentContainer#attach()
     */
    @Override
    public void attach() {
        super.attach();
        buildView();
    }

    private void buildView() {
        ResourceBundle names = Translations.get(getLocale());
        setCaption(names.getString(group.getName()));

        if (groupSecondLevel) {
            layout = new CssLayout();
        }
        setContent(layout);

        for (ClassificationItem classificationItem : group.getChildren()) {
            if (groupSecondLevel
                    && classificationItem instanceof ClassificationGroup) {
                /*
                 * If groupging of the second level was requested, add
                 * navigation buttons into a component group and add the higher
                 * level name as a caption.
                 */
                VerticalComponentGroup componentGroup = new VerticalComponentGroup();
                componentGroup.setCaption(names.getString(classificationItem
                        .getName()));
                ClassificationGroup subgroup = (ClassificationGroup) classificationItem;
                for (ClassificationItem subitem : subgroup.getChildren()) {
                    componentGroup.addComponent(new ItemNavigationButton(
                            subitem, names.getString(subitem.getName())));
                }
                layout.addComponent(componentGroup);
            } else {
                NavigationButton navigationButton = new ItemNavigationButton(
                        classificationItem, names.getString(classificationItem
                                .getName()));
                layout.addComponent(navigationButton);
            }

        }
    }

    /**
     * Helper class to wrap a classification item in a NavigationButton.
     */
    static class ItemNavigationButton extends NavigationButton implements
            NavigationButtonClickListener {
        private final ClassificationItem item;

        /**
         * Creates a navigation button with the localized name as a caption.
         * Button will navigate the manager to appropriate view for the
         * ClassificationItem: ClassificationGroupView for groups and
         * SpeciesView for species.
         * 
         * @param item
         * @param localizedName
         * @see ItemNavigationButon
         */
        public ItemNavigationButton(ClassificationItem item,
                String localizedName) {
            addClickListener(this);
            this.item = item;
            setCaption(localizedName);
        }

        /**
         * Handle the button click. If the clicked classification item was a
         * group, navigate to a new ClassificationGroupView. Else it must be
         * species, so display it in a special species view.
         * 
         * @see SpeciesView
         */
        public void buttonClick(NavigationButtonClickEvent event) {
            if (item instanceof ClassificationGroup) {
                getNavigationManager()
                        .navigateTo(
                                new ClassificationGroupView(
                                        (ClassificationGroup) item));
            } else {
                getNavigationManager().navigateTo(new SpeciesView(item));
            }
        }

    }

}
