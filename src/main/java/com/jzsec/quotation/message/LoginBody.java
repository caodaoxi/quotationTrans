package com.jzsec.quotation.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by caodaoxi on 17-7-14.
 */
public class LoginBody implements MessageBody {
    private long createTime = System.currentTimeMillis();
    private int heartbeat;
    private String sourceId;
    private String targetId;
    private String password;
    private String version;


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte[] encode() {
        byte[] empty = new byte[64];
        Arrays.fill(empty, (byte)0x20);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        byte[] data = null;
        try {
            data = this.getSourceId().getBytes("UTF-8");
            dos.write(data);
            dos.write(empty, 0, 20 - data.length);

            data = this.getTargetId().getBytes("UTF-8");
            dos.write(data);
            dos.write(empty, 0, 20 - data.length);
            dos.writeInt(this.getHeartbeat());
            data = this.getPassword().getBytes("UTF-8");
            dos.write(data);
            dos.write(empty, 0, 16 - data.length);
            data = this.getVersion().getBytes("UTF-8");
            dos.write(data);
            dos.write(empty, 0, 32 - data.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] bytes = bos.toByteArray();
        return bytes;
    }
}
