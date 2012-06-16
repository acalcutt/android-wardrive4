/*
 *   wardrive4 - android wardriving application (remake for the ICS)
 *   Copyright (C) 2012 Raffaele Ragni
 *   https://github.com/raffaeleragni/android-wardrive4
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ki.wardrive4.activity.mapoverlays;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import ki.wardrive4.utils.Geohash;

/**
 * Abstract overlay for WiFis.
 * 
 * Stuff taken from the old wardrive.
 * 
 * @author Raffaele Ragni <raffaele.ragni@gmail.com>
 */
public abstract class WiFiOverlay extends Overlay
{
    private static final int CIRCLE_RADIUS = 9;
    
    private static final int TEXT_SIZE = 12;
    
    private static final int INFO_WINDOW_HEIGHT = 18;
    
    protected void drawSingleWiFi(Canvas canvas, MapView mapView, GeoPoint geoPoint, String title, int level, boolean showLabels, Paint stroke, Paint fill, Paint text)
    {
        Point point = mapView.getProjection().toPixels(geoPoint, new Point());
        int bigness = (int) (CIRCLE_RADIUS) - (-level)/12;
        bigness = bigness < 1 ? 0 : bigness;

        int lesserMeasure = canvas.getWidth() > canvas.getHeight() ? canvas.getWidth() : canvas.getHeight();
        double sizeRatio = ((double) lesserMeasure) / 460d;
        text.setTextSize((int)(TEXT_SIZE * sizeRatio));
        
        canvas.drawCircle(point.x, point.y, (int) (CIRCLE_RADIUS*sizeRatio), stroke);
        canvas.drawCircle(point.x, point.y, (int)(bigness*sizeRatio), fill);

        if (showLabels && title != null && title.length() > 0)
        {
            RectF rect = new RectF(0, 0, getTextWidth(title, text) + 4 * 2, (int)(INFO_WINDOW_HEIGHT * sizeRatio));
            rect.offset(point.x + (int)(5*sizeRatio), point.y + (int)(5*sizeRatio));
            canvas.drawRect(rect, fill);
            canvas.drawText(title, point.x + 9, point.y + (int)(19*sizeRatio), text);
        }
    }
    
    private int getTextWidth(String text, Paint paint)
    {
        int _count = text.length();
        float[] widths = new float[_count];
        paint.getTextWidths(text, widths);
        int textWidth = 0;
        for (int i = 0; i < _count; i++)
        {
            textWidth += widths[i];
        }
        return textWidth;
    }
    
    protected String[] composeGeohashBetween(GeoPoint topLeft, GeoPoint bottomRight)
    {
        double latFrom = ((double) topLeft.getLatitudeE6()) / 1E6;
        double latTo = ((double) bottomRight.getLatitudeE6()) / 1E6;
        double lonFrom = ((double) topLeft.getLongitudeE6()) / 1E6;
        double lonTo = ((double) bottomRight.getLongitudeE6()) / 1E6;
        if (latFrom > latTo)
        {
            double x = latTo;
            latTo = latFrom;
            latFrom = x;
        }
        if (lonFrom > lonTo)
        {
            double x = lonTo;
            lonTo = lonFrom;
            lonFrom = x;
        }
        
        return new String[]
        {
            new Geohash().encode(latFrom, lonFrom), new Geohash().encode(latTo, lonTo)
        };
    }
}
