class Button
{
  int x, y;
  int size;
  color basecolor, highlightcolor, bordercolor;
  color currentcolor;
  boolean over = false;
  boolean pressed = false;   

  void update() 
  {
    if(over()) {
      currentcolor = highlightcolor;
    } 
    else {
      currentcolor = basecolor;
    }
  }

  boolean pressed() 
  {
    if(over) {
      pressed = true;
      return true;
    } 
    else {
      pressed = false;
      return false;
    }    
  }

  boolean over() 
  { 
    return true; 
  }

  

  boolean overCircle(int x, int y, int diameter) 
  {
    float disX = x - mouseX;
    float disY = y - mouseY;
    if(sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
      return true;
    } 
    else {
      return false;
    }
  }

}


