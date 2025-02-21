import model.GameModel;
import model.collision.CollisionManager;
import model.factories.AbstractGameObjectFactory;
import model.factories.GameObjectFactory;
import model.level.SpawnManager;
import model.physics.PhysicsManager;
import model.score.ScoreManager;
import model.camera.CameraManager;

public class Main
{
	public static void main(String[] args)
	{
		int screenWidth = 800;
		int screenHeight = 600;


		AbstractGameObjectFactory factory = new GameObjectFactory();


		CollisionManager collisionManager = new CollisionManager();


		SpawnManager spawnManager = new SpawnManager(factory);


		ScoreManager scoreManager = new ScoreManager();


		CameraManager cameraManager = new CameraManager(scoreManager, 0.5f);


		PhysicsManager physicsManager = new PhysicsManager(0.5f, 5.0f, 0.5f);


		GameModel model = new GameModel(screenWidth, screenHeight, factory,
										collisionManager, physicsManager,spawnManager,
										scoreManager, cameraManager);

		model.startGame();


	}
}