package fund.mymutual.cfsws.rest;

public class Funds {
    private String name;
    private String shares;
    private String price;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getShares() {
        return shares;
    }
    public void setShares(int shares) {
        this.shares = String.valueOf(shares);
    }
    
    public String getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = String.valueOf(price);
    }
}
