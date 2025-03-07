package model.camera;

import model.GameModel;
import model.entities.character.Character;
import model.score.ScoreManager;


public class CameraManager {
	private float currentOffset;
	private float previousOffset;
	private final ScoreManager scoreManager;
	private final float scoreFactor;

	public CameraManager(ScoreManager scoreManager, float scoreFactor) {
		this.scoreManager = scoreManager;
		this.scoreFactor = scoreFactor;
		this.currentOffset = 0;
		this.previousOffset = 0;
	}

	
	public void cameraUpdate(GameModel model, float deltaTime) {
		Character player = model.getPlayer();
		float screenHeight = model.getScreenHeight();
		float desiredOffset = getDesiredOffset(screenHeight, player);

		if (currentOffset < previousOffset) {
			float deltaOffset = previousOffset - currentOffset;
			int points = (int)(deltaOffset * scoreFactor);
			scoreManager.addPoints(points);
		}
		previousOffset = currentOffset;
		currentOffset = desiredOffset;
	}

	private float getDesiredOffset(float screenHeight, Character player) {
		float progressionScreenPoint = screenHeight / 2f - screenHeight * 0.05f;
		float desiredOffset = currentOffset;

		if (player.getY() < progressionScreenPoint - currentOffset) {
			desiredOffset = player.getY() - progressionScreenPoint;
		}

		if (desiredOffset > currentOffset) {
			desiredOffset = currentOffset;
		}
		return desiredOffset;
	}

	public void cameraReset() {
		this.currentOffset = 0;
		this.previousOffset = 0;
	}

	public float getCurrentOffset() {
		return currentOffset;
	}
}
