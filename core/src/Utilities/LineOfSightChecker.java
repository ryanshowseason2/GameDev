package Utilities;

import Entities.ViewedCollidable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class LineOfSightChecker implements RayCastCallback
{
	
	Body m_body = null;
	ViewedCollidable m_targetObject = null;
	Body m_self;
	public boolean m_hasLineOfSight;
	public LineOfSightChecker( Body target, Body self )
	{
		m_body = target;
		if( target != null )
		{
			m_targetObject = ( ViewedCollidable ) target.getUserData();
		}
		m_self = self;
		Reset();
	}

	public void Reset()
	{
		m_hasLineOfSight = true;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable navigator = (ViewedCollidable) m_self.getUserData();
		ViewedCollidable inTheWay = (ViewedCollidable) fixture.getBody().getUserData();
		
		if( fixture.getBody() != m_body &&
			fixture.getBody() != m_self &&
			( m_targetObject == null || ( m_targetObject != null && m_targetObject != inTheWay ))&& 
			inTheWay.m_factionCode == 0 &&
			!inTheWay.m_ignoreForPathing )
		{
			m_hasLineOfSight = false;
		}
		return 1;
	}

}
