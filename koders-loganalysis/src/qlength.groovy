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


def root =  "/Users/shoeseal/DATA/koders/"
           // "/home/sushil/Scratch/LogAnalysis/ese-data/"
def ip = // "all"
        "alltest"
def fIp = root + ip

def fReader = new FileReader(fIp)
def lengthCount = [:]

fReader.readLines().each {line ->

  String[] lineItems = line.trim().toString().split("\t", 9)
  if (lineItems.length != 9) {
  } else if(lineItems[6].equals("1000")) {
    String query = lineItems[8]
    String[] terms = query.split("\\s")
    int length = terms.length
    if (lengthCount.containsKey(length)) {
      lengthCount[length] = lengthCount[length] + 1
    } else {
      lengthCount[length] = 1
    }

  }
}

def fOp = root + ip + "--op_qlength-count.txt"
new File(fOp).withWriter {out ->
  out.writeLine("qlength\tcount")
  lengthCount.each {qlength, count ->
    out.writeLine(qlength + "\t" + count)
  }

}

println "done"
