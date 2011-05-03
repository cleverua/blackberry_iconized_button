package mypackage;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import com.cleverua.bb.ui.component.IconizedButtonFieldSet;
import com.cleverua.bb.ui.component.IconizedButtonFieldSet.ButtonFieldDataProvider;
import com.cleverua.bb.ui.component.IconizedButtonFieldSet.IButtonFieldDataProvider;

public class DemoApp extends UiApplication {
    
    public static void main(String[] args) {
        DemoApp myApp = new DemoApp();       
        myApp.enterEventDispatcher();
    }

    public DemoApp() {        
        pushScreen(new MyScreen());
    }    
}

class MyScreen extends MainScreen {

    public MyScreen() {
        setTitle(new LabelField("Iconized Button Demo", Field.FIELD_HCENTER));

        final ButtonFieldDataProvider btn1DataProvider = new ButtonFieldDataProvider(
            "Grow a tree",
            Bitmap.getBitmapResource("res/tree.png"),
            new Runnable() {
                public void run() {
                    Dialog.inform("'Grow a tree' button clicked.");
                }
            }
        );
        
        final ButtonFieldDataProvider btn2DataProvider = new ButtonFieldDataProvider(
            "Build a house",
            Bitmap.getBitmapResource("res/house.png"),
            new Runnable() {
                public void run() {
                    Dialog.inform("'Build a house' button clicked.");
                }
            }
        );
        
        final ButtonFieldDataProvider btn3DataProvider = new ButtonFieldDataProvider(
            "Bear a son",
            Bitmap.getBitmapResource("res/baby.png"),
            new Runnable() {
                public void run() {
                    Dialog.inform("'Bear a son' button clicked.");
                }
            }
        );
        
        final IButtonFieldDataProvider[] btnDataProviders = new IButtonFieldDataProvider[] {
            btn1DataProvider,
            btn2DataProvider,
            btn3DataProvider
        };
        
        final IconizedButtonFieldSet bfs = new IconizedButtonFieldSet(btnDataProviders);
        
        add(bfs);
    }
}
