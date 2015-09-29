/*
 * myBackground.java
 */
 
import javax.vecmath.Color3f;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import java.awt.image.*;
import java.io.*;
import javax.media.j3d.*;
import javax.imageio.*;



public class myBackground extends Background {

  /** Creates new myBackground */
  public myBackground(BoundingSphere bounds)
  {
    // Set up the background
    //Color3f bgColor = new Color3f(0.9f, 0.9f, 0.9f);
    Color3f bgColor = new Color3f(0.5f, 0.75f, 1f); //0.1f, 0.15f, 0.3f);
    
    /*
    String FileName = "clouds.gif";
    BufferedImage im = null;
		try {
		im = ImageIO.read(new File("./FlightObjects/Textures/"+FileName).toURL());
		} catch (IOException e) {System.out.println(e);}//MalformedURLException mue) {mue.printStackTrace;}
    ImageComponent2D Imagee = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, im);
    //setImage(Imagee);
    */
    
    setColor(bgColor);
    setApplicationBounds(bounds);
  }
 
}