package fund.mymutual.cfsws.rest;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fund.mymutual.cfsws.business.Position;

public class ViewPortfolioDTO {
    
    @NotEmpty
    private int cashInDollar;
    
    @NotNull
    private List<Position> positions;

    public int getCashInDollar() {
        return cashInDollar;
    }

    public void setCashInDollar(int cashInCents) {
        this.cashInDollar = cashInCents;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
