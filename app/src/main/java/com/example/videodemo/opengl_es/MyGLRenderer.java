package com.example.videodemo.opengl_es;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Miracle on 2020/7/12
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle triangle;
    private Square square;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //在View的OpenGL环境被创建的时候调用。
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);//设置背景颜色

        triangle = new Triangle();
        square = new Square();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 每一次View的重绘都会调用
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //重绘背景颜色
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        triangle.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //如果视图的几何形状发生变化（例如，当设备的屏幕方向改变时），则调用此方法。
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }
}
