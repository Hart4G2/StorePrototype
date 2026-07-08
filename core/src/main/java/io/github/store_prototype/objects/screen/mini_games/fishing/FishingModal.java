package io.github.store_prototype.objects.screen.mini_games.fishing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import io.github.store_prototype.utils.assets.Assets;

public class FishingModal extends Table {

    private static final float WORLD_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private static final float SEGMENT_RADIUS = 1f;
    private static final float SEGMENT_LENGTH = 25f;
    private static final int SEGMENT_COUNT = 200;
    private static final float MARGIN = 100f;

    private static final float DENSITY = 0.3f;
    private static final float FRICTION = 0.2f;
    private static final float RESTITUTION = 0f;
    private static final float LINEAR_DAMPING = 3f;
    private static final float ANGULAR_DAMPING = 5f;
    private static final float JOINT_FREQUENCY = 5f;
    private static final float JOINT_DAMPING_RATIO = 0.5f;

    private static final float MOUSE_JOINT_MAX_FORCE = Float.MAX_VALUE;
    private static final float TOUCH_GRAB_RADIUS = 20f;


    private final World world;
    private final Array<Body> segments = new Array<>();
    private final Body mouseJointBody;
    private MouseJoint mouseJoint;

    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final Label statusLabel;
    private final TextButton okButton;
    private final Runnable onCompleted;
    private TextureRegion reelRegion;
    private TextureRegion background;

    private boolean completed = false;

    public FishingModal(Skin skin, Runnable onCompleted) {
        this.onCompleted = onCompleted;
        setFillParent(true);
        align(Align.top | Align.center);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        camera.update();

        shapeRenderer = new ShapeRenderer();
        world = new World(new Vector2(0, 0), true);

        BodyDef mouseBodyDef = new BodyDef();
        mouseBodyDef.type = BodyDef.BodyType.StaticBody;
        mouseJointBody = world.createBody(mouseBodyDef);

        createFishingLine();

        statusLabel = new Label("Untangle the fishing line!", skin, "white_16");
        statusLabel.setFontScale(2);

        okButton = new TextButton("OK", skin);
        okButton.setVisible(false);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (completed) close();
            }
        });

        add(statusLabel).height(100).pad(10).padTop(50);
        row();
        add(okButton).width(100).height(50).pad(20).padTop(500);

        setTouchable(Touchable.enabled);
        addListener(createInputListener());

        background = new TextureRegion(Assets.getAssets().getTexture("gamescene/mini_games/fishing/background.png"));
        reelRegion = new TextureRegion(Assets.getAssets().getTexture("gamescene/mini_games/fishing/reel.png"));
    }

    private void createFishingLine() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float startX = w / 2f;
        float startY = h / 2f;

        // Стартовое статическое тело (начало лески)
        Body prevBody = createStaticBody(startX, startY);
        segments.add(prevBody);

        float x = startX;
        float y = startY;
        float angle = MathUtils.random() * 2 * MathUtils.PI;

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            angle += MathUtils.random() * 2.5f - 1.25f;
            float step = SEGMENT_LENGTH + MathUtils.random() * 8 - 4;
            x += Math.cos(angle) * step;
            y += Math.sin(angle) * step;

            // Отражение от границ
            if (x < MARGIN) { x = MARGIN; angle = MathUtils.PI - angle; }
            if (x > w - MARGIN) { x = w - MARGIN; angle = MathUtils.PI - angle; }
            if (y < MARGIN) { y = MARGIN; angle = -angle; }
            if (y > h - MARGIN) { y = h - MARGIN; angle = -angle; }

            Body segment = createDynamicSegment(x, y, prevBody);
            segments.add(segment);
            prevBody = segment;
        }

        // Усыпляем все тела (чтобы не дрожали до первого касания)
        for (Body body : segments) {
            if (body.getType() == BodyDef.BodyType.DynamicBody) {
                body.setAwake(false);
                body.setLinearVelocity(0, 0);
                body.setAngularVelocity(0);
            }
        }
    }

    private Body createStaticBody(float x, float y) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);
        return world.createBody(def);
    }

    private Body createDynamicSegment(float x, float y, Body prevBody) {
        // Тело
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x, y);
        Body body = world.createBody(def);

        // Форма (круг)
        CircleShape circle = new CircleShape();
        circle.setRadius(SEGMENT_RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = DENSITY;
        fixtureDef.friction = FRICTION;
        fixtureDef.restitution = RESTITUTION;
        body.createFixture(fixtureDef);
        circle.dispose();

        // Соединение с предыдущим звеном
        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.bodyA = prevBody;
        jointDef.bodyB = body;
        jointDef.length = SEGMENT_LENGTH;
        jointDef.frequencyHz = JOINT_FREQUENCY;
        jointDef.dampingRatio = JOINT_DAMPING_RATIO;
        world.createJoint(jointDef);

        // Параметры тела
        body.setFixedRotation(true);
        body.setLinearDamping(LINEAR_DAMPING);
        body.setAngularDamping(ANGULAR_DAMPING);
        body.setSleepingAllowed(true);

        return body;
    }

    private InputListener createInputListener() {
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (completed) return false;
                attemptGrab(new Vector2(x, y));
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (!completed && mouseJoint != null) {
                    mouseJoint.setTarget(new Vector2(x, y));
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (mouseJoint != null) {
                    world.destroyJoint(mouseJoint);
                    mouseJoint = null;
                }
            }
        };
    }

    private void attemptGrab(Vector2 touchPoint) {
        Body target = null;
        float minDist = TOUCH_GRAB_RADIUS;
        for (Body body : segments) {
            if (body.getType() == BodyDef.BodyType.StaticBody) continue;
            float dist = body.getPosition().dst(touchPoint);
            if (dist < minDist) {
                minDist = dist;
                target = body;
            }
        }
        if (target != null) {
            MouseJointDef def = new MouseJointDef();
            def.bodyA = mouseJointBody;
            def.bodyB = target;
            def.target.set(touchPoint);
            def.maxForce = MOUSE_JOINT_MAX_FORCE;
            mouseJoint = (MouseJoint) world.createJoint(def);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!completed) {
            updatePhysics();
            checkCompletion();
        }
    }

    private void updatePhysics() {
        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        if (mouseJoint == null) {
            for (Body body : segments) {
                if (body.getType() == BodyDef.BodyType.DynamicBody) {
                    body.setLinearVelocity(0, 0);
                    body.setAngularVelocity(0);
                }
            }
        }
    }

    private void checkCompletion() {
        if (isLineUnraveled()) {
            completed = true;
            statusLabel.setText("Ready to go fishing!");
            okButton.setVisible(true);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // 1. Фон и катушка (рисуются средствами batch)
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(reelRegion, 500, 100, Gdx.graphics.getHeight(), Gdx.graphics.getHeight());

        // 2. Завершаем batch и рисуем леску через ShapeRenderer
        batch.end();
        Gdx.gl.glLineWidth(10);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < segments.size - 1; i++) {
            Vector2 a = segments.get(i).getPosition();
            Vector2 b = segments.get(i + 1).getPosition();
            shapeRenderer.line(a, b);
        }
        shapeRenderer.end();

        // 3. Снова запускаем batch и рисуем UI (лейбл, кнопку) поверх лески
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    private boolean isLineUnraveled() {
        for (int i = 0; i < segments.size - 2; i++) {
            Vector2 a1 = segments.get(i).getPosition();
            Vector2 a2 = segments.get(i + 1).getPosition();
            for (int j = i + 2; j < segments.size - 1; j++) {
                Vector2 b1 = segments.get(j).getPosition();
                Vector2 b2 = segments.get(j + 1).getPosition();
                if (segmentsIntersect(a1, a2, b1, b2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean segmentsIntersect(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2) {
        float d1 = cross(a2.x - a1.x, a2.y - a1.y, b1.x - a1.x, b1.y - a1.y);
        float d2 = cross(a2.x - a1.x, a2.y - a1.y, b2.x - a1.x, b2.y - a1.y);
        float d3 = cross(b2.x - b1.x, b2.y - b1.y, a1.x - b1.x, a1.y - b1.y);
        float d4 = cross(b2.x - b1.x, b2.y - b1.y, a2.x - b1.x, a2.y - b1.y);

        if ((d1 * d2 < 0) && (d3 * d4 < 0)) return true;

        if (Math.abs(d1) < 1e-6 && Math.abs(d2) < 1e-6) {
            if (Math.max(a1.x, a2.x) < Math.min(b1.x, b2.x) ||
                Math.max(b1.x, b2.x) < Math.min(a1.x, a2.x)) return false;
            if (Math.max(a1.y, a2.y) < Math.min(b1.y, b2.y) ||
                Math.max(b1.y, b2.y) < Math.min(a1.y, a2.y)) return false;
            return true;
        }
        return false;
    }

    private float cross(float x1, float y1, float x2, float y2) {
        return x1 * y2 - y1 * x2;
    }

    private void close() {
        world.dispose();
        shapeRenderer.dispose();
        remove();
        if (onCompleted != null) onCompleted.run();
    }
}
