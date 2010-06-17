/*
 * koders-loganalysis: Tools/Scripts to analyze Koders usage log. 
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.util.HashSet;
import java.util.Set;

// import rita.wordnet.*;

public class CheckWord {

	public static boolean RemoveOperatorB4TypeDetect = false;
	// static RiWordnet wordnet = new RiWordnet();

	public static String getQueryType(String query) {
		
		if(RemoveOperatorB4TypeDetect){
			query = getQueryWoOperator(query);
		}
		
		boolean foundWord = false;
		boolean foundCode = false;
		String[] qparts = query.split("\\s");

		for (String part : qparts) {
			if (foundWord && foundCode)
				return QueryType.MIXED;

			part = part.toLowerCase();

			if (!part.matches("\\w+")) {
				foundCode = true;
				continue;
			}

			// if (wordnet.exists(part)) {
			if (InflDictionary.words.contains(part)){
				foundWord = true;
			} else {
				foundCode = true;
			}

		}

		if (foundWord && !foundCode) {
			return QueryType.NATURAL;
		}

		if (!foundWord && foundCode) {
			return QueryType.CODE;
		}

		if (!foundWord && !foundCode) {
			System.err
					.println("ERROR: query is neither a word nor a code!\nQuery: "
							+ query);
			System.exit(-1);
		}

		return QueryType.MIXED;

	}

	public static String cleanOperatorsInTerms(String term) {
		return term.replaceAll("^\"", "").replaceAll("\"$", "").replaceAll(
				"\\*$", "").replaceAll("^-", "").replaceAll("^\\+", "")
				.replaceAll("^cdef:", "").replaceAll("^mdef:", "").replaceAll(
						"^idef:", "").replaceAll("^mcall:", "");

	}

	
	public static String getOperator(String term) {

		if (term.startsWith("\"")) {
			return "PRE_QUOTE";
		}

		if (term.startsWith("-")) {
			return "-";
		}

		if (term.startsWith("+")) {
			return "+";
		}

		if (term.startsWith("cdef:")) {
			return "cdef";
		}
		if (term.startsWith("mdef:")) {
			return "mdef";
		}
		if (term.startsWith("idef:")) {
			return "idef";
		}
		if (term.startsWith("mcall:")) {
			return "mcall";
		}
		
		if (term.endsWith("\"")) {
			return "POST_QUOTE";
		}
		
		if (term.endsWith("*")) {
			return "POST_STAR";
		}

		return "0";
	}

	public static String getQueryWoOperator(String query){
		StringBuffer buf = new StringBuffer();
		for(String term: query.split("\\s")){
			buf.append(cleanOperatorsInTerms(term));
			buf.append(" ");
		}
		return buf.toString().trim();
	}
	
	public static Set<String> getAllOperators(String query){
		HashSet<String> operators = new HashSet<String>();
		
		for(String s: query.trim().split("\\s")){
			String op = getOperator(s.trim());
			operators.add(op);
		}
		
		return operators;
	}

}
