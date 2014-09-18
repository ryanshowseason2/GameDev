package Entities;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import Entities.Projectile.Characters;
import Entities.ViewedCollidable.DamageType;

public class WingBlade extends ViewedCollidable
{
	public enum Placement
	{
		Left,
		Right,
		Front
	}
	public boolean m_activated = false;
	public Ship m_ship;
	Placement m_placement = Placement.Left;
	ArrayList<Vector2>  m_contactPoints = new ArrayList<Vector2>();
	
	ParticleEffect m_saberHitEffect = new ParticleEffect();
    ParticleEffectPool m_saberHitEffectPool;
    PooledEffect m_pooledSaberHitEffect;
    public float m_extraDamage = 100;
    
    public EnumMap<Characters, Boolean> m_specialAbilitiesActivated = new EnumMap<Characters, Boolean>(Characters.class);
	
	public WingBlade(String appearanceLocation,
			World world, float startX, float startY,
			ArrayList<ViewedCollidable> aliveThings, int factionCode, Ship s)
	{
		super(appearanceLocation, 5f, world, startX, startY,
				aliveThings, factionCode);
		m_body.setUserData(this);
		m_ignoreForPathing = true;
		
		//m_objectSprite.setOrigin(0, m_objectSprite.getHeight()/2);
		m_objectSprite.setPosition(startX * 29f, startY* 29f);
		m_ship = s;
		
		m_saberHitEffect.load(Gdx.files.internal("data/saberhit.p"), Gdx.files.internal("data/"));
		m_saberHitEffectPool = new ParticleEffectPool(m_saberHitEffect, 1, 2);
		m_pooledSaberHitEffect = m_saberHitEffectPool.obtain();
	}

	@Override
	public void damageCalc(ViewedCollidable object2, float crashVelocity)
	{
		// TODO Auto-generated method stub
		if( m_activated)
		{
			
		}
	}

	@Override
	public void damageIntegrity( ViewedCollidable damageOrigin, float damage, DamageType type, boolean bypassShieldResistances, boolean bypassShields, boolean bypassResistances )
    {
    }
	
	public void Draw( SpriteBatch renderer )
    {
		if(m_activated)
		{
			Vector2 tmp = new Vector2();
			if( m_placement == Placement.Right )
			{
				tmp.x = m_ship.m_body.getPosition().x + (float) (2.583112706 * Math.cos( m_ship.m_angleRadians - Math.PI/2 + Math.PI/12));
				tmp.y = m_ship.m_body.getPosition().y + (float) (2.583112706 * Math.sin( m_ship.m_angleRadians - Math.PI/2 + Math.PI/12));
				m_body.setTransform(tmp , (float) (m_ship.m_angleRadians - Math.PI/2 + Math.PI/12) );
			}
			else if( m_placement == Placement.Left )
			{
				tmp.x = m_ship.m_body.getPosition().x + (float) (2.583112706 * Math.cos( m_ship.m_angleRadians + Math.PI/2 - Math.PI/12));
				tmp.y = m_ship.m_body.getPosition().y + (float) (2.583112706 * Math.sin( m_ship.m_angleRadians + Math.PI/2 - Math.PI/12));
				m_body.setTransform(tmp , (float) (m_ship.m_angleRadians + Math.PI/2 - Math.PI/12) );
			}
			else
			{
				tmp.x = m_ship.m_body.getPosition().x + (float) (1* Math.cos( m_ship.m_angleRadians));
				tmp.y = m_ship.m_body.getPosition().y + (float) (1 * Math.sin( m_ship.m_angleRadians));
				m_body.setTransform(tmp , (float) (m_ship.m_angleRadians ) );
			}
			
			
			float degrees = (float) Math.toDegrees( m_body.getAngle() );
			m_objectSprite.setRotation(degrees);
			
			if( m_placement == Placement.Front )
			{
				m_objectSprite.setPosition((m_body.getPosition().x + (float) (1* Math.cos( m_ship.m_angleRadians))) *29f - m_objectAppearance.getWidth() / 2, (m_body.getPosition().y + (float) (1* Math.sin( m_ship.m_angleRadians))) *29f - m_objectAppearance.getHeight() / 2   );
				m_objectSprite.setScale(1.1f,1f);
			}
			else
			{
				m_objectSprite.setPosition(m_body.getPosition().x *29f - m_objectAppearance.getWidth() / 2, m_body.getPosition().y *29f - m_objectAppearance.getHeight() / 2   );
				m_objectSprite.setScale(.5f);
			}
		   
		   m_objectSprite.draw( renderer );
		   
		   for( int i = 0; i < m_contactPoints.size(); i++ )
	   	   {
			   Vector2 contactPoint = m_contactPoints.get(i);
			   m_pooledSaberHitEffect.setPosition( contactPoint.x * 29f, contactPoint.y * 29f );
			   m_pooledSaberHitEffect.draw(renderer, 1f/60f);
	   	   }
	   	   m_contactPoints.clear();
		   
		   if( m_integrity <= 0 )
		   {
			   m_pooledDeathEffect.setPosition(m_objectXPosition, m_objectYPosition);
			   m_pooledDeathEffect.draw(renderer, 1f/60f );   
		   }
		   
		   m_extraDamage*=2;
		   if( m_extraDamage > 100 )
		   {
			   m_extraDamage = 100;
		   }
		}
    }
	
	public void HandleContact( Contact contact )
	{
		if( m_activated )
		{
			ExtractContactPoints(contact.getFixtureA() );
			ExtractContactPoints(contact.getFixtureB() );
			ApplyDamage(contact.getFixtureA());
			ApplyDamage(contact.getFixtureB());
		}
	}

	private void ApplyDamage( Fixture fixture)
	{
		if( !WingBlade.class.isInstance( fixture.getBody().getUserData()) &&
			!PlayerEntity.class.isInstance( fixture.getBody().getUserData()))
		{
			ViewedCollidable vc = (ViewedCollidable) fixture.getBody().getUserData();
			boolean directDamage = m_specialAbilitiesActivated.get(Characters.Yashpal);
			vc.damageIntegrity( m_ship, .25f, DamageType.Energy, true, directDamage, directDamage );
			vc.damageIntegrity( m_ship, m_extraDamage, DamageType.Energy );
			
			if( m_placement == Placement.Front )
			{
				vc.damageIntegrity( m_ship, .25f, DamageType.Energy, true, directDamage, directDamage );
				vc.damageIntegrity( m_ship, .25f, DamageType.Energy, true, directDamage, directDamage );
				vc.damageIntegrity( m_ship, m_extraDamage, DamageType.Energy );
			}
			m_extraDamage*=.6f;
			
			ShavretBladeSpecial(vc);
			
			if( m_specialAbilitiesActivated.get(Characters.Gourt) &&
					Ship.class.isInstance(vc) )
				{
					Ship ship = (Ship) vc;
					ship.AddOverTimeEffect( new RunawayStarSling(300, ship));
					
				}
			
			if( m_specialAbilitiesActivated.get(Characters.Sandy) &&
					Ship.class.isInstance(vc) )
				{
					Ship ship = (Ship) vc;
					if(ship.me.m_boostJuice > 0)
					{
						ship.damageIntegrity( m_ship, ship.me.m_boostJuice, DamageType.Energy);
						ship.me.m_boostJuice-=ship.me.m_boostJuice;
					}
					
				}
			
			if( m_specialAbilitiesActivated.get(Characters.Bobbi) &&
					Ship.class.isInstance(vc) )
			{
				Ship ship = (Ship) vc;
				ship.AddOverTimeEffect( new EngineBrakeFailure(700, ship));				
			}
			
			if( m_specialAbilitiesActivated.get(Characters.Noel) &&
					Ship.class.isInstance(vc) )
			{
				Ship ship = (Ship) vc;
				if( ship.AttemptHack(.01f) && ship.m_dogeCoin > 0 )
				{
					m_ship.m_dogeCoin+= 1;
					ship.m_dogeCoin-=1;
				}
			}
		}
	}

	private void ShavretBladeSpecial(ViewedCollidable vc)
	{
		if( m_specialAbilitiesActivated.get(Characters.Shavret) &&
			!PlayerEntity.class.isInstance(vc) )
		{
			float centerX = m_ship.m_body.getPosition().x;
			float centerY = m_ship.m_body.getPosition().y;
			float targetCenterX = vc.m_body.getPosition().x;
			float targetCenterY = vc.m_body.getPosition().y;
			double angleRadians = Math.atan2(centerY - targetCenterY,centerX - targetCenterX);
			float xForce =  (float)( -m_extraDamage*10 * Math.cos(angleRadians));
		    float yForce =  (float)( -m_extraDamage*10 * Math.sin(angleRadians));
		    vc.m_body.applyLinearImpulse(xForce, yForce, vc.m_body.getPosition().x, vc.m_body.getPosition().y, true);
		}
	}
	
	private void ExtractContactPoints(Fixture fixture )
	{
		if( PolygonShape.class.isInstance( fixture.getShape()) )
		{
			PolygonShape poly = (PolygonShape) fixture.getShape();
			for( int i = 0; i< poly.getVertexCount();i++)
			{
				Vector2 v = new Vector2();
				poly.getVertex(i, v);
				v = fixture.getBody().getWorldPoint(v);
				m_contactPoints.add( v );
			}
		}
	}
}
