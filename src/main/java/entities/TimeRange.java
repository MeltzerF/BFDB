package entities;

import java.util.Date;

/**
 * Created by Evgeniy Slobozheniuk on 24.11.17.
 */
public class TimeRange {
    private Date from;

    public final Date getFrom() {
        return from;
    }

    public final void setFrom(Date from) {
        this.from = from;
    }

    private Date to;

    public final Date getTo() {
        return to;
    }

    public final void setTo(Date to) {
        this.to = to;
    }
}
