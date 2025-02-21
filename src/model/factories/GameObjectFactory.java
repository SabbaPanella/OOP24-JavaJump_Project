package model.factories;

import model.entities.Character;
import model.entities.Platform;
import model.entities.Coin;

import java.util.Random;

public class GameObjectFactory extends AbstractGameObjectFactory
{
	@Override
	public Character createCharacter(float x, float y)
	{float width = 30;
		float height = 40;
		float jumpForce = 250;return new Character(x, y, width, height, jumpForce);
	}

	@Override
	public Platform createStandardPlatform(float x, float y)
	{float width = 100;
		float height = 10;return new Platform(x, y, width, height);
	}

	@Override
	public Platform createRandomPlatform(float x, float y)
	{
		Random rand = new Random();
		float width = 80 + rand.nextInt(41);
		float height = 10;return new Platform(x, y, width, height);
	}

	@Override
	public Coin createCoin(float x, float y)
	{float width = 20;
		float height = 20;return new Coin(x, y, width, height);
	}
}
