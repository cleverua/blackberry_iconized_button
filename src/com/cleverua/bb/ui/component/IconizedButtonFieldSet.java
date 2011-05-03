package com.cleverua.bb.ui.component;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;

public class IconizedButtonFieldSet extends VerticalFieldManager {
    
    public interface IButtonFieldDataProvider {
        String getLabel();
        Bitmap getIcon();
        Runnable getAction();
    }
    
    public static class ButtonFieldDataProvider implements IButtonFieldDataProvider {

        private String label;
        private Bitmap icon;
        private Runnable action;
        
        public ButtonFieldDataProvider(String label, Bitmap icon, Runnable action) {
            this.label  = label;
            this.icon   = icon;
            this.action = action;
        }
        
        public Runnable getAction() {
            return action;
        }

        public Bitmap getIcon() {
            return icon;
        }

        public String getLabel() {
            return label;
        }
    }
    
    private final int padding;
    private ButtonField[] buttons;
    
    public IconizedButtonFieldSet(IButtonFieldDataProvider[] dataProviders) {
        super(USE_ALL_WIDTH);
        
        // TODO: we are simple now (we don't do any argument validation), 
        // but it'd be nice to add this later.
        
        padding = 10; // TODO: use a smaller padding for OS 4.5
        
        buttons = createButtons(dataProviders);
        
        for (int i = 0; i < buttons.length; i++) {
            add(buttons[i]);
        }
    }

    /**
     * Constructs an empty field set. Buttons may be added later using the
     * {@link addButtons(IButtonFieldDataProvider[] dataProviders) } method.
     */
    public IconizedButtonFieldSet() {
        this(new IButtonFieldDataProvider[] {});
    }
    
    public void addButtons(IButtonFieldDataProvider[] dataProviders) {
        final ButtonField[] newButtons = createButtons(dataProviders);
        for (int i = 0; i < newButtons.length; i++) {
            final ButtonField button = newButtons[i];
            add(button);
            Arrays.add(buttons, button); // also keep array in sync 
        }
    }
    
    public void deleteAll() {
        super.deleteAll();
        buttons = new ButtonField[] {}; // also keep array in sync
    }
    
    private ButtonField[] createButtons(IButtonFieldDataProvider[] dataProviders) {
        final int length = dataProviders.length;
        
        final ButtonField[] result = new ButtonField[length];
        
        for (int i = 0; i < length; i++) {
            
            final IButtonFieldDataProvider dataProvider = dataProviders[i];
            
            final Bitmap icon = dataProvider.getIcon();
            final int iconHeight = icon.getHeight();
            final int iconWidth  = icon.getWidth();
            
            final ButtonField button = new ButtonField("", ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER) {
                protected void paint(Graphics g) {
                    super.paint(g);

                    // preserve DrawingOffset and ClippingRect
                    final XYPoint drawingOffset = new XYPoint();
                    g.getDrawingOffset(drawingOffset);
                    
                    final XYRect clip = g.getClippingRect();
                    
                    // this was a tricky part: to find a way of getting a right offset
                    final int yOffset = getHeightOfPrecedingChilds(this);
                    
                    try {
                        g.popContext(); // this allows us to draw on the entire ButtonField area
                        
                        g.drawBitmap(
                            padding, yOffset + ((getHeight() - iconHeight) >> 1),
                            iconHeight, iconWidth, icon, 0, 0
                        );
                        
                        final int colorBackup = g.getColor();
                        try {
                            g.setColor(isFocus() ? Color.WHITE : Graphics.WHITE);
                            g.drawText(
                                dataProvider.getLabel(),
                                padding + iconWidth + padding, 
                                yOffset + ((getHeight() - g.getFont().getHeight()) >> 1),
                                DrawStyle.ELLIPSIS, getWidth() - (padding + iconWidth + padding) - padding
                            );
                        } finally {
                            g.setColor(colorBackup);
                        }
                        
                    } finally {
                        g.pushContext(clip, drawingOffset.x, drawingOffset.y);
                    }
                }
                
                protected void layout(int width, int height) {
                    height = Math.max(getFont().getHeight(), iconHeight);
                    setExtent(width, height);
                }
            };
            
            button.setChangeListener(new FieldChangeListener() {
                public void fieldChanged(Field f, int context) {
                    if (f == button) { // just in case
                        dataProvider.getAction().run();
                    }
                }
            });
            
            result[i] = button;
        }
        
        return result;
    }
    
    private int getHeightOfPrecedingChilds(Field f) {
        // find out the index of the argument Field on the manager
        final int fieldsCount = getFieldCount();
        int childIndex = -1;
        for (int i = 0; i < fieldsCount; i++) {
            if (getField(i) == f) {
                childIndex = i;
                break;
            }
        }
        
        if (childIndex == -1) {
            // we did not find our button on the manager, this is unexpected
            throw new RuntimeException();
        }
        
        // iterate over the preceding child collecting their height
        int totalHeight = 0;
        for (int i = 0; i < childIndex; i++) {
            totalHeight += getField(i).getHeight();
        }
        
        return totalHeight;
    }
}
