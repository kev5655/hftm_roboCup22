package com.robotino.logistics;

/**
 * Wird nicht verwendet
 *
 * @author Fabian Leuenberger
 * @date 22.06.2022
 * @description Die Storage-Station besitzt ein mehrstöckiges Lager.
 *              Mit dieser Klasse kann der StatusStorageStations des Slots festgehalten werden.
 *              Ob dieser gefüllt ist, auf welcher Ebene sich dieser befindet und
 *              welche Eigenschaften die einsortierten Elemente besitzen.
 */
public class StatusStorageStations {

    private int shelf;
    private int slot;
    private boolean isFilled;
    private String description = null;      //Base_Black Cap_Black

    public StatusStorageStations(int shelf, int slot, boolean isFilled, String description) {
        this.shelf = shelf;
        this.slot = slot;
        this.isFilled = isFilled;
        this.description = description;
    }

    public StatusStorageStations(int shelf, int slot, boolean isFilled){
        this.shelf = shelf;
        this.slot = slot;
        this.isFilled = isFilled;
    }

    /**********Setters**********/
    public void setShelf(int shelf) {
        this.shelf = shelf;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**********Getters**********/
    public int getShelf() {
        return shelf;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public String getDescription() {
        if (description != null) {
            return description;
        } else
            return "No description";
    }
}
