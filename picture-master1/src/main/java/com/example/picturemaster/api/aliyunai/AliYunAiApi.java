package com.example.picturemaster.api.aliyunai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.picturemaster.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.example.picturemaster.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.example.picturemaster.api.aliyunai.model.GetOutPaintingTaskResponse;
import com.example.picturemaster.exception.BusinessException;
import com.example.picturemaster.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliYunAiApi {

    // 读取配置文件
    @Value("${aliYunAi.apiKey}")
    private String apiKey;

    // 创建任务地址
    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    // 查询任务状态
    public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 创建外部 AI 扩图任务
     * 该方法将请求参数发送到第三方 AI 服务，并返回任务创建的结果
     *
     * @param createOutPaintingTaskRequest 创建扩图任务的请求参数对象
     * @return 外部扩图任务的响应结果对象
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
        // 1. 校验请求参数：判断传入的请求对象是否为空
        if (createOutPaintingTaskRequest == null) {
            // 如果参数为空，抛出业务异常，提示“扩图参数为空”
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图参数为空");
        }

        // 2. 构建 HTTP POST 请求对象
        HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL) // 创建 POST 请求，指定目标 URL
                .header("Authorization", "Bearer " + apiKey) // 设置请求头：添加鉴权 Token（用于 API 身份验证）
                // 必须开启异步处理：告诉服务器这是一个异步任务，服务器应立即返回任务 ID，而不是等待结果
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json") // 设置请求头：指定请求体内容格式为 JSON
                // 设置请求体：将 Java 对象转换为 JSON 字符串作为请求体发送
                .body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));

        // 3. 发送请求并处理响应
        // try-with-resources 语法：会自动关闭 httpResponse 连接，防止资源泄露
        try (HttpResponse httpResponse = httpRequest.execute()) { // 执行请求，获得响应对象

            // 4. 校验 HTTP 状态码：判断响应状态码是否为 2xx (即 HTTP 请求是否成功)
            if (!httpResponse.isOk()) {
                // 如果 HTTP 请求失败（如 404, 500），记录错误日志，打印响应体内容
                log.error("请求异常：{}", httpResponse.body());
                // 抛出业务异常，提示“AI 扩图失败”
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
            }

            // 5. 解析响应体：将 JSON 格式的响应体字符串转换为 Java 对象
            CreateOutPaintingTaskResponse createOutPaintingTaskResponse = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);

            // 6. 校验业务状态码：判断响应对象中的业务 code 字段是否存在（通常 code 不为 null 表示业务逻辑处理失败）
            if (createOutPaintingTaskResponse.getCode() != null) {
                // 提取响应中的错误信息
                String errorMessage = createOutPaintingTaskResponse.getMessage();
                // 记录业务逻辑错误的日志
                log.error("请求异常：{}", errorMessage);
                // 抛出业务异常，提示具体的错误原因
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败，" + errorMessage);
            }

            // 7. 返回结果：如果一切正常，返回解析后的响应对象
            return createOutPaintingTaskResponse;
        }
    }


    /**
     * 根据 ID 获取外部 AI 绘画任务的结果
     *
     * @param taskId 任务 ID
     * @return 外部绘画任务的响应结果对象
     */
    public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
        // 1. 校验参数：判断传入的任务 ID 是否为空或空白字符串
        if (StrUtil.isBlank(taskId)) {
            // 如果 ID 为空，抛出业务异常，提示“任务 ID 不能为空”
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 ID 不能为空");
        }

        // 2. 构建请求 URL：使用 String.format 将任务 ID 填入 URL 模板中
        // 例如：GET_OUT_PAINTING_TASK_URL = "https://api.example.com/task/%s"，taskId = "123"  %s是占位符，实际用taskId替换%s
        // 最终 url = "https://api.example.com/task/123"
        String url = String.format(GET_OUT_PAINTING_TASK_URL, taskId);

        // 3. 发送 HTTP GET 请求
        // try-with-resources 语法：会自动关闭 httpResponse 连接，防止资源泄露
        try (HttpResponse httpResponse = HttpRequest.get(url) // 创建一个 GET 请求对象，指定请求地址
                .header("Authorization", "Bearer " + apiKey) // 设置请求头：添加鉴权信息（Bearer Token）
                .execute()) { // 执行请求，获得响应对象 httpResponse

            // 4. 判断 HTTP 响应状态码是否为 2xx (即请求是否成功)
            if (!httpResponse.isOk()) {
                // 如果请求失败（比如 404, 500），记录错误日志，打印响应体内容
                log.error("请求异常：{}", httpResponse.body());
                // 抛出业务异常，提示“获取任务结果失败”
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务结果失败");
            }

            // 5. 解析响应体
            // httpResponse.body() 获取到的是 JSON 格式的字符串
            // JSONUtil.toBean 将 JSON 字符串转换为指定的 Java 对象
            return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
        }
    }


}
