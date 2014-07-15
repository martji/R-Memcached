package com.myself.client;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

import com.myself.server.MDecoder;
import com.myself.server.MEncoder;

public class MClientPipelineFactory implements ChannelPipelineFactory 
{

	public ChannelPipeline getPipeline() throws Exception 
	{
		ChannelPipeline pipeline = Channels.pipeline();
		
		pipeline.addLast("decoder", new MDecoder());
		pipeline.addLast("encoder", new MEncoder());
		pipeline.addLast("handler", new MClientHandler());		
		return pipeline;
	}
}