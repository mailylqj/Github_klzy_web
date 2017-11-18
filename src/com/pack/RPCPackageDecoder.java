/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.pack;

import java.util.zip.Adler32;

import com.util.const_value;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * A {@link ProtocolDecoder} which decodes a text line into a string.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class RPCPackageDecoder implements ProtocolDecoder {
    private Logger log = Logger.getLogger(getClass());
    public final static int CPR_NOT_FOUND_PACKAGE = -1;//
    public final static int CPR_NOT_COMPLETE = -2;//
    public final static int CPR_DATA_CHECK_ERROR = -3;//

    public static class CheckValidResult {
        int result = 0;
        int package_size = 0;
        int proc_type = 0;  //1 dtu reg  ,2 dtu unreg  9 dtu send data ,0 others pro
    }

    private static final AttributeKey FRAGMENT_BUFFER = new AttributeKey(
            RPCPackageDecoder.class, "fragment");


    /**
     * {@inheritDoc}
     */
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        try {
            IoBuffer fragment = getFragmentBuffer(session);
            int limit = fragment.limit();
            fragment.limit(fragment.capacity());
            fragment.position(limit);
            fragment.put(in.array(), in.position(), in.remaining());
            in.position(in.limit());
            fragment.flip();

            while (fragment.remaining() > 0) {
                CheckValidResult r = CheckValidPackage(fragment);
                if (r.result == CPR_NOT_FOUND_PACKAGE || r.result == CPR_NOT_COMPLETE) {
                    fragment.position(r.package_size);
                    fragment.compact();
                    fragment.flip();
                    return;
                } else if (r.result == CPR_DATA_CHECK_ERROR) {
                    fragment.compact();
                    fragment.flip();
                    log.error("data error,skip " + r.package_size + " bytes.");
                } else {
                    fragment.position(r.result);
                    int write_lens = r.package_size;
                    if (r.proc_type != 0) {
                        write_lens += 4;  //add dtu head;
                    }
                    IoBuffer buf = IoBuffer.allocate(write_lens);
                    if (r.proc_type == 1) {
                        buf.putInt(const_value.DTU1_HEAD);
                    } else if (r.proc_type == 2) {
                        buf.putInt(const_value.DTU2_HEAD);
                    } else if (r.proc_type == 9) {
                        buf.putInt(const_value.DTU9_HEAD);
                    }
                    buf.put(fragment.array(), fragment.position(), r.package_size);
                    buf.position(0);
                    out.write(buf);
                    fragment.position(fragment.position() + r.package_size);
                    fragment.compact();
                    fragment.flip();
                }
            }

        } catch (Exception e) {
            //IdealLogger.Instance().PutException(e);
        }
    }

    private IoBuffer getFragmentBuffer(IoSession session) {
        IoBuffer buffer = (IoBuffer) session.getAttribute(FRAGMENT_BUFFER);

        if (buffer == null) {
            buffer = IoBuffer.allocate(5 * 1024).setAutoExpand(true);
            buffer.flip();
            session.setAttribute(FRAGMENT_BUFFER, buffer);
        }

        return buffer;
    }

    public static int CalcSendCRCSize(final IoBuffer message) {
        message.skip(1);
        byte fun = message.get();
        if (fun == 0xF) {
            return 9;  // DTU_Fun15_ControlData
        } else if (fun == 0x10) {
            message.skip(4);
            byte data_count = message.get();
            return 7 + data_count;       //DTU_Fun16_ControlData
        }

        return 6; // DTU_Common_ControlData
    }

    public static int CalcRecCRCSize(final IoBuffer message) {
        message.skip(1);
        byte fun = message.get();
        if (fun == 0x5 || fun == 0x6 || fun == 0xF || fun == 0x10) {
            return 6;  //
        } else if (fun == 0x1 || fun == 0x2 || fun == 0x3 || fun == 0x4) {
            byte data_count = message.get();
            return 3 + data_count;
        }

        return 6; // DTU_Common_ControlData
    }

    public static boolean CheckCRC(final IoBuffer message, int crc_size) {
        byte[] crc_byte = new byte[crc_size];
        message.get(crc_byte);

        int ret = CRCUtil.calcCrc16(crc_byte);
        int ms_crc = message.getUnsignedShort();
        return ms_crc == ret;
    }

    public static CheckValidResult proc_RIP_HEAD(CheckValidResult result, IoBuffer buffer) {
        final int head_offset = buffer.position();
        result.result = head_offset;
        result.package_size = 4; //rip0
        return result;
    }

    public static CheckValidResult proc_REG_HEAD(CheckValidResult result, IoBuffer buffer) {
        if (buffer.remaining() < 15) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        final int head_offset = buffer.position();
        result.result = head_offset;
        result.package_size = 15; //reg0+11 uuid
        return result;
    }

    public static CheckValidResult proc_MODS_HEAD(CheckValidResult result, IoBuffer buffer) {
        final int head_offset = buffer.position();
        if (buffer.remaining() < 23) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        buffer.skip(15);
        int cur_pos = buffer.position();
        int rpc_size = CalcSendCRCSize(buffer);
        buffer.position(cur_pos);
        boolean isEqual = CheckCRC(buffer, rpc_size);

        if (!isEqual) {
            result.result = CPR_DATA_CHECK_ERROR;
            result.package_size = head_offset + rpc_size + 17;
            System.err.println("package check error ");
            return result;//
        }
        result.result = head_offset;
        result.package_size = rpc_size + 17;

        return result;
    }

    public static CheckValidResult proc_MODC_HEAD(CheckValidResult result, IoBuffer buffer) {
        final int head_offset = buffer.position();
        if (buffer.remaining() < 10) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        buffer.skip(4);
        int cur_pos = buffer.position();
        int rpc_size = CalcRecCRCSize(buffer);
        buffer.position(cur_pos);
        boolean isEqual = CheckCRC(buffer, rpc_size);

        if (!isEqual) {
            result.result = CPR_DATA_CHECK_ERROR;
            result.package_size = head_offset + rpc_size + 6;
            System.err.println("package check error ");
            return result;//
        }
        result.result = head_offset;
        result.package_size = rpc_size + 6;

        return result;
    }

    public static CheckValidResult proc_HMIC_HEAD(CheckValidResult result, IoBuffer buffer) {
        final int head_offset = buffer.position();
        if (buffer.remaining() < 7) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        buffer.skip(4);
        byte pro_type = buffer.get();
        if (pro_type == 0x05) {
            buffer.position(head_offset);
            boolean isEqual = CheckCRC(buffer, 5);
            if (!isEqual) {
                result.result = CPR_DATA_CHECK_ERROR;
                result.package_size = head_offset + 7;
                result.proc_type = 0;
                System.err.println("package check error ");
                return result;//
            }
        } else if (pro_type == (byte) 0x01 || pro_type == (byte) 0x02
                || pro_type == (byte) 0x09 || pro_type == (byte) 0xA0
                || pro_type == (byte) 0xA1 || pro_type == (byte) 0xA2 || pro_type == (byte) 0xA3)  //reg or unreg or send or heart
        {
            int len = buffer.getUnsignedShort();
            if (buffer.remaining() < len - 7) {
                result.result = CPR_NOT_COMPLETE;
                return result;
            }
            buffer.position(head_offset);
            boolean isEqual = CheckCRC(buffer, len - 2);
            if (!isEqual) {
                result.result = CPR_DATA_CHECK_ERROR;
                result.package_size = head_offset + len;
                System.err.println("package check error ");
                return result;//
            }
            result.result = head_offset;
            result.package_size = len;
            result.proc_type = 0;
            return result;

        }
        return result;
    }

    public static CheckValidResult proc_PROT_HEAD(CheckValidResult result, IoBuffer buffer) {
        //		m_package_byte_size = 4		 // head ("PROT")
        //		+ 2				     //2byte call method index or errorcode
        //		+ 4					 //msg_lens
        //		+  message_byte_size //message
        //		+ 4;				 //Adler32
        final int head_offset = buffer.position();
        if (buffer.remaining() < 14) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        buffer.skip(6);
        int data_size = buffer.getInt();
        int remain_data_size = buffer.remaining();
        if (remain_data_size < data_size + 4) {//
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        //
        int adler_data;
        buffer.skip(data_size);
        if (buffer.remaining() < 4) {// ��ݰ�����
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        adler_data = buffer.getInt();
        Adler32 adler = new Adler32();
        adler.update(buffer.array(), head_offset + 4,
                6 + data_size);
        int checksum = (int) adler.getValue();
        if (adler_data != checksum) {
            result.result = CPR_DATA_CHECK_ERROR;
            result.package_size = head_offset + 14 + data_size;
            System.err.println("package check error " + adler_data
                    + "!=" + checksum);
            return result;// У�����
        }
        result.result = head_offset;
        result.package_size = 14 + data_size;
        return result;
    }

    public static CheckValidResult proc_DTU_HEAD(CheckValidResult result, IoBuffer buffer) {
        final int head_offset = buffer.position();
        if (buffer.remaining() < 16) {
            result.result = CPR_NOT_COMPLETE;
            return result;
        }
        buffer.skip(1);
        byte pro_type = buffer.get();
        if (pro_type == 0x01)  //dtu reg
        {
            int len = buffer.getUnsignedShort();
            if (len == 0x16) {
                if (buffer.remaining() < len - 4) {
                    result.result = CPR_NOT_COMPLETE;
                    return result;
                }
                buffer.skip(len - 5);
                byte end_byte = buffer.get();
                if (end_byte == (byte) 0x7B) {
                    result.result = head_offset;
                    result.package_size = len;
                    result.proc_type = 1;
                    return result;
                }
            }
        } else if (pro_type == (byte) 0x02)  //unreg
        {
            int len = buffer.getUnsignedShort();
            if (len == 0x10) {
                if (buffer.remaining() < len - 4) {
                    result.result = CPR_NOT_COMPLETE;
                    return result;
                }
                buffer.skip(len - 5);
                byte end_byte = buffer.get();
                if (end_byte == (byte) 0x7B) {
                    result.result = head_offset;
                    result.package_size = len;
                    result.proc_type = 2;
                    return result;
                }
            }
        } else if (pro_type == 0x09)  //send data
        {
            int len = buffer.getUnsignedShort();
            if (len >= 20 && len <= 16 + 1024)   //user data <=1024
            {
                if (buffer.remaining() < len - 4) {
                    result.result = CPR_NOT_COMPLETE;
                    return result;
                }
                buffer.skip(len - 5);
                byte end_byte = buffer.get();
                if (end_byte == (byte) 0x7B) {
                    //check modbus crc
                    int mod_bus_pos = buffer.position() - len + 15;
                    buffer.position(mod_bus_pos);
                    int rpc_size = CalcRecCRCSize(buffer);
                    buffer.position(mod_bus_pos);
                    boolean isEqual = CheckCRC(buffer, rpc_size);
                    if (!isEqual) {
                        result.result = CPR_DATA_CHECK_ERROR;
                        result.package_size = head_offset + len;
                        System.err.println("package check error ");
                        return result;//
                    }
                    result.result = head_offset;
                    result.package_size = len;
                    result.proc_type = 9;
                    return result;
                }
            }
        }
        return result;
    }

    public static CheckValidResult CheckValidPackage(IoBuffer buffer) {
        CheckValidResult result = new CheckValidResult();
        result.result = CPR_NOT_FOUND_PACKAGE;
        result.package_size = 0;
        result.proc_type = 0;

        int remain_data_size = buffer.remaining();
        if (remain_data_size == 0)
            return result;

        int head_code = 0;
        while (buffer.remaining() >= 4) {
            byte dtu_byte = buffer.get();
            byte next_dtu_byte = buffer.get();
            buffer.position(buffer.position() - 2);
            if (dtu_byte == (byte) 0x7B && next_dtu_byte != (byte) 0x7B) {
                proc_DTU_HEAD(result, buffer);
                if (result.result != CPR_NOT_FOUND_PACKAGE) {
                    return result;
                }
            } else {
                head_code = buffer.getInt();
                buffer.position(buffer.position() - 4);
            }

            if (head_code == const_value.PROT_HEAD) {
                proc_PROT_HEAD(result, buffer);
                return result;
            } else if (head_code == const_value.RIP0_HEAD) {
                proc_RIP_HEAD(result, buffer);
                return result;
            } else if (head_code == const_value.REG_HEAD) {
                proc_REG_HEAD(result, buffer);
                return result;
            } else if (head_code == const_value.MODS_HEAD) {
                proc_MODS_HEAD(result, buffer);
                return result;
            } else if (head_code == const_value.HMIS_HEAD) {

            } else if (head_code == const_value.MODC_HEAD) {
                proc_MODC_HEAD(result, buffer);
                return result;
            } else if (head_code == const_value.HMIC_HEAD) {
                proc_HMIC_HEAD(result, buffer);
                return result;
            } else {
                buffer.skip(1);
                result.package_size += 1;
            }
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
            throws Exception {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void dispose(IoSession session) throws Exception {
       // System.out.println("real——客户端异常 ");
    }

}