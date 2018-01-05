package com.ziv.video.player;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 包装IjkPlayer创建和播放控制
 *
 * @author ziv on 18-1-4.
 */

public class IjkWrapper {
    private static final String TAG = IjkWrapper.class.getSimpleName();
    private IjkMediaPlayer mMediaPlayer;

    private SurfaceHolder mHolder;
    private IMediaPlayer.OnErrorListener onErrorListener;
    private IMediaPlayer.OnPreparedListener onPreparedListener;

    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    /**
     * 设置播放View
     *
     * @param context 用于播放的SurfaceView
     * @param surfaceView 用于播放的SurfaceView
     */
    public void setPlayerView(Context context, SurfaceView surfaceView) {
        if (mMediaPlayer == null) {
            Log.e(TAG, "IjkWrapper.setPlayerView() - Media player is null!");
            throw new RuntimeException("Media player is null");
        }

        if (surfaceView != null) {
            mHolder = surfaceView.getHolder();
        } else {
            SurfaceView mSurfaceView = new SurfaceView(context);
            mHolder = mSurfaceView.getHolder();
        }
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mMediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    /**
     * 获取player播放器实例
     *
     * @return player播放器
     */
    public IjkMediaPlayer getInstance(){
        if (mMediaPlayer == null) {
            mMediaPlayer = new IjkMediaPlayer();
        }
        if (mHolder != null) {
            mMediaPlayer.setDisplay(mHolder);
        } else {
            Log.e(TAG, "IjkWrapper.getInstance() - SurfaceView Holder is null!");
        }
        initPlayer();
        mMediaPlayer.prepareAsync();
        return mMediaPlayer;
    }

    /**
     * 初始化播放器并设置监听
     */
    private void initPlayer() {
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 50);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnErrorListener(onErrorListener);
    }

    /**
     * 释放原生库
     * 在退出应用时务必调用
     */
    public void releasePlayer(){
        IjkMediaPlayer.native_profileEnd();
    }
}
