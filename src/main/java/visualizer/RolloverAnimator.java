package visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RolloverAnimator {
    private final AbstractButton button;
    private final long totalAnimationTime = 250;
    private long animationTime = totalAnimationTime;
    private boolean wasRolledOver;
    private Long startedAt;
    private double progress;
    private Timer timer;

    public RolloverAnimator(AbstractButton button, ImageIcon icon, ImageIcon rolloverIcon) {
        this.button = button;
        var model = button.getModel();

        model.addChangeListener(e -> {
            if (!model.isSelected()) {
                wasRolledOver = model.isRollover() && !wasRolledOver;
                prepare();
            }
        });

        timer = new Timer(0, e -> {
            if (startedAt == null) startedAt = System.currentTimeMillis();
            long duration = System.currentTimeMillis() - startedAt;

            progress = Math.min((double) duration / animationTime, 1.0);
            if (progress == 1.0) {
                startedAt = null;
                timer.stop();
            }

            var target = transitionImage(icon, rolloverIcon, progress);
            button.setIcon(new ImageIcon(target));

            if (!timer.isRunning()) {
                progress = 0.0;
            }
        });
    }

    private void prepare() {
        if (timer.isRunning()) {
            timer.stop();
            animationTime = (long) (totalAnimationTime * progress);
            startedAt = System.currentTimeMillis() - (totalAnimationTime - animationTime);
        } else {
            animationTime = totalAnimationTime;
            startedAt = null;
        }
        progress = 0.0;
        timer.start();
    }

    private BufferedImage transitionImage(ImageIcon icon, ImageIcon rolloverIcon, double progress) {
        var image = toBufferedImage(icon.getImage());
        var rolloverImage = toBufferedImage(rolloverIcon.getImage());
        int width = Math.max(image.getWidth(), rolloverImage.getWidth());
        int height = Math.max(image.getHeight(), rolloverImage.getHeight());
        var target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g2D = target.createGraphics();
        var mainImageAlpha = (float) (wasRolledOver ? 1.0f - progress : progress);
        var rollOverAlpha = (float) (wasRolledOver ? progress : 1.0f - progress);

        g2D.setComposite(AlphaComposite.SrcOver.derive(mainImageAlpha));
        g2D.drawImage(image, 2, 2, button);
        g2D.setComposite(AlphaComposite.SrcOver.derive(rollOverAlpha));
        g2D.drawImage(rolloverImage, 0, 0, button);
        g2D.dispose();
        return target;
    }

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) return (BufferedImage) image;

        var bImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        var g2D = bImage.createGraphics();
        g2D.drawImage(image, 0, 0, null);
        g2D.dispose();
        return bImage;
    }
}
