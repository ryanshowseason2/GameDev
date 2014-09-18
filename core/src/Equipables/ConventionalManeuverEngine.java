package Equipables;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import Entities.Ship;

public class ConventionalManeuverEngine extends ManeuverEngine
{

	public ConventionalManeuverEngine(Ship s, float maxVelocity)
	{
		super(s, maxVelocity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ManeuverForward()
	{
		if( m_boostJuice > 0 )
		{
			float xForce = 0;
			float yForce = 0;
			// TODO Auto-generated method stub
			xForce = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        yForce = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        m_boostJuice -= 20;	
	        
		    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
		}
	}

	@Override
	public void ManeuverBackward()
	{
		if( m_boostJuice > 0 )
		{
			float xForce = 0;
			float yForce = 0;
			// TODO Auto-generated method stub
			xForce = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        yForce = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        m_boostJuice -= 20;	
	        
		    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
		}
	}

	@Override
	public void ManeuverPort()
	{
		if( m_boostJuice > 0 )
		{
			float xForce = 0;
			float yForce = 0;
			// TODO Auto-generated method stub
			xForce = (float)(-m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        yForce = (float)(m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        m_boostJuice -= 20;	
	        
		    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
		}
	}

	@Override
	public void ManeuverStarboard()
	{
		if( m_boostJuice > 0 )
		{
			float xForce = 0;
			float yForce = 0;
			// TODO Auto-generated method stub
			xForce = (float)(m_boostMagnitude * Math.sin(m_ship.m_angleRadians));
	        yForce = (float)(-m_boostMagnitude * Math.cos(m_ship.m_angleRadians));
	        m_boostJuice -= 20;	
	        
		    m_ship.m_body.applyForceToCenter( xForce, yForce, true);
		}
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RegisterCollision()
	{
		// TODO Auto-generated method stub
		
	}

}
