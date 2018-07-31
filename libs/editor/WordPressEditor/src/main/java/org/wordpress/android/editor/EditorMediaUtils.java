package org.wordpress.android.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import org.wordpress.android.util.DisplayUtils;
import org.wordpress.android.util.ImageUtils;

public class EditorMediaUtils {
    private static final String MIME_TYPE_IMAGE_PREFIX = "image/";
    private static final String MIME_TYPE_VIDEO_PREFIX = "video/";
    private static final String MIME_TYPE_AUDIO_PREFIX = "audio/";

    public static BitmapDrawable getAztecPlaceholderDrawableFromResID(Context context, @DrawableRes int drawableId,
                                                                      int maxImageSizeForVisualEditor) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            bitmap = ImageUtils.getScaledBitmapAtLongestSide(bitmap, maxImageSizeForVisualEditor);
        } else if (drawable instanceof VectorDrawableCompat
                   || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(maxImageSizeForVisualEditor, maxImageSizeForVisualEditor,
                                         Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            throw new IllegalArgumentException("Unsupported Drawable Type: " + drawable.getClass().getName());
        }
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static int getMaximumThumbnailSizeForEditor(Context context) {
        Point size = DisplayUtils.getDisplayPixelSize(context);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int maximumThumbnailWidthForEditor = screenWidth > screenHeight ? screenHeight : screenWidth;
        int padding = DisplayUtils.dpToPx(context, 48) * 2;
        maximumThumbnailWidthForEditor -= padding;
        return maximumThumbnailWidthForEditor;
    }

    public static boolean isImageMimeType(String type) {
        return isExpectedMimeType(MIME_TYPE_IMAGE_PREFIX, type);
    }

    public static boolean isVideoMimeType(String type) {
        return isExpectedMimeType(MIME_TYPE_VIDEO_PREFIX, type);
    }

    public static boolean isAudioMimeType(String type) {
        return isExpectedMimeType(MIME_TYPE_AUDIO_PREFIX, type);
    }

    private static boolean isExpectedMimeType(String expected, String type) {
        if (type == null) {
            return false;
        } else {
            String[] split = type.split("/");
            return split.length == 2 && expected.startsWith(split[0]);
        }
    }
}
