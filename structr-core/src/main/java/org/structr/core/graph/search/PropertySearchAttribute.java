/**
 * Copyright (C) 2010-2019 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.core.graph.search;

import org.apache.commons.lang3.StringUtils;
import org.structr.api.search.ExactQuery;
import org.structr.api.search.FulltextQuery;
import org.structr.api.search.Occurrence;
import org.structr.core.GraphObject;
import org.structr.core.property.PropertyKey;

/**
 * Represents an attribute for textual search, used in {@link SearchNodeCommand}.
 */
public class PropertySearchAttribute<T> extends SearchAttribute<T> implements ExactQuery, FulltextQuery {

	private boolean isExactMatch = false;

	public PropertySearchAttribute(final PropertyKey<T> key, final T value, final Occurrence occur, final boolean isExactMatch) {

		super(occur, key, value);

		this.isExactMatch = isExactMatch;
	}

	@Override
	public String toString() {
		return "PropertySearchAttribute(" + super.toString() + ")";
	}

	@Override
	public Class getQueryType() {
		return isExactMatch ? ExactQuery.class : FulltextQuery.class;
	}

	@Override
	public boolean isExactMatch() {
		return isExactMatch;
	}

	@Override
	public void setExactMatch(final boolean exact) {
		this.isExactMatch = exact;
	}

	@Override
	public boolean includeInResult(final GraphObject entity) {

		T nodeValue          = entity.getProperty(getKey());
		Occurrence occur     = getOccurrence();
		T searchValue        = getValue();

		if (occur.equals(Occurrence.FORBIDDEN)) {

			if ((nodeValue != null) && compare(nodeValue, searchValue) == 0) {

				// don't add, do not check other results
				return false;
			}

		} else {

			if (nodeValue != null) {

				if (!isExactMatch) {

					if (nodeValue instanceof String && searchValue instanceof String) {

						String n = (String) nodeValue;
						String s = (String) searchValue;

						return StringUtils.containsIgnoreCase(n, s);

					}

				}

				if (compare(nodeValue, searchValue) != 0) {
					return false;
				}

			} else {

				if (searchValue != null && StringUtils.isNotBlank(searchValue.toString())) {
					return false;
				}
			}
		}

		return true;
	}

	private int compare(T nodeValue, T searchValue) {

		if (nodeValue instanceof Comparable && searchValue instanceof Comparable) {

			Comparable n = (Comparable)nodeValue;
			Comparable s = (Comparable)searchValue;

			return n.compareTo(s);
		}

		return 0;
	}
}
