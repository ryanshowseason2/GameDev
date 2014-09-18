package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import Entities.PlayerEntity;
import Entities.ViewedCollidable;

public class EnemyIndicatorButton extends Button
{
	ViewedCollidable m_trackedEntity;
	
	public EnemyIndicatorButton( ViewedCollidable trackedEntity, PlayerEntity p )
	{
		super( new Image( new TextureRegion( new Texture(Gdx.files.internal("data/targeting.png")))), new Skin(Gdx.files.internal("data/transskin.json")) );
		pad(10);
		m_trackedEntity = trackedEntity;
		addListener( new TargetButtonListener( p ) );
	}
}
