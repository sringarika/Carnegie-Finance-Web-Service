package fund.mymutual.cfsws.business;

import java.util.List;

public class Portfolio {
    private int cashInCents;
    private List<Position> positions;

    public int getCashInCents() {
        return cashInCents;
    }

    public void setCashInCents(int cashInCents) {
        this.cashInCents = cashInCents;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
