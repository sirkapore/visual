import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import ddf.minim.*; 
import ddf.minim.signals.*; 
import ddf.minim.analysis.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class spiralling_star extends PApplet {





  
Minim minim;
AudioInput input;
AudioOutput out;
FFT fft;

float h, w;

float theta = 0;

float x = 0;
float y = 0;
float x2 = 0;
float y2 = 0;
float numofCircles = 0.5f;
PVector spiralCenter = new PVector(0, 0);

float startRadius = 0;
float endRadius = 1.5f;
float currRadius = startRadius;

float totalRadian = numofCircles * PI * 2;
float startRadian = -PI;
float endRadian = startRadian + totalRadian;
float currentRadian = startRadian;
int numSections = 60; // bigger the number the smoother the spiral

// This depends on the current radius
float deltaAngle = totalRadian / numSections;

int sz = 500;

float easing =  0.4f;
float dim = 0;
public void setup() {
  
  size (sz, sz, P2D);
  frameRate(60);
  //ellipseMode(CENTER);
  colorMode(HSB, 1);
  //stroke(255);
  strokeWeight(1.9f);
  noFill();
  smooth();
  h = height /2; w = width /2;
  
  minim = new Minim (this);
  input = minim.getLineIn (Minim.STEREO, 1024*4);
  fft = new FFT (input.bufferSize (), 
                 input.sampleRate ());
  //fft.logAverages(11, 16);
  
}

public void drawRect(float scal){
  pushMatrix();
  translate(w,h);
  fill(0xff18312F);
  noStroke();
  rect(-dim*100*scal, -dim*100*scal, 200*dim*scal, 200*dim*scal);
  noFill();
  popMatrix();
}

public void draw () {
  dim = ease(dim, input.mix.level () * 10, easing);
  background(0.01f, 0.01f, 0.1f);
   
  drawRect(2);
  paper(35); 
  //startRadian -= deltaAngle;
  //endRadian += deltaAngle;
  noFill();

  theta += 0.002f;
  endRadius = map(sin(TWO_PI*theta*2), -1, 1, 1.4f, 1.8f);



  // auf Sound reagieren
  endRadius += dim*10;

//println(dim + " " + input.mix.level ()); 
/* //little animation
  if (endRadius<5)
     endRadius+=0.1;
  else
    endRadius=1;*/
    
float numLines = 10;

for (int i = 0; i<numLines; i++){
  
  //stroke(1);
  stroke(0.5f, 0.3f, 0.7f);
  startRadian = -PI;
  endRadian = startRadian + totalRadian;
  startRadian = startRadian + (float)(TWO_PI/numLines)*(float)i;
  //print (i + " " + startRadian/PI + "\n");
  endRadian = endRadian + (float)(TWO_PI/numLines)*(float)i;
  currentRadian = startRadian;
  drawSpiral(0.6f,0.9f/dim);
  
    //stroke(1);
  stroke(1, 0.3f, 0.7f);
  startRadian = -PI+0.1f;
  endRadian = startRadian + totalRadian+10.1f;
  startRadian = startRadian + (float)(TWO_PI/numLines)*(float)i;
  //print (i + " " + startRadian/PI + "\n");
  endRadian = endRadian + (float)(TWO_PI/numLines)*(float)i;
  currentRadian = startRadian;
  drawSpiral(3,0.5f);
  
  //  stroke(1);
  stroke(0.5f, 0.3f, 1);
  startRadian = -PI+0.3f;
  endRadian = startRadian + totalRadian+0.1f;
  startRadian = startRadian + (float)(TWO_PI/numLines)*(float)i;
  //print (i + " " + startRadian/PI + "\n");
  endRadian = endRadian + (float)(TWO_PI/numLines)*(float)i;
  currentRadian = startRadian;
  drawSpiral(3,0.2f);
}

if (frameCount % 2 == 0) 
  drawLines();

}

public void drawLines() {

float lineLen = 50;
stroke(0.7f,1/dim); 
for(int i = 0; i<20; i++) {
  lineLen = random(100,200);
  float rndR = random(195,210);
  float rndT = random(0, TWO_PI);
  pushMatrix();
  translate(w,h);
   x = cos(rndT) * rndR;
   y = sin(rndT) * rndR;
   x2 = cos(rndT) * (rndR+lineLen);
   y2 = sin(rndT) * (rndR+lineLen);
   line(x,y,x2,y2);
  popMatrix();
}

}

public void drawSpiral(float radiusAmp, float expAmp){
  pushMatrix();
  translate(w,h);

  x = 0;
  y = 0;
  beginShape();
  vertex(x,y);
  while (currentRadian < endRadian)
  {
    currentRadian += deltaAngle;
    currRadius = map(currentRadian, startRadian, endRadian, startRadius, endRadius);
    // with exponential growth of the radius
    currRadius = (exp(currRadius*expAmp)-1) * radiusAmp;
    x = cos(currentRadian) * currRadius;
    y = sin(currentRadian) * currRadius;
    if (pow(x,2) + pow(y,2) < pow(200,2))
      vertex(x, y);
    //curveVertex(x + spiralCenter.x, y + spiralCenter.y);
  }
  endShape();
  popMatrix();
}

public float[] get_band(){
      
  float g = 0;    // Gr\u00c3\u00bcnwert der F\u00c3\u00bcllfarbe
  float h = 0;    // H\u00c3\u00b6he von Rechteck und Linie
  float one = 0;
  int maxfreq = 3000;
  int maxamp = 80;
  int peak = 0;
  float lastone=0;
  
  float[] band = new float[maxfreq];
  //println(fft.specSize());
  
  float specStep; // Breite einer horiz. Linie
  float specScale = (float) width / (fft.specSize () - 1);
  
  // Erzeugen der 'Frequenz-Gruppen' (16 Bereich)
  // m\u00c3\u00b6gliche Schritte: 2-4-8-16-32-64-128
  //float[] group = getGroup (32);
  fft.forward (input.mix);  

  // Zeichnen des detailierten Frequenzspektrums
  noStroke ();
    for (int i = 0; i < maxfreq; i++) {
    //g = map (fft.getBand (i), 0, maxSpec, 50, 255);
    //h = map (fft.getBand (i), 0, maxSpec, 2, height);
    //one = map (fft.getBand (i), 0, maxSpec, 0, 1);
    float freq = fft.getFreq(i);

    //freq = log(freq)/log(1.2);
    
    g = map(freq,0,maxamp,100,255);
    h = map(freq,0,maxamp,2,height);
    one = map(freq,0,maxamp,0,1);
    
    //fill (0, g, 0);
    //rect (i * specScale, height - h, specScale, h);
        
    if (one>0){
          band[i]=one;
          if (one>0.05f && one > lastone){
            peak = i;
            lastone = one;
          }
    }
 }
 
     // average colors from the band[] 
    
      /// ******** TEXT DRAWING *******
/*  fill (255);  
  font = loadFont("letter20.vlw");
  textFont(font);
*/
  
  // new line signal    
  return band;
 }

public void paper(int in_val) {
  noStroke();
  float inHsb = map(in_val, 0,255,0,1);
  
  //small dust
  for (int i = 0; i<width-1; i+=2) {
    for (int j = 0; j<height-1; j+=2) {
      //fill(random(235-40, 235+30), in_val-2);
      //HSB:
      fill(random(0.76f, 1), inHsb*dim);
      rect(i, j, 2, 2);
    }
  }
 
 // large dust
  for (int i = 0; i<30; i++) {
    //fill(random(30, 170), random(in_val*2.5, in_val*5));
    fill(random(0.12f, 0.66f), random(inHsb*2.5f, inHsb*5));
    rect(random(0, width-2), random(0, height-2), random(1, 3), random(1, 3));
  }
}
    
    
public float ease(float value, float target, float easingVal) {
        float d = target - value;
        if (abs(d)>0.001f) value+= d*easingVal;
        return value;
}

class CLine {
  
  float vx, vy, thick, wobble=0;
  float theta = 0;
  
  CLine(float _vx ,float _vy,float _thick) {
    vx = _vx;
    vy = _vy;
    thick = _thick;
  }
  
  public void draw() {
    theta+=0.01f;
    strokeWeight(thick);
    noFill();
    
    beginShape();
      curveVertex(20,vy);
      curveVertex(20,vy);
      
      //curveVertex(w, vy-map(sin(theta*TWO_PI),-1,1,0,40));
      curveVertex(w-30, vy+map(sin(theta*TWO_PI+PI*theta),-1,1,0,30));  
      curveVertex(w+10, vy+map(sin(theta*TWO_PI+PI*theta),-1,1,0,40));         
      curveVertex(w+30, vy+map(sin(theta*TWO_PI+PI*theta),-1,1,0,30));  
      curveVertex(width-20, vy);      
      
      curveVertex(width-20, vy);      

    endShape();
  }
  
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "spiralling_star" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
