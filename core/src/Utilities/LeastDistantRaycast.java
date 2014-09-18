package Utilities;

import java.util.ArrayList;

import Entities.ViewedCollidable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class LeastDistantRaycast extends LaserRayCastBase implements RayCastCallback
{
	Body m_self;
	public float m_minDistance;
	Vector2 m_contactPoint;
	public ViewedCollidable m_entityHit;
	ArrayList<ViewedCollidable> m_hitEntities = new ArrayList<ViewedCollidable>();
	public LeastDistantRaycast(  Body self )
	{
		m_self = self;
		Reset();
	}
	
	public void Reset()
	{
		m_minDistance = Float.MAX_VALUE;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable navigator = (ViewedCollidable) m_self.getUserData();
		ViewedCollidable inTheWay = (ViewedCollidable) fixture.getBody().getUserData();
		float distance = navigator.m_body.getPosition().dst(point);
		if( fixture.getBody() != m_self &&
			!inTheWay.m_ignoreForPathing &&
			inTheWay.m_factionCode != navigator.m_factionCode &&
			distance < m_minDistance )
		{
			m_contactPoint = point;
			m_minDistance = distance;
			m_entityHit = inTheWay;
		}
		return 1;
	}

	@Override
	public float GetDistanceTraveled()
	{
		// TODO Auto-generated method stub
		return m_minDistance;
	}

	@Override
	public ArrayList<ViewedCollidable> GetEntitiesHit()
	{
		m_hitEntities.remove( m_entityHit );
		m_hitEntities.add( m_entityHit );
		return m_hitEntities;
	}
}
