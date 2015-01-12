package com.standardNaast.view.converter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.beansbinding.Converter;

import standardNaast.types.AbonnementStatus;

public class AbonnementStatusConverter extends
		Converter<AbonnementStatus, String> {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("com.standardNaast.bundle.Bundle"); //$NON-NLS-1$

	@Override
	public String convertForward(final AbonnementStatus arg0) {
		return AbonnementStatusConverter.BUNDLE.getString(arg0.name());
	}

	@Override
	public AbonnementStatus convertReverse(final String arg0) {
		final Enumeration<String> keys = AbonnementStatusConverter.BUNDLE
				.getKeys();
		final List<String> l = new ArrayList<>();

		while (keys.hasMoreElements()) {
			final String key = keys.nextElement();
			if (StringUtils.equals(
					AbonnementStatusConverter.BUNDLE.getString(key), arg0)) {
				l.add(key);
			}
		}

		for (final String key : l) {
			final AbonnementStatus status = AbonnementStatus.valueOf(key);
			if (status != null) {
				return status;
			}
		}
		return null;

	}

}
