package it.polimi.ingsw.views.utils;

import it.polimi.ingsw.InternalError;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which facilitates drawing / formatting on the console
 * by thinking it's a character square.
 * This "square" could be either a real square ({@link RealConsoleMatrix})
 * or a view ({@link MatrixView}).
 */
public abstract class ConsoleMatrix {
    private final int width;
    private final int height;
    private boolean autoLineBreak;

    public ConsoleMatrix(int width, int height, boolean autoLineBreak) {
        if (width < 1 || height < 1) {
            throw new InternalError("Too small");
        }
        this.width = width;
        this.height = height;
        this.autoLineBreak = autoLineBreak;
    }

    public static ConsoleMatrix newMatrix(int width, int height, boolean autoLineBreak) {
        return new RealConsoleMatrix(width, height, autoLineBreak);
    }

    public final ConsoleMatrix getView(int offsetX, int offsetY,
                                       int width, int height) {
        if (offsetX + width > this.getWidth() || offsetY + height > this.getHeight()) {
            throw new InternalError("getView out of range");
        }
        return new MatrixView(this,
                offsetX, offsetY,
                width, height,
                this.isAutoLineBreak());
    }

    public final ConsoleMatrix[] splitHorizontal(int[] widths) {
        int offset = 0;
        ConsoleMatrix[] views = new ConsoleMatrix[widths.length];
        for (int i = 0; i < views.length; ++i) {
            views[i] = this.getView(offset, 0, widths[i], this.getHeight());
            offset += widths[i];
        }
        return views;
    }

    public final ConsoleMatrix[] splitVertical(int[] heights) {
        int offset = 0;
        ConsoleMatrix[] views = new ConsoleMatrix[heights.length];
        for (int i = 0; i < views.length; ++i) {
            views[i] = this.getView(0, offset, this.getWidth(), heights[i]);
            offset += heights[i];
        }
        return views;
    }

    /**
     * Clear the content in the matrix.
     */
    public void clear() {
        this.clear(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Clear a square in the matrix.
     *
     * @param x      the x coordinate of the "clear square"
     * @param y      the y coordinate of the "clear square"
     * @param width  the width of the "clear square"
     * @param height the height of the "clear square"
     */
    public abstract void clear(int x, int y, int width, int height);

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isAutoLineBreak() {
        return this.autoLineBreak;
    }

    public void setAutoLineBreak(boolean value) {
        this.autoLineBreak = value;
    }

    /**
     * Get a character in the console matrix.
     * <p>
     * Java {@link Character} is a 16-bit type,
     * so it might not be able to handle all characters properly (such as emoji)
     * And it could be possible that we use emoji to represent stuffs in Santorini :)
     * So we use a 32-bit {@link Integer} code point instead.
     *
     * EDIT: turns out emoji doesn't work well in console...
     * So... there are some wasted efforts here :(
     *
     * @param x The x position of character.
     * @param y The y position of character.
     */
    public abstract int getCharacter(int x, int y);

    /**
     * Set a character
     *
     * @param x         The x position of character.
     * @param y         The y position of character.
     * @param codePoint The new character.
     */
    public abstract void setCharacter(int x, int y, int codePoint);

    public final void setCharacter(int x, int y, String character) {
        if (character.codePoints().count() != 1) {
            throw new InternalError("Single character allowed");
        }
        this.setCharacter(x, y, character.codePointAt(0));
    }

    /**
     * Get a {@link PrintWriter} associated with current matrix.
     *
     * @return The {@link PrintWriter} which will put output on the matrix
     */
    public final PrintWriter getPrintWriter() {
        PrintWriter stream = new PrintWriter(new MatrixWriter(this)) {
            /**
             * Avoid {@link PrintWriter} outputting platform specific
             * new line characters.
             */
            @Override
            public void println() {
                this.write('\n');
            }
        };
        this.notifyPrintWriter(stream);
        return stream;
    }

    /**
     * Notify a {@link PrintWriter} has been created.
     * Then the {@link ConsoleMatrix} can track the stream object,
     * for example call {@link PrintStream#flush()} when {@link #toString()}
     * is called.
     *
     * @param printWriter the newly created {@link PrintStream}
     */
    protected abstract void notifyPrintWriter(PrintWriter printWriter);

    @Override
    public abstract String toString();
}

/**
 * An actual {@link ConsoleMatrix}.
 */
class RealConsoleMatrix extends ConsoleMatrix {
    private final int[] buffer;
    private final List<PrintWriter> writers;
    private int lastRow;

    public RealConsoleMatrix(int width, int height, boolean autoLineBreak) {
        super(width, height, autoLineBreak);
        this.buffer = new int[height * width];
        this.clear();
        this.writers = new ArrayList<>();
        this.lastRow = 0;
    }

    @Override
    public void clear(int x, int y, int width, int height) {
        if ((x + width) > this.getWidth() || (y + height) > this.getHeight()) {
            throw new InternalError("Out of range");
        }

        final int space = " ".codePointAt(0);

        for (int row = 0; row < height; ++row) {
            final int offset = (y + row) * this.getWidth();
            for (int column = 0; column < width; ++column) {
                this.buffer[offset + column + x] = space;
            }
        }
    }

    @Override
    public int getCharacter(int x, int y) {
        if (x >= this.getWidth() || y >= this.getHeight()) {
            throw new InternalError("Out of range");
        }

        return this.buffer[y * this.getWidth() + x];
    }

    @Override
    public void setCharacter(int x, int y, int codePoint) {
        if (x >= this.getWidth() || y >= this.getHeight()) {
            throw new InternalError("Out of range");
        }

        this.buffer[y * this.getWidth() + x] = codePoint;
        if (y > this.lastRow) {
            this.lastRow = y;
        }
    }

    @Override
    public String toString() {
        this.writers.forEach(PrintWriter::flush);

        StringBuilder builder = new StringBuilder();
        int rows = Math.min(this.getHeight(), this.lastRow);
        for (int y = 0; y < rows; ++y) {
            final int offset = y * this.getWidth();
            for (int x = 0; x < this.getWidth(); ++x) {
                builder.appendCodePoint(this.buffer[offset + x]);
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    protected void notifyPrintWriter(PrintWriter printWriter) {
        this.writers.add(printWriter);
    }
}

class MatrixView extends ConsoleMatrix {
    private final ConsoleMatrix original;
    private final int offsetX;
    private final int offsetY;

    public MatrixView(ConsoleMatrix original,
                      int offsetX, int offsetY,
                      int width, int height,
                      boolean autoLineBreak) {
        super(width, height, autoLineBreak);
        this.original = original;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void clear(int x, int y, int width, int height) {
        this.original.clear(x + offsetX, y + offsetY, width, height);
    }

    @Override
    public int getCharacter(int x, int y) {
        return this.original.getCharacter(this.offsetX + x, this.offsetY + y);
    }

    @Override
    public void setCharacter(int x, int y, int codePoint) {
        this.original.setCharacter(this.offsetX + x, this.offsetY + y, codePoint);
    }

    @Override
    protected void notifyPrintWriter(PrintWriter printWriter) {
        this.original.notifyPrintWriter(printWriter);
    }

    @Override
    public String toString() {
        throw new InternalError("Not implemented yet for MatrixView");
    }
}

/**
 * The backend of a {@link PrintWriter} which writes to the
 * {@link ConsoleMatrix}.
 */
class MatrixWriter extends Writer {
    private final ConsoleMatrix matrix;
    private final int newLine;
    private int nextX;
    private int nextY;

    public MatrixWriter(ConsoleMatrix matrix) {
        this.matrix = matrix;
        this.newLine = "\n".codePointAt(0);
        this.nextX = 0;
        this.nextY = 0;
    }

    public void goToNextLine() {
        this.nextY += 1;
        this.nextX = 0;
        if (this.nextY < this.matrix.getHeight()) {
            int value = this.matrix.getCharacter(this.nextX, this.nextY);
            // force RealConsoleMatrix to extend its maxRows
            this.matrix.setCharacter(this.nextX, this.nextY, value);
        }
    }

    /**
     * Write a character. If {@link #matrix}'s {@link ConsoleMatrix#isAutoLineBreak()}
     * is enabled, then {@link #goToNextLine()} will be automatically called
     * when the end of line is reached.
     *
     * @param codePoint The character to write.
     */
    public void writeCodepoint(int codePoint) {
        if (codePoint == this.newLine) {
            this.goToNextLine();
            return;
        }

        if (this.nextX >= matrix.getWidth()) {
            if (this.matrix.isAutoLineBreak()) {
                this.goToNextLine();
            } else {
                // use a "…" to represent some omitted characters
                // because there are no more spaces.
                this.matrix.setCharacter(this.matrix.getWidth() - 1,
                        this.nextY,
                        "…");
                return;
            }
        }

        if (this.nextY >= matrix.getHeight()) {
            throw new InternalError("Max height exceeded");
        }

        this.matrix.setCharacter(this.nextX, this.nextY, codePoint);
        ++this.nextX;
    }

    @Override
    public void write(char[] buf, int off, int len) {
        int[] codePoints = new String(buf, off, len).codePoints().toArray();
        for (int codePoint : codePoints) {
            this.writeCodepoint(codePoint);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}