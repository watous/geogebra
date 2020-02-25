package org.geogebra.web.full.gui.menubar.action;

import com.google.gwt.user.client.Window;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows the tutorial
 */
public class ShowTutorialsAction extends DefaultMenuAction<Void> {

    @Override
    public void execute(Void item, AppWFull app) {
        String url = app.getLocalization().getTutorialURL(app.getConfig());
        Window.open(url, "_blank", "");
    }
}
