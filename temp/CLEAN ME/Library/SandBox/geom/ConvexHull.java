/*
 * %W% %E% Jeff C. So
 *
 *
 */


import java.util.*;
import java.awt.*;
import java.applet.Applet;
import java.applet.AudioClip;

/**
 * An applet that demonstrates the graph algorithm, by letting the user
 * pick the points in the screen and choose either the Quick Hull algor
 * or Brute Force algorithm, the program simulated the execution event,
 * showing which line or which point is being compared.
 *
 * @author Jeff C. So
 * @version %I%, %G%
 */

class pointExt extends Point {

   public pointExt(int x, int y) {
      super(x, y);
   }

   /** 
    * Draw a point.
    */
   public void draw(Graphics g, int size) {
      g.fillOval(x - 4, y - 4, size, size);
      g.setColor(Color.white);
      g.fillOval(x - 2, y - 2, 2, 2);
   }

   /** 
    * Draw the point being compared in different color and size.
    */
   public void blink(Graphics g) {
      g.setColor(Color.red);
      g.fillOval(x - 5, y - 5, 10, 10);
      g.setColor(Color.white);
      g.fillOval(x - 3, y - 3, 2, 2);
   }
}

class Line {
   pointExt point1;
   pointExt point2;
   float    slope;
   boolean  slopeUndefine;

   /**
    * Line constructor.
    */
   public Line(pointExt p1, pointExt p2) {
      point1 = p1;
      point2 = p2;
      if (p1.x == p2.x)
         slopeUndefine = true;
      else {    
         if (p2.y == p1.y)
            slope = (float)0;
         else
            slope = (float) (p2.y - p1.y) / (p2.x - p1.x);
         slopeUndefine = false;
      }
   }

   /**
    * Given a Check point and determine if this check point is lying on the
    * left side or right side of the first point of the line.
    */
   public boolean onLeft(pointExt chkpt) {
      if (this.slopeUndefine) {
         if (chkpt.x < point1.x) return true;
         else {
            if (chkpt.x == point1.x) {
               if (((chkpt.y > point1.y) && (chkpt.y < point2.y)) ||
                   ((chkpt.y > point2.y) && (chkpt.y < point1.y)))
                  return true;
               else
                  return false;
            }
            else return false;
         }
      }
      else {            
         /* multiply the result to avoid the rounding error */
         int x3 = (int) (((chkpt.x + slope * (slope * point1.x 
                          - point1.y + chkpt.y)) /
                         (1 + slope * slope)) * 10000);
         int y3 = (int) ((slope * (x3 / 10000 - point1.x) + point1.y) * 10000);

         if (slope == (float)0) {
            if ((chkpt.y*10000) > y3) return true; else return false; }
         else { if (slope > (float)0) {
                   if (x3 > (chkpt.x * 10000)) return true; else return false; }
                else {
                   if ((chkpt.x * 10000) > x3) return true; else return false; }
              }
      }
   }

   /**
    * Draw a line.
    */
   public void draw(Graphics g) {
      g.drawLine(point1.x, point1.y, point2.x, point2.y);
   }
}

/**
 * Main Screen Panel for showing the result.
 */
class SrcPanel extends Panel implements Runnable {
   ConvexHull Hull;
   Thread     kicker;

   /**
    * Variable indicates we are calculating the hull
    */
   boolean runMode = false;

   /** 
    * Variable indicates which algorithm we are using
    */
   public static final int MENU  = 0;
   public static final int Brute = 1;
   public static final int QUICK = 2;
   int    algor = Brute;
   int    preAlgor;

   /**
    * Variable indicates the demonstration speed
    */
   public static final int ZERO = 0;
   public static final int FAST = 20;
   public static final int SLOW = 100;
   int    speed = SLOW;

   /**
    * Variable indicates the sound is on or off
    */
   boolean soundOn = true;

   /**
    * Stores all the points
    */
   Vector points = new Vector();

   /**
    * Stores all the lines in the Hull
    */
   Vector hull   = new Vector();

   /**
    * Stores all the lines being checking 
    */
   Vector chkLns = new Vector();
   Vector tempLns = new Vector();
	
   /**
    * The point we are comparing with the chkLn
    */
   pointExt currPt     = new pointExt(0,0);
   int cx, cy, cz;

   public SrcPanel(ConvexHull Hull) {
      this.Hull = Hull;
   }

   /**
    * Detect the mouse down action and add the point
    */
   public synchronized boolean mouseDown(Event evt, int x, int y) {
      if (!runMode) {
         hull.removeAllElements();
      
         points.addElement(new pointExt(x, y));
         if (soundOn)
	    Hull.clank.play();
         repaint();
      } else {
	 stop();
	 hull.removeAllElements();
         points.addElement(new pointExt(x,y));
         repaint();
         start();
      }

      return true;
   }

   Image offscreen;
   Dimension offscreensize;
   Graphics offgraphics;

   public void paint(Graphics g) {
      Dimension d = size();
      g.setColor(Color.black);
      g.fillRect(0, 0, d.width, d.height);
   }

   /**
    * Display all the points and line in the hull.
    * If we are in execution mode, also display all the lines and points we are
    * checking
    */
   public synchronized void update(Graphics g) {
      Dimension d = size();
      if ((offscreen == null) || (d.width != offscreensize.width) ||
          (d.height != offscreensize.height)) {
         offscreen = createImage(d.width, d.height);
         offscreensize = d;
         offgraphics = offscreen.getGraphics();
      }

      offgraphics.setColor(Color.black);
      offgraphics.fillRect(0, 0, d.width-1, d.height-1);
 
      int np = points.size();
      int nl = hull.size();

      for (int i = 0; i < np; i++) {
         Color ptcolor = (algor == MENU)? Color.lightGray : Color.blue;
         offgraphics.setColor(ptcolor);
         ((pointExt) points.elementAt(i)).draw(offgraphics, 8);
      };

      for (int j = 0; j < nl; j++) {
         Color lncolor = (algor == MENU)? Color.lightGray : Color.blue;
         offgraphics.setColor(lncolor);
         ((Line) hull.elementAt(j)).draw(offgraphics);
      }

      if (runMode) {
         currPt.blink(offgraphics);
         offgraphics.setColor(Color.red);
	 for (int k = 0; k < chkLns.size(); k++) {
	    ((Line)chkLns.elementAt(k)).draw(offgraphics);
         }  

         offgraphics.setColor(Color.gray);
	 if (soundOn)
	    Hull.ding.play();
      }

      /* display a menu page */

      if (algor == MENU) {
	 if (soundOn)
	    Hull.menuMusic.play();
         offgraphics.setColor(Color.red);
         
         Font font0 = new Font("TimesRoman", Font.ITALIC, 24);
         offgraphics.setFont(font0);
         offgraphics.drawString("Convex Hull", 50, 50);
         Font font1 = new Font("TimesRoman", Font.ITALIC, 12);
         offgraphics.setFont(font1);
         offgraphics.drawString("Graph Algorithm Demonstration", 50, 80);

         offgraphics.setFont(getFont());
         offgraphics.setColor(Color.blue);
         offgraphics.fillOval(44, 90, 8, 8);
         offgraphics.setColor(Color.white);
         offgraphics.fillOval(46, 92, 2, 2);
         offgraphics.setColor(Color.red);
	 offgraphics.drawString("You pick those points", 60, 100);
         
         offgraphics.setColor(Color.red);
         offgraphics.fillOval(45, 104, 10, 10);
         offgraphics.setColor(Color.white);
         offgraphics.fillOval(46, 106, 2, 2);
         offgraphics.setColor(Color.red);
         offgraphics.drawString("The program is checking this point", 60, 115);
 
	 offgraphics.setColor(Color.blue);
         offgraphics.drawLine(30, 125, 52, 125);
         offgraphics.setColor(Color.red);
         offgraphics.drawString("Lines in Convex Hull", 60, 130);

         offgraphics.setColor(Color.red);
         offgraphics.drawLine(30, 145, 52, 145);
         offgraphics.drawString("The program is checking this line", 60, 150);

         offgraphics.setColor(Color.red);
         offgraphics.drawString("Programmed by : Jeff So", 50, 220);  

         setMethod(preAlgor);
      }

      g.drawImage(offscreen, 0, 0, null);
   }

   /**
    * Clear all the points and lines.
    */
   public void clearPoint() {
      chkLns.removeAllElements();
      hull.removeAllElements();
      points.removeAllElements();
      repaint();
   }

   /**
    * Set up which algorithm to use.  
    */
   public void setMethod(int method) {
      switch (method) {
         case 0:
            algor = MENU;
            break;
         case 1:
            algor = Brute;
            break;
         case 2:
            algor = QUICK;
            break;
         default:
            algor = Brute;
            break;
      }
   }

   /**
    * Run method
    */
   public void run() {
      repaint();
      while (true) {
         if (runMode) {
            switch (algor) {
               case Brute: hull.removeAllElements();
                           BruteForce();
                           runMode = false;
			   repaint();
                           break;
               case QUICK: hull.removeAllElements();
                           quickHull();
                           runMode = false;
			   repaint();
                           break;
               case MENU:  runMode = false;
                           repaint();
                           break;
               default:    System.out.println("Error in call algor\n");
            }
         
         }

         try { Thread.sleep(100); } catch (InterruptedException e) { break; }
      }
   }

   public void start() {
      kicker = new Thread(this);
      kicker.setPriority(Thread.MAX_PRIORITY);
      kicker.start();
   }

   public void stop() {
      kicker.stop();
   }

   /**
    * Brute Force Algorithm implementation
    */
   public void BruteForce() {
      boolean leftMost, rightMost;
      for (cx = 0; cx < points.size(); cx++) {
         for (int cy = (cx+1); cy < points.size(); cy++) {
            leftMost  = true;
            rightMost = true;
            Line temp = new Line((pointExt) points.elementAt(cx),
                                 (pointExt) points.elementAt(cy));

            for (int cz = 0; cz < points.size(); cz++) {
               currPt = (pointExt) points.elementAt(cz);
	       chkLns.removeAllElements();
    	       chkLns.addElement(new Line((pointExt) points.elementAt(cx),
	   	         		  (pointExt) points.elementAt(cy)));

               if ((cz != cx) && (cz != cy)) {
                  if (temp.onLeft((pointExt) points.elementAt(cz)))
                     leftMost = false;
                  else
                     rightMost = false;


                  repaint();
		  try { Thread.sleep(speed); } catch (InterruptedException e) {}
               }
            }

            if (leftMost || rightMost) {
	       if (soundOn) 
                  Hull.getone.play();
               hull.addElement(new Line((pointExt) points.elementAt(cx),
                                        (pointExt) points.elementAt(cy)));
            }
         }
      }
      repaint();
      if (soundOn)
	 Hull.doneMusic.play();
   }

   int indexChkLn = 0;

   /** 
    * Quick Hull Algorithm implementation.
    * Calculate the hull first and display the execution with the information from
    * chklns and tempHull.
    */

   Vector tempHull = new Vector();

   public void quickHull() {
      Vector P1 = new Vector();
      Vector P2 = new Vector();
      pointExt l = (pointExt)points.elementAt(0);
      pointExt r = (pointExt)points.elementAt(0);
      int minX = l.x;
      int maxX = l.x;
      int minAt = 0;
      int maxAt = 0;	

      chkLns.removeAllElements();
      tempLns.removeAllElements();
      tempHull.removeAllElements();
      
      /* find the max and min x-coord point */

      for (int i = 1; i < points.size(); i++) {
         currPt = (pointExt) points.elementAt(i);	
         if (((pointExt)points.elementAt(i)).x > maxX) {
            r = (pointExt)points.elementAt(i);
            maxX = ((pointExt)points.elementAt(i)).x;
	    maxAt = i;
         };

         if (((pointExt)points.elementAt(i)).x < minX) {
            l = (pointExt)points.elementAt(i);
            minX = ((pointExt)points.elementAt(i)).x;
	    minAt = i;
         };
	 repaint();
	 try { Thread.sleep(speed); } catch (InterruptedException e) {}

      }

      Line lr = new Line((pointExt) l, (pointExt) r);
      tempLns.addElement(new Line((pointExt) points.elementAt(maxAt),
	   	                 (pointExt) points.elementAt(minAt)));
      chkLns.addElement(new Line((pointExt) points.elementAt(maxAt),
	   	                 (pointExt) points.elementAt(minAt)));
      repaint();
      try { Thread.sleep(speed); } catch (InterruptedException e) {};

      /* find out each point is over or under the line formed by the two points */
      /* with min and max x-coord, and put them in 2 group according to whether */
      /* they are above or under                                                */
      for (int i = 0; i < points.size(); i++) {
	 if ((i != maxAt) && (i != minAt)) {
            currPt = (pointExt) points.elementAt(i);

            if (lr.onLeft((pointExt)points.elementAt(i))) {
               P1.addElement(new pointExt(((pointExt)points.elementAt(i)).x,
                                          ((pointExt)points.elementAt(i)).y));
            } else {
               P2.addElement(new pointExt(((pointExt)points.elementAt(i)).x,
                                       ((pointExt)points.elementAt(i)).y));
            }
            repaint();
            try { Thread.sleep(speed); } catch (InterruptedException e) {}
         }
	
      };
      	
      /* put the max and min x-cord points in each group */
      P1.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
      P1.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));

      P2.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
      P2.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));

      /* calculate the upper hull */
      quick(P1, l, r, 0);
      
      /* display the how the upper hull was calculated */
      for (int i=0; i<tempLns.size(); i++) {
        chkLns.addElement(new Line((pointExt) ((Line)tempLns.elementAt(i)).point1, 
     	                           (pointExt) ((Line)tempLns.elementAt(i)).point2));
        repaint();
        try { Thread.sleep(speed); } catch (InterruptedException e) {break;};

	for (int j=0; j<points.size(); j++) {
          if (((Line)tempLns.elementAt(i)).onLeft((pointExt)points.elementAt(j))) {	
               currPt = (pointExt) points.elementAt(j);
	       repaint();
               try { Thread.sleep(speed); } catch (InterruptedException e) {break;};
          }
        }
      }

      /* put the upper hull result in final result */
      for (int k=0; k<tempHull.size(); k++) {
         hull.addElement(new Line((pointExt) ((Line)tempHull.elementAt(k)).point1,
                                  (pointExt) ((Line)tempHull.elementAt(k)).point2));
      }		
      chkLns.removeAllElements();
      tempLns.removeAllElements();
      
      /* calculate the lower hull */
      quick(P2, l, r, 1);

      /* show how the lower hull was calculated */
      for (int i=0; i<tempLns.size(); i++) {
        chkLns.addElement(new Line((pointExt) ((Line)tempLns.elementAt(i)).point1, 
     	                           (pointExt) ((Line)tempLns.elementAt(i)).point2));
        repaint();
        try { Thread.sleep(speed); } catch (InterruptedException e) {break;};

	for (int j=0; j<points.size(); j++) {
          if (!((Line)tempLns.elementAt(i)).onLeft((pointExt)points.elementAt(j))) {	
               currPt = (pointExt) points.elementAt(j);
	       repaint();
               try { Thread.sleep(speed); } catch (InterruptedException e) {break;};
          }
        }
      }
		
      /* append the result from lower hull to final result */
      for (int k=0; k<tempHull.size(); k++) {
         hull.addElement(new Line((pointExt) ((Line)tempHull.elementAt(k)).point1,
                                  (pointExt) ((Line)tempHull.elementAt(k)).point2));
      }

      chkLns.removeAllElements();
      if (soundOn)
	 Hull.doneMusic.play();
   }


   /**
    * Recursive method to find out the Hull.
    * faceDir is 0 if we are calculating the upper hull.
    * faceDir is 1 if we are calculating the lower hull.
    */
   public synchronized void quick(Vector P, pointExt l, pointExt r, int faceDir) {
      if (P.size() == 2) {
         tempHull.addElement(new Line((pointExt) P.elementAt(0),
                                  (pointExt) P.elementAt(1)));
         return;
      } else {
	 int hAt = splitAt(P, l, r);
         Line lh = new Line((pointExt) l, (pointExt) P.elementAt(hAt));
         Line hr = new Line((pointExt) P.elementAt(hAt), (pointExt) r);
         Vector P1 = new Vector();
         Vector P2 = new Vector();

         for (int i = 0; i < (P.size() - 2); i++) {
	    if (i != hAt) {
               currPt = (pointExt) P.elementAt(i);
	       if (faceDir == 0) {
                  if (lh.onLeft((pointExt)P.elementAt(i))) {
                     P1.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
                                                ((pointExt)P.elementAt(i)).y));
                  }

		  if ((hr.onLeft((pointExt)P.elementAt(i)))) {
                  P2.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
                                             ((pointExt)P.elementAt(i)).y));
                  }
 	       } else {
                  if (!(lh.onLeft((pointExt)P.elementAt(i)))) {
                     P1.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
                                                ((pointExt)P.elementAt(i)).y));
                  };
	
	          if (!(hr.onLeft((pointExt)P.elementAt(i)))) {
                  P2.addElement(new pointExt(((pointExt)P.elementAt(i)).x,
                                             ((pointExt)P.elementAt(i)).y));
                  }; 
	       };
            }
         }

         P1.addElement(new pointExt(((pointExt)l).x, ((pointExt)l).y));
         P1.addElement(new pointExt(((pointExt)P.elementAt(hAt)).x, 
				    ((pointExt)P.elementAt(hAt)).y));

         P2.addElement(new pointExt(((pointExt)P.elementAt(hAt)).x, 
				    ((pointExt)P.elementAt(hAt)).y));
         P2.addElement(new pointExt(((pointExt)r).x, ((pointExt)r).y));
	 
	 pointExt h = new pointExt(((pointExt)P.elementAt(hAt)).x,
			           ((pointExt)P.elementAt(hAt)).y);

         tempLns.addElement(new Line((pointExt) l, (pointExt) h));
         tempLns.addElement(new Line((pointExt) h, (pointExt) r));

 	 if (faceDir == 0) {
            quick(P1, l, h, 0);
            quick(P2, h, r, 0);
	 } else {
	    quick(P1, l, h, 1);
            quick(P2, h, r, 1);
         }
      return;
      }
   }

   /**
    * Find out a point which is in the Hull for sure among a group of points
    * Since all the point are on the same side of the line formed by l and r,
    * so the point with the longest distance perpendicular to this line is 
    * the point we are lokking for.
    * Return the index of this point in the Vector/
    */
   public synchronized int splitAt(Vector P, pointExt l, pointExt r) {
      double    maxDist = 0;
      Line newLn = new Line((pointExt) l, (pointExt) r);

      int x3 = 0, y3 = 0;
      double distance = 0;
      int farPt = 0;

      for (int i = 0; i < (P.size() - 2); i++) {
         if (newLn.slopeUndefine) {
            x3 = l.x;
            y3 = ((pointExt)P.elementAt(i)).y;
         } else {
            if (r.y == l.y) {
               x3 = ((pointExt)P.elementAt(i)).x;
               y3 = l.y;
            } else {
                  x3 = (int) (((((pointExt)P.elementAt(i)).x + newLn.slope *
                                (newLn.slope * l.x - l.y +
                                ((pointExt)P.elementAt(i)).y))
                              / (1 + newLn.slope * newLn.slope)));
                  y3 = (int) ((newLn.slope * (x3 - l.x) + l.y));
            }
         }
         int x1 = ((pointExt)P.elementAt(i)).x;
         int y1 = ((pointExt)P.elementAt(i)).y;
         distance = Math.sqrt(Math.pow((y1-y3), 2) + Math.pow((x1-x3), 2));

         if (distance > maxDist) {
            maxDist = distance;
            farPt = i;
         }
      }
      return farPt;
   }
}

/**
 * Control panel for Convex Hull.
 */

public class ConvexHull extends Applet {
   SrcPanel srcPanel;
   Button   clearButton, doneButton, instruction;
   Checkbox sound;
   Choice   algorithms;
   AudioClip ding, clank, getone, doneMusic, menuMusic;
 
   public void init() {
      ding      = getAudioClip(getCodeBase(), "audio/ding.au");
      clank     = getAudioClip(getCodeBase(), "audio/clank.au");
      getone    = getAudioClip(getCodeBase(), "audio/return.au");
      doneMusic = getAudioClip(getCodeBase(), "audio/done.au");
      menuMusic = getAudioClip(getCodeBase(), "audio/title.au");

      setLayout(new BorderLayout());
      srcPanel = new SrcPanel(this);
      add("Center", srcPanel);
      Panel ctrlPanel = new Panel();
      add("North", ctrlPanel);

      algorithms = new Choice();
      algorithms.addItem("Brute Force");
      algorithms.addItem("Quick Hull");
      ctrlPanel.add(algorithms);

      Choice speed = new Choice();
      speed.addItem("Slow Demo");
      speed.addItem("Fast Demo");
      speed.addItem("No Delay");
      ctrlPanel.add(speed);
      sound = new Checkbox("Sound");
      sound.setState(true);
      ctrlPanel.add(sound);
      
      clearButton = new Button("Clear");
      ctrlPanel.add(clearButton);
      doneButton  = new Button("Go....");
      ctrlPanel.add(doneButton);

      instruction = new Button("???");
      ctrlPanel.add(instruction);
    }

   public void start() {
      srcPanel.start();
   }

   public void stop() {
      srcPanel.stop();
   }

   public boolean action(Event e, Object arg) {
      if (e.target instanceof Button) {
         if ((e.target).equals(instruction)) {
            srcPanel.preAlgor = srcPanel.algor;
            srcPanel.setMethod(SrcPanel.MENU);
            srcPanel.runMode = true;
         };

         if ((e.target).equals(clearButton)) {
            if (!srcPanel.runMode) {
               srcPanel.clearPoint();
            } else {
	       srcPanel.stop();
               srcPanel.runMode = false;
               srcPanel.clearPoint();
               srcPanel.start();
            }	
         };

         if ((e.target).equals(doneButton)) {
            if (srcPanel.points.size() > 2 ) 
               srcPanel.runMode = true;
         };

      };

      if (e.target instanceof Choice) {
         String choice = (String)arg;
         if (choice.equals("Brute Force")) {
            srcPanel.setMethod(SrcPanel.Brute);
         } else if (choice.equals("Quick Hull")) {
            srcPanel.setMethod(SrcPanel.QUICK);
         } else if (choice.equals("Slow Demo")) {
            srcPanel.speed = SrcPanel.SLOW;
         } else if (choice.equals("Fast Demo")) {
            srcPanel.speed = SrcPanel.FAST;
	 } else if (choice.equals("No Delay")) {
	    srcPanel.speed = SrcPanel.ZERO;
         }
      };

      if (e.target instanceof Checkbox) {
	 srcPanel.soundOn = ((Boolean)arg).booleanValue();
      };

      return true;
   }
}



