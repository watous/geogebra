package org.geogebra.desktop.factories;

import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.desktop.util.NumberFormatD;
import org.geogebra.desktop.util.ScientificFormat;

public class FormatFactoryD extends org.geogebra.common.factories.FormatFactory {
	@Override
	public ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote) {
		return new ScientificFormat(sigDigit, maxWidth, sciNote);
	}

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		NumberFormatD ret = new NumberFormatD();
		ret.setMaximumFractionDigits(digits);
		ret.setGroupingUsed(false);
		return ret;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s, int i) {
		return new NumberFormatD(s, i);
	}
}
