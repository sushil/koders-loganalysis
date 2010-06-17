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

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;


def linesToDiscard = []

def filePrefix = // "/Users/shoeseal/Scratch/LogAnalysis/koders-data/"
  "/Users/shoeseal/DATA/koders/"


def opFilePrefix = "/Users/shoeseal/Scratch/LogAnalysis/koders-data/"

/* old
def allJavaU = "uid-lang-terms-all-java-users.txt"
def fc = "uid-lang-terms-FC.txt"
def woRatio = "uid-lang-terms-FC-wo-ratio_limit.txt"
def lt15 = "uid-lang-terms-LT-15sact.txt"
*/

def testf = "test"
def testfT = "testTerms"
def ult1jq = // "ullt1jQ.txt"
  "uid-lang-lics-terms.txt"

// config
// change this to change file
def ip = ult1jq // testfT 

def file = filePrefix + ip 
 
ArrayList<TopicDoc> collection = new ArrayList<TopicDoc>();


// config
Util.RemoveStopWord = false;
Util.RemoveLessQueryUserThreshold = 0
Util.RemoveOutliers = true
Util.FilterLangUsers = true

// EXECUTION
pre()
Vocabulary.clear()
readFiles(file, collection, linesToDiscard)
// debug
// linesToDiscard.each() { td -> println td}
// removeErrorLines(file, file+".corrected", linesToDiscard)
println "Collection size: " + collection.size()
println "Uids Term Stat size: " + UidsTermStat.userData.size()


// debug: print sorted lang count per uid
/*
UidsTermStat.userData.each(){ uid, data ->
	print uid 
	// sort ascending
	sortedKey = data[0].keySet().toList().sort{data[0][it]}
	// print in reverse order
	sortedKey[-1..-(sortedKey.size())].each{ sortedLang -> 
		print "\t"+sortedLang+"," +  data[0][sortedLang]
	}
	println ""
}
*/

//debug: print term stat for each user
/*
print "uid\t"

(0..TermStat.getSize()-1).each() { print TermStat.getByOrdinal(it).name + "\t"}
println ""
UidsTermStat.userData.each() { uid, data ->
	print uid
	data[1].each(){print "\t" + it}
	println ""
}
*/

// debug
// println tDoc.key + " "+ tDoc.docAbstract

// debug
//
//collection.each() { td ->
//	println "z\t \t \t \t \t \t \t \t \t \t" + td.docAbstract + "\t" + td.key 
//}

// WRITE
// writeUidLicTermStat(opFilePrefix)
 writeCollectionFiles(opFilePrefix, ip, collection)

println "Writing Word Frequency .."
Vocabulary.writeWordFreq(opFilePrefix + ult1jq)

// end of script
println "== Done =="

//=========================================================

//FUNCTIONS

//SCRIPT START
def pre(){
	println "Topic Doc producer for dragon toolkit"
	println "Remove stop words: " + Util.RemoveStopWord
}


def readFiles(file, collection, linesToDiscard){

	println "..reading file"
	
	def f = new FileReader(file)
	// skip header
	def header = f.readLine()
	
	def oldUid = -1
	def uid = -1
	def userActCount = 0
	
	TopicDoc tDoc = new TopicDoc() 
	// StringBuffer lang = new StringBuffer()
	StringBuffer terms = null
	
	int linenum = 1
	int discardCount = 0
	
	f.readLines().each {
		linenum = linenum + 1
		cols = it.trim().toString().split("\\s", 4);
		
		def validLine = false;
		
		try{
			uid = Integer.valueOf(cols[0]).intValue()
			validLine = true
			
			if(cols.length >= 4) {
				if(! Util.lang.any { it.equals(cols[1])}) {
					validLine = false
					// System.err.println("Error in Line " + linenum + ". Not a language: " + cols[1])
				}
			} else {
				validLine = false
				// System.err.println("Error in Line " + linenum + ". Less than 3 fields")
			}
			
		}catch(Exception nfe){
			// nfe.printStackTrace()
			// System.err.println("Error in Line " + linenum + ". Not a uid: " + cols[0])
		}
		
		if (validLine){
			
			// this is how you are gettin the processed query items from the raw query
			String qterms = cols[3].trim().replaceAll("[^\\w]"," ").replaceAll("_"," ").trim()
			
			updateTermsStats(uid, cols[1], cols[2], cols[3], qterms)
			
			// should only proceed with a valid value of uid
			if (uid != oldUid) { 
			
				// if not the first line
				if(terms!=null){
					// dump curent document
					tDoc.docAbstract = terms.toString().trim()
											//.replaceAll(" _ZZZ_",".")
					
					Util.checkAndAddDocumentToCollection(collection, tDoc)
					
					// debug
					// println tDoc.key + " "+ tDoc.docAbstract
				}
				
				// create new
				tDoc = new TopicDoc()
				terms = new StringBuffer()
				oldUid = uid
				
				tDoc.key = uid
				
				qterms = Util.removeStopWords(qterms)
				if(qterms.length()>0){
					terms.append(" .")
					terms.append(qterms)
					//terms.append(" _ZZZ_")
				}
				
				userActCount = 1
				
			} else {
				
				// update current user/ doc
				tDoc.key = uid
				
				qterms = Util.removeStopWords(qterms)
				if(qterms.length()>0){
					terms.append(" .")
					terms.append(qterms)
					//terms.append(" _ZZZ_")
				}
				
				userActCount = userActCount + 1
			}

            Vocabulary.addTerms(qterms)

		}else{
			discardCount++
			linesToDiscard.add(linenum)
		}
	}
	
	// dump last
	tDoc.docAbstract = terms.toString().trim()
									//.replaceAll(" _ZZZ_",".")
									//	.replaceAll("_",".")
	
	Util.checkAndAddDocumentToCollection(collection, tDoc)
	
	println "lines read: " + linenum
	println "lines discarded: " + discardCount

} // end ReadFiles()

def passedFilter(uid) {
	
	boolean passOutlier = !(Util.RemoveOutliers==true  && Util.isOutlier(uid)) 
	boolean passLangUser = (Util.FilterLangUsers==true)?isLangUser(uid):true
	
	return (passOutlier && passLangUser)
}

def isLangUser(uid){
	
	def langCount = UidsTermStat.userLangCount(new Integer(uid))
	
	assert (langCount!=null && langCount.size()>0)
	
	sortedKey = langCount.keySet().toList().sort{ langCount[it] }
	
	def firstLang = sortedKey[-1]
	
	// consider that desired language is java then the following
	// sequences are valid
	//   java, .., ..
	//   *, java, ..
	
	// java, ..
	if(firstLang.equals(Util.FirstLang)) 
		return true;
	
	// secondLang will never be null here because of the way dataset
	// is generated
	def secondLang = (sortedKey.size()>=2)?sortedKey[-2]:null;
	
	// *, java
	if(firstLang.equals(Util.SecondLang) && secondLang.equals(Util.FirstLang))
		return true;
	
	// all other cases
	return false;
	
}

def writeUidLicTermStat(opfPrefix){

	def opFile = opfPrefix + "uid-lic-term-stat.csv"
	println "..writing uid-lic-term-stat"
	
	new File(opFile).withWriter { out ->
		// writer header
		out.write("uid")
		(0..TermStat.getSize()-1).each() { out.write("\t" + TermStat.getByOrdinal(it).name) }
		out.write("\tlic-count\tlic-1\tlic-2")
		out.writeLine("")
		
		// write data (rows)
		UidsTermStat.userData.each() { uid, data ->
			
			if( passedFilter(uid.toString()) ) {
				
				// uid
				out.write(uid + "")
				
				// term stat vector
				data[1].each() { out.write("\t" + it) } 
				
				// sort license ascending
				sortedKey = data[2].keySet().toList().sort{data[2][it]}
				
				// lic count
				out.write("\t" + sortedKey.size())
				// fist choice license
				out.write("\t" + sortedKey[-1])
				// second choice license
				if (sortedKey.size()>=2)
					out.write("\t" + sortedKey[-2])
				else
					out.write("\t" + "NA")
					
				out.writeLine("")
				
			} // end pass filter
		}// end each user data
	}// end write	
}


def writeCollectionFiles(opFilePrefix, ip, collection){

	def opf = (Util.RemoveStopWord==true)?(ip + ".sw-rem"):ip
		opf = (Util.RemoveLessQueryUserThreshold>0)?opf + ".LT" + Util.RemoveLessQueryUserThreshold + "Qrem":opf
		opf = (Util.RemoveOutliers==true)?opf + ".OLrem":opf
		opf = (Util.FilterLangUsers==true)?opf + ".LFltr":opf
	
	def opFile = opFilePrefix + opf + ".collection"
	println "..writing collection"
	//create dragon compatible file
	new File(opFile).withWriter { out ->
	    collection.each() { td ->
	     if( passedFilter(td.key) )
	    	out.writeLine("z\t \t \t \t \t \t \t \t \t \t" + 
	    			      td.docAbstract.replaceAll("\\."," ").replaceAll("[\\s]+"," ").trim() + 
	    			      "\t" + 
	    			      td.key)
	    }
	}
	
	def opFileNoDup = opFilePrefix + opf + ".NoDup.collection"	
	println "..writing user-queries"
	//produce a no duplicate query collection
	new File(opFileNoDup).withWriter { out ->
		collection.each() { td ->
			if( passedFilter(td.key) ){
				// print td.docAbstract + "\n--\n"
				String dupRemovedAbs = Util.removeDuplicateSentences(td.docAbstract, "\\.", " ")
				
				// print dupRemovedAbs + "\n==\n"
				
				out.writeLine("z\t \t \t \t \t \t \t \t \t \t" + dupRemovedAbs + "\t" + td.key)
				//out.writeLine(td.key + "\t" + dupRemovedAbs)
			}
		}
	}
	
	def opFileUserTerms = opFilePrefix + opf + ".Terms.collection"
	println "..writing user-terms"
	//produce a no duplicate terms per user collection
	new File(opFileUserTerms).withWriter { out ->
		collection.each() { td ->
			if( passedFilter(td.key) ){
				String dupRemovedAbsTerms = Util.removeDuplicateSentences(td.docAbstract.replaceAll("\\."," "),"\\s"," ")
				// println dupRemovedAbsTerms
		 		out.writeLine(td.key + "\t" + dupRemovedAbsTerms)
			}
		}
	}

} // end writeFiles()


/***
 * inFile: input file
 * opFile: out file to be produced
 * linesToDiscard: list of integers that are the lines that would be removed
 */
def removeErrorLines(inFile, opFile, linesToDiscard){
	
	if (linesToDiscard.size() <= 0) {
		println "Nothing to discard."
		return
	}
	
	println ".. creating a new files with erroneous lines discarded"
	inF  = new FileReader(inFile)
	
	new File(opFile).withWriter { out ->
		
		// write first line (header)
		out.writeLine(inF.readLine())
		
		// println "Not Writing"
		
		lineCount = 1
		inF.readLines().each() { td ->
			
			// println "Read: " + td
		    lineCount = lineCount + 1
			if (! linesToDiscard.any { it == lineCount })
				out.writeLine(td)
			else {
 				// println lineCount + " : " + td
			}
		}
	}
}

def updateTermsStats(uid, lang, lic, rawQuery, processedTerms){
	def uidData = UidsTermStat.userData.get(uid,[])
	
	assert (uidData.size()>=0 || uidData.size()<=3)  
	
	if(uidData.size()==3){
		
	} else if(uidData.size()==2){ 
		uidData[2] = [:]
	} else if(uidData.size()==1) {
		uidData[1] = new int[TermStat.getSize()]
		uidData[2] = [:]
	} else if(uidData.size()==0) {
		uidData[0] = [:]
		uidData[1] = new int[TermStat.getSize()]
		uidData[2] = [:]
	}
	
	
	updateUidLangCount(uid, lang, uidData[0] /*lang map*/)
	uidData[1] = updateUidTermStat(uid, rawQuery, uidData[1] /* old term stat vector*/)
	updateUidLicCount(uid, lic, uidData[2] /*lic map*/)
}

def updateUidLicCount(uid, lic, licMap) {
	licMap[lic] = licMap.get(lic,0) + 1
}

def updateUidLangCount(uid, lang, langMap) {
	langMap[lang] = langMap.get(lang,0) + 1
}

def updateUidTermStat(uid, rawQuery, oldTermStatVec) {
	
	int[] newTermStatVec = TermStat.getTermStatVectorForQuery(rawQuery)
	return TermStat.getUpdatedTermStatVector(oldTermStatVec, newTermStatVec)
	
	//debug
	/*
	(0..TermStat.getSize()-1).each() { print TermStat.getByOrdinal(it).name + ", "}
	println ""
	print uid + ":" 
	termvec.each() {print it + ", "}
	println ""
	*/
}

// CLASSES

class UidsTermStat{
	
//	 Language count from all search activities.
//	 Also depends on if the input data has all search activites
//	 or just unique queries
//	 [userid : [lang1:count1, lang2:count2, .. ] .. ]
	
	//[userid : [lang-count-list],  [term-stat-vector] ]
	def static userData = [:]	

	def static userLangCount(uid) {
		return userData[uid][0]
	}

	def static userLicCount(uid) {
		return userData[uid][2]
	}

	def static userTermsStatVector(uid) {
		return userData[uid][1]
	}
	

}

public enum TermStat {
	
    // counts per query
	MAX_TERMS_IN_QRY("max_trms"),
    MIN_TERMS_IN_QRY("min_trms"),
    QUERIES_WITH_QUOTES("quotes_q"),
    // QUERIES_WITH_FQN("fqns-q"),
    METHOD_LIKE_QRY("m_like_q"), 
    NATURAL_LANG("natlang_q"), // Conjunction of: no fqn, no relation, no method call, terms used > some threshold
    XML_LIKE_QRY("xml_like_q"),
    EXPR_LIKE_QRY("exp_like_q"), // expression like query == that has ';'
    JAVA("java"), // has the word java; used as xxx.java or xxx java
    
    // count per term
    MAX_CHARS_IN_TERM("max_trm_wdt"),
    MIN_CHARS_IN_TERM("min_trm_wdt"),
    EXCLUDE_TERM("exclude_t"), // term starts with '-'
    STEM_TERM("stem_t"), // term ends with '*'
    RELATIONS_LIKE_TERMS("rel_t"), // extends, implements, mcalls
    DEFS_LIKE_TERMS("def_t"),   // mdef, cdef, idef, file
    FQN_LIKE_TERMS("fqn_t"),
    MDEF_TERMS("mdef"),
    CDEF_TERMS("cdef"),
    IDEF_TERMS("idef"),
    FILE_TERMS("file"),
    MCALL_TERMS("mcall"),
    EXTEND_TERMS("extends"),
    IMPLEMENTS_TERMS("implements"),
    JAVADOC_TAGS("jdoc_t"),
    JAVA_KEYWORDS("keyw_t"),
    ANNOTATIONS("annot");

    
    private static final Map<Integer, TermStat> ordinalEnumMap = new HashMap<Integer, TermStat>();

	static {
	    for(TermStat ts : EnumSet.allOf(TermStat.class))
	         ordinalEnumMap.put(ts.ordinal(), ts);
	}
	
	public static int getSize(){
		return ordinalEnumMap.size()
	}
    
	public static TermStat getByOrdinal(int ordinal){
		if(ordinal<0 || ordinal>ordinalEnumMap.size()-1)
			throw new IllegalArgumentException("Ordinal out of range, provided: " + ordinal)
		
		return ordinalEnumMap.get(new Integer(ordinal))
	}
	
    public String name;
    TermStat(String name){
    	this.name = name;
    }
    
    /**
     * contents in termStatVector indexed as per the ordinals of
     * this enum
     */
    public static getUpdatedTermStatVector(oldV, newV) {
    	
    	assert (newV!=null && newV.size() == ordinalEnumMap.size())
    	if (oldV !=null) assert (oldV.size() == ordinalEnumMap.size())
    	
    	if(oldV == null) 
    		return newV
    	else
    		(0..oldV.size()-1).each() { newV[it] = TermStat.getByOrdinal(it).getUpdatedTermValue(oldV[it], newV[it]) }
    	
    	return newV
    }
    
    private int getUpdatedTermValue(oldV, newV) {
    	switch(this) {
	    	case MAX_TERMS_IN_QRY:
	    	case MAX_CHARS_IN_TERM:
	    		return (newV>oldV)?newV:oldV
	    		
	    	case MIN_TERMS_IN_QRY:
	    	case MIN_CHARS_IN_TERM:
	    		// 0 is the default initialized value and can never be a valid count
	    		return (newV<oldV || oldV == 0)?newV:oldV
	    	
	    	case EXPR_LIKE_QRY:
	    	case JAVA:
	    	case QUERIES_WITH_QUOTES: 
	    	case METHOD_LIKE_QRY: 
	    	case NATURAL_LANG:
	    	case XML_LIKE_QRY:
	    	case EXCLUDE_TERM:
	    	case STEM_TERM:
	    	case RELATIONS_LIKE_TERMS:
	    	case DEFS_LIKE_TERMS:
	    	case FQN_LIKE_TERMS:
	    	case MDEF_TERMS:
	    	case CDEF_TERMS:
	    	case IDEF_TERMS:
	    	case FILE_TERMS:
	    	case MCALL_TERMS:
	    	case EXTEND_TERMS:
	    	case IMPLEMENTS_TERMS:
	    	case JAVADOC_TAGS:
	    	case JAVA_KEYWORDS:
	    	case ANNOTATIONS:
	    		return oldV + newV
    	}
    }
     
     
    public static getTermStatVectorForQuery(String query) {
    	
    	assert (query!=null)
    	
    	int[] termVec = new int[TermStat.getSize()]
    	def _terms = query.trim().split("[\\s]+")
    	def _termsCount = _terms.size()

    	// PER-QUERY
    	def _maxTerms = 0, _minTerms = 0
    	def _quotes = 0, _mLike = 0, _xmlLike = 0, _exprLike = 0
    	def _natLang = 0, _java = 0
		
    	_maxTerms = _minTerms = _termsCount
    	
		// quoted
		if (query.indexOf("\"") != -1)
			_quotes++
		
		// expression like
		if (query.indexOf(";") != -1)
			_exprLike++
		
		// xml-like
		// < .. >
    	if (query.startsWith("\"<") || query.startsWith("<"))
    		_xmlLike = 1
    	
		// method-like 
		def	m_pat = /\w+\(.*\)/
		def mMatcher = (query =~ m_pat)
		if (mMatcher.count > 0) {
			_mLike++
		}
		
		// PER-TERM
		// fqn patterns
    	// .. validJavaName.validJavaName.validJavaName
    	def  _fqn = 0
    	def	fqn_pat = /(\w+\.\w+\.\w+)(\.w+)*/
    	def fqnMatcher = (query =~ fqn_pat)
    	_fqn = fqnMatcher.count
    	
		def _minCharsInTerm = _terms[0].length()
		def _maxCharsInTerm = _terms[0].length()
		def _rel = 0, _def = 0, _jDocs = 0, _jKW = 0, _annot = 0
		def _cdef = 0, _mdef = 0, _idef = 0, _file = 0, _mcall = 0, _extends = 0, _implements = 0
		def _excludeT = 0, _stemT = 0
		
		_terms.each() { term -> 
    		
			def _l = term.length()
    		
			if (_l < _minCharsInTerm) _minCharsInTerm = _l
    		if (_l > _maxCharsInTerm) _maxCharsInTerm = _l
    		
    		if (term.startsWith("-")) _excludeT++
    		if (term.endsWith("*")) _stemT++
    		
    		// replace non-words from the start of the term
    		def _cleanTerm = term.replaceAll("^[\\W]+","")
    		
    		// defs
    		if(_cleanTerm.startsWith("cdef:")) { _cdef++ ; _def++ }
    		if(_cleanTerm.startsWith("mdef:")) { _mdef++ ; _def++ }
    		if(_cleanTerm.startsWith("idef:")) { _idef++ ; _def++ }
    		if(_cleanTerm.startsWith("file:")) { _file++ ; _def++ }
    		
    		// rels
    		if(_cleanTerm.startsWith("mcall:")) { _mcall++ ; _rel++ }
    		
    		if(term.equals("extends")) { _extends++ ; _rel++ }
    		if(term.equals("implements")) { _implements++ ; _rel++ }
    		
    		// javadocs, annotations
    		if (term.startsWith("@")) {
    			if (Util.isJavaDocTag(term)) { _jDocs++ } else _annot++
    		}
    		
    		// java keywords
    		if (Util.isJavaKeyword(term)) {
    			_jKW ++
    		}
    		
    		// .java or java
    		if(term.trim().endsWith(".java") || term.trim().equals("java"))
    			_java = 1
    		
    		// TODO
    		// natural language term found in dictionary
    		// non-dictionary term (possibly, compound or acronym)
    	} // end each term
    	
    	// natural
    	if (_rel == 0 && _def == 0 && _fqn == 0 &&
    		_jDocs == 0 && _annot == 0 && 
    		_xmlLike == 0 && _mLike == 0 && _exprLike == 0 &&
    		_termsCount > 3 ) 
    		_natLang = 1
    	
    
    	termVec[TermStat.MAX_TERMS_IN_QRY.ordinal()] = _maxTerms;
    	termVec[TermStat.MIN_TERMS_IN_QRY.ordinal()] = _minTerms;
    	termVec[TermStat.QUERIES_WITH_QUOTES.ordinal()] = _quotes; 
    	termVec[TermStat.EXPR_LIKE_QRY.ordinal()] = _exprLike;
    	termVec[TermStat.METHOD_LIKE_QRY.ordinal()] = _mLike; 
    	termVec[TermStat.NATURAL_LANG.ordinal()] = _natLang;
    	termVec[TermStat.XML_LIKE_QRY.ordinal()] = _xmlLike;
    	termVec[TermStat.MAX_CHARS_IN_TERM.ordinal()] = _maxCharsInTerm;
    	termVec[TermStat.MIN_CHARS_IN_TERM.ordinal()] = _minCharsInTerm;
    	termVec[TermStat.EXCLUDE_TERM.ordinal()] = _excludeT;
    	termVec[TermStat.STEM_TERM.ordinal()] = _stemT;
    	termVec[TermStat.RELATIONS_LIKE_TERMS.ordinal()] = _rel;
    	termVec[TermStat.DEFS_LIKE_TERMS.ordinal()] = _def;
    	termVec[TermStat.FQN_LIKE_TERMS.ordinal()] = _fqn;
    	termVec[TermStat.MDEF_TERMS.ordinal()] = _mdef;
    	termVec[TermStat.CDEF_TERMS.ordinal()] = _cdef;
    	termVec[TermStat.IDEF_TERMS.ordinal()] = _idef;
    	termVec[TermStat.FILE_TERMS.ordinal()] = _file;
    	termVec[TermStat.MCALL_TERMS.ordinal()] = _mcall;
    	termVec[TermStat.EXTEND_TERMS.ordinal()] = _extends;
    	termVec[TermStat.IMPLEMENTS_TERMS.ordinal()] = _implements;
    	termVec[TermStat.JAVADOC_TAGS.ordinal()] = _jDocs;
    	termVec[TermStat.JAVA_KEYWORDS.ordinal()] = _jKW;
    	termVec[TermStat.ANNOTATIONS.ordinal()] = _annot;
    	termVec[TermStat.JAVA.ordinal()] = _java;
    	
    	return termVec
    }
    
}


class TopicDoc{
	public String docAbstract;
	public String meta ="";
	public String title = "";
	public String key;
}

public class Vocabulary{
  static def termCount = [:]


  static clear(){
    termCount.clear()
  }

  static addTerms(terms){
    for(String term: terms.split("\\s")){
      add(term)
    }
  }

  static add(term){
    if(termCount.containsKey(term)){
      termCount[term] = termCount[term] + 1
    } else {
      termCount[term] = 1
    }
  }

  static writeWordFreq(fileLocPrefix) {
    def opFile = fileLocPrefix + "_vocab.txt"

    new File(opFile).withWriter {out ->
      out.writeLine("word\tcount")
      termCount.each {word, count ->
        out.writeLine(word + "\t" + count)
      }

    }

  }
}

public class Util{
	
	static boolean RemoveStopWord = false; 
	static boolean RemoveOutliers = false;
	static boolean FilterLangUsers = false;
	static int RemoveLessQueryUserThreshold = 0;
	
	static String FirstLang = "java";
	static String SecondLang = "*";
	
	
	def static OutlierDocKeys = ["6315974", "6316056", "12409189"];
	
	def static stopwords = [];
	static {
		def stopWordFile = "/Users/shoeseal/Scratch/LogAnalysis/nlpdata/exp/koders.stopword"
		new FileReader(stopWordFile).readLines().each(){ td ->
			def _sWord = td.trim() 
			if(_sWord.length() > 0) {
				stopwords.add(_sWord);
			}
		}
	}
	
	def static lang = ["*", "javascript",
		"coldfusion",
		"objectivec",
		"smalltalk",
		"delphi",
		"vb.net",
		"php",
		"ruby",
		"perl",
		"ada",
		"asp",
		"prolog",
		"fortran",
		"eiffel",
		"cpp",
		"c",
		"scheme",
		"c#",
		"vb",
		"cobol",
		"lua",
		"actionscript",
		"assembler",
		"tcl",
		"java",
		"erlang",
		"python",
		"jsp",
		"matlab",
		"lisp",
		"mathematica",
		"sql"]
	
	def static javaKeywords = [
		"abstract", "continue", "for", "new", "switch",
		"assert", 	"default", 	"goto", "package", "synchronized",
		"boolean", 	"do", 	"if", 	"private", 	"this",
		"break", 	"double", 	"implements", 	"protected", "throw",
		"byte", 	"else", 	"import", 	"public", 	"throws",
		"case", 	"enum", 	"instanceof", 	"return", 	"transient",
		"catch", 	"extends", 	"int", 	"short", 	"try",
		"char", 	"final", 	"interface", 	"static", 	"void",
		"class", 	"finally", 	"long", 	"strictfp", 	"volatile",
		"const", 	"float", 	"native", 	"super",  	"while"
	    ]
	
	def static javaDocTags = [
		"@author",
		"@code", //"{@code}",
		"@docRoot", //"{@docRoot}",
		"@exception",
		"@inheritDoc", //"{@inheritDoc}",
		"@link", //"{@link}",
		"@linkplain", //"{@linkplain}",
		"@literal", //"{@literal}",
		"@param",
		"@return",
		"@see",
		"@serial",
		"@serialData",
		"@serialField",
		"@since",
		"@throws",
		"@value", //"{@value}",
		"@version"
		]
	
	def static structureQueryTerms = [
	                                  "cdef:",
	                                  "mdef:",
	                                  "mcall:",
	                                  "file:"
	                                  ]
	
	def static licenses = [
		"*",
		"afl",
		"al20",
		"apsl",
		"asl",
		"bsd",
		"cpl",
		"epl10",
		"gpl",
		"gtpl",
		"ibmpl",
		"iosl",
		"lgpl",
		"mitd",
		"mpl10",
		"mpl11",
		"mscl",
		"mspl",
		"msrl",
		"msvsdk",
		"msvssdk",
		"npl10",
		"npl11",
		"osl",
		"psfl",
		"spl",
		"w3c",
		"wxwll",
		"zll",
		"zpl"
		]
	
	public static boolean isStopWord(String w){
		return Util.stopwords.any { it.equals(w)};
	}
	
	public static boolean isOutlier(String key){
		 return Util.OutlierDocKeys.any { it.equals(key)}; 
	}
	
	public static boolean isJavaDocTag(String term) {
		return Util.javaDocTags.any { it.equals(term)};
	}
	
	public static boolean isJavaKeyword(String term) {
		return Util.javaKeywords.any { it.equals(term)};
	}
	
	public static String removeStopWords(String w){
		
		if(!RemoveStopWord) return w;
		
		// println("in: " + w)
		if (w.length()>0){
			StringBuilder sb = new StringBuilder()
			w.split("\\s").each(){ s ->
				if(!isStopWord(s)){
					sb.append(s + " ")
				}
			}
			// println("out: " + sb.toString().trim())
			return sb.toString().trim()
		}else
			return ""
	}
	
	
	// ordering will be lost
	public static String removeDuplicateSentences(String paragraph, String sentenceDelimiterInSource, String sentenceDelimiterInResult){
		
		if (paragraph == null || sentenceDelimiterInSource == null) return null;
		
		String[] sentenceArr = paragraph.split(sentenceDelimiterInSource);
		
		HashSet<String> uniqueSentences = new HashSet<String>()
		
		for(String sentence : sentenceArr){
			if(!uniqueSentences.contains(sentence)) uniqueSentences.add(sentence)
		}
		
		StringBuffer result = new StringBuffer()
		for(String sentence: uniqueSentences){
			result.append(sentence)
			result.append(sentenceDelimiterInResult)
		}
		
		return result.toString();
	}
	

	public static String arrayToString(String[] a, String separator) {
	    StringBuffer result = new StringBuffer();
	    if (a.length > 0) {
	        result.append(a[0]);
	        for (int i=1; i<a.length; i++) {
	            result.append(separator);
	            result.append(a[i]);
	        }
	    }
	    return result.toString();
	}
	
	// invoked whenever a new user is found in the list
	public static void checkAndAddDocumentToCollection(ArrayList<TopicDoc> c, TopicDoc td){
		int numOfSentences = td.docAbstract.split("\\.").length
		// need to add 1 to threshold as the array size after split is 1 if no delimiter is found
		if (numOfSentences > Util.RemoveLessQueryUserThreshold + 1) 
			c.add(td)
			
			
	}
	
}



