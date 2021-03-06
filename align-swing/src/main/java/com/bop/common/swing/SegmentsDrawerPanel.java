/*
 * Copyright 2002-2003 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bop.common.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;

/**
 * @author Marco Ruiz
 */
@SuppressWarnings("serial")
public class SegmentsDrawerPanel<SEGMENTABLE_T> extends JPanel {

    public static int getFactored(double orig, double ratio) {
        return new Double(orig * ratio + 0.5).intValue();
    }

    private final Function<SEGMENTABLE_T, Segment> segmentMapper;

    private int maxX, maxY;
	private List<SEGMENTABLE_T> segments;
	private Color backgroundHighlightColor = null;

	public SegmentsDrawerPanel(Function<SEGMENTABLE_T, Segment> segmentMapper) {
		this(segmentMapper, null);
	}
	
	public SegmentsDrawerPanel(Function<SEGMENTABLE_T, Segment> segmentMapper, Color backgroundHighlight) {
		this.segmentMapper = segmentMapper;
		setBackgroundHighlightColor(backgroundHighlight);
	}

    public void setBackgroundHighlightColor(Color highlight) {
		this.backgroundHighlightColor = highlight;
	}

	public void paintSegmentables(List<SEGMENTABLE_T> segmentables, int maxX, int maxY) {
    	segments = null;
    	setMaxSegmentableValues(maxX, maxY);
    	paintSegmentables(segmentables);
    }

    public void setMaxSegmentableValues(int maxX, int maxY) {
    	this.maxX = maxX;
    	this.maxY = maxY;
        repaint();
    }
    
    public void paintSegmentables(List<SEGMENTABLE_T> segmentables) {
        segments = segmentables;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); 				//paint background
        if (segments == null || segments.isEmpty()) return;

        double xRatio = 1.0 * getWidth()  / maxX;
        double yRatio = 1.0 * getHeight() / maxY;
        
       	paintEncapsulatingArea(g, xRatio, yRatio);
       	segments.stream().forEach(segmentable -> drawSegment(g, segmentable, xRatio, yRatio));
    }
    
	private void paintEncapsulatingArea(Graphics g, double xRatio, double yRatio) {
		if (backgroundHighlightColor == null) return;

		Segment firstTrace = getScaledSegment(segments.get(0), xRatio, yRatio);
		Segment lastTrace = getScaledSegment(segments.get(segments.size() - 1), xRatio, yRatio);
		
        g.setColor(backgroundHighlightColor);
        g.fillRect(	firstTrace.getStartX(), 
	        		firstTrace.getStartY(), 
					lastTrace.getEndX() - firstTrace.getStartX() + 1, 
					lastTrace.getEndY() - firstTrace.getStartY() + 1);
	}
	
	private void drawSegment(Graphics g, SEGMENTABLE_T segmentable, double xRatio, double yRatio) {
		Segment segment = getScaledSegment(segmentable, xRatio, yRatio);
        g.setColor(segment.getColor());
		g.drawLine(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
	}

	private Segment getScaledSegment(SEGMENTABLE_T target, double xRatio, double yRatio) {
		return segmentMapper.apply(target).withRatios(xRatio, yRatio);
	}
}

