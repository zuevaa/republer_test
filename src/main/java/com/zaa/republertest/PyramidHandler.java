package com.zaa.republertest;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
@Service
@Sharable
@Qualifier("pyramidHandler")
public class PyramidHandler extends ChannelInboundHandlerAdapter  {
    private final Integer WEIGHT = 50;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof DefaultHttpRequest)) {
            respError(ctx);
            return;
        }
        DefaultHttpRequest request = (DefaultHttpRequest) msg;
        if (!request.method().toString().equals("GET")) {
            respError(ctx);
            return;
        }
        Integer level, index;
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        if (decoder.path().equals("/weight")) {
            try {
                level = Integer.decode(decoder.parameters().get("level").get(0));
                index = Integer.decode(decoder.parameters().get("index").get(0));
            } 
            catch (NumberFormatException e) {
                respError(ctx);
                return;                
            }
        }
        else if (decoder.path().indexOf("/weight/") == 0) {
            try {
                String[] splitPath = decoder.path().split("/");
                level = Integer.decode(splitPath[2]);
                index = Integer.decode(splitPath[3]);
            }
            catch (NumberFormatException e) {
                respError(ctx);
                return;                
            }
        }
        else {
            respNotFound(ctx);
            return;
        }
        Double result = getHumanEdgeWeight(level, index);
        ByteBuf buf = Unpooled.copiedBuffer(result.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    public void respError(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    public void respNotFound(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    public Double getHumanEdgeWeight(Integer level, Integer index) {
        Double parent_l, parent_r;
        if ((index < 0) || (index > level)) {
            throw new IllegalArgumentException(); 
        }
        if (level == 0) {
            return 0.0;
        }
        try {
            parent_l = (getHumanEdgeWeight(level-1, index-1)+WEIGHT)/2;
        }
        catch (IllegalArgumentException e) {
            parent_l = 0.0;
        }
        try {
            parent_r = (getHumanEdgeWeight(level-1, index)+WEIGHT)/2;
        }
        catch (IllegalArgumentException e) {
            parent_r = 0.0;
        }
        return parent_l + parent_r;
    }
}
