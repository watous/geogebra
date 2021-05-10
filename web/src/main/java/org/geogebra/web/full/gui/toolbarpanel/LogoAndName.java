package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LogoAndName implements IsWidget {

	private static final int LOGO_MARGIN = 72; // 24px top + 48px bottom
	private final int headerHeight;
	private final Widget panel;

	/**
	 * @param app application
	 * @param headerHeight header height
	 */
	public LogoAndName(App	app, int headerHeight) {
		this.headerHeight = headerHeight;
		String transKey = app.getConfig().getVersion().getTransKey();
		if (app.getConfig().getSubAppCode() != null) {
			transKey = GeoGebraConstants.Version.SUITE.getTransKey();
		}
		Label name = new Label(app.getLocalization().getMenu(transKey));
		NoDragImage icon = new NoDragImage(((AppWFull) app).getActivity().getIcon(),
				24);
		panel = LayoutUtilW.panelRow(icon, name);
		panel.addStyleName("avNameLogo");
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	/**
	 * @param aView algebra view
	 * @param parentHeight parent panel height in pixels
	 */
	public void onResize(AlgebraViewW aView, int parentHeight) {
		AppW app = aView.getApp();
		boolean showLogo = !app.getAppletFrame().isKeyboardShowing();
		panel.setVisible(showLogo);
		if (showLogo) {
			int minHeight = parentHeight - panel.getOffsetHeight() - LOGO_MARGIN - headerHeight;
			aView.getElement().getStyle().setProperty("minHeight", minHeight + "px");
		} else {
			aView.getElement().getStyle().clearProperty("minHeight");
		}
	}
}
