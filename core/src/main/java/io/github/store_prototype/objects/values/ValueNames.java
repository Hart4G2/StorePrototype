package io.github.store_prototype.objects.values;

public enum ValueNames {

    MONEY, COOKIES, COKE, CHIPS, NEWSPAPERS;

    public String getName() {
        switch (this){
            case COKE: return "Coke";
            case COOKIES: return "Cookies";
            case MONEY: return "Money";
            case NEWSPAPERS: return "Newspaper";
            case CHIPS: return "Chips";
            default: return "";
        }
    }
}
