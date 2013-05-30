package com.vaadin.demo.parking.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.demo.parking.model.ClassificationGroup;
import com.vaadin.demo.parking.model.Species;
import com.vaadin.demo.parking.util.ClassificatiodDataReader;

/**
 * Navigation manager (with NavigationView) is a great component to organize
 * hierarchical data. ClassificationHierarchy displays the taxonomy of birds
 * from order to species.
 * <p>
 * Views which are displayed in this navigation manager are
 * ClassificationGroupView and SpeciesView.
 * 
 * @see NavigationManager
 * @see SpeciesView
 * @see ClassificationGroupView
 */
@SuppressWarnings("serial")
public class ClassificationHierarchy extends NavigationManager {

    private static ClassificationGroup root;

    /**
     * Creates a classification hierarchy displaying the birds classification
     * group in the top level view
     */
    public ClassificationHierarchy() {
        navigateTo(new ClassificationGroupView(getBirds(), true));
    }

    /**
     * @return the classification group of birds
     */
    private static ClassificationGroup getBirds() {
        if (root == null) {
            try {
                root = ClassificatiodDataReader.readSpecies();
                return root;
            } catch (Exception e) {
                Logger.getAnonymousLogger()
                        .log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return root;
    }

    /**
     * Public method for other UI components to control the shown species.
     * 
     * @param species
     *            the species where to navigate in the manager
     */
    public void showSpecies(Species species) {
        // first go back to top level
        while (getPreviousComponent() != null) {
            navigateBack();
        }
        // then navigate to species via its group
        ClassificationGroup parent2 = species.getParent();
        navigateTo(new ClassificationGroupView(parent2));
        navigateTo(new SpeciesView(species));
    }

}
