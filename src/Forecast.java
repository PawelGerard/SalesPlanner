public class Forecast {

    int productId, customerId;
    String product, valueInPeriod1, valueInPeriod2, valueInPeriod3, valueInPeriod4, valueInPeriod5, valueInPeriod6;

    public Forecast(int productId, int customerId, String product, String valueInPeriod1, String valueInPeriod2, String valueInPeriod3, String valueInPeriod4, String valueInPeriod5, String valueInPeriod6) {
        this.productId = productId;
        this.customerId = customerId;
        this.product = product;
        this.valueInPeriod1 = valueInPeriod1;
        this.valueInPeriod2 = valueInPeriod2;
        this.valueInPeriod3 = valueInPeriod3;
        this.valueInPeriod4 = valueInPeriod4;
        this.valueInPeriod5 = valueInPeriod5;
        this.valueInPeriod6 = valueInPeriod6;
    }

    public int getProductId() {
        return productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getProduct() {
        return product;
    }

    public String getValueInPeriod1() {
        return valueInPeriod1;
    }

    public String getValueInPeriod2() {
        return valueInPeriod2;
    }

    public String getValueInPeriod3() {
        return valueInPeriod3;
    }

    public String getValueInPeriod4() {
        return valueInPeriod4;
    }

    public String getValueInPeriod5() {
        return valueInPeriod5;
    }

    public String getValueInPeriod6() {
        return valueInPeriod6;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setValueInPeriod1(String valueInPeriod1) {
        this.valueInPeriod1 = valueInPeriod1;
    }

    public void setValueInPeriod2(String valueInPeriod2) {
        this.valueInPeriod2 = valueInPeriod2;
    }

    public void setValueInPeriod3(String valueInPeriod3) {
        this.valueInPeriod3 = valueInPeriod3;
    }

    public void setValueInPeriod4(String valueInPeriod4) {
        this.valueInPeriod4 = valueInPeriod4;
    }

    public void setValueInPeriod5(String valueInPeriod5) {
        this.valueInPeriod5 = valueInPeriod5;
    }

    public void setValueInPeriod6(String valueInPeriod6) {
        this.valueInPeriod6 = valueInPeriod6;
    }

    public int getSum() {
        int v1,v2,v3,v4,v5,v6;
        if (getValueInPeriod1()!=null) v1 = Integer.parseInt(getValueInPeriod1()); else v1 =0;
        if (getValueInPeriod2()!=null) v2 = Integer.parseInt(getValueInPeriod2()); else v2 =0;
        if (getValueInPeriod3()!=null) v3 = Integer.parseInt(getValueInPeriod3()); else v3 =0;
        if (getValueInPeriod4()!=null) v4 = Integer.parseInt(getValueInPeriod4()); else v4 =0;
        if (getValueInPeriod5()!=null) v5 = Integer.parseInt(getValueInPeriod5()); else v5 =0;
        if (getValueInPeriod6()!=null) v6 = Integer.parseInt(getValueInPeriod6()); else v6 =0;
        return v1+v2+v3+v4+v5+v6;
    }
}
