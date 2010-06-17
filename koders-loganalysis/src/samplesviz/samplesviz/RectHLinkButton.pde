class RectHLinkButton extends Button
{
  
  public String hLink;
  public String label = "View downloaded file ...";
  int w;
  int h;
  
  public PFont font = loadFont("Courier-14.vlw") ;
  public int fontSize = 14;
  public color fontFill = 125;
  
  // current translations under effect
  int tx;
  int ty;
  
  RectHLinkButton(int ix, int iy, int iw, int ih, color icolor, color ihighlight, String hLink) 
  {
    x = ix;
    y = iy;
    w = iw;
    h = ih;
    bordercolor = icolor;
    basecolor = icolor;
    highlightcolor = ihighlight;
    currentcolor = basecolor;
    this.hLink = hLink;
  }

  boolean overRect(int x, int y, int width, int height) 
  {
    if (mouseX >= x + tx && mouseX <= x+tx+width && 
      mouseY >= y + ty && mouseY <= y+ty+height) {
        // print("tx " + tx + ", ty " + ty + ", mx" + mouseX + ", my " + mouseY + ", x " + x + " ,y " + y + ", w"  + width +  ", h" + height + "\n");
        return true;
    } 
    else {
      return false;
    }
  }
  
  boolean over() 
  {
    if( overRect(x, y, w, h) ) {
      over = true;
      return true;
    } 
    else {
      over = false;
      return false;
    }
  }

  void display(int _x, int _y) 
  {
    this.tx = _x;
    this.ty = _y;
    
    stroke(bordercolor);
    fill(currentcolor);
    rect(x, y, w, h);
    textFont(font, fontSize);
    fill(fontFill);
    //println("y: " + (h-fontSize)/2);
    text( this.label, x+9, y + fontSize + 2  );
  }
}

