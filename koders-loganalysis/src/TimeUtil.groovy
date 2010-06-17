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

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */
public class TimeUtil {
	
	static String pattern = "yyyy-MM-dd HH:mm:ss.SSS z"
	static SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US)
	
	static public int minDiff(String start, String end){
		
		long diff = milisecDiff(start, end)
		// return minutes
		return diff / (1000 * 60)
	}
	
	static public int secDiff(String start, String end){
		
		long diff = milisecDiff(start, end)
		// return minutes
		return diff / 1000
	}
	
	// expects input in format:
	//  yyyy-MM-dd HH:mm:ss.SSS
	static public long milisecDiff(String start, String end) {
		
		Date sd = dateFormat.parse(start + " PST")
		Date ed = dateFormat.parse(end + " PST")
		
		long diff = ed.getTime() - sd.getTime()
		return diff
	}
		
	static public String printElapsedTime(long start, long stop) {
		
		String elapsed = new String("");
		
		long hours = calcElapsedHours(start, stop);
		long min = calcElapsedMinutes(start, stop) % 60;
		long sec = calcElapsedSeconds(start, stop) % 60;
		long msec = calcElapsedMillis(start, stop) % 1000;
		
		if(hours < 10)
			elapsed = elapsed.concat("0");
		elapsed = elapsed.concat(String.valueOf(hours));
		elapsed = elapsed.concat(":");
		
		if(min < 10)
			elapsed = elapsed.concat("0");
		elapsed = elapsed.concat(String.valueOf(min));
		elapsed = elapsed.concat(":");
		
		if(sec < 10)
			elapsed = elapsed.concat("0");
		elapsed = elapsed.concat(String.valueOf(sec));
		elapsed = elapsed.concat(".");
		
		if(msec < 10)
			elapsed = elapsed.concat("00");
		else if(msec < 100)
			elapsed = elapsed.concat("0");
		elapsed = elapsed.concat(String.valueOf(msec));
		
		return elapsed;
	}
	
	static public long calcElapsedMillis(long start, long stop) {
		return stop - start;
	}
	
	static public long calcElapsedSeconds(long start, long stop) {
		return calcElapsedMillis(start, stop) / 1000;
	}
	
	static public long calcElapsedMinutes(long start, long stop) {
		return calcElapsedSeconds(start, stop) / 60;
	}
	
	static public long calcElapsedHours(long start, long stop) {
		return calcElapsedMinutes(start, stop) / 60;
	}
}