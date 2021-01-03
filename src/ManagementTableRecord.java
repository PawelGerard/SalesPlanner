public class ManagementTableRecord {

    String valueC1, valueC2, valueC3, valueC4, valueC5, valueC6, valueC7, valueC8;

    public ManagementTableRecord(String valueC1, String valueC2, String valueC3, String valueC4, String valueC5, String valueC6, String valueC7, String valueC8) {
        this.valueC1 = valueC1;
        this.valueC2 = valueC2;
        this.valueC3 = valueC3;
        this.valueC4 = valueC4;
        this.valueC5 = valueC5;
        this.valueC6 = valueC6;
        this.valueC7 = valueC7;
        this.valueC8 = valueC8;
    }

    public String getValueC1() {
        return valueC1;
    }

    public String getValueC2() {
        return valueC2;
    }

    public String getValueC3() {
        return valueC3;
    }

    public String getValueC4() {
        return valueC4;
    }

    public String getValueC5() {
        return valueC5;
    }

    public String getValueC6() {
        return valueC6;
    }

    public String getValueC7() {
        return valueC7;
    }

    public String getValueC8() {
        return valueC8;
    }

    public void setValueC1(String valueC1) {
        this.valueC1 = valueC1;
    }

    public void setValueC2(String valueC2) {
        this.valueC2 = valueC2;
    }

    public void setValueC3(String valueC3) {
        this.valueC3 = valueC3;
    }

    public void setValueC4(String valueC4) {
        this.valueC4 = valueC4;
    }

    public void setValueC5(String valueC5) {
        this.valueC5 = valueC5;
    }

    public void setValueC6(String valueC6) {
        this.valueC6 = valueC6;
    }

    public void setValueC7(String valueC7) {
        this.valueC7 = valueC7;
    }

    public void setValueC8(String valueC8) {
        this.valueC8 = valueC8;
    }

    @Override
    public String toString() {
        return "ManagementTableRecord{" +
                "valueC1='" + valueC1 + '\'' +
                ", valueC2='" + valueC2 + '\'' +
                ", valueC3='" + valueC3 + '\'' +
                ", valueC4='" + valueC4 + '\'' +
                ", valueC5='" + valueC5 + '\'' +
                ", valueC6='" + valueC6 + '\'' +
                ", valueC7='" + valueC7 + '\'' +
                ", valueC8='" + valueC8 + '\'' +
                '}';
    }
}
