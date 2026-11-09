package com.emm233.games.manager;

import com.emm233.games.core.Texture;
import com.emm233.launcher.util.Resource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class TextureManager implements AutoCloseable {
    private final Map<String, Texture> textures = new HashMap<>();

    public void load(String... keyPath) {
        if (keyPath.length % 2 != 0) {
            throw new RuntimeException("Error loading image because parameter is short(or redundant) of key or path");
        }

        String[] key = new String[]{""};

        Arrays.stream(keyPath).forEach((s -> {
            if (key[0].isEmpty()) {
                key[0] = s;
            } else {
                this.load(key[0],s);
                key[0] = "";
            }
        }));
    }
    public void load(String key,String path) {
        int w;
        int h;
        ByteBuffer byteBuffer = null;
        ByteBuffer imb = null;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wb = stack.mallocInt(1);
            IntBuffer hb = stack.mallocInt(1);
            IntBuffer channle = stack.mallocInt(1);

            byte[] bytes = Resource.getBytes(path);
            imb = MemoryUtil.memAlloc(bytes.length);
            imb.put(bytes).flip();

            byteBuffer = STBImage.stbi_load_from_memory(imb,wb,hb,channle,4);
            if (byteBuffer == null) {
                throw new RuntimeException("Error loading image : " + STBImage.stbi_failure_reason());
            }

            w = wb.get();
            h = hb.get();

            int id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D,id);
            glPixelStorei(GL_UNPACK_ALIGNMENT,1);

            //这两行第一行设置图片缩小时的取色方式，为了不出现摩尔纹为线性和近邻的混合，第二行是放大时，像素风一般为近邻
            //GL_TEXTURE_MIN_FILTER和GL_TEXTURE_MAG_FILTER必须一起设置，原因：不知道
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,w,h,0,GL_RGBA,GL_UNSIGNED_BYTE,byteBuffer);

            //配合上面两行第一行
            glGenerateMipmap(GL_TEXTURE_2D);

            glPixelStorei(GL_UNPACK_ALIGNMENT,4);
            glBindTexture(GL_TEXTURE_2D,0);

            if (this.textures.containsKey(key)) {
                this.textures.get(key).close();
                this.textures.remove(key);
            }
            this.textures.put(key,new Texture(id,w,h));
        } finally {
            if (imb != null) MemoryUtil.memFree(imb);
            if (byteBuffer != null) STBImage.stbi_image_free(byteBuffer);
        }
    }

    public Texture get(String key) {
        if (this.textures.containsKey(key)) {
            return this.textures.get(key);
        }
        throw new RuntimeException("Failed to get the texture");
    }

    @Override
    public void close() {
        this.textures.values().forEach(Texture::close);
        this.textures.clear();
    }
}
