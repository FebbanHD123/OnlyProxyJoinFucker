package de.febanhd.onlyproxyjoinfucker.utils;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MinecraftPingUtil {
   public static byte PACKET_HANDSHAKE = 0;
   public static byte PACKET_STATUSREQUEST = 0;
   public static byte PACKET_PING = 1;
   public static int PROTOCOL_VERSION = 4;
   public static int STATUS_HANDSHAKE = 1;

   public static void validate(Object o, String m) {
      if (o == null) {
         throw new RuntimeException(m);
      }
   }

   public static void io(boolean b, String m) throws IOException {
      if (b) {
         throw new IOException(m);
      }
   }

   public static int readVarInt(DataInputStream in) throws IOException {
      int i = 0;
      int j = 0;

      byte k;
      do {
         k = in.readByte();
         i |= (k & 127) << j++ * 7;
         if (j > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((k & 128) == 128);

      return i;
   }

   public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
      while((paramInt & -128) != 0) {
         out.writeByte(paramInt & 127 | 128);
         paramInt >>>= 7;
      }

      out.writeByte(paramInt);
   }
}
