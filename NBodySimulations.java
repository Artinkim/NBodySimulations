import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class NBodySimulations extends JComponent {
	float[][] particles;
	Mode mode = Mode.PARTICLE;
	final public int height;
	final public int width;

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
			particles[i][7] = rand.nextInt(20)+5;
		}
		JFrame frame = new JFrame("Particle System");
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(this);
		while (true) {
			this.repaint();
		}
	}

	private void updateDrawParticle(Graphics g) {
		for (int i = 0; i < particles.length; i++) {
			if (particles[i][0] < 0 || particles[i][0] > width) {// ONLY REVERSES VELOCITY DOES NOT MOVE PARTICLE BACK
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
			for (int j = i + 1; j < particles.length; j++) {
				// System.out.println(i);
				float dx = particles[i][0] - particles[j][0];
				float dy = particles[i][1] - particles[j][1];
				float r = (float) Math.sqrt(dx * dx + dy * dy);
				float force = (particles[i][7] * particles[j][7]) / (r * r);
				float ndx = dx / Math.abs(dx);
				float ndy = dy / Math.abs(dy);
				force*=0.001f;
				particles[i][2] -= dx * force;
				particles[i][3] -= dy * force;
				particles[j][2] += dx * force;
				particles[j][3] += dy * force;
				if (r < radius) {
					float weight = (1.0f - (r / radius));

//					System.out.println(g.getColor());
//					System.out.println((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f);
					// System.out.println((int) ((particles[i][4] + particles[j][4]) / 2.0f)+"
					// "+(1.0f - (c / radius)));
//					g.setColor(new Color((int) ((1 - (c / radius)) * (particles[i][4] + particles[j][4]) / 2.0f),
//							(int) ((1 - (c / radius)) * (particles[i][5] + particles[j][5]) / 2.0f),
//							(int) ((1 - (c / radius)) * (particles[i][6] + particles[j][6]) / 2.0f)));

					g.setColor(new Color((int) ((particles[i][4] + particles[j][4]) / 2.0f),
							(int) ((particles[i][5] + particles[j][5]) / 2.0f),
							(int) ((particles[i][6] + particles[j][6]) / 2.0f), (int) (255 * weight)));
					g.drawLine((int) particles[i][0], (int) particles[i][1], (int) particles[j][0],
							(int) particles[j][1]);
				}
			}

		}
	}

	public void paint(Graphics g) {
		switch (mode) {
		case PARTICLE:
			updateDrawParticle(g);
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
}

enum Mode {
	PARTICLE, MASS, BOID
};
