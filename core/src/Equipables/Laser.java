package Equipables;

import java.util.ArrayList;
import java.util.EnumMap;

import Utilities.AllinPathRaycast;
import Utilities.LaserRayCastBase;
import Utilities.LeastDistantRaycast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import Entities.EnemyShip;
import Entities.Projectile;
import Entities.Ship;
import Entities.ViewedCollidable;
import Entities.Projectile.Characters;
import Entities.ViewedCollidable.DamageType;

public class Laser extends CounterMeasure implements QueryCallback
{
	Texture m_laserMidBackground;
	Texture m_laserMidForeground;
	Sprite m_laserMidBackgroundSprite;
	Sprite m_laserMidForegroundSprite;
	
	Texture m_laserEndBackground;
	Texture m_laserEndForeground;
	Sprite m_laserEndBackgroundSprite;
	Sprite m_laserEndForegroundSprite;
	
	float m_chargeUpCounter = 0;
	float m_chargedDuration = 60;
	float m_chargeUpCriticalMass = 300;
	ArrayList<ViewedCollidable> m_targets = new ArrayList<ViewedCollidable>();
	boolean m_superAbilityActivated = false;
	ViewedCollidable m_currentTargetBeingDamaged;
	
	public Laser(World w, Ship s, ArrayList<ViewedCollidable> aliveThings )
	{
		super(w, s, aliveThings,  new Image( new Texture(Gdx.files.internal("data/lasericon.png") ) ) );
		// TODO Auto-generated constructor stub
		m_rangeEnablersAndMultipliers[1] = 2f;
		m_laserMidBackground = new Texture(Gdx.files.internal("data/laser.png"));
		m_laserMidBackgroundSprite = new Sprite( m_laserMidBackground );
		m_laserMidForeground = new Texture(Gdx.files.internal("data/laserOverlayStatic.png"));
		m_laserMidForegroundSprite = new Sprite( m_laserMidForeground );
		
		m_laserEndBackground = new Texture(Gdx.files.internal("data/start2.png"));
		m_laserEndBackgroundSprite = new Sprite( m_laserEndBackground );
		
		m_laserEndForeground = new Texture(Gdx.files.internal("data/start2Over.png"));
		m_laserEndForegroundSprite = new Sprite( m_laserEndForeground );
	}
	
	@Override
	public Image GetImageCopy()
	{
		return new Image( new Texture(Gdx.files.internal("data/lasericon.png") ) );
	}

	@Override
	public void AcquireAndFire( SpriteBatch renderer )
	{
		SetupTargetsList();
		
		if( m_targets.size() > 0 )
		{
			for( int i = 0; i < m_targets.size(); i++ )
			{
				m_target = m_targets.get(i);
				float distanceToCurrentTarget = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition() );
				
				if( distanceToCurrentTarget <= m_range )
				{
					float objectXPosition = m_ship.m_objectXPosition;// - m_ship.m_objectAppearance.getWidth() / 2;
					float objectYPosition = m_ship.m_objectYPosition;// - m_ship.m_objectAppearance.getHeight() / 2 ;
					
					//Resolve who the laser hit and where it impacted
					LaserRayCastBase lrcb;
					Vector2 targetPositionModded = new Vector2( m_target.m_body.getPosition() );
					if( m_specialAbilitiesActivated.get(Characters.Sandy) )
					{
						lrcb = new AllinPathRaycast(m_ship.m_body, m_range );
						float centerX = m_ship.m_body.getPosition().x;
						float centerY = m_ship.m_body.getPosition().y;
						float targetCenterX = m_target.m_body.getPosition().x;
						float targetCenterY = m_target.m_body.getPosition().y;
						double angleRadians = Math.atan2(-centerY + targetCenterY,-centerX + targetCenterX);
						
						targetPositionModded.x += m_range * Math.cos(angleRadians);
						targetPositionModded.y += m_range * Math.sin(angleRadians);
					}
					else
					{
						lrcb = new LeastDistantRaycast(m_ship.m_body);
					}
					m_world.rayCast(lrcb, m_ship.m_body.getPosition(), targetPositionModded );
					
					float distanceLaserTraveled = lrcb.GetDistanceTraveled();
					
					SetupLaserSpritePositions(objectXPosition, objectYPosition,	distanceLaserTraveled);				
					SetupSpriteRotations();
					
					
					
					renderer.end();    // actual drawing is done on end(); if we do not end, we contaminate previous rendering.
					renderer.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
					renderer.begin();
	
					m_laserMidBackgroundSprite.draw(renderer);	
					m_laserEndBackgroundSprite.draw(renderer);	
					
					ArrayList<ViewedCollidable> hitEntities = lrcb.GetEntitiesHit();
					for(int j = 0; j< hitEntities.size(); j++ )
					{
						
						float targetShields = 0;
						if( Ship.class.isInstance(hitEntities.get(j)) )
						{
							Ship ship = (Ship) hitEntities.get(j);
							targetShields = ship.m_shieldIntegrity;
						}
						
						if( !BobbiLaserMicrowave(hitEntities.get(j)) )
						{
							ViewedCollidable vc = hitEntities.get(j);
									
							if( vc != null )
							{
								vc.damageIntegrity( m_ship, 1f, DamageType.Energy);
							}
							else
							{
								int iv = 9;
							}
						}
						
						YashpalLaserSpecial(hitEntities, j, targetShields);
						
						SSidHackingLaser(hitEntities, j);
						
						NoelLaserHack(hitEntities, j);
					}
					
					if( m_chargeUpCounter > m_chargeUpCriticalMass )
					{
						m_laserMidForegroundSprite.draw(renderer);
						m_laserEndForegroundSprite.draw(renderer);
						
						for(int k = 0; k< hitEntities.size(); k++ )
						{
							ViewedCollidable vc = hitEntities.get(k);
							
							if( vc != null )
							{
								vc.damageIntegrity(m_ship, 5f, DamageType.Energy);
														
								GourtLaserSpecial(hitEntities, k);
							
								BeliceLaserSpecial(hitEntities, k);
							}
						}					
						
						if( m_chargeUpCounter > ( m_chargeUpCriticalMass + m_chargedDuration ) )
						{
							m_chargeUpCounter = 0;
							if( m_chargedDuration > 60 )
							{
								m_chargedDuration = 60;
								DisengageCM();
							}							
						}
					}
					
					m_chargeUpCounter++;
				}
				else
				{
					m_target = null;
				}
			}
		}
		
	}

	private void YashpalLaserSpecial(ArrayList<ViewedCollidable> hitEntities,
			int j, float targetShields)
	{
		// TODO optimize this cast I've already done it
		if( m_specialAbilitiesActivated.get(Characters.Yashpal ) &&
				Ship.class.isInstance(hitEntities.get(j)) )
		{
			Ship ship = (Ship) hitEntities.get(j);
			
			if( targetShields == ship.m_shieldIntegrity && ship.m_shieldIntegrity > 0 )
			{
				m_ship.m_shieldIntegrity+= 2;
			}														
		}
	}

	private void BeliceLaserSpecial(ArrayList<ViewedCollidable> hitEntities,
			int k)
	{
		// TODO Optimize out the array
		if( m_specialAbilitiesActivated.get(Characters.Belice ) &&
			Ship.class.isInstance(hitEntities.get(k)) )
		{
			Ship s = (Ship) hitEntities.get(k);
			if(s.m_shieldIntegrityRechargeFactor > 0)
			{
				s.m_shieldIntegrityRechargeFactor*= -3;
			}
		}
	}

	private void SSidHackingLaser(ArrayList<ViewedCollidable> hitEntities, int j)
	{
		// TODO optimize out the arraylist
		if( m_specialAbilitiesActivated.get(Characters.SSid) &&
				EnemyShip.class.isInstance(hitEntities.get(j)) )
		{
			EnemyShip s = (EnemyShip) hitEntities.get(j);
			if( s.AttemptHack( .35f ) )
			{
				s.m_soundTheAlarmCounter -=5;
			}
		}
	}

	private boolean BobbiLaserMicrowave(ViewedCollidable vc )
	{
		boolean activated = false;
		if( m_specialAbilitiesActivated.get(Characters.Bobbi) &&
				Ship.class.isInstance(vc) )
		{
			Ship s = (Ship) vc;
			if( s.m_shieldIntegrity > 0 )
			{
				s.damageIntegrity( m_ship, 1f, DamageType.Energy, true, true, true );
				activated = true;
			}
		}
		return activated;
	}

	private void NoelLaserHack(ArrayList<ViewedCollidable> hitEntities, int j)
	{
		// TODO Optimize out the array
		if( m_specialAbilitiesActivated.get(Characters.Noel) &&
				EnemyShip.class.isInstance(hitEntities.get(j)) )
		{
			EnemyShip s = (EnemyShip) hitEntities.get(j);
			
			if( s.AttemptHack( .1f ))
			{
				s.m_navigatingTo.x = -1;
				s.DisengageCurrentTarget();
			}
		}
	}

	private void GourtLaserSpecial(ArrayList<ViewedCollidable> hitEntities,
			int k)
	{
		// TODO Optimize out the array
		if( m_specialAbilitiesActivated.get(Characters.Gourt) &&
			Ship.class.isInstance(hitEntities.get(k)) )
		{
			Ship s = (Ship) hitEntities.get(k);
			
			if( s.m_shieldIntegrity > 0 )
			{			
				m_currentTargetBeingDamaged = s;
				m_world.QueryAABB(this, m_ship.m_body.getPosition().x - m_range / 1, m_ship.m_body.getPosition().y - m_range / 1, m_ship.m_body.getPosition().x + m_range / 1, m_ship.m_body.getPosition().y + m_range / 1 );
			}
		}
	}

	private void SetupTargetsList()
	{
		m_targets.clear();
		if( m_superAbilityActivated && m_specialAbilitiesActivated.get(Characters.Shavret) )
		{
			for( int i = 0; i < m_ship.m_trackedHostileTargets.size(); i++ )
			{
				ViewedCollidable vc = m_ship.m_trackedHostileTargets.get(i);
				float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range )
				{
					m_targets.add( vc );
				}
			}
		}
		else
		{			
			// Pull the closest tracked target from the ship computer! 			
			float leastDistance = Float.MAX_VALUE;
			for( int i = 0; i < m_ship.m_trackedHostileTargets.size(); i++ )
			{
				ViewedCollidable vc = m_ship.m_trackedHostileTargets.get(i);
				float distance = vc.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range && 
				    ( distance < leastDistance ) )
				{
					m_targets.clear();
					leastDistance = distance;
					m_targets.add( vc );
				}
			}	
			
			if( m_target != null )
			{
				float distance = m_target.m_body.getPosition().dst(m_ship.m_body.getPosition());
				if( distance <= m_range && m_target.m_integrity > 0 )
				{
					m_targets.clear();
					m_targets.add( m_target );
				}
				else
				{
					m_target = null;
				}
			}
		}
		
	}

	private void SetupSpriteRotations()
	{
		float centerX = m_ship.m_body.getPosition().x;
		float centerY = m_ship.m_body.getPosition().y;
		float targetCenterX = m_target.m_body.getPosition().x;
		float targetCenterY = m_target.m_body.getPosition().y;
		double angleRadians = Math.atan2(-centerY + targetCenterY,-centerX + targetCenterX);
		m_laserMidBackgroundSprite.setRotation((float) Math.toDegrees(angleRadians));
		m_laserMidForegroundSprite.setRotation((float) Math.toDegrees(angleRadians));
		m_laserEndBackgroundSprite.setRotation((float) Math.toDegrees(angleRadians));
		m_laserEndForegroundSprite.setRotation((float) Math.toDegrees(angleRadians));
	}

	private void SetupLaserSpritePositions(float objectXPosition,
			float objectYPosition, float distanceLaserTraveled)
	{
		m_laserMidBackgroundSprite.setPosition(objectXPosition ,objectYPosition );
		m_laserMidBackgroundSprite.setOrigin(0, m_laserMidBackgroundSprite.getHeight()/2 );
		m_laserMidBackgroundSprite.setSize(29f*distanceLaserTraveled, m_laserMidBackgroundSprite.getHeight() );
		m_laserMidBackgroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass / 2 );
		
		m_laserMidForegroundSprite.setPosition(objectXPosition ,objectYPosition );
		m_laserMidForegroundSprite.setOrigin(0, m_laserMidForegroundSprite.getHeight()/2 );
		m_laserMidForegroundSprite.setSize(29f*distanceLaserTraveled, m_laserMidForegroundSprite.getHeight() );
		m_laserMidForegroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
		
		m_laserEndBackgroundSprite.setPosition(objectXPosition,objectYPosition );
		m_laserEndBackgroundSprite.setOrigin(0, m_laserEndBackgroundSprite.getHeight()/2 );
		m_laserEndBackgroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
		
		m_laserEndForegroundSprite.setPosition(objectXPosition ,objectYPosition );
		m_laserEndForegroundSprite.setOrigin(0, m_laserEndForegroundSprite.getHeight()/2 );
		m_laserEndForegroundSprite.setScale( 1f,m_chargeUpCounter / m_chargeUpCriticalMass );
	}

	@Override
	public void EngageCM( Button b )
	{
		super.EngageCM(b);
		m_chargedDuration = 600;
		m_superAbilityActivated = true;
	}

	@Override
	public void DisengageCM()
	{
		super.DisengageCM();
		m_superAbilityActivated = false;
	}

	@Override
	public boolean reportFixture(Fixture fixture)
	{
		ViewedCollidable vc = (ViewedCollidable) fixture.getBody().getUserData();
		if( vc.m_factionCode != m_ship.m_factionCode &&
			vc != m_currentTargetBeingDamaged )
		{
			float centerX = m_currentTargetBeingDamaged.m_body.getPosition().x;
			float centerY = m_currentTargetBeingDamaged.m_body.getPosition().y;
			float targetCenterX = fixture.getBody().getPosition().x;
			float targetCenterY = fixture.getBody().getPosition().y;
			double angleRadians = Math.atan2(-centerY + targetCenterY,-centerX + targetCenterX);
			float forceX = (float) (-9000 * Math.cos(angleRadians));
			float forceY = (float) (-9000 * Math.sin(angleRadians));
			fixture.getBody().applyForceToCenter(forceX, forceY, true);
		}
		return true;
	}

}
