package Entities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import BodyLoaderPackage.BodyEditorLoader;
import Utilities.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class ViewedCollidable 
{
	public float m_objectXPosition;
	public float m_objectYPosition;
    public Texture m_objectAppearance;
    Sprite m_objectSprite;
    public Body m_body;
    public float m_angleDegrees = 0;
    public double m_angleRadians = 0;
    public float m_drawScale = 29f;
    public float m_integrity = 1000f;
    public float m_maxIntegrity = 1000f;
    ParticleEffect m_deathEffect = new ParticleEffect();
    ParticleEffectPool m_deathEffectPool;
    PooledEffect m_pooledDeathEffect;
    public int m_factionCode = 0;
    public World m_world;
    ArrayList<ViewedCollidable> m_aliveThings;
    float[] m_damageResistances = {1,1,1,1};
    float[] m_damageReductions = {0,0,0,0};
    public boolean m_ignoreForPathing = false;
    public boolean m_isTargetable = true;
    public static boolean m_inMenu = false;
    public float m_detectionRange = Float.MAX_VALUE;
    public float m_detectionRangeReset = 50f;
    int m_deathSoundIndex = -1;
    
    float m_organicMatter = 0;
    float m_rareMetals = 0;
    float m_water = 0;
    float m_dogeCoin = 0;
	public boolean m_untargetable = false;
	
	static Map m_textures = new HashMap();
    
    public enum DamageType 
    {
        Collision(0), 
        Penetration(1), 
        Energy(2), 
        Explosion(3);
        
        public int value;

        private DamageType(int value) 
        {
            this.value = value;
        }
        
     };  

     
	   
      public ViewedCollidable(String appearanceLocation, float collisionScale,	World world, float startX, float startY,ArrayList<ViewedCollidable> aliveThings, int factionCode)
	   {
		  m_world = world;
		  m_aliveThings = aliveThings;
		  m_factionCode = factionCode;
	      m_objectXPosition = startX*29f;
	      m_objectYPosition = startY*29f;
	      if( m_textures.containsKey(appearanceLocation) )
	      {
	    	  m_objectAppearance = (Texture) m_textures.get(appearanceLocation);
	      }
	      else
	      {
	    	  m_objectAppearance = new Texture(Gdx.files.internal("data/"+appearanceLocation+ "/"+appearanceLocation+".png"));
	    	  m_textures.put(appearanceLocation, m_objectAppearance);
	      }
	      //m_objectAppearance = new Texture(Gdx.files.internal("data/"+appearanceLocation+ "/"+appearanceLocation+".png"));
	      m_objectSprite = new Sprite( m_objectAppearance );
	      // First we create a body definition
	      BodyDef bodyDef = new BodyDef();
	      // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
	      bodyDef.type = BodyType.DynamicBody;
	      // Set our body's starting position in the world
	      bodyDef.position.set(startX, startY);

	      // Create our body in the world using our body definition
	      m_body = world.createBody(bodyDef);

	      // Create a circle shape and set its radius to 6
	      CircleShape circle = new CircleShape();
	      float radius = Math.max(m_objectAppearance.getWidth() / 2 / 29f, m_objectAppearance.getHeight() / 2 / 29f ) ;
	      circle.setRadius(radius);

	      // Create a fixture definition to apply our shape to
	      FixtureDef fixtureDef = new FixtureDef();
	      fixtureDef.density = 0.1f; 
	      fixtureDef.friction = 0f;
	      fixtureDef.restitution = 0.1f; // Make it bounce a little bit
	      fixtureDef.filter.groupIndex = (short) (m_factionCode * -1);
	      
	      if( collisionScale == 0 )
	      {
	    	  fixtureDef.shape = circle;
	    	// Create our fixture and attach it to the body
		      Fixture fixture = m_body.createFixture(fixtureDef);
	      }
	      else
	      {
	    	  BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("data/"+appearanceLocation+"/"+appearanceLocation+ ".json"));
	    	  loader.attachFixture(m_body, "Name", fixtureDef, collisionScale);
	      }
	      
	      // Remember to dispose of any shapes after you're done with them!
	      // BodyDef and FixtureDef don't need disposing, but shapes do.
	      circle.dispose();
	      aliveThings.add(this);
	   }

	public void Draw( SpriteBatch renderer )
	   {
		   m_objectXPosition = m_body.getPosition().x*29f;
		   m_objectYPosition = m_body.getPosition().y*29f;

		   m_objectSprite.setPosition(m_objectXPosition - m_objectAppearance.getWidth() / 2, m_objectYPosition - m_objectAppearance.getHeight() / 2 );
		   m_objectSprite.draw( renderer );
		   
		   if( m_integrity <= 0 )
		   {
			   m_pooledDeathEffect.setPosition(m_objectXPosition, m_objectYPosition);
			   m_pooledDeathEffect.draw(renderer, 1f/60f );
			   if( m_deathSoundIndex != -1 )
			   {
				   AudioManager.PlaySound(m_deathSoundIndex, false, this );
				   m_deathSoundIndex = -1;
			   }
		   }		   
	   }
	   
	   void Setlocation( float x, float y )
	   {
	      m_objectXPosition = x;
	      m_objectYPosition = y;
	   }

	   public abstract void damageCalc(ViewedCollidable object2, float crashVelocity);
	   
	   public void damageIntegrity(ViewedCollidable damageOrigin, float damage, DamageType type )
	   {
		   damageIntegrity( damageOrigin, damage, type, false, false, false );
	   }
	   
	   public void damageIntegrity( ViewedCollidable damageOrigin, float damage, DamageType type, boolean bypassShieldResistances, boolean bypassShields, boolean bypassResistances )
	   {
		   if( !bypassResistances )
		   {
			   damage = damage * m_damageResistances[type.value];
			   damage = damage > m_damageReductions[type.value] ? damage - m_damageReductions[type.value] : 0;
		   }
		   
		   m_integrity -= damage;
	   }

	public boolean deathThroesDone() 
	{
		boolean dead = true;
		
		if( m_pooledDeathEffect != null )
		{
			dead = m_pooledDeathEffect.isComplete(); 
		}

		if( dead && m_pooledDeathEffect != null )
		{
			m_pooledDeathEffect.free();
		}
		
		return dead;
	}

	public void destroy()
	{
	}
}
