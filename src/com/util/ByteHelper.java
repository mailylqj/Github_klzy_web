package com.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ByteHelper {

    private  final int INVALID_HASHID = 0;
    
    public  final int hash_normal(final byte[] utf8_byte) {
        int nr = 1, nr2 = 4;
        if (utf8_byte == null || utf8_byte.length == 0) {
            return INVALID_HASHID;
        }

        int length = utf8_byte.length;

        for (int i = 0; i < length; ++i) {
            nr ^= (((nr & 63) + nr2) * (utf8_byte[i])) + (nr << 8);
            nr2 += 3;
        }

        return nr;
    }

    public  Map<String, String> StringToMap(String mapText) {
        if (mapText == null || mapText.length() == 0)
            return null;
        Map<String, String> map = new HashMap<String, String>();
        if (mapText == null || mapText.equals("")) {
            return map;
        }
        String[] text = mapText.split("\\" + "|");
        for (String str : text) {
            String[] keyText = str.split("#");
            if (keyText.length < 2) {
                continue;
            }
            String key = keyText[0]; // key
            String value = keyText[1]; // value
            map.put(key, value);
        }
        return map;
    }

    public  String MapToString(Map<?, ?> map) {
        if (map == null || map.size() == 0)
            return null;
        StringBuffer sb = new StringBuffer();
        // 遍历map
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object key = obj;
            String sss = key.toString();
            Object value = map.get(key);
            sb.append(key.toString() + "#" + value.toString());
            sb.append("|");
        }
        return sb.toString();
    }

    public  Vector<byte[]> ByteArrayToVector(byte[] vecByteArray) {
        if (vecByteArray == null || vecByteArray.length == 0)
            return null;
        Vector<byte[]> vec = new Vector<byte[]>();
        if (vecByteArray == null || vecByteArray.length == 0) {
            return vec;
        }
        ByteBuffer buffer = ByteBuffer.allocate(vecByteArray.length);
        buffer.put(vecByteArray);
        buffer.rewind();

        while (buffer.remaining() > 0) {
            int key_len = buffer.getInt();
            byte[] key = new byte[key_len];
            buffer.get(key);
            vec.add(key);
        }
        return vec;
    }

    public  byte[] VectorToByteArray(Vector<byte[]> vec) {
        if (vec == null || vec.size() == 0)
            return null;
        int byte_len = 0;
        for (byte[] obj : vec) {
            if (obj == null) {
                continue;
            }
            byte_len += 4;
            byte_len += obj.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(byte_len);
        for (byte[] obj : vec) {
            if (obj == null) {
                continue;
            }
            buffer.putInt(obj.length);
            buffer.put(obj);
        }
        return buffer.array();
    }

    public  Map<String, byte[]> ByteArrayToMap(byte[] mapByteArray) {
        if (mapByteArray == null || mapByteArray.length == 0)
            return null;
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        ByteBuffer buffer = ByteBuffer.allocate(mapByteArray.length);
        buffer.put(mapByteArray);
        buffer.rewind();

        while (buffer.remaining() > 0) {
            int key_len = buffer.getInt();
            byte[] key = new byte[key_len];
            buffer.get(key);
            String key_str = null;
            try {
                key_str = new String(key, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                //IdealLogger.Instance().PutException(e);
            }
            //
            int value_len = buffer.getInt();
            byte[] value = new byte[value_len];
            buffer.get(value);
            map.put(key_str, value);
        }
        return map;
    }

    public  byte[] MapToByteArray(Map<String, byte[]> map) {
        if (map == null || map.size() == 0)
            return null;
        // 遍历map
        int byte_len = 0;
        try {
            for (String obj : map.keySet()) {
                if (obj == null) {
                    continue;
                }
                String key = obj;
                byte[] value = map.get(key);
                if (value == null)
                    continue;
                byte_len += 4;
                byte_len += key.getBytes("UTF-8").length;
                byte_len += 4;
                byte_len += value.length;
            }
            ByteBuffer buffer = ByteBuffer.allocate(byte_len);
            for (String obj : map.keySet()) {
                if (obj == null) {
                    continue;
                }
                String key = obj;
                byte[] value = map.get(key);
                if (value == null)
                    continue;
                buffer.putInt(key.getBytes("UTF-8").length);
                buffer.put(key.getBytes("UTF-8"));
                buffer.putInt(value.length);
                buffer.put(value);
            }
            return buffer.array();
        } catch (UnsupportedEncodingException e) {
            //IdealLogger.Instance().PutException(e);
        }
        return null;
    }

    public  byte[] StringToByteArray(String s) {
        try {
            return s.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public  String ByteArrayToString(byte b[]) {
        try {
            return new String(b, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            //IdealLogger.Instance().PutException(e);
        }
        return null;
    }

    public  byte[] intToByteArray(int i) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (i & 0xff);// ���λ
        targets[1] = (byte) ((i >> 8) & 0xff);// �ε�λ
        targets[2] = (byte) ((i >> 16) & 0xff);// �θ�λ
        targets[3] = (byte) ((i >> 24) & 0xff);// ���λ,�޷�����ơ�
        return targets;
    }

    public  int ByteArrayToint(byte b[]) {
        int targets = (b[0] & 0xff) | ((b[1] << 24) >>> 16) // | ��ʾ��λ��
                | ((b[2] << 24) >>> 8) | (b[3] << 24);
        return targets;
    }

    public  byte[] shortToByteArray(short i) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (i & 0xff);//
        targets[1] = (byte) ((i >> 8) & 0xff);//
        return targets;
    }

    public  short ByteArrayToshort(byte b[]) {
        int targets = b[0] | (b[1] << 8);
        return (short) targets;
    }

    public  byte[] longToByteArray(long i) {
        byte[] targets = new byte[8];

        targets[0] = (byte) (i);
        targets[1] = (byte) (i >> 8);
        targets[2] = (byte) (i >> 16);
        targets[3] = (byte) (i >> 24);
        targets[4] = (byte) (i >> 32);
        targets[5] = (byte) (i >> 40);
        targets[6] = (byte) (i >> 48);
        targets[7] = (byte) (i >> 56);
        return targets;
    }

    public  long ByteArrayTolong(byte b[]) {
        return ((((long) b[7] & 0xff) << 56)
                | (((long) b[6] & 0xff) << 48)
                | (((long) b[5] & 0xff) << 40)
                | (((long) b[4] & 0xff) << 32)
                | (((long) b[3] & 0xff) << 24)
                | (((long) b[2] & 0xff) << 16)
                | (((long) b[1] & 0xff) << 8)
                | ((long) b[0] & 0xff));
    }

}
