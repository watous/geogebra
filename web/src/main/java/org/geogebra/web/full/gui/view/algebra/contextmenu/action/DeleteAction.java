package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.core.client.Scheduler;

public class DeleteAction extends DefaultMenuAction<GeoElement> {

	@Override
	public void execute(GeoElement item, AppWFull app) {
		item.remove();
		app.storeUndoInfo();
		Scheduler.get().scheduleDeferred(app.getAlgebraView()::resetDataTestOnDelete);
	}
}
