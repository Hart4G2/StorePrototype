package io.github.store_prototype.objects.values;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.gui.NotifyPlayerValuesChangedEvent;

public class PlayerValues {

    private static PlayerValues playerValues;

    public static PlayerValues getPlayerValues() {
        if(playerValues == null){
            playerValues = new PlayerValues();
        }
        return playerValues;
    }

    private Value money;
    private Value chips;
    private Value cookies;
    private Value newspapers;
    private Value coke;

    public PlayerValues() {
        money = new Value(ValueNames.MONEY, 100);
        chips = new Value(ValueNames.CHIPS, 100);
        cookies = new Value(ValueNames.COOKIES, 100);
        newspapers = new Value(ValueNames.NEWSPAPERS, 100);
        coke = new Value(ValueNames.COKE, 100);
    }

    public int getAmount(ValueNames value){
        switch (value){
            case COKE: return coke.getAmount();
            case CHIPS: return chips.getAmount();
            case MONEY: return money.getAmount();
            case COOKIES: return cookies.getAmount();
            case NEWSPAPERS: return newspapers.getAmount();
            default: return -1;
        }
    }

    public void setAmount(ValueNames value, int amount){
        switch (value){
            case COKE: {
                coke.setAmount(amount);
                break;
            }
            case CHIPS: {
                chips.setAmount(amount);
                break;
            }
            case MONEY: {
                money.setAmount(amount);
                break;
            }
            case COOKIES: {
                cookies.setAmount(amount);
                break;
            }
            case NEWSPAPERS: {
                newspapers.setAmount(amount);
                break;
            }
        }

        SimplePublisher.getPublisher().publish(new NotifyPlayerValuesChangedEvent());
    }

    public boolean buyProducts(int cost, Value value){
        if(isEnough(ValueNames.MONEY, cost) && isEnough(value.getName(), value.getAmount())) {
            setAmount(ValueNames.MONEY, getAmount(ValueNames.MONEY) - cost);

            setAmount(value.getName(), getAmount(value.getName()) - value.getAmount());

            SimplePublisher.getPublisher().publish(new NotifyPlayerValuesChangedEvent());
            return true;
        }
        return false;
    }

    private boolean isEnough(ValueNames name, int amount) {
        return getAmount(name) >= amount;
    }

    public Value getValue(ValueNames name) {
        switch (name){
            case COKE: return coke;
            case CHIPS: return chips;
            case NEWSPAPERS: return newspapers;
            case COOKIES: return cookies;
            default: return money;
        }
    }
}
