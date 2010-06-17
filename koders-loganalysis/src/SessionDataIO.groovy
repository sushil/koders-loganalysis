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

class SessionDataIO {
	static def updateCount(amap, akey){
		if(amap.containsKey(akey)){
			amap[akey] = amap[akey] + 1
		} else {
			amap[akey] = 1
		}
	}
	
	static def writeCount(filename, amap, header){
		new File(filename).withWriter {out ->
			out.writeLine(header)
			amap.each() {variablev, countv ->
				out.writeLine(variablev + "\t" + countv)
			}
			
		}
	}
	
	static def printCount(amap, countVariable){
		println countVariable + "\tcount"
		amap.each() { variablev, countv ->
			println(variablev + "\t" + countv)
		}
	}
	
	static def String list2String(lst){
		StringBuffer buf = new StringBuffer()
		lst.each(){ item ->
			buf.append (item)
		}
		return buf.toString()
	}
	
	static def String compressSequence(lst){
		StringBuffer buf = new StringBuffer()
		lst.eachWithIndex(){ item, pos ->
			if(pos>0){
				if(lst[pos]==lst[pos-1]){
					if(buf.charAt(buf.length()-1)!='^'){
						buf.append ('^')
					}
				} else {
					buf.append (item)
				}
			} else {
				buf.append (item)
			}
		}
		return buf.toString()
	}
	
	static def String compressSequence2(lst){
		StringBuffer buf = new StringBuffer()
		lst.eachWithIndex(){ item, pos ->
			if(pos>0){
				if(lst[pos]==lst[pos-1]){
					
				} else {
					buf.append (item)
				}
			} else {
				buf.append (item)
			}
		}
		return buf.toString()
	}
}
