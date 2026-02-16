package com.example.picturemaster.manager.websocket.disruptor;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 图片编辑事件 Disruptor 环形缓冲区配置
 */
@Configuration
public class PictureEditEventDisruptorConfig {
    @Resource
    private PictureEditEventWorkHandler pictureEditEventWorkHandler;

    @Bean("pictureEditEventDisruptor")
    public Disruptor<PictureEditEvent> disruptor() {
        // 定义 ringBuffer 的大小，百万级别请求
        int bufferSize = 1024 * 256;
        // 创建 Disruptor
        Disruptor<PictureEditEvent> disruptor = new Disruptor<>(
                PictureEditEvent::new,
                bufferSize,
                // 创建线程工厂，设置线程名称的前缀
                ThreadFactoryBuilder.create().setNamePrefix("pictureEditEventDisruptor").build()
        );

        // 设置消费者
        disruptor.handleEventsWithWorkerPool(pictureEditEventWorkHandler);
        // 启动disruptor
        disruptor.start();
        return disruptor;
    }
}
