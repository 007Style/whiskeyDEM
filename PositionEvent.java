/*
 * PositionEvent.java
 */
 
import java.awt.AWTEvent;
import javax.vecmath.*;


public class PositionEvent extends AWTEvent 
{
  static Point3d   m_newPos;
  static Vector3d  m_travel;
  static double    m_distanceTraveled;
  private flightPacket p = new flightPacket();
  
  /** Creates new PositionEvent */
  public PositionEvent(FlightBehavior source, Point3d newPos, Vector3d travel, double distanceTraveled) {
    super(source, RESERVED_ID_MAX + 1);
    //System.out.println("ControlJPanel.flightClient.s " + ControlJPanel.flightClient.s);
    //System.out.println("SimType " + ProgramConfig.getSimType());
    if(ProgramConfig.getSimType()==0)
    {
    	if((ControlJPanel.flightServer.s != null))  //Server On  -- Send
    	{
	    	//System.out.println("Server part");
    		m_newPos = newPos;
      	m_travel = travel; 
      	m_distanceTraveled = distanceTraveled;
    		p.m_newPos = m_newPos;
    		p.m_travel = m_travel;
    		p.m_distanceTraveled = m_distanceTraveled;
    		p.fps = MovementModel.getFPS();
    		if((MovementModel.getFPS()>=ProgramConfig.getSyncAfterFrame()) && (ProgramConfig.getSyncAfterFrame()>=0))
    		{
	    		if(!(ProgramConfig.getSyncAfterFrame()==0))  MovementModel.setFPS(0);
  	  		p.sync = true;
    		}
    		else
    		{
	    		p.sync = false;	
    		}
    		if(ControlJPanel.forceSync == true)
    		{
	    		MovementModel.setFPS(0);
    			p.sync = true;
    			ControlJPanel.forceSync = false;
    		}
    		//System.out.println("ServerSend " + p);
    		ControlJPanel.flightServer.sSend(p);
    	}
    	else if((ControlJPanel.flightClient.s != null))  //Client On  -- Recv
    	{
	    	//System.out.println("Client part");
      	//p = ControlJPanel.flightClient.cRecv();	
      	/*if(ControlJPanel.flightClient==null)
      	{
	      	System.out.println("NULL");
      		p = new flightPacket();
      	}
      	else
      	{
	      	p = ControlJPanel.flightClient.sRecv();	
     		}*/
     		p = ControlJPanel.flightClient.sRecv();	
     		//System.out.println("ClientRecv " + p);
      	m_newPos = p.m_newPos;
    		m_travel = p.m_travel;
    		m_distanceTraveled = p.m_distanceTraveled;
    	}
    	else
    	{
	      m_newPos = newPos;
      	m_travel = travel;
      	m_distanceTraveled = distanceTraveled;
    	}	
  	}
  	else if(ProgramConfig.getSimType()==1)
    {
    	if((ControlJPanel.nioFlightServer.s != null))  //Server On  -- Send
    	{
	    	//System.out.println("Server part");
    		m_newPos = newPos;
      	m_travel = travel; 
      	m_distanceTraveled = distanceTraveled;
    		p.m_newPos = m_newPos;
    		p.m_travel = m_travel;
    		p.m_distanceTraveled = m_distanceTraveled;
    		p.fps = MovementModel.getFPS();
    		if((MovementModel.getFPS()>=ProgramConfig.getSyncAfterFrame()) && (ProgramConfig.getSyncAfterFrame()>=0))
    		{
	    		if(!(ProgramConfig.getSyncAfterFrame()==0))  MovementModel.setFPS(0);
  	  		p.sync = true;
    		}
    		else
    		{
	    		p.sync = false;	
    		}
    		if(ControlJPanel.forceSync == true)
    		{
	    		MovementModel.setFPS(0);
    			p.sync = true;
    			ControlJPanel.forceSync = false;
    		}
    		ControlJPanel.nioFlightServer.sSend(p);
    	}
    	else if((ControlJPanel.nioFlightClient.s != null))  //Client On  -- Recv
    	{
    		//System.out.println("Client part");
    		ControlJPanel.nioFlightClient.nioRecv();	
     		p = ControlJPanel.nioFlightClient.sRecv();	
      	m_newPos = p.m_newPos;
    		m_travel = p.m_travel;
    		m_distanceTraveled = p.m_distanceTraveled;
    	}
    	else
    	{
	      m_newPos = newPos;
      	m_travel = travel;
      	m_distanceTraveled = distanceTraveled;
    	}	
  	}
  	else
  	{
	    m_newPos = newPos;
     	m_travel = travel;
     	m_distanceTraveled = distanceTraveled;
    }	
	}
	  
	public Point3d getNewPos()
  {
    //System.out.println("m_newPos: " + m_newPos);
    //if(Server.s!=null) ControlJPanel.flightServer.sSend(m_newPos.toString());
    //Server.sSend(m_newPos);
    return m_newPos;
  }
  
  public Vector3d getTravel()
  {
    //System.out.println("m_travel: " + m_travel);
    //if(Server.s!=null) ControlJPanel.flightServer.sSend(m_travel.toString());
    /*
    String processVec = "(1000.0, 1906.6666666666765, 14500.0)";
    int index = 0;
    int endex = 0;
    double[] v = new double[3];
    endex = processVec.indexOf(",", index);
    v[0] = Double.parseDouble(processVec.substring(index+1, endex));
    index = endex;
    endex = processVec.indexOf(",", endex+1);
    v[1] = Double.parseDouble(processVec.substring(index+2, endex));
    index = endex;
    endex = processVec.indexOf(")", endex);
    v[2] = Double.parseDouble(processVec.substring(index+2, endex));
    System.out.println(v[0] + "," + v[1] + "," + v[2]);
    */
    return m_travel;
  }
  
  public double getDistanceTraveled()
  {
    //System.out.println("m_distanceTraveled: " + m_distanceTraveled);
    //if(Server.s!=null) ControlJPanel.flightServer.sSend(Double.toString(m_distanceTraveled));
    return m_distanceTraveled;
  }
}