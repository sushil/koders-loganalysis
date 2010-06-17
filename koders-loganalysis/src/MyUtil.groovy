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


public class MyUtil{
	
	public static printDoubleArray(double[] darray){
		for (int i=0; i<darray.length; i++){
			print " " + darray[i]
		}
		println ""
	}
	
	public static double[] ListToDoubleArray(alist) {
		
		double[] darray = new double[alist.size()]
		
		for(int i=0; i<darray.length; i++){
			darray[i] = (Double) alist[i].doubleValue()
		}
		
	return darray
	
	}
	
	private static double log (int base, double n) {
		double log_base_e_of_n = Math.log (n);
		double log_base_e_of_base = Math.log (base);
		
		return log_base_e_of_n / log_base_e_of_base;
	}
	
	/** squared euclidean distance **/
    static public double squaredEuclidean( double[] x, double[] y)
    {
        if( x.length != y.length ) throw new RuntimeException("Arguments must have same number of dimensions.");

        double cumssq = 0.0;
        for(int i=0; i < x.length; i++)
            cumssq += (x[i] - y[i]) * (x[i] - y[i]);

        return cumssq;
    }

    /** euclidean distance **/
    static public double euclidean( double[] x, double[] y)
    {
        return Math.sqrt( squaredEuclidean(x,y) );
    }


	
}