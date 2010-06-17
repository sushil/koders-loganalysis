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

public class Status{
	
	public boolean disable = false
	
	long lines = 0l
	int interval = 1
	long processed = 0l
	long start = 0l
	
	def public reset(elementSize){
		if(elementSize<100){
			interval = elementSize
		} else {
			interval = elementSize/100
		}
		
		start = System.currentTimeMillis()
		lines = elementSize
		processed = 0
	}
	
	def public update(){
		if (disable) return
		processed = processed + 1
		if( (processed % interval) == 0){
			// leftPad( ((int)((processed/lines)*100)) + "",  3.intValue(), " ") 
			
			// print  // ((int)((processed/lines)*100)) + 
			//  "" 
			// + " % done. Time elaspsed: " 
			// + 
			int _p = ((processed / lines) * 100) + 1 
			print '\b' * 46
			String _m = '.. processed ' + _p.toString().padLeft(3) + " % " + " in: " + TimeUtil.printElapsedTime(start.longValue(), System.currentTimeMillis().longValue())
			// print _m.length()
			print _m      
		}
		
	}
	
	
}

