package com.ihunuo.hnmjpeg.opengl2.filter;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import com.ihunuo.hnmjpeg.opengl2.utils.MatrixUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: classes.dex */
public class GroupFilter extends AFilter {
    private int[] fFrame;
    private int[] fRender;
    private int[] fTexture;
    private int fTextureSize;
    private int height;
    private Queue<AFilter> mFilterQueue;
    private List<AFilter> mFilters;
    private int size;
    private int textureIndex;
    private int width;

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void initBuffer() {
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onCreate() {
    }

    public GroupFilter(Resources resources) {
        super(resources);
        this.width = 0;
        this.height = 0;
        this.size = 0;
        this.fTextureSize = 2;
        this.fFrame = new int[1];
        this.fRender = new int[1];
        this.fTexture = new int[2];
        this.textureIndex = 0;
        this.mFilters = new ArrayList();
        this.mFilterQueue = new ConcurrentLinkedQueue();
    }

    public void addFilter(AFilter aFilter) {
        MatrixUtils.flip(aFilter.getMatrix(), false, true);
        this.mFilterQueue.add(aFilter);
    }

    public boolean removeFilter(AFilter aFilter) {
        boolean remove = this.mFilters.remove(aFilter);
        if (remove) {
            this.size--;
        }
        return remove;
    }

    public AFilter removeFilter(int i) {
        AFilter remove = this.mFilters.remove(i);
        if (remove != null) {
            this.size--;
        }
        return remove;
    }

    public void clearAll() {
        this.mFilterQueue.clear();
        this.mFilters.clear();
        this.size = 0;
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public void draw() {
        updateFilter();
        this.textureIndex = 0;
        if (this.size > 0) {
            for (AFilter aFilter : this.mFilters) {
                GLES20.glBindFramebuffer(36160, this.fFrame[0]);
                GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.fTexture[this.textureIndex % 2], 0);
                GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, this.fRender[0]);
                int i = this.textureIndex;
                if (i == 0) {
                    aFilter.setTextureId(getTextureId());
                } else {
                    aFilter.setTextureId(this.fTexture[(i - 1) % 2]);
                }
                aFilter.draw();
                unBindFrame();
                this.textureIndex++;
            }
        }
    }

    private void updateFilter() {
        while (true) {
            AFilter poll = this.mFilterQueue.poll();
            if (poll == null) {
                return;
            }
            poll.create();
            poll.setSize(this.width, this.height);
            this.mFilters.add(poll);
            this.size++;
            Log.d("ccc", "updateFilter: size=" + this.size);
        }
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    public int getOutputTexture() {
        return this.size == 0 ? getTextureId() : this.fTexture[(this.textureIndex - 1) % 2];
    }

    @Override // com.ihunuo.hnmjpeg.opengl2.filter.AFilter
    protected void onSizeChanged(int i, int i2) {
        this.width = i;
        this.height = i2;
        updateFilter();
        createFrameBuffer();
        Log.d("ccc", "onSizeChanged: ");
    }

    private boolean createFrameBuffer() {
        GLES20.glGenFramebuffers(1, this.fFrame, 0);
        GLES20.glGenRenderbuffers(1, this.fRender, 0);
        genTextures();
        GLES20.glBindFramebuffer(36160, this.fFrame[0]);
        GLES20.glBindRenderbuffer(36161, this.fRender[0]);
        GLES20.glRenderbufferStorage(36161, 33189, this.width, this.height);
        Log.d("ccc", "createFrameBuffer: " + this.width + this.height);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.fTexture[0], 0);
        GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, this.fRender[0]);
        unBindFrame();
        return false;
    }

    private void genTextures() {
        GLES20.glGenTextures(this.fTextureSize, this.fTexture, 0);
        for (int i = 0; i < this.fTextureSize; i++) {
            GLES20.glBindTexture(3553, this.fTexture[i]);
            GLES20.glTexImage2D(3553, 0, 6408, this.width, this.height, 0, 6408, 5121, null);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10241, 9729);
        }
    }

    private void unBindFrame() {
        GLES20.glBindRenderbuffer(36161, 0);
        GLES20.glBindFramebuffer(36160, 0);
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteRenderbuffers(1, this.fRender, 0);
        GLES20.glDeleteFramebuffers(1, this.fFrame, 0);
        GLES20.glDeleteTextures(1, this.fTexture, 0);
    }
}
