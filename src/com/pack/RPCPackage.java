package com.pack;

import com.google.protobuf.InvalidProtocolBufferException;
import com.ideal.logic.const_value;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;


public class RPCPackage {
    /////////////////////////////////////////////////////////
    public static ByteBuffer PackageClientProtoData(com.google.protobuf.Message message, short msg_idx) {
        if (message == null)
            return null;
        final byte message_serial_data[] = message.toByteArray();
        int message_byte_size = message_serial_data.length;

        int package_byte_size = (4 // package_head
                + 2 // proto_method_index
                + 4 // message_byte_size
                + message_byte_size // message_serial_data
                + 4);

        ByteBuffer buffer = ByteBuffer.allocate(package_byte_size);

        buffer.putInt(const_value.PROT_HEAD);
        buffer.putShort(msg_idx);
        buffer.putInt(message_byte_size);
        buffer.put(message_serial_data);

        Adler32 adler = new Adler32();
        final byte[] buffer_byte_array = buffer.array();
        adler.update(buffer_byte_array, 4, 6 + message_byte_size);
        buffer.putInt((int) adler.getValue());

        buffer.flip();

        return buffer;
    }


    public static class RPCProtoData extends Object {
        public short method_index = -1;
        public com.google.protobuf.Message message = null;
    }

    public static Boolean UnPackageFrontProtoData(RPCProtoData data, IoBuffer buffer,
                                                  com.google.protobuf.Service rpc_service) {
        int head = buffer.getInt();
        //
        data.method_index = buffer.getShort();
        com.google.protobuf.Descriptors.ServiceDescriptor serv_desc = rpc_service
                .getDescriptorForType();
        com.google.protobuf.Message message_prototype = null;
        try {
            if (data.method_index >= 0 && data.method_index < 50) {
                final com.google.protobuf.Descriptors.MethodDescriptor method = serv_desc.getMethods().get(data.method_index);
                if (method == null)
                    return false;
                message_prototype = rpc_service.getResponsePrototype(method);

            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //
        int msg_len = buffer.getInt();// buffer.ReadU32();
        byte message_data[] = new byte[msg_len];
        buffer.get(message_data);// buffer.Read(m_data,u16_htons);
        buffer.position(buffer.position() + 4);
        try {
            data.message = message_prototype.newBuilderForType()
                    .mergeFrom(message_data, 0, message_data.length).build();
        } catch (InvalidProtocolBufferException e) {
            return false;
        }
        return true;
    }
}
