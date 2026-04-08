package io.github.store_prototype.utils.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;

public class CustomTextTooltip extends CustomToolTip<Label> {
    public CustomTextTooltip (@Null String text, Skin skin) {
        this(text, CustomTooltipManager.getInstance(), skin.get(TextTooltip.TextTooltipStyle.class));
    }

    public CustomTextTooltip (@Null String text, Skin skin, String styleName) {
        this(text, CustomTooltipManager.getInstance(), skin.get(styleName, TextTooltip.TextTooltipStyle.class));
    }

    public CustomTextTooltip (@Null String text, TextTooltip.TextTooltipStyle style) {
        this(text, CustomTooltipManager.getInstance(), style);
    }

    public CustomTextTooltip (@Null String text, CustomTooltipManager manager, Skin skin) {
        this(text, manager, skin.get(TextTooltip.TextTooltipStyle.class));
    }

    public CustomTextTooltip (@Null String text, CustomTooltipManager manager, Skin skin, String styleName) {
        this(text, manager, skin.get(styleName, TextTooltip.TextTooltipStyle.class));
    }

    public CustomTextTooltip (@Null String text, final CustomTooltipManager manager, TextTooltip.TextTooltipStyle style) {
        super(null, manager);

        container.setActor(newLabel(text, style.label));

        setStyle(style);
    }

    protected Label newLabel (String text, Label.LabelStyle style) {
        return new Label(text, style);
    }

    public void setStyle (TextTooltip.TextTooltipStyle style) {
        if (style == null) throw new NullPointerException("style cannot be null");
        container.setBackground(style.background);
        container.maxWidth(style.wrapWidth);

        boolean wrap = style.wrapWidth != 0;
        container.fill(wrap);

        Label label = container.getActor();
        label.setStyle(style.label);
        label.setWrap(wrap);
    }

    /** The style for a text tooltip, see {@link CustomTextTooltip}.
     * @author Nathan Sweet */
    static public class TextTooltipStyle {
        public Label.LabelStyle label;
        public @Null Drawable background;
        /** 0 means don't wrap. */
        public float wrapWidth;

        public TextTooltipStyle () {
        }

        public TextTooltipStyle (Label.LabelStyle label, @Null Drawable background) {
            this.label = label;
            this.background = background;
        }

        public TextTooltipStyle (TextTooltip.TextTooltipStyle style) {
            label = new Label.LabelStyle(style.label);
            background = style.background;
            wrapWidth = style.wrapWidth;
        }
    }
}
