package entities;

/**
 * Created by Evgeniy Slobozheniuk on 13.01.2018.
 */
public class RunnerCatalog {
    private Long selectionId;
    private String runnerName;
    private Double handicap;

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public Double getHandicap() {
        return handicap;
    }

    public void setHandicap(Double handicap) {
        this.handicap = handicap;
    }

    public String toString() {
        return "{" + "" + "selectionId=" + getSelectionId() + ","
                + "runnerName=" + getRunnerName() + "," + "handicap="
                + getHandicap() + "," + "}";
    }
}
