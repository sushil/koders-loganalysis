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


/**
 * Reformulation is calculated based on sequence of search activities in a
 * session, even if a download appears between two search activities 
 * in a session they will be used to compute reformulation stats  
 * 
 * @author <a href="bajracharya@gmail.com">Sushil Bajracharya</a>
 * @created Mar 16, 2010
 *
 */

class ReformulationStat {
	/*
	int countTermsAdd        // A only new terms are added
	int countDelTerms        // D only existing terms are delete
	int countModifyOperators // O add or remove operators on the same query
	int countNewQuery        // N completely new query, no common terms from previous query 
	int countRepeatQuery     // = no reformulation, same query
	int countOtherMod        // M some delete some add (not any of the above, two queries have at least one common term)
	
	int countStartQuery		 // n a new query that starts a session -- there is no reformulation here
	*/
		
	
	def reformTypeQueryCount = [:]
	
	def reformulationPatternDCount = [:]
	
	// q1 = previous query, q2 = current query
	public static String getReformulationType(q1, q2){
		
		assert ! (q1 == "" && q2 == "")
		
		if(q1 == q2) return "="
		
		if(q1 == "") return "n"
		
		String[] q1Parts = q1.trim().split("\\s")
		String[] q2Parts = q2.trim().split("\\s")
		
		if(q1Parts.length == q2Parts.length){
			
			if(CheckWord.getQueryWoOperator(q1) ==
			CheckWord.getQueryWoOperator(q2))
				return "O"
			
		} 
		
		String[] big
		String[] small
		
		if(q1Parts.length > q2Parts.length){
			big = q1Parts
			small = q2Parts
		} else {
			small = q1Parts
			big = q2Parts
		}
		
		int foundType = howFound(big, small)
		
		if (foundType == 0)
			return "N"
		
		if (foundType == 2){
			if(q1Parts.length > q2Parts.length){
				return "D"
			} else {
				return "A"
			}
		}
		
		return "M"
	}
	
	// 0 - no terms found, 1 - some terms found, some not, 2 - all terms found
	def static int howFound(big, small){
		
		boolean someFound = false
		boolean allFound = true
		
		small.each { term -> 
			if(term in big){
				someFound = true
			} else {
				allFound = false
			}
		}
		
		if(!someFound) 
			return 0
		else if(someFound && !allFound)
			return 1
		else
			return 2
		
	}
	
	
}

/*
 [generalize]
 A generalize refinement had a new search string with one of the following properties: 
 - it was a substring of the original, 
 - it contained a proper subset of the tokens in the original, 
 - it split a single token into multiple tokens and left the rest unchanged. 
 [specialize]
 A specialize refinement had a new search string with one of the following properties: 
 - it was a superstring of the original, 
 - it added tokens to the original
 - it combined several tokens from the original together into one and left the rest unchanged. 
 [reformulate]
 A reformulate refinement had 
 - a new search string that contained some tokens in common with the original 
 but was neither a generalization nor specialization. 
 [new]
 A new query had no tokens in common with the original. 
 [spell check]
 Spelling refinements were any queries where spelling errors were corrected, 
 as defined by Levenshtein distances between corresponding tokens all being 
 less than 3.
 */

/*
 Adding terms: 
 Deleting terms: 
 Modifying operators only: 
 Totally changing the query:	(new query)
 Otherwise modifying query terms: (some delete some add)
 Adding one term:	
 Deleting one term:	
 operators everywhere:
 */
