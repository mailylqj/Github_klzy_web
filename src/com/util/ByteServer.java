package com.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class ByteServer {

    private  static  ByteServer instance = new ByteServer();

    public static ByteServer getInstance() {
        return instance;
    }

    /**
     * @param b
     * @param dataType  数据类型
     * @param HLtype    高低位
     * @param formatLen 小数点位数
     * @return
     */
    public float byteArrayToValue(byte[] b, int dataType, int HLtype, int formatLen) {

        b = HLChange(dataType, b, HLtype);
        float value = 0;
        switch (dataType) {
            case 0: //位 转换
                value = byte2short_w(b);
                break;
            case 1: // 16 无符号
                value = byte2short_w(b);
                break;
            case 2: // 16 有符号
                value = byte2short_n(b);
                break;
            case 3: // 32 无符号
                value = byte2int_w(b);
                break;
            case 4: // 32 有符号
                value = byte2int_n(b);
                break;
            case 5: // 32 浮点
                value = byte2float(b);
                break;
            case 6: // 64 浮点符号
                break;
            case 7: // long 长整
                break;
            case 8:
                break;
        }
        if (formatLen > 0)
            value = (float) (value / Math.pow(10, formatLen));
        return value;
    }

    /**
     * 16-无符号
     */
    public float byte2short_w(byte[] b) {
        return ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
    }

    /**
     * 16-有符号
     */
    public float byte2short_n(byte[] b) {
        float value = ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
        if (value != 0) {
            if (value > 32767) {
                negative(value);
            }
        }
        return value;
    }

    /**
     * 32-无符号
     */
    public float byte2int_w(byte[] b) {
        return ((b[3] & 0xFF) << 24) | ((b[2] & 0xFF) << 16) | ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
    }

    /**
     * 32-有符号
     */
    public float byte2int_n(byte[] b) {
        float value = ((b[3] & 0xFF) << 24) | ((b[2] & 0xFF) << 16) | ((b[1] & 0xFF) << 8) | (b[0] & 0xFF);
        if (value != 0) {
            if (value > 2147483647) {
                negative(value);
            }
        }
        return value;
    }

    /**
     * 浮点数
     */
    public float byte2float(byte[] b) {
        int l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);

        float value = Float.intBitsToFloat(l);
        return value;
        //BigDecimal bd = new BigDecimal(f);
        //float value = bd.setScale(formatLen, BigDecimal.ROUND_HALF_UP).floatValue();
    }


    /**
     * 长整型
     */
    public float byte2long(byte[] b) {
        return ((((long) b[7] & 0xff) << 56)| (((long) b[6] & 0xff) << 48)| (((long) b[5] & 0xff) << 40)| (((long) b[4] & 0xff) << 32)
                |(((long) b[3] & 0xff) << 24)| (((long) b[2] & 0xff) << 16) | (((long) b[1] & 0xff) << 8)| ((long) b[0] & 0xff));
    }

    /**
     *  浮点转byte
     */
    public byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    /**
     *  整形转byte
     */
    public byte[] int2byte(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000) >> 24);
        byte_src[2] = (byte) ((value & 0x00FF0000) >> 16);
        byte_src[1] = (byte) ((value & 0x0000FF00) >> 8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }

    /**
     * @param value
     */
    private float negative(float value) {
        String binary_str = null;
        String content = null;

        value = ~((int) value - 1);
        binary_str = Integer.toBinaryString((int) value);
        binary_str = binary_str.substring(16, binary_str.length());
        String fh = binary_str.substring(0, 1);
        content = "-" + binary_str.substring(1, binary_str.length());
        value = Integer.parseInt(content, 2);

        return value;
    }

    /**
     * 高低字节数据交换 原表示为 12345678
     */
    public byte[] HLChange(int datatype, byte[] b_src, int type) {
        byte[] b_rst = new byte[10];
        //批量传输的32位64都需交换高低字节

        switch (type) {
            case 0:
                // 12345678-12345678
                b_rst[0] = b_src[0];
                b_rst[1] = b_src[1];
                b_rst[2] = b_src[2];
                b_rst[3] = b_src[3];
                break;
            case 1:
                // 1234-2143
                b_rst[0] = b_src[1];
                b_rst[1] = b_src[0];
                b_rst[2] = b_src[3];
                b_rst[3] = b_src[2];
                break;
            case 2:
                // 1234 - 3412
                b_rst[0] = b_src[2];
                b_rst[1] = b_src[3];
                b_rst[2] = b_src[0];
                b_rst[3] = b_src[1];
                break;
            case 3:
                // 1234 - 4321
                b_rst[0] = b_src[3];
                b_rst[1] = b_src[2];
                b_rst[2] = b_src[1];
                b_rst[3] = b_src[0];
                break;
            case 4:
                // 12345678 - 21436587
                b_rst[0] = b_src[1];
                b_rst[1] = b_src[0];
                b_rst[2] = b_src[3];
                b_rst[3] = b_src[2];
                b_rst[4] = b_src[5];
                b_rst[5] = b_src[4];
                b_rst[6] = b_src[7];
                b_rst[7] = b_src[6];
                break;
            case 5:
                // 12345678 - 56781234
                b_rst[0] = b_src[4];
                b_rst[1] = b_src[5];
                b_rst[2] = b_src[6];
                b_rst[3] = b_src[7];
                b_rst[4] = b_src[0];
                b_rst[5] = b_src[1];
                b_rst[6] = b_src[2];
                b_rst[7] = b_src[3];
                break;
            case 6:

                break;
        }
        return b_rst;
    }

    public  byte[] StringToByteArray(String s) {
        try {
            return s.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
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


    // 16进制字符串转字节数组
    public byte[] hexString2Bytes(String hex) {
        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }



}
