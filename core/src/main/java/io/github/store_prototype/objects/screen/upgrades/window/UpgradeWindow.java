package io.github.store_prototype.objects.screen.upgrades.window;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

import io.github.render_demo.test1.events.ModalClosedEvent;
import io.github.render_demo.test1.upgrades.UpgradeScene;
import io.github.render_demo.test1.upgrades.UpgradeScene.Upgrades;

public class UpgradeWindow extends Dialog implements Disposable {

    private Table content;
    private ScrollPane scroll;
    private List<Upgrades> upgrades;
    private List<UpgradeItem> upgradeItems;

    public UpgradeWindow(String title, Skin skin) {
        super(title, skin);

        content = new Table();
        content.defaults().pad(10);

        setUpgrades();

        scroll = new ScrollPane(content, skin);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        getContentTable().add(scroll).expand().fill();
        getContentTable().pad(0);

        setModal(true);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(true);

        Button closeButton = new TextButton("X", skin);
        closeButton.setHeight(25);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        getTitleTable().add(closeButton).padRight(5).height(closeButton.getHeight());
    }

    @Override
    public Dialog show(Stage stage) {
        super.show(stage);
        setSize(stage.getWidth() * 0.6f, stage.getHeight() * 0.7f);
        setPosition(
            (stage.getWidth() - getWidth()) / 2,
            (stage.getHeight() - getHeight()) / 2
        );
        return this;
    }

    public Actor getScrollPane() {
        return scroll;
    }

    private void setUpgrades(){
        getUpgrades();
        content.clearChildren(true);

        for(UpgradeItem upgrade : upgradeItems){
            addItem(upgrade);
        }
    }

    private void getUpgrades(){
        List<Upgrades> boughtUpgrades = UpgradeScene.getBoughtUpgrades();
        List<Upgrades> availableUpgrades = UpgradeScene.getAvailableUpgrades();
        upgrades = new ArrayList<>();

        for(Upgrades upgrade: availableUpgrades){
            if(!boughtUpgrades.contains(upgrade)){
                upgrades.add(upgrade);
            }
        }

        getUpgradeItems();
    }

    private void getUpgradeItems(){
        upgradeItems = new ArrayList<>();

        for (Upgrades upgrade: upgrades){
            if(upgrade == Upgrades.BEAUTIFUL_PRICE_TAGS){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/beautiful_price_tags_icon.png", "Beautiful price tags", "+5% reputation", "Buy", upgrade));
            }
            if(upgrade == Upgrades.GOOSE_FOOT_KEYCHAIN){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/keychain_icon.png", "Goose foot keychain", "+5% reputation", "Buy", upgrade));
            }
            if(upgrade == Upgrades.VENDING_MACHINE){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/vending_machine_icon.png", "Vending machine", "Additional income", "Buy", upgrade));
            }
            if(upgrade == Upgrades.ADVERTISING){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/advertising_icon.png", "Advertising", "+5% reputation", "Buy", upgrade));
            }
            if(upgrade == Upgrades.CARNIVAL_COSTUMES){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/carnival_costumes_icon.png", "Carnival Costumes", "Attracts children and their parents", "Buy", upgrade));
            }
            if(upgrade == Upgrades.CAT){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/cat_icon.png", "Cat", "Attracts single middle-aged women", "Buy", upgrade));
            }
            if(upgrade == Upgrades.CHRISTMAS_DECORATIONS){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/christmas_decorations_icon.png", "Christmas decorations", "Attracts very thrifty people", "Buy", upgrade));
            }
            if(upgrade == Upgrades.FIRST_AID_KIT_ANTIPOHMELIN){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/first_aid_kit_antipohmelin_icon.png", "First aid kit \"Antipohmelin\"", "Helps after drinking", "Buy", upgrade));
            }
            if(upgrade == Upgrades.LICENSE_FOR_DIETARY_SUPPLEMENTS){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/license_for_dietary_supplements_icon.png", "License for dietary supplements", "Attracts people with a healthy lifestyle", "Buy", upgrade));
            }
            if(upgrade == Upgrades.PAINTING){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/painting_icon.png", "Painting", "Improving the appearance of the store", "Buy", upgrade));
            }
            if(upgrade == Upgrades.PIECE_OF_METEORITE){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/piece_of_meteorite_icon.png", "Piece if meteorite", "Rare artifact", "Buy", upgrade));
            }
            if(upgrade == Upgrades.POSSIBILITY_OF_LOSING_ONCE){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/possibility_of_losing_once_icon.png", "Death's gift", "Allows you to lose once", "Buy", upgrade));
            }
            if(upgrade == Upgrades.PRODUCTS_WITH_DEFECTS){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/products_with_defects_icon.png", "Products with defects", "Allows you to sell products with expired dates", "Buy", upgrade));
            }
            if(upgrade == Upgrades.REDUCTION_OF_THE_FINE){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/reduction_of_the_fine_icon.png", "Reduction of rent", "Reduced daily store fee", "Buy", upgrade));
            }
            if(upgrade == Upgrades.REPUTATION_INCREASE){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/reputation_increase_icon.png", "Reputation increase", "+5% reputation", "Buy", upgrade));
            }
            if(upgrade == Upgrades.SIGH_SPINNER){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/sign_spinner_icon.png", "Sign spinner", "More passers-by are becoming shoppers", "Buy", upgrade));
            }
            if(upgrade == Upgrades.SUPPLIER_PRICE_REDUCTION){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/supplier_price_reduction_icon.png", "Supplier price reduction", "Allows you to buy groceries at a lower price", "Buy", upgrade));
            }
            if(upgrade == Upgrades.TAX_DISCOUNT){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/tax_discount_icon.png", "Tax discount", "Increases profits from each product", "Buy", upgrade));
            }
            if(upgrade == Upgrades.OLD_RADIO){
                upgradeItems.add(new UpgradeItem("gamescene/upgrades/old_radio_icon.png", "Old radio", "Who wouldn't notice a shop with good music, right?", "Buy", upgrade));
            }
        }
    }

    private void addItem(UpgradeItem upgradeItem) {
        TextButton buyButton = upgradeItem.getBuyButton();
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setUpgrades();
            }
        });

        content.add(upgradeItem.getImage()).size(64, 64).pad(10);

        Table textTable = new Table();
        textTable.add(upgradeItem.getNameLabel()).expandX().fillX().left().height(32).row();
        textTable.add(upgradeItem.getDescriptionLabel()).expandX().fillX().left().height(32);
        content.add(textTable).expandX().fillX().padLeft(10).height(64);

        content.add(buyButton).padLeft(20).padBottom(30).height(64).width(150).row();
    }

    @Override
    public void hide() {
        super.hide();
        if (getStage() != null) {
            getStage().getRoot().fire(new ModalClosedEvent());
        }
    }

    @Override
    public void dispose() {
        for(UpgradeItem item : upgradeItems){
            item.dispose();
        }
    }
}
