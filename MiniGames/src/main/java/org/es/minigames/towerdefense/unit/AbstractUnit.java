package org.es.minigames.towerdefense.unit;

import org.es.engine.graphics.sprite.Sprite;

/**
 * This class represents a fighting unit.
 *
 * @author Cyril Leroux
 *         Created on 30/01/14.
 */
public abstract class AbstractUnit<AnimationId extends Enum<AnimationId>> extends Destructible<AnimationId> {

    /** Damage caused by each attack of the unit. */
    protected int mDamage;
    /** Attack range of the unit. Expressed in tiles. */
    protected float mAttackRange;
    /** Delay in milliseconds between two attacks. */
    protected long mAttackDelay;
    /** Timestamp of the last attack in milliseconds. */
    protected long mLastAttack;

    /** The rotation angle of the unit in degrees. */
    protected double mRotationAngle;

    protected AbstractUnit(Sprite<AnimationId> sprite, float width, float height, int weight, int health,
                           int damage, float attackRange, long attackDelay) {
        super(sprite, width, height, weight, health);
        mHealth = health;
        mDamage = damage;
        mAttackRange = attackRange;
        mAttackDelay = attackDelay;
    }
}
