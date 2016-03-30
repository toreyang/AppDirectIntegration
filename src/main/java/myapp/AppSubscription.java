package myapp;

import java.util.*;

public class  AppSubscription implements java.io.Serializable{
    private String creator;
    private String EditionCode;
    private String PricingDuration;

    public Set<String> getAssignees() {
        return assignees;
    }

    private Set<String> assignees = new HashSet<String>();

    public String getCreator() {
        return creator;
    }

    public String getEditionCode() {
        return EditionCode;
    }

    public String getPricingDuration() {
        return PricingDuration;
    }

    public void setCreator(String creator) {
        this.creator = creator;
        assignees.add(creator);
    }

    public void setEditionCode(String editionCode) {
        EditionCode = editionCode;
    }

    public void setPricingDuration(String pricingDuration) {
        PricingDuration = pricingDuration;
    }

    public void addAssignee(String assignee){
        assignees.add(assignee);
    }

    public void removeAssignee(String assignee){
        assignees.remove(assignee);
    }

}