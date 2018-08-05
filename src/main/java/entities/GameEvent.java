package entities;

/**
 * Created by Evgeniy Slobozheniuk on 18.04.2018.
 */
public class GameEvent {
    private String marketId;
    private boolean isInPlay;
    private Double totalMatched;
    private Double totalAvailable;

    public GameEvent(String marketId, boolean isInPlay, Double totalMatched, Double totalAvailable) {
        this.marketId = marketId;
        this.isInPlay = isInPlay;
        this.totalMatched = totalMatched;
        this.totalAvailable = totalAvailable;
    }

    public GameEvent(MarketBook marketBook) {
        this.marketId = marketBook.getMarketId();
        this.isInPlay = marketBook.getInplay();
        this.totalMatched = marketBook.getTotalMatched();
        this.totalAvailable = marketBook.getTotalAvailable();
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public boolean isInPlay() {
        return isInPlay;
    }

    public void setInPlay(boolean inPlay) {
        isInPlay = inPlay;
    }

    public Double getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(Double totalMatched) {
        this.totalMatched = totalMatched;
    }

    public Double getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(Double totalAvailable) {
        this.totalAvailable = totalAvailable;
    }


}
