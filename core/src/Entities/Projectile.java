package Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import Entities.ViewedCollidable.DamageType;

public class Projectile extends ViewedCollidable
{
	public enum Characters
	{
		Sandy,
		Gourt,
		Noel,
		Shavret,
		Bobbi,
		SSid,
		Belice,
		Yashpal
	}
	
	EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(Characters.class);
	float m_originX;
	float m_originY;
	public float m_projectileVelocity = -70f;
	int m_bulletLife = 100;
	boolean m_etherealBullet = false;
	float m_minDistance;
	Ship m_ship;
	ViewedCollidable m_originalTarget;
	public boolean m_stopAtFirstShields = false;
	public float m_additionalDamage = 0;
	
	public Projectile(String appearanceLocation, float collisionScale, World world, float startX,
			float startY, ArrayList<ViewedCollidable> aliveThings, int factionCode)
	{
		super(appearanceLocation, collisionScale, world, startX, startY, aliveThings, factionCode);
		// TODO Auto-generated constructor stub		
		m_originX = startX;
		m_originY = startY;
		MassData data = new MassData();
		data.mass = .00001f;
		m_body.setMassData(data);
		m_body.setBullet(true);
		m_body.setUserData(this);
		m_integrity = 1;
		m_ignoreForPathing = true;
		m_isTargetable = false;
		m_body.getFixtureList().get(0).setSensor(true);
		
		PopulateSpecials();
		SetSpecials( m_specialAbilitiesActivated );
	}

	private void PopulateSpecials()
	{
		m_specialAbilitiesActivated.put(Characters.Sandy, false);
		m_specialAbilitiesActivated.put(Characters.Gourt, false);
		m_specialAbilitiesActivated.put(Characters.Noel, false);
		m_specialAbilitiesActivated.put(Characters.Shavret, false);
		m_specialAbilitiesActivated.put(Characters.Bobbi, false);
		m_specialAbilitiesActivated.put(Characters.SSid, false);
		m_specialAbilitiesActivated.put(Characters.Belice, false);
		m_specialAbilitiesActivated.put(Characters.Yashpal, false);
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		if( ((!m_etherealBullet && object2.m_isTargetable) ||
			( m_etherealBullet && object2.m_factionCode != 0 ) ) &&
			!WingBlade.class.isInstance(object2 ) )
		{
			DamageType dType = DamageType.Penetration;
			
			dType = ShavretsDamageType(object2);
			
			object2.damageIntegrity( m_ship, crashVelocity/5 + m_additionalDamage, dType );	
			
			
			if( m_stopAtFirstShields )
			{
				if( Ship.class.isInstance(object2) )
				{
					Ship s = (Ship)object2;
					if(s.m_shieldIntegrity>0)
					{
						m_integrity = 0;
					}
				}
			}
			else
			{
				m_integrity -=1;
			}
			
			BobbisHackingBullets(object2);
			SSidsHackingBullets(object2);
			NoelsHackingBullets(object2);
		}
	}

	private DamageType ShavretsDamageType(ViewedCollidable object2)
	{
		DamageType dType;
		if( m_specialAbilitiesActivated.get(Characters.Shavret) && 
			object2.m_damageResistances[DamageType.Penetration.value] > object2.m_damageResistances[DamageType.Collision.value] )
		{
			dType = DamageType.Collision;
		}
		else
		{
			dType = DamageType.Penetration;
		}
		return dType;
	}

	private void BobbisHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.Bobbi) &&
			Ship.class.isInstance(object2) )
		{				
			Ship s = (Ship) object2;
			if( s != null )
			{
				if( s.AttemptHack( 5.0f ) )
				{
					s.m_shieldRechargeDelay+= 5;
				}
			}
		}
	}
	
	private void SSidsHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.SSid) &&
				EnemyShip.class.isInstance(object2) )
		{				
			EnemyShip s = (EnemyShip) object2;
			if( s != null && s.m_shieldIntegrity <= 0 && s.m_fighterGroup.size() > 0 )
			{
				if( s.AttemptHack( 15.0f ) )
				{
					ViewedCollidable vc = s.m_fighterGroup.get(0);
					m_ship.m_trackedHostileTargets.add( vc );
					s.m_fighterGroup.remove(vc);
					s.m_soundTheAlarmCounter = 0;
				}
			}
		}
	}
	
	private void NoelsHackingBullets(ViewedCollidable object2)
	{
		if( m_specialAbilitiesActivated.get(Characters.Noel) &&
				EnemyShip.class.isInstance(object2) )
		{				
			EnemyShip s = (EnemyShip) object2;
			if( s != null && s.m_shieldIntegrity <= 0 && s.ce.m_enginePotency > 0 )
			{
				if( s.AttemptHack( 15.0f ) )
				{
					s.ce.m_enginePotency*=.75;
				}
			}
		}
	}
	
	public void Fire( Ship origin, ViewedCollidable target, float accuracy )
	{
		m_ship = origin;
		m_originalTarget = target;
		float centerX = origin.m_body.getPosition().x;
		float centerY = origin.m_body.getPosition().y;
		float targetCenterX = target.m_body.getPosition().x;
		float targetCenterY = target.m_body.getPosition().y;	
		
		if( m_specialAbilitiesActivated.get(Characters.Belice) )
		{				
			Vector2 predictivePoint = Intercept( m_ship.m_body.getPosition(), target.m_body.getPosition(), target.m_body.getLinearVelocity(), m_projectileVelocity );
			if( predictivePoint.x != Float.NEGATIVE_INFINITY )
			{
				targetCenterX = predictivePoint.x;
				targetCenterY = predictivePoint.y;
			}
			accuracy = 0;
		}
		
		m_angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX) + accuracy;
		m_angleDegrees = (float) (m_angleRadians * 180 / Math.PI);
		m_objectSprite.rotate((float) Math.toDegrees(m_angleRadians));
		m_body.setFixedRotation(true);
		m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angleDegrees ) );
		float xSpeed =  (float)(m_projectileVelocity * Math.cos(m_angleRadians));
        float ySpeed =  (float)(m_projectileVelocity * Math.sin(m_angleRadians));
        
        //xSpeed = xSpeed + ( origin.m_body.getLinearVelocity().x * xSpeed > 0 ? origin.m_body.getLinearVelocity().x : 0);
        //ySpeed = ySpeed + ( origin.m_body.getLinearVelocity().y * ySpeed > 0 ? origin.m_body.getLinearVelocity().y : 0);
        m_body.setLinearVelocity( xSpeed, ySpeed );
	}
	
	@Override
	public void Draw( SpriteBatch renderer)
	{
		super.Draw(renderer);
		
		m_bulletLife--;
		if(m_bulletLife <= 0 )
		{
			m_integrity = 0;
		}	
		
		if( m_specialAbilitiesActivated.get(Characters.Gourt))
		{
			Vector2 targetPos = m_originalTarget.m_body.getPosition();
			Vector2 bulletPos = m_body.getPosition();
			double angleRadians = Math.atan2(targetPos.y - bulletPos.y, targetPos.x - bulletPos.x);
			float xForce = 0;
		    float yForce = 0;
		    xForce = (float)(30f * Math.cos(angleRadians));
	        yForce = (float)(30f * Math.sin(angleRadians));
	        m_body.applyForceToCenter(xForce, yForce, true);
		}
        
        m_angleRadians = Math.atan2(m_body.getLinearVelocity().y, m_body.getLinearVelocity().x);
        float degrees = (float) (m_angleRadians * 180 / Math.PI);
        float difference = degrees - m_angleDegrees;
        m_objectSprite.rotate( (float) (difference) );
        m_angleDegrees = (float) degrees;
        m_body.setTransform(m_body.getPosition(), (float) Math.toRadians( m_angleDegrees ) );
	}

	public void SetSpecials( EnumMap<Characters, Boolean> specialAbilitiesActivated )
	{
		m_specialAbilitiesActivated = specialAbilitiesActivated;
		
		if( m_specialAbilitiesActivated.get(Characters.Yashpal))
		{
			m_etherealBullet = true;
		}
		
		if( m_specialAbilitiesActivated.get(Characters.Sandy))
		{
			m_body.getFixtureList().get(0).setSensor(false);
			m_body.getFixtureList().get(0).setRestitution(1f);
			m_integrity++;
		}		
	}
	
	@Override
	public void damageIntegrity( ViewedCollidable damageOrigin, float damage , DamageType type)
	{
	}
	
	/**
	 * Return the firing solution for a projectile starting at 'src' with
	 * velocity 'v', to hit a target, 'dst'.
	 *
	 * @param Object src position of shooter
	 * @param Object dst position & velocity of target
	 * @param Number v   speed of projectile
	 * @return Object Coordinate at which to fire (and where intercept occurs)
	 *
	 * E.g.
	 * >>> intercept({x:2, y:4}, {x:5, y:7, vx: 2, vy:1}, 5)
	 * = {x: 8, y: 8.5}
	 */
	public Vector2 Intercept( Vector2 src, Vector2 dst, Vector2 dstVel, float projectileVelocity) 
	{
	  float tx = dst.x - src.x;
	  float ty = dst.y - src.y;
	  float tvx = dstVel.x;
	  float tvy = dstVel.y;

	  // Get quadratic equation components
	  float a = tvx*tvx + tvy*tvy - projectileVelocity*projectileVelocity;
	  float b = 2 * (tvx * tx + tvy * ty);
	  float c = tx*tx + ty*ty;    

	  // Solve quadratic
	  Vector2 ts = quad(a, b, c); // See quad(), below

	  // Find smallest positive solution
	  Vector2 sol = new Vector2();
	  sol.x = Float.NEGATIVE_INFINITY;
	  if (ts.x != Float.NEGATIVE_INFINITY ) 
	  {
	    float t0 = ts.x; 
	    float t1 = ts.y;
	    float t = Math.min(t0, t1);
	    if (t < 0)
	    {
	    	t = Math.max(t0, t1);  
	    }
	    
	    if (t > 0) 
	    {
	        sol.x = dst.x + dstVel.x*t;
	        sol.y = dst.y + dstVel.y*t;	      
	    }
	    
	  }

	  return sol;
	}


	/**
	 * Return solutions for quadratic
	 */
	public Vector2 quad( float a, float b, float c) 
	{
	  Vector2 sol = new Vector2();;
	  if (Math.abs(a) < 1e-6) 
	  {
	    if (Math.abs(b) < 1e-6) 
	    {
	    	if(Math.abs(c) < 1e-6)
	    	{
	    		sol.x = 0;
	    		sol.y = 0;
	    	}
	    	else
	    	{
	    		sol.x = Float.NEGATIVE_INFINITY;
	    		sol.y = Float.NEGATIVE_INFINITY;
	    	}
	    } 
	    else 
	    {
	      sol.x = -c/b;
	      sol.y = -c/b;
	    }
	  } 
	  else 
	  {
	    float disc = b*b - 4*a*c;
	    if (disc >= 0) 
	    {
	      disc = (float) Math.sqrt(disc);
	      a = 2*a;
	      sol.x = (-b-disc)/a;
	      sol.y = (-b+disc)/a;
	    }
	  }
	  return sol;
	}
}
