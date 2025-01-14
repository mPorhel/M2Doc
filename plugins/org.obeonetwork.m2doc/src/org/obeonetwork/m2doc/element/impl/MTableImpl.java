/*******************************************************************************
 *  Copyright (c) 2017 Obeo. 
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *   
 *   Contributors:
 *       Obeo - initial API and implementation
 *  
 *******************************************************************************/
package org.obeonetwork.m2doc.element.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.obeonetwork.m2doc.element.MElement;
import org.obeonetwork.m2doc.element.MTable;

/**
 * A table that can be inserted in a word document.
 * 
 * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
 */
public class MTableImpl implements MTable {

    /**
     * A table row, whose label can be used as row header.
     * 
     * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
     */
    public static class MRowImpl implements MRow {

        /**
         * The {@link List} of {@link MCell}.
         */
        private final List<MCell> cells = new ArrayList<>();

        @Override
        public List<MCell> getCells() {
            return cells;
        }
    }

    /**
     * A table cell.
     * 
     * @author <a href="mailto:yvan.lussaud@obeo.fr">Yvan Lussaud</a>
     */
    public static class MCellImpl extends AbstractMElementContainer implements MCell {

        /**
         * The background {@link Color}.
         */
        private Color backgroundColor;

        /**
         * The vertical alignemnt.
         */
        private VAlignment vAlign;

        /**
         * Constructor.
         * 
         * @param contents
         *            the contents
         * @param backgroundColor
         *            the background {@link Color}
         */
        public MCellImpl(MElement contents, Color backgroundColor) {
            super(contents);
            this.backgroundColor = backgroundColor;
        }

        @Override
        public Color getBackgroundColor() {
            return backgroundColor;
        }

        @Override
        public void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @Override
        public VAlignment getVAlignment() {
            return vAlign;
        }

        @Override
        public void setVAlignment(VAlignment alignement) {
            vAlign = alignement;
        }

    }

    /**
     * The {@link List} of {@link MRow}.
     */
    private final List<MRow> rows = new ArrayList<>();

    /**
     * The table label.
     */
    private String label;

    /**
     * The style ID of this table.
     */
    private String styleID;

    @Override
    public List<MRow> getRows() {
        return rows;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int getColumnsCount() {
        int result = 0;
        for (MRow mRow : rows) {
            int rowWidth = mRow.getCells().size();
            if (rowWidth > result) {
                result = rowWidth;
            }
        }
        return result;
    }

    @Override
    public String getStyleID() {
        return styleID;
    }

    @Override
    public void setStyleID(String styleID) {
        this.styleID = styleID;
    }
}
