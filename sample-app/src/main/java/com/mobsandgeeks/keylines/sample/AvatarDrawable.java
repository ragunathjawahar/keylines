/*
 * Copyright (C) 2016 Ragunath Jawahar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobsandgeeks.keylines.sample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * A {@link Drawable} that displays an alphabet on a circular background. The color of the
 * background is determined by the text passed in using {@link #setAvatarText(String)}.
 *
 * @author Ragunath Jawahar
 */
class AvatarDrawable extends Drawable {

    // Constants
    @ColorInt private static final int COLORS[] = {
            0xFF4DB6AC, 0xFF4DD0E1, 0xFF4FC3F7, 0xFF66BB6A, 0xFF7986CB, 0xFF8C9EFF,
            0xFF90A4AE, 0xFF9575CD, 0xFF9CCC65, 0xFF9E9E9E, 0xFF9FA8DA, 0xFFA1887F,
            0xFFAED581, 0xFFBA68C8, 0xFFEF5350, 0xFFF06292, 0xFFFBC02D, 0xFFFF8A65,
            0xFFFFA726
    };
    private static final int N_COLORS = COLORS.length;
    private static final float TEXT_SCALE = 0.6f; /* 60% of the circle's diameter */

    // Attributes
    private int width, height;
    private int backgroundRadius;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Rect textBounds;

    private String avatarText;

    AvatarDrawable() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(backgroundPaint);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textBounds = new Rect();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        drawCircleBackground(canvas);
        drawAvatarText(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        backgroundPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        backgroundPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        width = bounds.width();
        height = bounds.height();
        backgroundRadius = Math.min(width, height) / 2;

        // Text size
        int diameter = Math.min(width, height);
        textPaint.setTextSize(diameter * TEXT_SCALE);
    }

    void setAvatarText(@Nullable String text) {
        // Extract avatar text
        if (!TextUtils.isEmpty(text)) {
            avatarText = String.valueOf(Character.toUpperCase(text.charAt(0)));
        }

        // Determine background color
        text = text != null ? text : "null" /* Anything would do, we just need a non-null value. */;
        int colorIndex = Math.abs(text.hashCode() % N_COLORS);
        backgroundPaint.setColor(COLORS[colorIndex]);

        invalidateSelf();
    }

    private void drawCircleBackground(@NonNull Canvas canvas) {
        canvas.drawCircle(width / 2, height / 2, backgroundRadius, backgroundPaint);
    }

    private void drawAvatarText(@NonNull Canvas canvas) {
        if (TextUtils.isEmpty(avatarText)) {
            return;
        }

        // Text
        textPaint.getTextBounds(avatarText, 0, avatarText.length(), textBounds);
        int x = width / 2;
        int y = (int) ((height / 2) - ((textPaint.ascent() + textPaint.descent()) / 2)) ;
        canvas.drawText(avatarText, x, y, textPaint);
    }

}
