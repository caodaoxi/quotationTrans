package com.jzsec.quotation.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import java.nio.ByteOrder;
import java.util.List;

public class BodyLengthAdjustFieldBasedFrameDecoder extends ByteToMessageDecoder {
    private final ByteOrder byteOrder;
    private final int maxFrameLength;
    private final int lengthFieldOffset;
    private final int lengthFieldLength;
    private final int lengthFieldEndOffset;
    private final int lengthAdjustment;
    private final int initialBytesToStrip;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private long tooLongFrameLength;
    private long bytesToDiscard;
    private long bodyLengthAdjustment;


    public BodyLengthAdjustFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, long bodyLengthAdjustment) {
        this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0, bodyLengthAdjustment);
    }

    public BodyLengthAdjustFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, long bodyLengthAdjustment) {
        this(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, true);
    }

    public BodyLengthAdjustFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
        this(ByteOrder.BIG_ENDIAN, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, bodyLengthAdjustment, failFast);
    }

    public BodyLengthAdjustFieldBasedFrameDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, long bodyLengthAdjustment, boolean failFast) {
        if(byteOrder == null) {
            throw new NullPointerException("byteOrder");
        } else if(maxFrameLength <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + maxFrameLength);
        } else if(lengthFieldOffset < 0) {
            throw new IllegalArgumentException("lengthFieldOffset must be a non-negative integer: " + lengthFieldOffset);
        } else if(initialBytesToStrip < 0) {
            throw new IllegalArgumentException("initialBytesToStrip must be a non-negative integer: " + initialBytesToStrip);
        } else if(lengthFieldOffset > maxFrameLength - lengthFieldLength) {
            throw new IllegalArgumentException("maxFrameLength (" + maxFrameLength + ") " + "must be equal to or greater than " + "lengthFieldOffset (" + lengthFieldOffset + ") + " + "lengthFieldLength (" + lengthFieldLength + ").");
        } else {
            this.byteOrder = byteOrder;
            this.maxFrameLength = maxFrameLength;
            this.lengthFieldOffset = lengthFieldOffset;
            this.lengthFieldLength = lengthFieldLength;
            this.lengthAdjustment = lengthAdjustment;
            this.lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
            this.initialBytesToStrip = initialBytesToStrip;
            this.bodyLengthAdjustment = bodyLengthAdjustment;
            this.failFast = failFast;
        }
    }

    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode(ctx, in);
        if(decoded != null) {
            out.add(decoded);
        }

    }

    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if(this.discardingTooLongFrame) {
            long actualLengthFieldOffset = this.bytesToDiscard;
            int localBytesToDiscard = (int)Math.min(actualLengthFieldOffset, (long)in.readableBytes());
            in.skipBytes(localBytesToDiscard);
            actualLengthFieldOffset -= (long)localBytesToDiscard;
            this.bytesToDiscard = actualLengthFieldOffset;
            this.failIfNecessary(false);
        }

        if(in.readableBytes() < this.lengthFieldEndOffset) {
            return null;
        } else {
            int actualLengthFieldOffset1 = in.readerIndex() + this.lengthFieldOffset;
            long frameLength = this.getUnadjustedFrameLength(in, actualLengthFieldOffset1, this.lengthFieldLength, this.byteOrder);
            if(frameLength < 0L) {
                in.skipBytes(this.lengthFieldEndOffset);
                throw new CorruptedFrameException("negative pre-adjustment length field: " + frameLength);
            } else {
                frameLength += (long)(this.lengthAdjustment + this.lengthFieldEndOffset);
                if(frameLength < (long)this.lengthFieldEndOffset) {
                    in.skipBytes(this.lengthFieldEndOffset);
                    throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less " + "than lengthFieldEndOffset: " + this.lengthFieldEndOffset);
                } else if(frameLength > (long)this.maxFrameLength) {
                    long frameLengthInt1 = frameLength - (long)in.readableBytes();
                    this.tooLongFrameLength = frameLength;
                    if(frameLengthInt1 < 0L) {
                        in.skipBytes((int)frameLength);
                    } else {
                        this.discardingTooLongFrame = true;
                        this.bytesToDiscard = frameLengthInt1;
                        in.skipBytes(in.readableBytes());
                    }

                    this.failIfNecessary(true);
                    return null;
                } else {
                    int frameLengthInt = (int)frameLength;
                    if(in.readableBytes() < frameLengthInt) {
                        return null;
                    } else if(this.initialBytesToStrip > frameLengthInt) {
                        in.skipBytes(frameLengthInt);
                        throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less " + "than initialBytesToStrip: " + this.initialBytesToStrip);
                    } else {
                        in.skipBytes(this.initialBytesToStrip);
                        int readerIndex = in.readerIndex();
                        int actualFrameLength = frameLengthInt - this.initialBytesToStrip;
                        ByteBuf frame = this.extractFrame(ctx, in, readerIndex, actualFrameLength);
                        in.readerIndex(readerIndex + actualFrameLength);
                        return frame;
                    }
                }
            }
        }
    }

    protected long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        buf = buf.order(order);
        long frameLength;
        switch(length) {
            case 1:
                frameLength = (long)buf.getUnsignedByte(offset);
                break;
            case 2:
                frameLength = (long)buf.getUnsignedShort(offset);
                break;
            case 3:
                frameLength = (long)buf.getUnsignedMedium(offset);
                break;
            case 4:
                frameLength = buf.getUnsignedInt(offset);
                break;
            case 5:
            case 6:
            case 7:
            default:
                throw new DecoderException("unsupported lengthFieldLength: " + this.lengthFieldLength + " (expected: 1, 2, 3, 4, or 8)");
            case 8:
                frameLength = buf.getLong(offset);
        }

        return frameLength + this.bodyLengthAdjustment;
    }

    private void failIfNecessary(boolean firstDetectionOfTooLongFrame) {
        if(this.bytesToDiscard == 0L) {
            long tooLongFrameLength = this.tooLongFrameLength;
            this.tooLongFrameLength = 0L;
            this.discardingTooLongFrame = false;
            if(!this.failFast || this.failFast && firstDetectionOfTooLongFrame) {
                this.fail(tooLongFrameLength);
            }
        } else if(this.failFast && firstDetectionOfTooLongFrame) {
            this.fail(this.tooLongFrameLength);
        }

    }

    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        ByteBuf frame = ctx.alloc().buffer(length);
        frame.writeBytes(buffer, index, length);
        return frame;
    }

    private void fail(long frameLength) {
        if(frameLength > 0L) {
            throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
        } else {
            throw new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding");
        }
    }
}
