package io.github.store_prototype.objects.screen.upgrades;

import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.events.Event;

public class UpgradeScene implements SimpleEventListener {

    public enum Upgrades {
        BEAUTIFUL_PRICE_TAGS, GOOSE_FOOT_KEYCHAIN, VENDING_MACHINE, ADVERTISING, CARNIVAL_COSTUMES, CAT,
        CHRISTMAS_DECORATIONS, FIRST_AID_KIT_ANTIPOHMELIN, LICENSE_FOR_DIETARY_SUPPLEMENTS, PAINTING,
        PIECE_OF_METEORITE, POSSIBILITY_OF_LOSING_ONCE, PRODUCTS_WITH_DEFECTS, REDUCTION_OF_THE_FINE,
        REPUTATION_INCREASE, SIGH_SPINNER, SUPPLIER_PRICE_REDUCTION, TAX_DISCOUNT
    }

    private Stage stage;
    private BeautifulPriceTags beautifulPriceTags;
    private GooseKeychain gooseKeychain;
    private VendingMachine vendingMachine;

    private static List<Upgrades> boughtUpgrades = new ArrayList();

    public UpgradeScene(Stage stage) {
        this.stage = stage;
        beautifulPriceTags = new BeautifulPriceTags();
        beautifulPriceTags.setVisible(false);

        gooseKeychain = new GooseKeychain();
        gooseKeychain.setVisible(false);

        vendingMachine = new VendingMachine();
        vendingMachine.setVisible(false);
    }

    public void init(){
        stage.addActor(beautifulPriceTags);
        stage.addActor(gooseKeychain);
        stage.addActor(vendingMachine);
    }

    public void resize(float worldWidth, float worldHeight){
        beautifulPriceTags.resize(worldWidth, worldHeight);
        gooseKeychain.resize(worldWidth, worldHeight);
        vendingMachine.resize(worldWidth, worldHeight);
    }

    @Override
    public void handleChange(Event event) {
        try{
            UpgradeEvent e = (UpgradeEvent) event;
            activateUpgrade(e.getName());
        } catch (Exception ignore){}
    }

    public void activateUpgrade(Upgrades upgrade){
        switch (upgrade){
            case CAT:{
                boughtUpgrades.add(Upgrades.CAT);
                break;
            }
            case PAINTING:{
                boughtUpgrades.add(Upgrades.PAINTING);
                break;
            }
            case ADVERTISING:{
                boughtUpgrades.add(Upgrades.ADVERTISING);
                break;
            }
            case SIGH_SPINNER:{
                boughtUpgrades.add(Upgrades.SIGH_SPINNER);
                break;
            }
            case TAX_DISCOUNT:{
                boughtUpgrades.add(Upgrades.TAX_DISCOUNT);
                break;
            }
            case BEAUTIFUL_PRICE_TAGS:{
                beautifulPriceTags.setVisible(true);
                boughtUpgrades.add(Upgrades.BEAUTIFUL_PRICE_TAGS);
                break;
            }
            case VENDING_MACHINE:{
                vendingMachine.setVisible(true);
                boughtUpgrades.add(Upgrades.VENDING_MACHINE);
                break;
            }
            case GOOSE_FOOT_KEYCHAIN:{
                gooseKeychain.setVisible(true);
                boughtUpgrades.add(Upgrades.GOOSE_FOOT_KEYCHAIN);
                break;
            }
            case CARNIVAL_COSTUMES:{
                boughtUpgrades.add(Upgrades.CARNIVAL_COSTUMES);
                break;
            }
            case PIECE_OF_METEORITE:{
                boughtUpgrades.add(Upgrades.PIECE_OF_METEORITE);
                break;
            }
            case REPUTATION_INCREASE:{
                boughtUpgrades.add(Upgrades.REPUTATION_INCREASE);
                break;
            }
            case CHRISTMAS_DECORATIONS:{
                boughtUpgrades.add(Upgrades.CHRISTMAS_DECORATIONS);
                break;
            }
            case PRODUCTS_WITH_DEFECTS:{
                boughtUpgrades.add(Upgrades.PRODUCTS_WITH_DEFECTS);
                break;
            }
            case REDUCTION_OF_THE_FINE:{
                boughtUpgrades.add(Upgrades.REDUCTION_OF_THE_FINE);
                break;
            }
            case SUPPLIER_PRICE_REDUCTION:{
                boughtUpgrades.add(Upgrades.SUPPLIER_PRICE_REDUCTION);
                break;
            }
            case FIRST_AID_KIT_ANTIPOHMELIN:{
                boughtUpgrades.add(Upgrades.FIRST_AID_KIT_ANTIPOHMELIN);
                break;
            }
            case POSSIBILITY_OF_LOSING_ONCE:{
                boughtUpgrades.add(Upgrades.POSSIBILITY_OF_LOSING_ONCE);
                break;
            }
            case LICENSE_FOR_DIETARY_SUPPLEMENTS:{
                boughtUpgrades.add(Upgrades.LICENSE_FOR_DIETARY_SUPPLEMENTS);
                break;
            }
        }
    }

    public static List<Upgrades> getBoughtUpgrades(){
        return boughtUpgrades;
    }
}
