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
 * @author <a href="sbajrach@ics.uci.edu">skb</a> 
 *
 */

public class Util2 {
		
		/**
		 * returns the array of integers whose entries represent
		 * the rank of elements in darr, as if where that element 
		 * in darr would appear when darr is sorted
		 * : lower the number, lower the rank
		 * 
		 */
		public static int[] elementRank(double []darr, int n){
			int[] indexArr = new int[n];
			
			// create copies so that the array from topic modeling is not changed
			double[] orig = new double[darr.length],  sorted = new double[darr.length]; 
			System.arraycopy(darr, 0, orig, 0, darr.length);
			System.arraycopy(darr, 0, sorted, 0, darr.length);
			
			Arrays.sort(sorted);
			
			for(int i = 0; i < n; i++ ){
				int foundIndex = Arrays.binarySearch(sorted, orig[i])
				
				assert foundIndex >= 0
				
				indexArr[i] = foundIndex
			}
			
			return indexArr;
		}
		
		/**
		 * assumes non negative values in the darr
		 * @param darr original array with doubles
		 * @param n number of top indices to return
		 * @return array with the indices that point to the top 'n' elements
		 * 			in the 'darr' 
		 */
		public static int[] topNIndicesFromArray(double[] darr, int n) {
			int[] indexArr = new int[n];
			
			// create copies so that the array from topic modeling is not changed
			double[] orig = new double[darr.length],  sorted = new double[darr.length]; 
			System.arraycopy(darr, 0, orig, 0, darr.length);
			System.arraycopy(darr, 0, sorted, 0, darr.length);
			
			Arrays.sort(sorted);
			
			for(int i = 0; i < n; i++ ){
				indexArr[i] = linearSearchUnsortedArray(orig, sorted[darr.length - 1 - i]);
				// this is not to be found again
				orig[indexArr[i]] = -1; 
			}
			
			return indexArr; 
		}
		
			private static int linearSearchUnsortedArray(double[] darr, double value){
				
				for(int i=0; i<darr.length; i++){
					if (darr[i] == value)	return i;
				}
				
				return -1;
			}
	}