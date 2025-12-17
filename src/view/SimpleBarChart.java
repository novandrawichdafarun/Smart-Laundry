package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


public class SimpleBarChart extends JPanel {

	private Map<String, Double> data;
	// configurable properties
	private Color barColor = new Color(52, 152, 219);
	private int padding = 30;
	private Font labelFont = new Font("Segoe UI", Font.PLAIN, 11);
	private Font valueFont = new Font("Segoe UI", Font.PLAIN, 11);
	private int barCornerRadius = 0;
	private int barGap = 8;
	private boolean showValues = true;
	private Function<Double, String> valueFormatter = SimpleBarChart::formatRupiah;

	public SimpleBarChart() {
		this(null);
	}

	public SimpleBarChart(Map<String, Double> data) {
		this.data = data;
		setPreferredSize(new Dimension(100, 200));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	public void updateData(Map<String, Double> newData) {
		this.data = newData;
		repaint();
	}

	// Setters for reuse/customization
	public void setBarColor(Color color) {
		this.barColor = color;
		repaint();
	}

	public void setPadding(int padding) {
		this.padding = padding;
		repaint();
	}

	public void setLabelFont(Font font) {
		this.labelFont = font;
		repaint();
	}

	public void setValueFont(Font font) {
		this.valueFont = font;
		repaint();
	}

	public void setBarCornerRadius(int radius) {
		this.barCornerRadius = radius;
		repaint();
	}

	public void setBarGap(int gap) {
		this.barGap = gap;
		repaint();
	}

	public void setShowValues(boolean show) {
		this.showValues = show;
		repaint();
	}

	public void setValueFormatter(Function<Double, String> formatter) {
		this.valueFormatter = formatter != null ? formatter : SimpleBarChart::formatRupiah;
		repaint();
	}

	private static String formatRupiah(double number) {
		DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
		DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

		formatRp.setCurrencySymbol("Rp. ");
		formatRp.setMonetaryDecimalSeparator(',');
		formatRp.setGroupingSeparator('.');

		kursIndonesia.setDecimalFormatSymbols(formatRp);
		kursIndonesia.setMaximumFractionDigits(0);

		return kursIndonesia.format(number);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (data == null || data.isEmpty()) {
			g.drawString("Belum ada data penjualan", 20, 100);
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int width = getWidth();
		int height = getHeight();

		// find max
		double maxVal = 0;
		for (Double val : data.values()) {
			if (val != null && val > maxVal) {
				maxVal = val;
			}
		}
		if (maxVal == 0) {
			maxVal = 1;
		}

		// axis line
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawLine(padding, height - padding, width - padding, height - padding);

		int count = data.size();
		int totalGap = (count - 1) * barGap;
		int availableWidth = Math.max(0, width - 2 * padding - totalGap);
		int barWidth = count > 0 ? Math.max(1, availableWidth / count) : 0;
		int x = padding;

		FontMetrics labelFm = g2d.getFontMetrics(labelFont);
		FontMetrics valueFm;

		for (Map.Entry<String, Double> entry : data.entrySet()) {
			Double value = entry.getValue();
			double val = value == null ? 0 : value;
			int barHeight = (int) ((val / maxVal) * (height - 2 * padding));
			barHeight = Math.max(0, barHeight);

			// bar
			g2d.setColor(barColor);
			g2d.fillRoundRect(x, height - padding - barHeight, barWidth, barHeight, barCornerRadius, barCornerRadius);

			// label
			g2d.setColor(Color.DARK_GRAY);
			g2d.setFont(labelFont);
			int labelWidth = labelFm.stringWidth(entry.getKey());
			int labelX = x + (barWidth - labelWidth) / 2;
			g2d.drawString(entry.getKey(), labelX, height - padding + labelFm.getHeight() - 4);

			// value (optional)
			if (showValues) {
				String valueText = valueFormatter.apply(val);
				g2d.setFont(valueFont);
				valueFm = g2d.getFontMetrics();
				int valWidth = valueFm.stringWidth(valueText);
				int valX = x + (barWidth - valWidth) / 2;
				g2d.setColor(Color.BLACK);
				g2d.drawString(valueText, valX, height - padding - barHeight - 5);
			}

			x += barWidth + barGap;
		}
	}
}
