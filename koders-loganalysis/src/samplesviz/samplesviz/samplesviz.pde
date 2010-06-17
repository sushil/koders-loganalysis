    
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

final String message = "Press 'T' to get to this list of Topics. Click on any of the topics " + 
                       "to change the selected topic to sample from. Press 'N' to get next " +
                       "random sample from the current selected topic.";

String urlDomain = "http://localhost:3000";
String urlTopicsList = "/list_topics";
String urlNextSamplePrefix = "/random_sample?topic=";
String selectedTopic = "JAVA";

ArrayList lines = new ArrayList();
ArrayList links = new ArrayList();
ArrayList topics = new ArrayList();
ArrayList topicsInLines = new ArrayList();
HashMap topicsColors = new HashMap();

PFont lFont;
VScrollBar vsb;
HScrollBar hsb;

PFont fBig;
PFont fMed;
PFont fSmall;

final int default_baseY = 24;
final int default_baseX = 0;

final int MODE_TOPIC_LIST = 0;
final int MODE_SAMPLE = 1;

int app_mode = MODE_TOPIC_LIST;

int baseY = default_baseY;
int baseX = default_baseX;
int yIncrement = 26;
int linesHeight = 0;
int linesWidth = 0;

boolean mouseReleased = false;

int sampleCount = 0;

void setup() {
  
  size(1200, 720);
  smooth();
  background(220);
  lFont = loadFont("Courier-24.vlw");
  
  fBig = loadFont("AmericanTypewriter-CondensedLight-26.vlw");
  fMed = loadFont("AmericanTypewriter-CondensedLight-14.vlw");
  fSmall = loadFont("AmericanTypewriter-CondensedLight-10.vlw");
  
  vsb = new VScrollBar(width-8, 10, 10, height-20, 3*8+1);
  hsb = new HScrollBar(height-8, 10, 10, width-20, 1);
  loadTopics();
  
}

void update(int x, int y)
{
  
  Iterator itr = links.iterator();
  
  if(app_mode==MODE_SAMPLE){
    while(itr.hasNext()){
        ((RectHLinkButton) itr.next()).update();
    }
  }
  
  if(app_mode==MODE_TOPIC_LIST){
    itr = topics.iterator();
      while(itr.hasNext()){
        ((RectHLinkButton) itr.next()).update();
    }
  }
  
  if(mouseReleased) {
    
   if(app_mode==MODE_SAMPLE){ 
      itr = links.iterator();
      while(itr.hasNext()){
        RectHLinkButton _but = ((RectHLinkButton) itr.next());
        if(_but.over()) {
          //currentcolor = _but.basecolor;
          link(_but.hLink, "_new");
          
        }
      }
   }
      
   if(app_mode==MODE_TOPIC_LIST){
     itr = topics.iterator();
     while(itr.hasNext()){
       RectHLinkButton _but = ((RectHLinkButton) itr.next());
       if(_but.over()) {
         //currentcolor = _but.basecolor;
         selectedTopic = _but.label;     
       }  
     }
   }
    
    mouseReleased = false;
    // //println("mr - false");
    
  }
}

void mouseClicked(){
  //println("md");
  setMouseXY();

}

void mouseReleased(){
  mouseReleased = true;
  // //println(mouseX + " " + mouseY);
}

void draw() {
 //println(baseY);
  
  background(200);

 update(mouseX, mouseY);
 //baseX = default_baseX;
 //baseY = default_baseY;

 int relX = 0, relY = 0; 

 if(app_mode==MODE_SAMPLE){
    
   if(linesHeight + default_baseY > height ){
     vsb.update();
     // vsb.display();  
     // println((vsb.getPos()-15) * (linesHeight/vsb.sheight) + " " + vsb.getPos());
     relY = (int) ((vsb.getPos()-15) * (linesHeight/vsb.sheight) /*height == vsb.height*/);
    }
 
   if(linesWidth + default_baseY > width ){
     hsb.update();
     hsb.display();
     relX = (int) ((hsb.getPos()-15) * linesWidth/hsb.swidth /*height == vsb.height*/)/2 ;
   }
 
    pushMatrix();
    // println(default_baseX - baseX);
    translate( default_baseX - relX, default_baseY - relY);
    
    Iterator itr = lines.iterator();  
    while(itr.hasNext()){
      ((Label) itr.next()).display();
    }
    
    Iterator mti = topicsInLines.iterator();
    while(mti.hasNext()){
      ((Label) mti.next()).display();
    }
    
    Iterator li = links.iterator();
    while(li.hasNext()){
      ((RectHLinkButton) li.next()).display(default_baseX - relX, default_baseY - relY);    
    }
    
    popMatrix();
    
    if(linesHeight + default_baseY > height ){
     
     vsb.display();  
     
    }
 
   if(linesWidth + default_baseY > width ){
     
     hsb.display();
     
   }
   
   displaySampleCount();
   
 }
  
 if(app_mode==MODE_TOPIC_LIST){
   background(110); 
   
   Iterator ti = topics.iterator();
    while(ti.hasNext()){
      ((RectHLinkButton) ti.next()).display(0, 0);    
    }
    
    
    text(message, 15, height - 140, 400, 70);
 }

}

void nextSample(){
  
  sampleCount = sampleCount + 1;
  
  lines.clear();
  links.clear();
  topicsInLines.clear();
  
  try{
    
    int _incr = 0;
    linesWidth= 0;
      
    InputStream ins = new URL(getUrlNextSample()).openStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(ins));
    String _line;
    while((_line=br.readLine())!=null){
    
      color buttoncolor = color(51);
      color highlight = color(65);
      
      if(_line.trim().startsWith("http://www.koders.com/kv.aspx?fid=")){
        links.add(new RectHLinkButton(54, baseY + _incr - 10, 220, 21, buttoncolor, highlight, _line.trim()));
      } else {
        
        String[] _lineSplits = _line.split("\t");
        
        // query
        Label _label = new Label(10, baseY + _incr, 24, _lineSplits[0], lFont);
        if(_lineSplits[0].startsWith("-> ")) _label.fillColor = color(125, 0, 25);
        lines.add(_label);
        
        
        // matched topics
        if(_lineSplits.length>1){
          String[] _weights = _lineSplits[1].split(",");
          if(_weights.length%2==0){
            
            textFont(lFont,24);
            int _qWidth =  (int) textWidth(_lineSplits[0]);
            float _weightsXOffsetSoFar = 0;
            
            // add "|" before printing topics
            Label _b4Topics = new Label(20 + _qWidth + (int)_weightsXOffsetSoFar, baseY + _incr, 26, "|", fBig);
            _b4Topics.fillColor = color(255,255,255,160) ;
            topicsInLines.add( _b4Topics ); 
            _weightsXOffsetSoFar = _weightsXOffsetSoFar + textWidth("|") + 4;
            
            for(int _wi = 0; _wi < _weights.length-1; _wi = _wi + 2){
              
              String _matchedTopic = _weights[_wi];
              float _weight = new Float(_weights[_wi+1]).floatValue();
              int _wCharSize = 6;
              //int _previousTopicTextWidth = (_wi>=2)?_weights[_wi-2].length():0;
              
              int _fSize = (int)( 24 + (4*Math.log(_weight)) );
              // print (_weight + "-" + _fSize + " ");
	      _fSize = (_fSize < 8)?8:_fSize;
	
              Label _tLabel = new Label(20 + _qWidth + (int)_weightsXOffsetSoFar, baseY + _incr, _fSize, _matchedTopic, fBig);
              _tLabel.fillColor = ((Integer) topicsColors.get(_matchedTopic)).intValue();
              topicsInLines.add(_tLabel); 
              
              textFont(fBig,_fSize);
              _weightsXOffsetSoFar = _weightsXOffsetSoFar + textWidth(_matchedTopic) + 4; //.length() * _wCharSize);
              
            } 
            
            
            int _thisLineWidth = 20 + _qWidth + (int)_weightsXOffsetSoFar;
            if(_thisLineWidth > linesWidth) linesWidth = _thisLineWidth;
          
          }
        }
        
        
      }
      
      _incr = _incr + getYIncrement();
      linesHeight = _incr;
    }
    
    baseY = default_baseY;
    // vsb.scrollToTop();
    vsb = new VScrollBar(width-8, 15, 10, height-30, 1);//3*8+1);
    hsb = new HScrollBar(15, height-8, width-30, 10, 1);
    
  } catch (Exception e) {
    e.printStackTrace();
  }
}

void displaySampleCount(){
  textFont(fMed, 14);
  fill(0);
  rect(width - 82, height - 38, 40, 18);
  fill(255);
  text(sampleCount,width - 80, height - 24); 

}

void loadTopics(){

   try{
    
    int _incr = 0;
    String[] _topics = new String[50];
      
    InputStream ins = new URL(getUrlTopicsList()).openStream();
    BufferedReader br = new BufferedReader(new InputStreamReader(ins));
    String _line;
    int _i = 0;
    while((_line=br.readLine())!=null){
      _topics[_i] = _line;
      _i++;
    }
    
    _i = 0;
    for(int i=0;i<10;i++){
      for(int j=0;j<5;j++){
      
      color buttoncolor = color(180);
      color highlight = color(205, 92, 92);
      
      String _topic = _topics[_i];
      RectHLinkButton _rBut = new RectHLinkButton(15 + (j*150), 26 + (i * 26), 7 * (_topic.length() + 4), 20, buttoncolor, highlight, "");
      _rBut.label = _topic;
      _rBut.fontFill = 0;
      topics.add(_rBut);
      
      _i++;
      }
    }
    
    initTopicColors(_topics);
    
  } catch (Exception e) {
    e.printStackTrace();
  }
  
}

public void initTopicColors(String[] topics){
  Set _colors = new HashSet();
  
  
  for(int i = 0; i < topics.length; i++){

    color _c = getRandomColor();   
    while(_colors.contains(new Integer(_c))){
      _c = getRandomColor();
    }        
    
    topicsColors.put(topics[i], new Integer(_c));
    _colors.add(new Integer(_c));
  }
  
  
}

public color getRandomColor(){
  int R = (int) random(40, 140);
  int G = (int) random(40, 160);
  int B = (int) random(60, 200);
  color c = color(R,G,G);
  return c;
}

int getYIncrement(){
  return yIncrement;
}



/**
 * Handle key presses.
 */
void keyPressed() {
  
  // println(keyCode);
  
  switch(keyCode) {
    
    case 78:
      app_mode=MODE_SAMPLE;
      nextSample();
      break;
      
    case 84:
      app_mode=MODE_TOPIC_LIST;
      topicChanged();
      break;
  
  }
}

public void topicChanged(){
  sampleCount = 0;
}

public String getUrlNextSample(){
  return urlDomain + urlNextSamplePrefix + selectedTopic;
}

String getUrlTopicsList(){
  return urlDomain + urlTopicsList;
}

void setMouseXY() 
{
  //if(mouseX>=0 && mouseX<width && mouseY>=0 && mouseY<height) 
  //  return;
  
  Point mouse, winloc;
  mouse = MouseInfo.getPointerInfo().getLocation();
  winloc = frame.getLocation();
  if(!frame.isUndecorated()){
    winloc.x += 3;
    winloc.y += 29;
  }
  mouseX = mouse.x-winloc.x;
  mouseY = mouse.y-winloc.y;
  
 // //println ("setm " + mouseX + " " + mouseY);
}

