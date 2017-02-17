package fund.mymutual.cfsws.rest;

import java.util.List;

public class ViewPortfolioDTO extends MessageDTO {
    private String message;

    private String cash;

    private List<PositionDTO> funds;

    public ViewPortfolioDTO(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public List<PositionDTO> getFunds() {
        return funds;
    }

    public void setFunds(List<PositionDTO> funds) {
        this.funds = funds;
    }
}
