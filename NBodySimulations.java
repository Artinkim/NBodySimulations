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

public class ParticleSystem extends JComponent implements ChangeListener, ActionListener, ItemListener {
	float[][] particles;
	Mode mode = Mode.PARTICLE;
	boolean LINES = false;
	boolean DISPVEL = true;
	boolean DISPACC = true;
	boolean MASS = false;
	boolean BOID = false;
	final public int height;
	final public int width;
	float gravity = 0.0001f;
	JButton linesButton;
	JButton stopMotionButton;
	JButton velocityButton;
	JButton accelerationButton;

	public static void main(String[] args) {
		new ParticleSystem(1000, 1000, 2000);
	}

	ParticleSystem(int width, int height, int particleCount) {
		this.width = width;
		this.height = height;
		particles = new float[particleCount][8];
		Random rand = new Random();
		for (int i = 0; i < particleCount; i++) {
			particles[i][0] = rand.nextFloat() * width;
			particles[i][1] = rand.nextFloat() * height;
			particles[i][2] = rand.nextFloat() * (((height + width) / 1000) + 0.05f);
			particles[i][3] = rand.nextFloat() * (((height + width) / 1000) + 0.05f);
			particles[i][4] = rand.nextInt(255);
			particles[i][5] = rand.nextInt(255);
			particles[i][6] = rand.nextInt(255);
			particles[i][7] = rand.nextInt(20) + 5;
		}

		JPanel p = new JPanel();

		JSlider gravitySlider = new JSlider(0,1000,100);
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

	private void updateDrawParticle(Graphics g) {
		for (int i = 0; i < particles.length; i++) {
			if (particles[i][0] < 0 || particles[i][0] > width) { // ONLY REVERSES VELOCITY DOES NOT MOVE PARTICLE BACK
																	// IN BOUNDS(BAD FOR HIGH SPEEDS)
				particles[i][2] *= -1;
			}
			if (particles[i][1] < 0 || particles[i][1] > height) {
				particles[i][3] *= -1;
			}
			particles[i][0] += particles[i][2];
			particles[i][1] += particles[i][3];
			g.setColor(new Color((int) particles[i][4], (int) particles[i][5], (int) particles[i][6]));
			g.fillOval((int) particles[i][0], (int) particles[i][1], (int) particles[i][7], (int) particles[i][7]);
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

	public void drawLinesGivenRadiusNaive(Graphics g, float radius) {
		for (int i = 0; i < particles.length; i++) {
			int difi = (int) (particles[i][7] / 2);
			int cx = (int) particles[i][0] + difi;
			int cy = (int) particles[i][1] + difi;
			float tempVX = particles[i][2];
			float tempVY = particles[i][3];
			for (int j = i + 1; j < particles.length; j++) {
				// System.out.println(i);
				if (MASS || BOID || LINES) {
					float dx = particles[i][0] - particles[j][0];
					float dy = particles[i][1] - particles[j][1];
					float r = (float) Math.sqrt(dx * dx + dy * dy);

					if (MASS == true) {
						float force = (particles[i][7] * particles[j][7]) / (r * r);
						force *= gravity;
						particles[i][2] -= dx * force;
						particles[i][3] -= dy * force;
						particles[j][2] += dx * force;
						particles[j][3] += dy * force;
					}
					if(r<100) {
						if(r>20) {
							particles[i][2] -= dx*0.00001f;
							particles[i][3] -= dy*0.00001f;
							particles[j][2] += dx*0.00001f;
							particles[j][3] += dy*0.00001f;
						} else {
							particles[i][2] += dx*0.01f*(1/r);
							particles[i][3] += dy*0.01f*(1/r);
							particles[j][2] -= dx*0.01f*(1/r);
							particles[j][3] -= dy*0.01f*(1/r);
						}
					}
					if (r < radius) {
						float weight = (1.0f - (r / radius));

//						System.out.println(g.getColor());
//						System.out.println((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f);
						// System.out.println((int) ((particles[i][4] + particles[j][4]) / 2.0f)+"
						// "+(1.0f - (c / radius)));
//						g.setColor(new Color((int) ((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][5] + particles[j][5]) / 2.0f),
//								(int) ((1 - (c / radius)) * (particles[i][6] + particles[j][6]) / 2.0f)));
						if (LINES) {
							g.setColor(new Color((int) ((particles[i][4] + particles[j][4]) / 2.0f),
									(int) ((particles[i][5] + particles[j][5]) / 2.0f),
									(int) ((particles[i][6] + particles[j][6]) / 2.0f), (int) (255 * weight)));

							int difj = (int) (particles[j][7] / 2);
							g.drawLine(cx, cy, (int) particles[j][0] + difj, (int) particles[j][1] + difj);
						}

					}
				}

			}
			if (DISPVEL) {
				g.setColor(new Color(0, 255, 0));
				g.drawLine(cx, cy, (int) (cx + particles[i][2] * 10), (int) (cy + particles[i][3] * 10));
			}
			if (DISPACC) {
				g.setColor(new Color(255, 0, 0));
				g.drawLine(cx, cy, (int) (cx + (particles[i][2] - tempVX) * 500),
						(int) (cy + (particles[i][3] - tempVY) * 500));
			}
			g.setColor(new Color((int) particles[i][4], (int) particles[i][5], (int) particles[i][6]));
			g.fillOval((int) particles[i][0], (int) particles[i][1], (int) particles[i][7], (int) particles[i][7]);
			if (particles[i][0] < 0 || particles[i][0] > width) { // ONLY REVERSES VELOCITY DOES NOT MOVE PARTICLE BACK
																	// IN BOUNDS(BAD FOR HIGH SPEEDS)
				particles[i][2] *= -1;
				particles[i][0] += particles[i][2];
			}
			if (particles[i][1] < 0 || particles[i][1] > height) {
				particles[i][3] *= -1;
				particles[i][1] += particles[i][3];
			}
			particles[i][0] += particles[i][2];
			particles[i][1] += particles[i][3];

		}
	}

	public void paint(Graphics g) {
		switch (mode) {
		case PARTICLE:
			// updateDrawParticle(g);
			drawLinesGivenRadiusNaive(g, 100);
			break;
		case MASS:
			updateMass();
			break;
		case BOID:
			updateBoid();
			break;
		}
	}

	public void updateDrawParticleLine(Graphics g) {
		g.clearRect(0, 0, WIDTH, HEIGHT);
		Random rand = new Random();
		particles[0][0] += particles[0][2];
		particles[0][1] += particles[0][3];
		g.fillOval((int) particles[0][0], (int) particles[0][1], 2, 2);

		for (int i = 1; i < particles.length; i++) {
			if (particles[i][0] < 0 || particles[i][0] > width) {
				particles[i][2] *= -1;
			}
			if (particles[i][1] < 0 || particles[i][1] > height) {
				particles[i][3] *= -1;
			}
			particles[i][0] += particles[i][2];
			particles[i][1] += particles[i][3];
			// System.out.println(particles[i][0]+" "+particles[i][3]+" "+width);
			g.setColor(new Color((int) particles[i][4], (int) particles[i][5], (int) particles[i][6]));
			g.fillOval((int) particles[i][0], (int) particles[i][1], 2, 2);
			g.drawLine((int) particles[i - 1][0], (int) particles[i - 1][1], (int) particles[i][0],
					(int) particles[i][1]);
		}
	}
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
				particles[i][2] *= 0.1f;
				particles[i][3] *= 0.1f;
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
		} else if (box.getSelectedIndex() == 1) {
			BOID = true;
			MASS = false;
		}
	}
}

enum Mode {
	PARTICLE, MASS, BOID, WATER_AKA_GRAVITY_AND_SLOSHING
};
