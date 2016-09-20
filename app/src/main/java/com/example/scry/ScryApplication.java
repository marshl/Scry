package com.example.scry;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScryApplication {
    public static ScryApplication instance;

    public class ToggleField {
        public ToggleField(int _resourceID) {
            this.resourceID = _resourceID;
        }

        public int resourceID = -1;
        public boolean bool = true;
    }

    public String searchString;

    public boolean isSearchingName = true;
    public boolean isSearchingType = false;
    public boolean isSearchingRules = false;

    private boolean searchWhite = true;
    private boolean searchBlue = true;
    private boolean searchBlack = true;
    private boolean searchRed = true;
    private boolean searchGreen = true;
    private boolean searchColourless = true;

    public boolean matchColoursExactly = false;
    public boolean excludeUnselectedColours = false;
    public boolean multicolouredOnly = false;

    private int searchColourFlags;

    private boolean searchAllColours;

    public enum TYPE {
        CREATURE,
        ARTIFACT,
        ENCHANTMENT,
        INSTANT,
        SORCERY,
        LAND,
        PLANESWALKER,
    }

    public enum COLOUR {
        WHITE,
        BLUE,
        BLACK,
        RED,
        GREEN,
        COLOURLESS,
    }

    public HashMap<TYPE, ToggleField> typeSearchFlags;

    public SparseArray<ToggleField> toggleButtonMap;
    public HashMap<COLOUR, ToggleField> colourSearchFlags;

    private boolean searchTypeCreature = true;
    private boolean searchTypeArtifact = true;
    private boolean searchTypeEnchantment = true;
    private boolean searchTypeInstant = true;
    private boolean searchTypeSorcery = true;
    private boolean searchTypeLand = true;
    private boolean searchTypePlaneswalker = true;

    public int selectedSetIndex;
    private ScrySet searchSet = null;

    private boolean searchAllTypes;

    public HashMap<Integer, OracleCard> cardMap;

    public ArrayList<OracleCard> searchResults;

    public ArrayList<ScrySet> setList;
    public ArrayList<ScryFormat> formatList;

    public enum OWNERSHIP {
        ALL,
        OWNED,
        NOT_OWNED,
    }

    public OWNERSHIP ownershipSearch = OWNERSHIP.ALL;

    public boolean isInitialised = false;

    public ScryApplication() {
        instance = this;

        this.typeSearchFlags = new HashMap<TYPE, ToggleField>();
        this.typeSearchFlags.put(TYPE.CREATURE, new ToggleField(R.id.type_creature_imagebutton));
        this.typeSearchFlags.put(TYPE.ARTIFACT, new ToggleField(R.id.type_artifact_imagebutton));
        this.typeSearchFlags.put(TYPE.ENCHANTMENT, new ToggleField(R.id.type_enchantment_imagebutton));
        this.typeSearchFlags.put(TYPE.INSTANT, new ToggleField(R.id.type_instant_imagebutton));
        this.typeSearchFlags.put(TYPE.SORCERY, new ToggleField(R.id.type_sorcery_imagebutton));
        this.typeSearchFlags.put(TYPE.LAND, new ToggleField(R.id.type_land_imagebutton));
        this.typeSearchFlags.put(TYPE.PLANESWALKER, new ToggleField(R.id.type_planeswalker_imagebutton));

        this.colourSearchFlags = new HashMap<COLOUR, ToggleField>();
        this.colourSearchFlags.put(COLOUR.WHITE, new ToggleField(R.id.ib_colour_w));
        this.colourSearchFlags.put(COLOUR.BLUE, new ToggleField(R.id.ib_colour_u));
        this.colourSearchFlags.put(COLOUR.BLACK, new ToggleField(R.id.ib_colour_b));
        this.colourSearchFlags.put(COLOUR.RED, new ToggleField(R.id.ib_colour_r));
        this.colourSearchFlags.put(COLOUR.GREEN, new ToggleField(R.id.ib_colour_g));
        this.colourSearchFlags.put(COLOUR.COLOURLESS, new ToggleField(R.id.ib_colour_c));

        this.toggleButtonMap = new SparseArray<ToggleField>();
        for (Map.Entry<TYPE, ToggleField> entry : this.typeSearchFlags.entrySet()) {
            toggleButtonMap.put(entry.getValue().resourceID, entry.getValue());
        }
        for (Map.Entry<COLOUR, ToggleField> entry : this.colourSearchFlags.entrySet()) {
            this.toggleButtonMap.put(entry.getValue().resourceID, entry.getValue());
        }

        this.cardMap = new HashMap<Integer, OracleCard>();
    }

    public void AddCard(OracleCard _card) {
        cardMap.put(_card.id, _card);
    }

    public void Search() {
        this.searchTypeCreature = this.typeSearchFlags.get(TYPE.CREATURE).bool;
        this.searchTypeArtifact = this.typeSearchFlags.get(TYPE.ARTIFACT).bool;
        this.searchTypeEnchantment = this.typeSearchFlags.get(TYPE.ENCHANTMENT).bool;
        this.searchTypeInstant = this.typeSearchFlags.get(TYPE.INSTANT).bool;
        this.searchTypeSorcery = this.typeSearchFlags.get(TYPE.SORCERY).bool;
        this.searchTypeLand = this.typeSearchFlags.get(TYPE.LAND).bool;
        this.searchTypePlaneswalker = this.typeSearchFlags.get(TYPE.PLANESWALKER).bool;

        this.searchAllTypes = this.searchTypeCreature && this.searchTypeArtifact && this.searchTypeEnchantment
                && this.searchTypeInstant && this.searchTypeSorcery && this.searchTypeLand && this.searchTypePlaneswalker;

        this.searchWhite = this.colourSearchFlags.get(COLOUR.WHITE).bool;
        this.searchBlue = this.colourSearchFlags.get(COLOUR.BLUE).bool;
        this.searchBlack = this.colourSearchFlags.get(COLOUR.BLACK).bool;
        this.searchRed = this.colourSearchFlags.get(COLOUR.RED).bool;
        this.searchGreen = this.colourSearchFlags.get(COLOUR.GREEN).bool;
        this.searchColourless = this.colourSearchFlags.get(COLOUR.COLOURLESS).bool;

        this.searchAllColours = searchWhite && searchBlue && searchBlack && searchRed && searchGreen && searchColourless;

        this.searchColourFlags = 0;
        this.searchColourFlags += this.searchWhite ? 1 : 0;
        this.searchColourFlags += this.searchBlue ? 2 : 0;
        this.searchColourFlags += this.searchBlack ? 4 : 0;
        this.searchColourFlags += this.searchRed ? 8 : 0;
        this.searchColourFlags += this.searchGreen ? 16 : 0;

        this.searchString = this.searchString.toLowerCase();

        this.searchSet = this.selectedSetIndex > 0 ? this.setList.get(this.selectedSetIndex - 1) : null;

        this.searchResults = new ArrayList<OracleCard>();
        for (HashMap.Entry<Integer, OracleCard> card : cardMap.entrySet()) {
            if (this.CardMatch(card.getValue())) {
                this.searchResults.add(card.getValue());
            }
        }

        java.util.Collections.sort(this.searchResults);
    }

    public boolean CardMatch(OracleCard _card) {
        if (!this.searchAllColours && ((_card.colour & this.searchColourFlags) == 0
                && !(_card.colour == 0 && this.searchColourless))) {
            return false;
        }

        if (this.matchColoursExactly) {
            if (_card.colour != this.searchColourFlags) {
                return false;
            }
        }

        if (this.excludeUnselectedColours && !this.searchAllColours) {
            if ((_card.colour & ~this.searchColourFlags) != 0) {
                return false;
            }
        }

        if (this.multicolouredOnly && _card.numColours < 2) {
            return false;
        }


        if (!this.searchAllTypes) {
            if ((!this.searchTypeCreature || !_card.type.contains("Creature"))
                    && (!this.searchTypeArtifact || !_card.type.contains("Artifact"))
                    && (!this.searchTypeEnchantment || !_card.type.contains("Enchantment"))
                    && (!this.searchTypeInstant || !_card.type.contains("Instant"))
                    && (!this.searchTypeSorcery || !_card.type.contains("Sorcery"))
                    && (!this.searchTypeLand || !_card.type.contains("Land"))
                    && (!this.searchTypePlaneswalker || !_card.type.contains("Planeswalker"))) {
                return false;
            }
        }

        if (this.searchString != null && !this.searchString.isEmpty()) {
            boolean textMatch = false;
            if (this.isSearchingName && _card.name.toLowerCase().contains(this.searchString)) {
                textMatch = true;
            }

            if (!textMatch && this.isSearchingType) {
                boolean inType = _card.type != null && _card.type.toLowerCase().contains(this.searchString);
                boolean inSubtype = _card.subtype != null && _card.subtype.toLowerCase().contains(this.searchString);

                if (inType || inSubtype) {
                    textMatch = true;
                }
            }

            if (!textMatch && this.isSearchingRules && _card.rules != null && _card.rules.toLowerCase().contains(searchString)) {
                textMatch = true;
            }

            if (!textMatch) {
                return false;
            }
        }

        if (this.searchSet != null) {
            boolean inSet = false;
            for (CardSet set : _card.sets) {
                if (set.setcode.equals(searchSet.setcode)) {
                    inSet = true;
                    break;
                }
            }

            if (!inSet) {
                return false;
            }
        }

        if (this.ownershipSearch == OWNERSHIP.OWNED && _card.total <= 0
                || this.ownershipSearch == OWNERSHIP.NOT_OWNED && _card.total > 0) {
            return false;
        }

        return true;
    }

    public void Reset() {
        this.searchString = "";

        this.isSearchingName = true;
        this.isSearchingType = false;
        this.isSearchingRules = false;

        for (COLOUR colour : COLOUR.values()) {
            this.colourSearchFlags.get(colour).bool = true;
        }

        this.matchColoursExactly = false;
        this.excludeUnselectedColours = false;
        this.multicolouredOnly = false;

        for (TYPE type : TYPE.values()) {
            this.typeSearchFlags.get(type).bool = true;
        }

        this.selectedSetIndex = 0;
        this.searchSet = null;

        this.ownershipSearch = OWNERSHIP.ALL;
    }
}
