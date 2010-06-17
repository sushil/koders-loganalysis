class Label {
public int x;
public int y;
public int lsize;
public String ltext;
public PFont font;

public color fillColor = 0;

public Label(int x, int y, int lsize, String ltext, PFont font){
  this.x = x;
  this.y = y;
  this.lsize = lsize;
  this.ltext = ltext;
  this.font = font;
}

public void display(){
  fill(fillColor);
  textFont(font, lsize);
  text(ltext,x,y); 
}

}
