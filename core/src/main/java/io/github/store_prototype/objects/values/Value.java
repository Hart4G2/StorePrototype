package io.github.store_prototype.objects.values;

public class Value {

    private ValueNames value;

    private int amount;

    public Value(ValueNames value, int amount) {
        this.value = value;
        this.amount = amount;
    }

    public ValueNames getName() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
