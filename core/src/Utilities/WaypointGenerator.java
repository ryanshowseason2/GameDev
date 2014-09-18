package Utilities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import Entities.EnemyShip.SeekType;
import Entities.ViewedCollidable;

public class WaypointGenerator implements RayCastCallback
{

	float m_minDistance = Float.MAX_VALUE;
	World m_world;
	Waypoint m_start;
	Vector2 m_target;
	public Waypoint m_left;
	public Waypoint m_right;
	Body m_body;
	Vector2 m_tmpSource = new Vector2();
	
	public WaypointGenerator( Body b, World w, Waypoint start, Vector2 target)
	{
		m_target = target;
		m_world = w;
		m_start = start;
		m_body = b;
	}
	
	public void Generate( float radiusForClearence )
	{
		m_left = new Waypoint( m_start.m_origin );
		m_right = new Waypoint( m_start.m_origin );
		m_left.m_isLeftFork = true;
		
		if( !m_start.m_isLeftPath && !m_start.m_isRightPath )
		{
			m_left.m_isLeftPath = true;
			m_right.m_isRightPath = true;
		}
		else
		{
			m_left.m_isLeftPath = m_start.m_isLeftPath;
			m_left.m_isRightPath = m_start.m_isRightPath;
			m_right.m_isLeftPath = m_start.m_isLeftPath;
			m_right.m_isRightPath = m_start.m_isRightPath;
		}
		
		
		m_tmpSource.x = m_start.m_origin.x;
		m_tmpSource.y = m_start.m_origin.y;
		m_world.rayCast(this, m_start.m_origin, m_target );
		
		double angleRadians = Math.atan2(m_start.m_origin.y - m_target.y, m_start.m_origin.x - m_target.x);
		if( m_minDistance == Float.MAX_VALUE )
		{
			m_tmpSource.x = m_start.m_origin.x -(float)(radiusForClearence * Math.sin(angleRadians));
			m_tmpSource.y = m_start.m_origin.y +(float)(radiusForClearence * Math.cos(angleRadians));
			m_world.rayCast(this, m_tmpSource, m_target );
		}
		
		if(m_minDistance == Float.MAX_VALUE )
		{
			m_tmpSource.x = m_start.m_origin.x +(float)(radiusForClearence * Math.sin(angleRadians));
			m_tmpSource.y = m_start.m_origin.y -(float)(radiusForClearence * Math.cos(angleRadians));
			m_world.rayCast(this, m_tmpSource, m_target );
		}
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction)
	{
		ViewedCollidable navigator = (ViewedCollidable) m_body.getUserData();
		ViewedCollidable inTheWay = (ViewedCollidable) fixture.getBody().getUserData();
		
		if( m_body != fixture.getBody() &&
			navigator.m_factionCode != inTheWay.m_factionCode &&
			!inTheWay.m_ignoreForPathing &&
			m_tmpSource.dst(point) < m_minDistance )
		{
			m_minDistance = m_tmpSource.dst(point);
			
			//generate the points
			// deviate the angle set that as the waypoint
			Vector2 pos = m_tmpSource;
			double angleRadians = Math.atan2(point.y - pos.y, point.x - pos.x);
			angleRadians += Math.PI /2;
			
			m_right.m_waypoint.x = (float) (point.x + 5 * Math.cos(angleRadians));
			m_right.m_waypoint.y = (float) (point.y + 5 * Math.sin(angleRadians));
			
			angleRadians -= Math.PI;
			m_left.m_waypoint.x = (float) (point.x + 5 * Math.cos(angleRadians));
			m_left.m_waypoint.y = (float) (point.y + 5 * Math.sin(angleRadians));						
		}
		return 1;
	}

}
