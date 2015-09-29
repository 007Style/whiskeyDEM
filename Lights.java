/*
 * Lights.java
 */

import javax.media.j3d.*;
import javax.vecmath.*;


public class Lights extends BranchGroup {

  /** Creates new Lights */
  public Lights(BoundingSphere bounds)
  {
    // Set up the ambient light
    Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);
    AmbientLight ambientLightNode = new AmbientLight(ambientColor);
    ambientLightNode.setInfluencingBounds(bounds);
    addChild(ambientLightNode);

    // Set up the directional lights
    Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
    Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
    Color3f light2Color = new Color3f(0.3f, 0.3f, 0.4f);
    Vector3f light2Direction  = new Vector3f(-6.0f, -2.0f, -1.0f);
    Color3f light3Color = new Color3f(1.0f, 1.0f, 0.9f);
    Vector3f light3Direction  = new Vector3f(-4.0f, 7.0f, 12.0f);

    DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
    light1.setInfluencingBounds(bounds);
    addChild(light1);

    DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
    light2.setInfluencingBounds(bounds);
    addChild(light2);

    DirectionalLight light3 = new DirectionalLight(light3Color, light3Direction);
    light3.setInfluencingBounds(bounds);
    addChild(light3);
  }

}