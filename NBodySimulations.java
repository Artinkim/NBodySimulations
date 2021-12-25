import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NBodySimulations extends JComponent implements ChangeListener, ActionListener, ItemListener {
	// float[][] particles;
	Particle[] particles;
	Mode mode = Mode.PARTICLE;
	boolean LINES = false;
	boolean DISPVEL = true;
	boolean DISPACC = true;
	boolean MASS = false;
	boolean BOID = false;
	final public int height;
	final public int width;
	float gravity = 0.001f;
	JButton linesButton;
	JButton stopMotionButton;
	JButton velocityButton;
	JButton accelerationButton;

	public static void main(String[] args) {
		new ParticleSystem(1000, 1000, 200);
	}

	ParticleSystem(int width, int height, int particleCount) {
		this.width = width;
		this.height = height;
		particles = new Particle[particleCount];
		Random rand = new Random();

		for (int i = 0; i < particleCount; i++) {
			particles[i] = new Particle(new Vector2(rand.nextFloat() * width, rand.nextFloat() * height),
					new Vector2(rand.nextFloat() * (((height + width) / 1000) + 0.05f),
							rand.nextFloat() * (((height + width) / 1000) + 0.05f)),
					new Vector2(0, 0), rand.nextFloat() * 20 + 5,
					new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		}

		JPanel p = new JPanel();

		JSlider gravitySlider = new JSlider(0, 1000, 100);
		gravitySlider.addChangeListener(this);

		linesButton = new JButton("Display Lines");
		linesButton.addActionListener(this);

		velocityButton = new JButton("Display Velocity");
		velocityButton.addActionListener(this);

		accelerationButton = new JButton("Display Acceleration");
		accelerationButton.addActionListener(this);

		stopMotionButton = new JButton("Reset Velocity");
		stopMotionButton.addActionListener(this);

		String s1[] = { "Particles", "Gravity Simulation", "Boids" };
		JComboBox modeBox = new JComboBox(s1);
		modeBox.addItemListener(this);

		p.add(gravitySlider);
		p.add(linesButton);
		p.add(velocityButton);
		p.add(accelerationButton);
		p.add(stopMotionButton);
		p.add(modeBox);

		JFrame frame = new JFrame("Particle System");
		frame.add(p);
		frame.pack();
		frame.add(this);
		frame.pack();
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		while (true) {
			this.repaint();
		}
	}

	private void updateBoid() {

	}

	private void updateMass() {
//		Arrays.sort(particles,new Comparator<float[]>() {
//		public int compare(float[] a, float[] b) {
//	        return Float.compare(a[0], b[0]);
//	    }
//	});
	}

	public Color lerpColor(Color a, Color b, float t) {
		return new Color(0, 0, 0);
	}

	public void drawLinesGivenRadiusNaiveMore(Graphics g) {
		for (int i = 0; i < particles.length; i++) {
			int particlesWithinRadius = 1; 
			Vector2 avgDirection = new Vector2(0,0);
			Vector2 avgPosition = new Vector2(0,0);
			for (int j = 0; j < particles.length; j++) {
				// System.out.println(i);
				if(i==j) {
					continue;
				}
				if (MASS || BOID || LINES) {
					Vector2 diff = Vector2.sub(particles[i].pos, particles[j].pos);
					float r = diff.length();
					if (MASS) {
						float force = (float) ((particles[i].mass * particles[j].mass) / Math.pow( (double)( (r*r) +Math.max(particles[i].mass,particles[j].mass)),3/2.0));
						force *= gravity;
						Vector2 diffN = diff.normalized();
						diffN.scale(-force);
						particles[i].accel.add(diffN);
					}
					if (r < 100) {
						Vector2 diffN = Vector2.sub(particles[i].pos,particles[j].pos);
						diffN.normalize();
						if (r > 20) {
							
						} else {
							diffN.scale(0.1f);
							particles[i].accel.add(diffN);
						}
						avgPosition.add(diffN);
						avgDirection.add(particles[j].vel);
						particlesWithinRadius++;
					}
					if (r < 150) {
						float weight = (1.0f - (r / 150));

//						System.out.println(g.getColor());
//						System.out.println((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f);
						// System.out.println((int) ((particles[i][4] + particles[j][4]) / 2.0f)+"
						// "+(1.0f - (c / radius)));
//						g.setColor(new Color((int) ((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][5] + particles[j][5]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][6] + particles[j][6]) / 2.0f)));
						if (LINES) {
//							Particles.drawLine(particles[i],particles[j]);
//							g.setColor(new Color((int) ((particles[i][4] + particles[j][4]) / 2.0f),
//									(int) ((particles[i][5] + particles[j][5]) / 2.0f),
//									(int) ((particles[i][6] + particles[j][6]) / 2.0f), (int) (255 * weight)));
						}

					}
				}

			}
			if(BOID) {
				avgDirection.scale(1.0f/particlesWithinRadius);
				avgPosition.scale(1.0f/particlesWithinRadius);
//				Vector2 diffV = Vector2.sub(avgDirection.normalized(),particles[i].vel.normalized()).normalized();
//				Vector2 diffP = Vector2.sub(avgPosition,particles[i].pos).normalized();
				Vector2 diffV = avgDirection.normalized();
				Vector2 diffP = avgPosition.normalized();
				diffV.scale(0.1f);
				diffP.scale(0.001f);
				particles[i].accel.add(diffV);
				particles[i].accel.add(diffP);
			}
			if (DISPVEL) {
				particles[i].drawVelocity(g);
			}
			if (DISPACC) {
				particles[i].drawAcceleration(g);
			}
			if(BOID) {
				particles[i].vel.add(particles[i].accel);
				particles[i].vel.normalize();
				particles[i].accel.zero();
			}
			particles[i].draw(g);
			particles[i].update(width,height);
		}
	}

	public void drawLinesGivenRadiusNaive(Graphics g) {
		for (int i = 0; i < particles.length; i++) {
			for (int j = i + 1; j < particles.length; j++) {
				// System.out.println(i);
				if (MASS || BOID || LINES) {
					Vector2 diff = Vector2.sub(particles[i].pos, particles[j].pos);
					float r = diff.length();
					if (MASS == true) {
						float force = (float) ((particles[i].mass * particles[j].mass) / Math.pow( (double)( (r*r) +Math.max(particles[i].mass,particles[j].mass)),3/2.0));
						force *= gravity*0.0001f;
						Vector2 diffN = diff.normalized();
						diffN.scale(-force);
						particles[i].accel.add(diffN);
						diffN.scale(-1);
						particles[j].accel.add(diffN);
					}
//					if (r < 100) {
//						Vector2 diffN = Vector2.sub(particles[i].pos,particles[j].pos);
//						diffN.normalize();
//						diffN.scale(0.00001f);
//						if (r > 20) {
//							particles[i].turn.add(diffN);
//							diffN.scale(-1);
//							particles[j].turn.add(diffN);
//						} else {
//							particles[j].turn.add(diffN);
//							diffN.scale(-1);
//							particles[j].turn.add(diffN);
//						}
//						Vector2 diffV = Vector2.sub(particles[i].vel.normalized(),particles[j].vel.normalized());
//						diffV.scale(0.0000001f);
//						particles[i].turn.add(diffV);
//						particles[i].particlesWithinRadius++;
//						diffV.scale(-1);
//						particles[j].accel.add(diffV);
//						particles[j].particlesWithinRadius++;
//					}
					if (r < 150) {
						float weight = (1.0f - (r / 150));

//						System.out.println(g.getColor());
//						System.out.println((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f);
						// System.out.println((int) ((particles[i][4] + particles[j][4]) / 2.0f)+"
						// "+(1.0f - (c / radius)));
//						g.setColor(new Color((int) ((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][5] + particles[j][5]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][6] + particles[j][6]) / 2.0f)));
						if (LINES) {
//							Particles.drawLine(particles[i],particles[j]);
//							g.setColor(new Color((int) ((particles[i][4] + particles[j][4]) / 2.0f),
//									(int) ((particles[i][5] + particles[j][5]) / 2.0f),
//									(int) ((particles[i][6] + particles[j][6]) / 2.0f), (int) (255 * weight)));
						}

					}
				}

			}
			if (DISPVEL) {
				particles[i].drawVelocity(g);
			}
			if (DISPACC) {
				particles[i].drawAcceleration(g);
			}
			particles[i].draw(g);
			particles[i].update(width, height);
		}
	}

	public void paint(Graphics g) {
		switch (mode) {
		case PARTICLE:
			// updateDrawParticle(g);
			drawLinesGivenRadiusNaiveMore(g);
			break;
		case MASS:
			updateMass();
			break;
		case BOID:
			updateBoid();
			break;
		}
	}

//	public void updateDrawParticleLine(Graphics g) {
//		g.clearRect(0, 0, WIDTH, HEIGHT);
//		Random rand = new Random();
//		particles[0][0] += particles[0][2];
//		particles[0][1] += particles[0][3];
//		g.fillOval((int) particles[0][0], (int) particles[0][1], 2, 2);
//
//		for (int i = 1; i < particles.length; i++) {
//			if (particles[i][0] < 0 || particles[i][0] > width) {
//				particles[i][2] *= -1;
//			}
//			if (particles[i][1] < 0 || particles[i][1] > height) {
//				particles[i][3] *= -1;
//			}
//			particles[i][0] += particles[i][2];
//			particles[i][1] += particles[i][3];
//			// System.out.println(particles[i][0]+" "+particles[i][3]+" "+width);
//			g.setColor(new Color((int) particles[i][4], (int) particles[i][5], (int) particles[i][6]));
//			g.fillOval((int) particles[i][0], (int) particles[i][1], 2, 2);
//			g.drawLine((int) particles[i - 1][0], (int) particles[i - 1][1], (int) particles[i][0],
//					(int) particles[i][1]);
//		}
//	}
//	public void drawLines(Graphics g) {
//	for (int i = 1; i < particles.length; i++) {
//		float a = (particles[i - 1][0] - particles[i][0]) * (particles[i - 1][0] - particles[i][0]);
//		float b = (particles[i - 1][1] - particles[i][1]) * (particles[i - 1][1] - particles[i][1]);
//		float c = (float) Math.sqrt(a + b);
//		g.setColor(new Color((int) ((1) * (particles[i - 1][4] + particles[i][4]) / 2.0f),
//				(int) ((1) * (particles[i - 1][5] + particles[i][5]) / 2.0f),
//				(int) ((1 - (Math.max(width, height))) * (particles[i - 1][6] + particles[i][6]) / 2.0f)));
//		g.drawLine((int) particles[i - 1][0], (int) particles[i - 1][1], (int) particles[i][0],
//				(int) particles[i][1]);
//	}
//}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider slider = (JSlider) e.getSource();
		gravity = slider.getValue() / 1000000f;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton button = (JButton) e.getSource();
		System.out.println(e.getSource());
		if (e.getSource().equals(linesButton)) {
			LINES = !LINES;
		} else if (e.getSource().equals(stopMotionButton)) {
			for (int i = 0; i < particles.length; i++) {
				particles[i].vel.zero();
				particles[i].accel.zero();
			}
		} else if (e.getSource().equals(velocityButton)) {
			DISPVEL = !DISPVEL;
		} else if (e.getSource().equals(accelerationButton)) {
			DISPACC = !DISPACC;
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		JComboBox box = (JComboBox) e.getSource();
		if (box.getSelectedIndex() == 0) {
			BOID = false;
			MASS = false;
		} else if (box.getSelectedIndex() == 1) {
			BOID = false;
			MASS = true;
		} else if (box.getSelectedIndex() == 2) {
			BOID = true;
			MASS = false;
		}
	}
}

class Particle {
	Vector2 pos;
	Vector2 vel;
	Vector2 accel;
	float mass;
	Color color;

	public Particle(Vector2 pos, Vector2 vel, Vector2 accel, float mass, Color color) {
		this.pos = pos;
		this.vel = vel;
		this.accel = accel;
		this.mass = mass;
		this.color = color;
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (pos.x - mass / 2), (int) (pos.y - mass / 2), (int) mass, (int) mass);
	}

	public void update(int width, int height) {
		if (pos.x < 0 || pos.x > width) {
			vel.x *= -1;
		}
		if (pos.y < 0 || pos.y > height) {
			vel.y *= -1;
		}
		// accel.scale(1.0f / particlesWithinRadius);
		vel.add(accel);
		//System.out.println(accel.x);
		pos.add(vel);
		accel.zero();
	}

	public void drawVelocity(Graphics g) {
		Vector2 velN = vel.normalized();
		g.setColor(new Color(0, 255, 0));
		g.drawLine((int) pos.x, (int) pos.y, (int) (pos.x + velN.x * 10), (int) (pos.y + velN.y * 10));
	}

	public void drawAcceleration(Graphics g) {
		Vector2 accN = accel.normalized();// Normalized should not happen because magnitude is gotten rid of
		g.setColor(new Color(255, 0, 0));
		g.drawLine((int) pos.x, (int) pos.y, (int) (pos.x + accel.x * 10), (int) (pos.y + accel.y * 10));
	}

}

class Vector2 {
	float x;
	float y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void normalize() {
		float l = length();
		if (l != 0) {
			x/=l;
			y/=l;
		}
	}

	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}

	public void zero() {
		this.x = 0;
		this.y = 0;
	}

	public void add(Vector2 a) {
		this.x += a.x;
		this.y += a.y;
	}

	public void sub(Vector2 a) {
		this.x -= a.x;
		this.y -= a.y;
	}

	public void scale(float c) {
		x *= c;
		y *= c;
	}

	public static Vector2 mul(Vector2 a, Vector2 b) {
		return new Vector2(a.x * b.x, a.y * b.y);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public Vector2 normalized() {
		float l = length();
		if (l != 0) {
			return new Vector2(x / l, y / l);
		} else {
			return new Vector2(0, 0);
		}

	}
}

enum Mode {
	PARTICLE, MASS, BOID, WATER_AKA_GRAVITY_AND_SLOSHING
};
