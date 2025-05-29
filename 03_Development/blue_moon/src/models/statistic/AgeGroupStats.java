package models.statistic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AgeGroupStats {
    private final SimpleStringProperty ageRange;
    private final SimpleIntegerProperty maleCount;
    private final SimpleIntegerProperty femaleCount;
    private final SimpleIntegerProperty totalCount;

    public AgeGroupStats(String ageRange, int male, int female) {
        this.ageRange = new SimpleStringProperty(ageRange);
        this.maleCount = new SimpleIntegerProperty(male);
        this.femaleCount = new SimpleIntegerProperty(female);
        this.totalCount = new SimpleIntegerProperty(male + female);
    }

    public String getAgeRange() { return ageRange.get(); }
    public int getMaleCount() { return maleCount.get(); }
    public int getFemaleCount() { return femaleCount.get(); }
    public int getTotalCount() { return totalCount.get(); }
}
