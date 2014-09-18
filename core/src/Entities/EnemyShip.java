package Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import Utilities.LineOfSightChecker;
import Utilities.Waypoint;
import Utilities.WaypointGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import Entities.Projectile.Characters;
import Equipables.InertialCruiseEngine;

public class EnemyShip extends Ship implements QueryCallback
{
	public enum SeekType
	{
		RamTarget, EnterFiringRange, TravelingToWaypoint, Stationary
	}

	SeekType m_seekType;
	SeekType m_onDeckSeekType;	
	private ViewedCollidable m_navigationTarget = null;
	Body m_navigationTargetBody = null;
	float m_wayPointX;
	float m_wayPointY;
	public Vector2 m_navigatingTo;
	ArrayList<EnemyShip> m_fighterGroup = new ArrayList<EnemyShip>();
	public int m_soundTheAlarmCounter = 0;
	
	ParticleEffect m_targetingEffect = new ParticleEffect();
	ParticleEffectPool m_targetingEffectPool;
	PooledEffect m_pooledTargetingEffect;
	private double m_fieldOfView = Math.PI/2;
	boolean m_showTargeting = true;
	protected float m_proximityAllowed = 10f;
	

	public EnemyShip(String appearanceLocation, float collisionScale, World world, float startX,
			float startY, float initialAngleAdjust, float maxV,
			int factionCode, ArrayList<ViewedCollidable> aliveThings)
	{
		super(appearanceLocation, collisionScale, world, startX, startY, maxV, aliveThings,
				factionCode);
		m_factionCode = factionCode;
		m_objectSprite.rotate((float) initialAngleAdjust);
		MassData data = new MassData();
		data.mass = 10;
		m_body.setMassData(data);
		m_body.setUserData(this);
		m_deathEffect.load(Gdx.files.internal("data/explosionred.p"),
				Gdx.files.internal("data/"));
		m_deathEffectPool = new ParticleEffectPool(m_deathEffect, 1, 2);
		m_pooledDeathEffect = m_deathEffectPool.obtain();
		m_onDeckSeekType = m_seekType = SeekType.EnterFiringRange;
		ce = new InertialCruiseEngine(this, maxV);
		m_navigatingTo = new Vector2();
		m_navigatingTo.x = (float)Math.PI;
		
		m_targetingEffect.load(Gdx.files.internal("data/targetingradar.p"), Gdx.files.internal("data/"));
		m_targetingEffectPool = new ParticleEffectPool(m_targetingEffect, 1, 2);
		m_pooledTargetingEffect = m_targetingEffectPool.obtain();
		m_weaponsFree = 0;
	}
	
	public boolean HasReachedWaypoint()
	{
		return !ce.EnginesEngaged() && !m_freezeShip;
	}
	
	public void AddToFighterGroup(EnemyShip e )
	{
		m_fighterGroup.add(e);
		e.m_fighterGroup.add( this );
	}

	@Override
	public void Draw(SpriteBatch renderer)
	{
		super.Draw(renderer);
		if(!m_inMenu && ! m_freezeShip )
		{
			IfTargetDeadDisengage();
			
			IfNoTargetAttemptToAcquireOne();
			
			IfTargetIsUntargetableOrOutOfSightAndRangeDisengage();
	
			IfTargetExistsOrWaypointNavigateTowards();
			
			HandleTargetingDrawAndWeaponsFree(renderer);
			
			UpdateTrackedTargetsList();
		}
	}

	protected void IfTargetExistsOrWaypointNavigateTowards()
	{
		if (m_navigationTarget != null || m_navigatingTo.x != Math.PI )
		{				
			NavigateToTarget();
		}
	}

	protected void IfTargetIsUntargetableOrOutOfSightAndRangeDisengage()
	{
		if( m_navigationTarget != null &&
			( m_navigationTarget.m_untargetable || m_navigationTarget.m_body.getPosition().dst(m_body.getPosition()) > m_navigationTarget.m_detectionRange && !CheckWingAndCenterPaths(0, m_body.getPosition(), m_navigationTarget.m_body.getPosition() ) ) )
		{
			//Check that target is still in detection range
			m_navigationTarget = null;
			m_trackedHostileTargets.remove(m_navigationTarget);
		}
	}

	protected void IfNoTargetAttemptToAcquireOne()
	{
		float centerX = m_body.getPosition().x;
		float centerY = m_body.getPosition().y;
		m_world.QueryAABB(this, centerX - m_sensorRange / 2, centerY
				- m_sensorRange / 2, centerX + m_sensorRange / 2,
				centerY + m_sensorRange / 2);
		
		if (m_navigationTarget == null && m_trackedHostileTargets.size() > 0)
		{
			m_navigationTarget = m_trackedHostileTargets.get(0);
		}
	}

	protected void IfTargetDeadDisengage()
	{
		if( m_navigationTarget != null && m_navigationTarget.m_integrity <= 0)
		{
			m_navigationTarget = null;
			m_trackedHostileTargets.remove(m_navigationTarget);
		}
	}

	private void HandleTargetingDrawAndWeaponsFree(SpriteBatch renderer)
	{
		if( m_trackedHostileTargets.size() > 0 && m_navigationTarget != null && m_showTargeting )
		{
			if( m_pooledTargetingEffect.isComplete() )
			{
				m_pooledTargetingEffect.reset();
				m_weaponsFree = 180;
			}
			
			if( m_weaponsFree == 0 )
			{
				m_pooledTargetingEffect.setPosition(m_objectXPosition, m_objectYPosition);
				m_pooledTargetingEffect.getEmitters().get(0).getRotation().setLow( m_angleDegrees );
				m_pooledTargetingEffect.getEmitters().get(1).getRotation().setLow( m_angleDegrees );
				m_pooledTargetingEffect.getEmitters().get(2).getRotation().setLow( m_angleDegrees );
				m_pooledTargetingEffect.getEmitters().get(3).getRotation().setLow( m_angleDegrees );
				
				m_pooledTargetingEffect.getEmitters().get(4).getRotation().setLow( m_angleDegrees + 90 );
				m_pooledTargetingEffect.getEmitters().get(4).getRotation().setHigh( m_angleDegrees - 90 );
				m_pooledTargetingEffect.getEmitters().get(5).getRotation().setLow( m_angleDegrees + 90 );
				m_pooledTargetingEffect.getEmitters().get(5).getRotation().setHigh( m_angleDegrees - 90 );
				m_pooledTargetingEffect.getEmitters().get(6).getRotation().setLow( m_angleDegrees + 90 );
				m_pooledTargetingEffect.getEmitters().get(6).getRotation().setHigh( m_angleDegrees - 90 );
				m_pooledTargetingEffect.getEmitters().get(7).getRotation().setLow( m_angleDegrees + 90 );
				m_pooledTargetingEffect.getEmitters().get(7).getRotation().setHigh( m_angleDegrees - 90 );
				
				m_pooledTargetingEffect.draw(renderer, 1f/60f);
			}
			else
			{
				m_weaponsFree--;
			}
		}
	}

	private void UpdateTrackedTargetsList()
	{
		//update tracked targets
		ArrayList<ViewedCollidable> targetsToRemove = new ArrayList<ViewedCollidable>();
		
		for( int i = 0; i< m_trackedHostileTargets.size(); i++ )
		{
			ViewedCollidable vc = m_trackedHostileTargets.get(i);
			if( vc != null )
			{ 
				boolean outOfRange = vc.m_body.getPosition().dst(m_body.getPosition()) > vc.m_detectionRange;
				
				boolean noLineOfSight = !CheckWingAndCenterPaths(0, m_body.getPosition(), vc.m_body.getPosition() );
				
				boolean notAlive = vc.m_integrity <=0; 
				if( (outOfRange && noLineOfSight) || notAlive )
				{
					targetsToRemove.add(vc);
				}				
			}
		}
		
		for( int i = 0; i< targetsToRemove.size(); i++ )
		{
			m_trackedHostileTargets.remove(targetsToRemove.get(i) );
		}
		
		AlertAllies();
	}

	private void AlertAllies()
	{
		if( m_trackedHostileTargets.size() > 0)
		{
			m_soundTheAlarmCounter++;
			
			if( m_soundTheAlarmCounter > 400 )
			{
				for( int i = 0; i < m_fighterGroup.size(); i++ )
				{
					if( m_fighterGroup.get(i).m_navigationTarget == null )
					{
						m_fighterGroup.get(i).m_navigationTarget = m_navigationTarget;
						m_fighterGroup.get(i).m_trackedHostileTargets.remove(m_navigationTarget);
						m_fighterGroup.get(i).m_trackedHostileTargets.add(m_navigationTarget);
					}
				}
			}
		}
		else
		{
			m_soundTheAlarmCounter = 0;
		}
	}

	protected void NavigateToTarget()
	{
		if( m_seekType != SeekType.Stationary )
		{
			CalculateWaypoint();
			DriveEnginesToWaypoint();
		}
	}

	private void DriveEnginesToWaypoint()
	{
		Vector2 pos = m_body.getPosition();
		Vector2 vec = new Vector2();
		vec.x = m_wayPointX;
		vec.y = m_wayPointY;
		m_angleRadians = Math.atan2(vec.y - pos.y, vec.x - pos.x);
		float degrees = (float) (m_angleRadians * 180 / Math.PI);
		float difference = degrees - m_angleDegrees;
		m_objectSprite.rotate((float) (difference));
		m_angleDegrees = (float) degrees;
		m_body.setTransform(m_body.getPosition(), (float) Math.toRadians(m_angleDegrees));
		
		switch (m_seekType)
		{
		case RamTarget:
			// Keep accelerating into target
			RammingLogic(pos, vec);
			break;
		case EnterFiringRange:
			//  get within firing range and drift.
			FiringDistanceLogic(pos, vec);
			break;
		case TravelingToWaypoint:
			//  get within firing range and drift.
			RammingLogic(pos, vec);
			break;
		case Stationary:
		default:
			// do nothing
			break;
		}
		
		

		
	}

	protected void RammingLogic(Vector2 pos, Vector2 vec)
	{
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y))
		{
			ce.EngineBrake();			
		} else
		{
			ce.ThrottleForward();
			ce.EngageEngine();
		}
	}
	
	protected void FiringDistanceLogic(Vector2 pos, Vector2 vec)
	{
		float distance = pos.dst(vec);
		if ((m_body.getLinearVelocity().x > 5 && vec.x < pos.x)
				|| (m_body.getLinearVelocity().x < -5 && vec.x > pos.x)
				|| (m_body.getLinearVelocity().y > 5 && vec.y < pos.y)
				|| (m_body.getLinearVelocity().y < -5 && vec.y > pos.y)
				|| pos.dst(vec) <= m_proximityAllowed  )
		{
			ce.EngineBrake();
			ce.DisengageEngine();
		} 
		else if( distance > m_proximityAllowed )
		{
			ce.ThrottleForward();
			ce.EngageEngine();
		}
	}

	private void CalculateWaypoint()
	{
		/*
		float radius = Math.max(m_objectAppearance.getWidth() / 2, m_objectAppearance.getHeight() / 2) / 29f;
		Vector2 source = new Vector2();
		Vector2 destination = new Vector2();
		if( m_navigationTarget != null )
		{
			destination.x = m_navigationTarget.m_body.getPosition().x;
			destination.y = m_navigationTarget.m_body.getPosition().y;	
			m_navigatingTo.x = m_navigationTarget.m_body.getPosition().x;
			m_navigatingTo.y = m_navigationTarget.m_body.getPosition().y;
		}
		else
		{
			destination.x = m_navigatingTo.x;
			destination.y = m_navigatingTo.y;	
		}
			
		source.x = m_body.getPosition().x;
		source.y = m_body.getPosition().y;	
		
		// Check LOS to target only! return true or false
		 if( CheckWingAndCenterPaths(radius, source, destination) )
		 {
			 // We're good to go
			m_wayPointX = destination.x;
			m_wayPointY = destination.y;
			m_seekType = m_onDeckSeekType;
		 }
		 else
		 {
			 RegularPathFinding(radius);			 
		 }*/
		Vector2 destination = new Vector2();
		float radius = Math.max(m_objectAppearance.getWidth() / 2, m_objectAppearance.getHeight() / 2) / 29f;
		Vector2 source = new Vector2();
		source.x = m_body.getPosition().x;
		source.y = m_body.getPosition().y;	
		
		if( m_navigationTarget != null )
		{
			destination.x = m_navigationTarget.m_body.getPosition().x;
			destination.y = m_navigationTarget.m_body.getPosition().y;
			if( CheckWingAndCenterPaths(radius, source, destination) )
			{
				 // We're good to go
				m_wayPointX = destination.x;
				m_wayPointY = destination.y;
				m_seekType = m_onDeckSeekType;
				m_navigatingTo.x = destination.x;
				m_navigatingTo.y = destination.y;
			}
			else
			{
				if( m_navigatingTo.x == Math.PI ||
					HasReachedWaypoint() )
				{
					RegularPathFinding(radius);
				}
			}
		}
		else if( m_navigatingTo.x != Math.PI )
		{
			destination.x = m_navigatingTo.x;
			destination.y = m_navigatingTo.y;	
			if( !CheckWingAndCenterPaths(radius, source, destination) )
			{
				RegularPathFinding(radius);
			}
			else
			{
				 // We're good to go
				m_wayPointX = destination.x;
				m_wayPointY = destination.y;
				m_seekType = m_onDeckSeekType;
			}
		}
		
	}

	protected void RegularPathFinding(float radius)
	{
		// we've got a path to find
		 m_seekType = SeekType.TravelingToWaypoint;
		 // initiate path current position to the ship's position add to generation list
		 boolean losToEnemy = false;
		 boolean takeLeftPath = false;
		 Waypoint start = new Waypoint( m_body.getPosition());
		 ArrayList<Waypoint> generationList = new ArrayList<Waypoint>();
		 ArrayList<Waypoint> vetList = new ArrayList<Waypoint>();
		 ArrayList<Waypoint> validWaypoints = new ArrayList<Waypoint>();
		 generationList.add( start );
		 
		 Waypoint leftPath = null;
		 Waypoint rightPath = null;
		 
		 int iterations = 0;
		 
		 // while we don't have a waypoint with LOS
		 while( !losToEnemy )
		 {			 
			 
			 // for each item in generation list
			 for( int i = 0; i < generationList.size(); i++ )
			 {
				 // look in the direction of the enemy ship and 
				 // Generate left and right possible waypoints
				 WaypointGenerator gen = new WaypointGenerator( m_body, m_world, generationList.get(i), m_navigatingTo );
				 gen.Generate( radius );
				 vetList.add(gen.m_left);
				 vetList.add(gen.m_right);
			 }
			 
			 generationList.clear();
			 
			 //for each generated waypoint
			 for( int i = 0; i < vetList.size(); i++)
			 {
				 boolean losToWaypoint = false;
				 Waypoint w = vetList.get(i);
				 //while
				 int wayPointIterations = 0;
				 while ( !losToWaypoint )
				 {						 
				 	//if have LOS to generated waypoint
					 if( CheckWingAndCenterPaths(radius, w.m_origin, w.m_waypoint) )
					 {
				 		// add to list points to check if we have LOS to target
						 validWaypoints.add(w);
						 losToWaypoint = true;
						 
						 // Set the initial left and right forks for later on.
						 if( w.m_isLeftPath && leftPath == null )
						 {
							 leftPath = w;
						 }
						 
						 if( w.m_isRightPath && rightPath == null )
						 {
							 rightPath = w;
						 }
					 }
					 else //else don't have LOS
					 {
				 		// regenerate the point
						 Vector2 conflictedPoint = new Vector2( w.m_waypoint );
						 WaypointGenerator gen = new WaypointGenerator( m_body, m_world, w, conflictedPoint );
						 gen.Generate( radius );
						 
						 if(w.m_isLeftFork)
						 {
							 w.m_waypoint.x = gen.m_left.m_waypoint.x;
							 w.m_waypoint.y = gen.m_left.m_waypoint.y;
						 }
						 else
						 {
							 w.m_waypoint.x = gen.m_right.m_waypoint.x;
							 w.m_waypoint.y = gen.m_right.m_waypoint.y; 
						 }							 
					 }
					 
					 wayPointIterations++;
					 
					 if( wayPointIterations > 400 )
					 {
						 losToWaypoint = true;
					 }
				 }
			 }
			 
			 vetList.clear();
			 
			 // for each waypoints the ship has LOS to as long as we haven't detected LOS
			 //to enemy ship
			 for( int i = 0; i < validWaypoints.size() && !losToEnemy; i++)
			 {		
				 
				 Waypoint w = validWaypoints.get(i);
			 	// if LOS to enemy ship or last seen location, or nav point set by the level
				 if( CheckWingAndCenterPaths(radius, w.m_waypoint, m_navigatingTo ) )
				 {
			 		// set loop var end loop
					 losToEnemy = true;
					 takeLeftPath = w.m_isLeftPath;
				 }
				 else // else
				 {
			 		// add this waypoint to the generation list
					 w.m_origin = w.m_waypoint;
					 generationList.add(w);
				 }
			 }
			 
			 validWaypoints.clear();
			 
			 iterations++;
			 
			 if( iterations > 7 )
			 {
				 losToEnemy = true;
			 }
		 }
		 
		 
		 if( takeLeftPath )
		 {
			 if( leftPath != null )
			 {
				 m_wayPointX = leftPath.m_waypoint.x;
				 m_wayPointY = leftPath.m_waypoint.y;
			 }
			 else
			 {
				 // if we got through the loop and got nothing just blow through!
				 m_wayPointX = m_navigatingTo.x;
				 m_wayPointY = m_navigatingTo.y;
			 }
		 }
		 else
		 {
			 if( rightPath != null )
			 {
				 m_wayPointX = rightPath.m_waypoint.x;
				 m_wayPointY = rightPath.m_waypoint.y;
			 }
			 else
			 {
				 // if we got through the loop and got nothing just blow through!
				 m_wayPointX = m_navigatingTo.x;
				 m_wayPointY = m_navigatingTo.y;
			 }
		 }
	}

	private boolean CheckWingAndCenterPaths(float radius, Vector2 source,
			Vector2 destination)
	{
		boolean hasLineOfSight = CheckPath( source, destination);
		double angleRadians = Math.atan2(destination.y - source.y, destination.x - source.x);
		if( hasLineOfSight )
		{
			Vector2 tmp = new Vector2();
			tmp.x = source.x -(float)(radius * Math.sin(angleRadians));
			tmp.y = source.y +(float)(radius * Math.cos(angleRadians));
			hasLineOfSight = CheckPath( tmp, destination);
		}
		
		if(hasLineOfSight )
		{
			Vector2 tmp = new Vector2();
			tmp.x = source.x +(float)(radius * Math.sin(angleRadians));
			tmp.y = source.y -(float)(radius * Math.cos(angleRadians));
			hasLineOfSight = CheckPath( tmp, destination);
		}
		
		return hasLineOfSight;
	}

	private boolean CheckPath(Vector2 source, Vector2 destination)
	{
		LineOfSightChecker check = new LineOfSightChecker( m_navigationTargetBody, m_body );
		m_world.rayCast(check, source, destination);
		
		return check.m_hasLineOfSight;
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable p = (ViewedCollidable) fixture.getBody().getUserData();
		
		if (p != null && 
			p.m_factionCode != m_factionCode &&
			p.m_isTargetable &&
			p.m_factionCode != 0 &&
			Ship.class.isInstance(p) )
		{			
			Ship s = (Ship) fixture.getBody().getUserData();
			
			if( s != null )
			{
				if( s.m_body.getPosition().dst(m_body.getPosition()) <= s.m_detectionRange )
				{
					m_trackedHostileTargets.remove(p);
					m_trackedHostileTargets.add(p);
				}
			}
			else
			{
				m_trackedHostileTargets.remove(p);
				m_trackedHostileTargets.add(p);
			}
		}
		return true;
	}

	public void SetCurrentTarget(ViewedCollidable p)
	{
		SetCurrentTarget(p, true );
	}
	
	public void SetCurrentTarget(ViewedCollidable p, boolean addToTrackedTargets )
	{
		m_navigationTarget = p;
		m_navigationTargetBody = p.m_body;
		
		if( addToTrackedTargets )
		{
			m_trackedHostileTargets.remove(p);
			m_trackedHostileTargets.add(p);
		}
	}
	
	public void DisengageCurrentTarget()
	{		
		m_navigationTarget = null;
		m_navigationTargetBody = null;
	}
	
	public ViewedCollidable GetTarget()
	{
		return m_navigationTarget;
	}

}
