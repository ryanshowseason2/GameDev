package Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import Entities.Asteroid;
import Entities.Asteroid.AsteroidSizeClass;
import Entities.Asteroid.AsteroidTypes;
import Entities.ViewedCollidable;

public class ClutterSpawner
{
	World m_world;
	ArrayList<ViewedCollidable> m_aliveThings;
	
	public ClutterSpawner( World w, ArrayList<ViewedCollidable> aliveThings)
	{
		m_world = w;
		m_aliveThings = aliveThings;
	}
	
	public void SpawnAsteroids( AsteroidTypes t, AsteroidSizeClass s , float x, float y, float radius, int amount )
	{
		for( int i = 0; i < amount; i++ )
		{
			float xrandom = (float) ((Math.random() - .5) * 2 * radius);
			float yrandom = (float) ((Math.random() - .5) * 2 * radius);
			Asteroid a = new Asteroid( RetrieveAsteroidString( t, s), RetrieveAsteroidCollisionSize(t, s), RetrieveAsteroidDrawSize( t, s ), m_world, x+ xrandom, y+yrandom, m_aliveThings );
			a.SetAsteroidTypeAndSize(t,s);
		}
	}
	
	public void SpawnAsteroidsFromImage( String imageLocation, float pixelScale, float radius, int amount )
	{
		Pixmap spawnMap = new Pixmap(Gdx.files.internal(imageLocation));
		float halfWidth = spawnMap.getWidth() /2;
		float halfHeight = spawnMap.getHeight() /2;
		Random rand = new Random();
		
		for( int i = spawnMap.getWidth()-1; i > 0; i--)
		{
			for( int j = spawnMap.getHeight()-1; j > 0; j-- )
			{
				Color c = new Color( spawnMap.getPixel(i, j));
				
				if( Math.random() < c.a )
				{
					float zeroTo255Scale = c.r*255;
					//if( zeroTo255Scale == 1 )
					{
						SpawnAsteroids( randomType(), randomSize(), (halfWidth - i)*pixelScale, (halfHeight - j)*pixelScale, radius, rand.nextInt(amount)+1 );
					}
				}
				
			}
		}
		spawnMap.dispose();
	}
	
	private static String RetrieveAsteroidString(AsteroidTypes t, AsteroidSizeClass s)
	{
		String size = "";
		String type = "";
		
		switch( t )
		{
			case PlainType:
				type = "asteroid";
				break;
			case Explosive:
				type = "explosive";
				break;
			case Rock:
				type = "rock";
				break;
			case Rock2:
				type = "rock2";
				break;
			case Hive:
				type = "hive";
				break;
			case Hive2:
				type = "hive2";
				break;
			case Lined:
				type = "lined";
				break;
			case Lined2:
				type = "lined2";
				break;
			case Lesion:
				type = "lesion";
				break;
			case Lesion2:
				type = "lesion2";
				break;
			case Comet:
				type = "comet";
				break;
			case Comet2:
				type = "comet2";
				break;
		}
		
		if( t != AsteroidTypes.PlainType )
		{
			switch( s )
			{
				case Chunk:
					size = "chunk";
					break;
				case Full:
					size = "full";
					break;
				case Round:
					size = "round";
					break;
				case Shard:
					size = "shard";
					break;
			}
		}
		
		String combined = size+type;
		return combined;
	}
	
	private static float RetrieveAsteroidDrawSize( AsteroidTypes t, AsteroidSizeClass s)
	{
		float size = 1.0f;
		
		if( t == AsteroidTypes.PlainType )
		{
			size = .1f;
		}
		else
		{
			switch( s )
			{
				case PlainSize:
					size = .1f;
					break;
				case Chunk:
					size = .4f;
					break;
				case Full:
					size = .6f;
					break;
				case Round:
					size = .5f;
					break;
				case Shard:
					size = .4f;
					break;
			}
		}
		
		return size;
	}
	
	private static float RetrieveAsteroidCollisionSize( AsteroidTypes t, AsteroidSizeClass s)
	{
		float size = 1.0f;
		
		if( t == AsteroidTypes.PlainType )
		{
			// The size is predetermined!
			size = 4.5f;
		}
		else
		{
			switch( s )
			{
				case PlainSize:
					size = 4.5f;
					break;
				case Chunk:
					size = 1.4f;
					break;
				case Full:
					size = 2.5f;
					break;
				case Round:
					size = 1.8f;
					break;
				case Shard:
					size = 1.0f;
					break;
			}
		}
		
		return size;
	}
	
	private static final List<AsteroidTypes> classVALUES = Collections.unmodifiableList(Arrays.asList(AsteroidTypes.values()));
	private static final int classSIZE = classVALUES.size();
	private static final Random RANDOM = new Random();

	public static AsteroidTypes randomType()  
	{
	    return classVALUES.get(RANDOM.nextInt(classSIZE));
	}
		  
	private static final List<AsteroidSizeClass> sizeVALUES = Collections.unmodifiableList(Arrays.asList(AsteroidSizeClass.values()));
	private static final int sizeSIZE = sizeVALUES.size();

	public static AsteroidSizeClass randomSize()  
	{
	    return sizeVALUES.get(RANDOM.nextInt(sizeSIZE-1)+1);
	} 
}
