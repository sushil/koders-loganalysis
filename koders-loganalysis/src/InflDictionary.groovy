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

class InflDictionary {
	
	public static HashSet<String> words = new HashSet<String>()
	
	private def static load(inflFile){
		words.clear()
		def fReader = new FileReader(inflFile)
		fReader.readLines().each {line ->
			
			// println line
			line.split("\\s").each() { term -> 
				// println term		
				term = term.toLowerCase().replaceAll("[\\W]|[0-9]|[_]","")
				if(term.matches("\\w+") && !term.matches("[0-9]+")){
						words.add(term)
				}
			}
		}
	}
	
	
	def static writeWordList(inflFile, dictFile){
		load(inflFile)
		
		new File(dictFile).withWriter {out ->
			words.each() { out.writeLine(it) }
		}
	}
	
	def static loadDict(dict){
		words.clear()
		def fReader = new FileReader(dict)
		fReader.readLines().each {
			words.add(it)
		}
	}
	
}
