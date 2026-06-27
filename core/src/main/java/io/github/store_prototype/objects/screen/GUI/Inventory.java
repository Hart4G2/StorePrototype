package io.github.store_prototype.objects.screen.GUI;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import io.github.store_prototype.utils.size.ObjectSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class Inventory extends Table {

    public enum Items {
        ALCOHOL, MAGAZINES, AUDIOCASSETTES, BAG
    }

    private final float refItemWidth = 63f;
    private float itemWidth = 63f;

    private ObjectSize size;
    private Array<Item> items = new Array<>();

    private static Inventory instance;

    public static Inventory getInstance() {
        if(instance == null){
            instance = new Inventory();
        }
        return instance;
    }

    public Inventory() {
        size = new ObjectSize(10f, 800, 300f, 100f);
        setSize(size.getWidth(), size.getHeight());
        align(Align.topLeft);
        setFillParent(false);
    }

    public void addItem(Item item) {
        items.add(item);

        add(item.getImage()).size(itemWidth, itemWidth).pad(5);

        if(items.size % 5 == 0){
            row();
        }
        pack();
        setBounds(size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    public boolean hasItem(Items itemName) {
        for (Item item : items) {
            if (item.getName().equals(itemName)) return true;
        }
        return false;
    }

    public void removeItem(Items itemName) {
        for (Item item : items){
            if(item.getName().equals(itemName)){
                items.removeValue(item, false);
                removeActor(item.getImage());
            }
        }
    }

    public void resize(float width, float height){
        size.resize(width, height);
        itemWidth = ScreenScaler.scaleX(refItemWidth);

        setBounds(size.getX(), size.getY(), size.getWidth(), size.getHeight());

        for (Cell<?> cell : getCells()) {
            cell.size(itemWidth, itemWidth);
        }
        invalidate();
    }
}
