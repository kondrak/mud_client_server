package mud_gui;

import javax.swing.*;
import java.awt.*;

/**
 * This class implements a dynamic location image on the main client panel.
 * 
 * @author ?
 * @version ?
 *
 */

public class DrawingPanel extends JPanel {
  private Image img;

  DrawingPanel (Image img)
  { this.img = img; }

  public void paintComponent (Graphics g) {
   super.paintComponent (g);

   int imgX = getSize().width/128;
   int imgY = getSize().height/128;

   //Draw image centered in the middle of the panel    
   g.drawImage (img, imgX, imgY, this);
  } // paintComponent  

  public void RedrawPicture(Image picture) {
	  img = picture;
  }
  
} // DrawingPanel
