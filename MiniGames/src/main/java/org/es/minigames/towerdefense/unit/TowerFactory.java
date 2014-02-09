package org.es.minigames.towerdefense.unit;

import android.content.res.Resources;
import android.graphics.Rect;

import org.es.gameengine.drawable.Animation;
import org.es.gameengine.drawable.GenericSprite;
import org.es.gameengine.drawable.Sprite;
import org.es.gameengine.drawable.SpriteSheet;
import org.es.gameengine.drawable.SpriteSheetAnimation;
import org.es.minigames.R;

import java.util.EnumMap;

/**
 * A factory to build Towers.
 * Created by Cyril Leroux on 02/02/14.
 */
public class TowerFactory {

    private static final int RES_BASIC_TOWER_SS = R.drawable.sprite_sheet_tower_1;
    private static final int BASIC_TOWER_HEALTH = 200;
    private static final int BASIC_TOWER_DAMAGE = 20;
    private static final int BASIC_TOWER_ATTACK_RANGE = 3;
    private static final int BASIC_TOWER_ATTACK_DELAY = 1000;
    private static final int BASIC_TOWER_WEIGHT = 2;

    public static Tower createTower(Tower.Type type, Resources resources) {

        switch (type) {
            case BASIC:
                return createBasicTower(resources);
            default:
                return null;
        }
    }

    private static Tower createBasicTower(Resources resources) {

        final SpriteSheet spriteSheet = new SpriteSheet(resources, RES_BASIC_TOWER_SS, 4, 2);

        final EnumMap<Tower.AnimationId, Animation> mAnimations = new EnumMap<>(Tower.AnimationId.class);

        mAnimations.put(Tower.AnimationId.DOWN,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(0, 0) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.DOWN_LEFT,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(0, 1) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.LEFT,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(0, 2) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.LEFT_UP,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(0, 3) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.UP,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(1, 0) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.UP_RIGHT,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(1, 1) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.RIGHT,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(1, 2) }, -1, false, null));

        mAnimations.put(Tower.AnimationId.RIGHT_DOWN,
                new SpriteSheetAnimation(spriteSheet.getBitmap(),
                        new Rect[]{ spriteSheet.getRect(1, 3) }, -1, false, null));


        return createTower(new GenericSprite<>(mAnimations, Tower.AnimationId.DOWN),
                BASIC_TOWER_HEALTH,
                BASIC_TOWER_DAMAGE,
                BASIC_TOWER_ATTACK_RANGE, BASIC_TOWER_ATTACK_DELAY,
                BASIC_TOWER_WEIGHT);
    }

    private static Tower createTower(Sprite<Tower.AnimationId> sprite, int health, int damage, int attackRange, int attackDelay, int weight) {
        return new Tower(sprite, health, damage, attackRange, attackDelay, weight);
    }
}
