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


public class DateElements {
	
	public int year, month, date, hour, minute, second

	//	 assuming date is a String in the following
	// format:
	//   2007-01-04 08:37:45.000
	//   yyyy-mm-dd hh:mm:ss.ms
	public DateElements getUpdatedDateElements(String sDate){
		setDateElements(sDate)
		return this
	}
	
	private void setDateElements(String sDate){
		String[] fChunk = sDate.split(" ")
		
		String[] ymd = fChunk[0].split("-")
		this.year = new Integer(ymd[0]).intValue()
		this.month = new Integer(ymd[1]).intValue()
		this.date = new Integer(ymd[2]).intValue()
		
		String[] hms = fChunk[1].split(":")
		this.hour = new Integer(hms[0]).intValue()
		this.minute = new Integer(hms[1]).intValue()
		
		String[] sm = hms[2].split("\\.")
		this.second = new Integer(sm[0]).intValue()
	}
		
}
