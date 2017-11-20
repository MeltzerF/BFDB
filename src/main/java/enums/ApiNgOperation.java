package enums;

/**
 * Created by Evgeniy Slobozheniuk on 21.11.17.
 */
public enum ApiNgOperation {
    LISTEVENTTYPES("listEventTypes"),
    LISTCOMPETITIONS("listCompetitions"),
    LISTTIMERANGES("listTimeRanges"),
    LISTEVENTS("listEvents"),
    LISTMARKETTYPES("listMarketTypes"),
    LISTCOUNTRIES("listCountries"),
    LISTVENUES("listVenues"),
    LISTMARKETCATALOGUE("listMarketCatalogue"),
    LISTMARKETBOOK("listMarketBook"),
    PLACORDERS("placeOrders");

    private String operationName;

    ApiNgOperation(String operationName){
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }
}
